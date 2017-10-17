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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import org.ops4j.ramler.exc.GeneratorException;
import org.ops4j.ramler.generator.ApiTraverser;
import org.ops4j.ramler.generator.ApiVisitor;
import org.ops4j.ramler.generator.Names;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 * @author Harald Wellmann
 *
 */
public class ModelCreatingApiVisitor implements ApiVisitor {

    private static Logger log = LoggerFactory.getLogger(ModelCreatingApiVisitor.class);

    private TypescriptGeneratorContext context;

    public ModelCreatingApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        ObjectCreatingApiVisitor visitor = new ObjectCreatingApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser();
        traverser.traverse(type, visitor);
    }

    @Override
    public void visitNumberType(NumberTypeDeclaration type) {
        createTypeAlias(type, "number");
    }

    @Override
    public void visitStringType(StringTypeDeclaration type) {
        if (context.getApiModel().isEnum(type)) {
            EnumTypeApiVisitor visitor = new EnumTypeApiVisitor(context);
            ApiTraverser traverser = new ApiTraverser();
            traverser.traverse(type, visitor);
        }
        else {
            createTypeAlias(type, "string");
        }
    }

    /**
     * @param type
     */
    private void createTypeAlias(TypeDeclaration type, String targetType) {
        StringBuilder output = new StringBuilder();

        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, String> contextObject = ImmutableMap.of("name", type.name(), "tsType", targetType);
        engine.getMustache("typeAlias").render(output, contextObject);

        String moduleName = Names.buildLowerKebabCaseName(type.name());
        writeToFile(output.toString(), moduleName);
    }


    private void writeToFile(String content, String moduleName) {
        String tsFileName = moduleName + ".ts";
        File tsFile = new File(context.getConfig().getTargetDir(), tsFileName);
        log.debug("generating {}\n{}", tsFileName, content);
        try {
            Files.write(tsFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exc) {
            throw new GeneratorException(exc);
        }
    }
}
