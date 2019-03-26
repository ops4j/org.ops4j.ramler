/*
 * Copyright 2017 OPS4J Contributors
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
package org.ops4j.ramler.typescript;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.ops4j.ramler.typescript.TypescriptConstants.ANY;
import static org.ops4j.ramler.typescript.TypescriptConstants.BOOLEAN;
import static org.ops4j.ramler.typescript.TypescriptConstants.NUMBER;
import static org.ops4j.ramler.typescript.TypescriptConstants.STRING;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Map;

import org.ops4j.ramler.exc.GeneratorException;
import org.ops4j.ramler.generator.Names;
import org.ops4j.ramler.generator.Version;
import org.ops4j.ramler.model.ApiModel;
import org.ops4j.ramler.typescript.trimou.TypescriptTemplateEngine;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.util.ImmutableMap;

/**
 * Context information shared by all API visitors generating Typescript code.
 *
 * @author Harald Wellmann
 *
 */
public class TypescriptGeneratorContext {

    private static Logger log = LoggerFactory.getLogger(TypescriptGeneratorContext.class);

    private TypescriptConfiguration config;

    private ApiModel apiModel;

    private TypescriptTemplateEngine templateEngine;

    private Appendable output;

    /**
     * Creates a generator context for the given configuration.
     *
     * @param config
     *            Typescript generator configuration
     */
    public TypescriptGeneratorContext(TypescriptConfiguration config) {
        this.config = config;
    }

    /**
     * Gets the generator configuration.
     *
     * @return generator configuration
     */
    public TypescriptConfiguration getConfig() {
        return config;
    }

    /**
     * Sets the generator configuration.
     *
     * @param config
     *            generator configuration
     */
    public void setConfig(TypescriptConfiguration config) {
        this.config = config;
    }

    /**
     * Gets the RAML API model.
     *
     * @return API model
     */
    public ApiModel getApiModel() {
        return apiModel;
    }

    /**
     * Sets the RAML API model.
     *
     * @param apiModel
     *            API model
     */
    public void setApiModel(ApiModel apiModel) {
        this.apiModel = apiModel;
    }

    /**
     * Gets the template engine for code generation.
     *
     * @return template engine
     */
    public TypescriptTemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    /**
     * Sets the template engine.
     *
     * @param templateEngine
     *            template engine
     */
    public void setTemplateEngine(TypescriptTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Get the current output to which the template engine will write.
     *
     * @return the output
     */
    public Appendable getOutput() {
        return output;
    }

    /**
     * Sets the current output for the template engine.
     *
     * @param output
     *            the output to set
     */
    public void setOutput(Appendable output) {
        this.output = output;
    }

    /**
     * Starts a new output and generates a header comment.
     *
     * @return output
     */
    public StringBuilder startOutput() {
        StringBuilder builder = new StringBuilder();
        this.output = builder;

        Map<String, String> contextObject = ImmutableMap.of("version", Version.getRamlerVersion(),
            "date", ZonedDateTime.now().truncatedTo(SECONDS).format(ISO_OFFSET_DATE_TIME));
        getTemplateEngine().getEngine().getMustache("generated").render(output, contextObject);
        return builder;
    }

    /**
     * Gets the Typescript name for a given RAML property type.
     *
     * @param propertyType
     *            property type
     * @return Typescript name
     */
    public String getTypescriptPropertyType(TypeDeclaration propertyType) {
        String typeName = propertyType.type();
        if (propertyType instanceof StringTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (propertyType instanceof NumberTypeDeclaration) {
            return getDeclaredName(typeName, NUMBER);
        }
        if (propertyType instanceof BooleanTypeDeclaration) {
            return getDeclaredName(typeName, BOOLEAN);
        }
        if (propertyType instanceof DateTimeTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (propertyType instanceof DateTimeOnlyTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (propertyType instanceof DateTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (propertyType instanceof TimeOnlyTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (propertyType instanceof AnyTypeDeclaration) {
            return getDeclaredName(typeName, ANY);
        }
        if (propertyType instanceof ObjectTypeDeclaration) {
            return typeName;
        }
        if (propertyType instanceof ArrayTypeDeclaration) {
            return apiModel.getItemType(propertyType) + "[]";
        }
        throw new GeneratorException(
            "unsupported declaration type: " + propertyType.getClass().getName());
    }

    /**
     * Gets the Typescript name for a given user-defined RAML type.
     *
     * @param type
     *            RAML type
     * @return Typescript name
     */
    public String getTypescriptType(TypeDeclaration type) {
        String typeName = type.name();
        if (type instanceof StringTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (type instanceof NumberTypeDeclaration) {
            return getDeclaredName(typeName, NUMBER);
        }
        if (type instanceof BooleanTypeDeclaration) {
            return getDeclaredName(typeName, BOOLEAN);
        }
        if (type instanceof DateTimeTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (type instanceof DateTimeOnlyTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (type instanceof DateTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (type instanceof TimeOnlyTypeDeclaration) {
            return getDeclaredName(typeName, STRING);
        }
        if (type instanceof ObjectTypeDeclaration) {
            return typeName;
        }
        if (type instanceof ArrayTypeDeclaration) {
            return apiModel.getItemType(type) + "[]";
        }
        throw new GeneratorException("unsupported declaration type: " + type.getClass().getName());
    }

    private String getDeclaredName(String typeName, String fallback) {
        if (apiModel.getDeclaredType(typeName) == null) {
            return fallback;
        }
        else {
            return typeName;
        }
    }

    public void writeToFile(String content, String typeName) {
        String moduleName = Names.buildLowerKebabCaseName(typeName);
        String tsFileName = moduleName + ".ts";
        File tsFile = new File(config.getTargetDir(), tsFileName);
        log.debug("generating {}\n{}", tsFileName, content);
        try {
            Files.write(tsFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException exc) {
            throw new GeneratorException(exc);
        }
    }

}
