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

import java.io.IOException;

import org.ops4j.ramler.common.helper.FileHelper;
import org.ops4j.ramler.common.model.ApiModel;
import org.ops4j.ramler.common.model.ApiModelBuilder;
import org.ops4j.ramler.common.model.ApiTraverser;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.ops4j.ramler.typescript.trimou.TypeScriptTemplateEngine;

/**
 * Creates a TypeScript model for a given RAML model. Entry point for code generation, the actual
 * work is carried out by a number of API visitors.
 * <p>
 * Code generation is based on Trimou templates.
 *
 * @author Harald Wellmann
 *
 */
public class TypeScriptGenerator {

    private TypeScriptConfiguration config;
    private TypeScriptGeneratorContext context;

    /**
     * Creates a generator with the given configuration.
     *
     * @param config
     *            TypeScript generator configuration
     */
    public TypeScriptGenerator(TypeScriptConfiguration config) {
        this.config = config;
        this.context = new TypeScriptGeneratorContext(config);
    }

    /**
     * Generates TypeScript code.
     *
     * @throws IOException
     *             if the target directory cannot be created
     */
    public void generate() throws IOException {
        ApiModel apiModel = new ApiModelBuilder().buildApiModel(config.getSourceFile());
        context.setApiModel(apiModel);
        TypeScriptTemplateEngine engine = new TypeScriptTemplateEngine();
        context.setTemplateEngine(engine);
        FileHelper.createDirectoryIfNeeded(config.getTargetDir());

        ApiVisitor modelVisitor = new ModelCreatingApiVisitor(context);
        ApiVisitor resourceVisitor = new ResourceCreatingApiVisitor(context);
        ApiVisitor serviceVisitor = new ServiceCreatingApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser(apiModel);
        traverser.traverse(apiModel.getApi(), modelVisitor);
        traverser.traverse(apiModel.getApi(), resourceVisitor);
        traverser.traverse(apiModel.getApi(), serviceVisitor);
    }
}
