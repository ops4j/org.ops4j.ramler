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
package org.ops4j.ramler.generator;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.ops4j.ramler.exc.Exceptions;
import org.ops4j.ramler.exc.ParserException;
import org.ops4j.ramler.model.ApiModel;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.codemodel.writer.FileCodeWriter;

/**
 * Generates JAX-RS resource interfaces and POJO model classes for all types defined in a given RAML
 * specification.
 * <p>
 *
 * @author Harald Wellmann
 *
 */
public class Generator {

    private static Logger log = LoggerFactory.getLogger(Generator.class);

    private Configuration config;

    private GeneratorContext context;

    /**
     * Creates a JAX-RS code generator with the given configuration.
     *
     * @param config
     *            code generator configuration
     */
    public Generator(Configuration config) {
        this.config = config;
        this.context = new GeneratorContext(config);
    }

    GeneratorContext getContext() {
        return context;
    }

    /**
     * Generates code for the given configuration.
     */
    public void generate() {
        Api api = buildApi();
        if (api == null) {
            return;
        }

        buildCodeModel();
        writeCodeModel();
    }

    private Api buildApi() {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(config.getSourceFile());
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult result : ramlModelResult.getValidationResults()) {
                log.error("{}: {}", result.getPath(), result.getMessage());
            }
            throw new ParserException("RAML syntax errors, see previous messages");
        }

        Api api = ramlModelResult.getApiV10();
        context.setApiModel(new ApiModel(api));
        return api;
    }

    private void buildCodeModel() {
        PojoCreatingApiVisitor pojoCreator = new PojoCreatingApiVisitor(context);
        PojoGeneratingApiVisitor pojoVisitor = new PojoGeneratingApiVisitor(context);
        ResourceGeneratingApiVisitor resourceVisitor = new ResourceGeneratingApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser();
        Stream.of(pojoCreator, pojoVisitor, resourceVisitor)
            .forEach(v -> traverser.traverse(context.getApiModel().getApi(), v));
    }

    private void writeCodeModel() {
        try {
            File dir = config.getTargetDir();
            FileHelper.createDirectoryIfNeeded(dir);
            context.getCodeModel().build(new FileCodeWriter(dir));
        }
        catch (IOException exc) {
            throw Exceptions.unchecked(exc);
        }
    }
}
