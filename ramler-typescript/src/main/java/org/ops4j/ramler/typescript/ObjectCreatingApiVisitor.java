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

import org.ops4j.ramler.exc.GeneratorException;
import org.ops4j.ramler.generator.ApiTraverser;
import org.ops4j.ramler.generator.ApiVisitor;
import org.ops4j.ramler.generator.Names;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Harald Wellmann
 *
 */
public class ObjectCreatingApiVisitor implements ApiVisitor {

    private static Logger log = LoggerFactory.getLogger(ObjectCreatingApiVisitor.class);

    private TypescriptGeneratorContext context;
    private StringBuilder output;

    public ObjectCreatingApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
        this.output = new StringBuilder();
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }

        log.debug("generating {}\n{}", type.name(), output);
        context.setOutput(output);

        ObjectImportApiVisitor importVisitor = new ObjectImportApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser();
        traverser.traverse(type, importVisitor);
        output.append("\n");

        ObjectBodyApiVisitor bodyVisitor = new ObjectBodyApiVisitor(context);
        traverser.traverse(type, bodyVisitor);


    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        String tsFileName = Names.buildLowerKebabCaseName(type.name()) + ".ts";
        log.debug("{}", output);
        File tsFile = new File(context.getConfig().getTargetDir(), tsFileName);
        try {
            Files.write(tsFile.toPath(), output.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException exc) {
            throw new GeneratorException(exc);
        }

    }
}
