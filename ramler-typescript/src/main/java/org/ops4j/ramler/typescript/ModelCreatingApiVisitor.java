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

import org.ops4j.ramler.exc.GeneratorException;
import org.ops4j.ramler.generator.ApiVisitor;
import org.ops4j.ramler.generator.Names;
import org.ops4j.ramler.typescript.trimou.TypescriptTemplateEngine;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

import com.google.common.io.Files;

/**
 * @author Harald Wellmann
 *
 */
public class ModelCreatingApiVisitor implements ApiVisitor {

    private TypescriptGeneratorContext context;

    public ModelCreatingApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        TypescriptTemplateEngine engine = context.getTemplateEngine();
        String content = engine.renderType(context, type);
        String tsFileName = Names.buildLowerKebabCaseName(type.name()) + ".ts";
        File tsFile = new File(context.getConfig().getTargetDir(), tsFileName);
        try {
            Files.write(content, tsFile, StandardCharsets.UTF_8);
        } catch (IOException exc) {
            throw new GeneratorException(exc);
        }
    }

}
