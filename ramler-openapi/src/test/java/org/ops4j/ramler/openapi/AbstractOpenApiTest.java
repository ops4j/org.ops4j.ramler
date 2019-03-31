/*
 * Copyright 2016 OPS4J Contributors
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

import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.json.JsonReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractOpenApiTest {

    protected static Logger log = LoggerFactory.getLogger(AbstractOpenApiTest.class);

    protected OpenApiGenerator generator;

    protected Set<String> methodNames;

    protected Set<String> fieldNames;

    private OpenApiConfiguration config;

    @BeforeAll
    public void setUp() throws IOException {
        generateOpenApi();
        validateJson();
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
        JsonSchema schema = service.readSchema(getClass().getResourceAsStream("/schema/openapi-v3-schema.json"));
        List<Problem> problems = new ArrayList<>();

        Path path = config.getTargetDir().toPath().resolve(getBasename() + ".json");
        try (JsonReader reader = service.createReader(path, schema, ProblemHandler.collectingTo(problems))) {
            reader.readValue();
        }

        if (problems.isEmpty()) {
            return;
        }
        problems.forEach(p -> log.error(p.toString()));
        fail("There are JSON schema validation problems.");
    }


    public abstract String getBasename();

}
