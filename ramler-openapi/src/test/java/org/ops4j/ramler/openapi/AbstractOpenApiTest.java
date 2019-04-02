/*
 * Copyright 2019 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.ramler.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.json.JsonReader;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.runtime.io.OpenApiParser;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractOpenApiTest {

    private static final String SCHEMAS_PREFIX = "#/components/schemas/";

    protected static Logger log = LoggerFactory.getLogger(AbstractOpenApiTest.class);

    protected OpenApiGenerator generator;

    protected Set<String> methodNames;

    protected Set<String> fieldNames;

    private OpenApiConfiguration config;

    private OpenAPIImpl openApi;

    private Schema schema;

    @BeforeAll
    public void setUp() throws IOException, ParseException {
        generateOpenApi();
        validateJson();
        parseYaml();
    }

    @BeforeEach
    public void reset() {
        schema = null;
    }

    private void generateOpenApi() throws IOException {
        config = new OpenApiConfiguration();
        config.setSourceFile(String.format("raml/%s.raml", getBasename()));
        config.setTargetDir(new File("target/generated/openapi/" + getBasename()));
        config.setGenerateJson(true);
        config.setGenerateYaml(true);

        generator = new OpenApiGenerator(config);
        generator.generate();
    }

    private void validateJson() {
        JsonValidationService service = JsonValidationService.newInstance();
        JsonSchema schema = service
            .readSchema(getClass().getResourceAsStream("/schema/openapi-v3-schema.json"));
        List<Problem> problems = new ArrayList<>();

        ProblemHandler handler = service.createProblemPrinter(log::error);
        Path path = config.getTargetDir()
            .toPath()
            .resolve(getBasename() + ".json");
        try (JsonReader reader = service.createReader(path, schema,
            ProblemHandler.collectingTo(problems))) {
            reader.readValue();
        }

        if (problems.isEmpty()) {
            return;
        }
        handler.handleProblems(problems);
        fail("There are JSON schema validation problems.");
    }

    private void parseYaml() throws IOException, ParseException {
        Path path = config.getTargetDir()
            .toPath()
            .resolve(getBasename() + ".yaml");
        openApi = OpenApiParser.parse(path.toUri()
            .toURL());

    }

    protected void assertSchemas(String... names) {
        assertThat(openApi.getComponents()
            .getSchemas()
            .keySet()).containsExactlyInAnyOrder(names);
    }

    protected void assertProperties(String... properties) {
        assertThat(schema)
            .as("expectSchema() must be called before assertProperties()")
            .isNotNull();
        assertThat(schema.getProperties()
            .keySet()).containsExactlyInAnyOrder(properties);
    }

    protected void assertRequiredProperties(String... properties) {
        assertThat(schema)
            .as("expectSchema() must be called before assertRequiredProperties()")
            .isNotNull();
        assertThat(schema.getRequired()).containsExactlyInAnyOrder(properties);
    }

    private Schema findSchema(String schemaName) {
        Schema schema = openApi.getComponents()
            .getSchemas()
            .get(schemaName);
        assertThat(schema)
            .as("schema '%s' not found", schemaName)
            .isNotNull();
        return schema;
    }

    protected void assertEnumValues(String schemaName, Object... enumValues) {
        Schema schema = findSchema(schemaName);
        assertThat(schema.getEnumeration()).containsExactly(enumValues);
    }

    protected void assertUnion(String schemaName, String... variants) {
        Schema schema = findSchema(schemaName);
        assertThat(schema.getOneOf()).isNotNull();
        assertThat(schema.getOneOf()).extracting(s -> {
            return s.getRef()
                .replaceAll(SCHEMAS_PREFIX, "");
        })
            .containsExactly(variants);
    }

    protected void assertStringProperty(String propertyName) {
        assertThat(schema)
            .as("expectSchema() must be called before assertStringProperty()")
            .isNotNull();
        Schema propertySchema = schema.getProperties()
            .get(propertyName);
        assertThat(propertySchema).isNotNull();
        assertThat(propertySchema.getType()).isEqualTo(SchemaType.STRING);

    }

    protected void assertIntegerProperty(String propertyName) {
        assertThat(schema)
            .as("expectSchema() must be called before assertIntegerProperty()")
            .isNotNull();
        Schema propertySchema = schema.getProperties()
            .get(propertyName);
        assertThat(propertySchema).isNotNull();
        assertThat(propertySchema.getType()).isEqualTo(SchemaType.INTEGER);

    }

    protected void assertRefProperty(String propertyName, String type) {
        assertThat(schema)
            .as("expectSchema() must be called before assertRefProperty()")
            .isNotNull();
        Schema propertySchema = schema.getProperties()
            .get(propertyName);
        assertThat(propertySchema).isNotNull();
        assertThat(propertySchema.getRef()).isEqualTo(SCHEMAS_PREFIX + type);
    }

    protected void assertArrayPropertyRef(String propertyName, String itemType) {
        Schema itemSchema = findItemSchema(propertyName);
        assertThat(itemSchema.getRef())
            .isEqualTo(SCHEMAS_PREFIX + itemType);
    }

    private Schema findItemSchema(String propertyName) {
        assertThat(schema)
            .as("expectSchema() must be called before assertArrayProperty()")
            .isNotNull();
        Schema propertySchema = schema.getProperties()
            .get(propertyName);
        assertThat(propertySchema).isNotNull();
        assertThat(propertySchema.getType()).isEqualTo(SchemaType.ARRAY);
        return propertySchema.getItems();
    }

    protected void expectSchema(String schemaName) {
        assertThat(schema)
            .as("expectSchema() must not be called more than once per test")
            .isNull();
        schema = findSchema(schemaName);

    }

    public abstract String getBasename();

}
