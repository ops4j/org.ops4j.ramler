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

import org.ops4j.ramler.generator.ApiModelBuilder;
import org.ops4j.ramler.generator.ApiTraverser;
import org.ops4j.ramler.model.ApiModel;
import org.ops4j.ramler.typescript.trimou.TypescriptTemplateEngine;

/**
 * @author Harald Wellmann
 *
 */
public class TypescriptGenerator {

    private TypescriptConfiguration config;
    private TypescriptGeneratorContext context;

    public TypescriptGenerator(TypescriptConfiguration config) {
        this.config = config;
        this.context = new TypescriptGeneratorContext(config);
    }

    public void generate() {
        ApiModel apiModel = new ApiModelBuilder().buildApiModel(config.getSourceFile());
        context.setApiModel(apiModel);
        TypescriptTemplateEngine engine = new TypescriptTemplateEngine();
        engine.setTemplateDir(config.getTemplateDir());
        context.setTemplateEngine(engine);
        config.getTargetDir().mkdirs();

        ModelCreatingApiVisitor modelVisitor = new ModelCreatingApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser();
        traverser.traverse(context.getApiModel().getApi(), modelVisitor);
    }

}
