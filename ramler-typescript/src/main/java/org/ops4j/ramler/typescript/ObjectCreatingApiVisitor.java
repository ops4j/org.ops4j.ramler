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

import org.ops4j.ramler.model.ApiTraverser;
import org.ops4j.ramler.model.ApiVisitor;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

/**
 * Generates a TypeScript interface for a given RAML object type.
 *
 * @author Harald Wellmann
 *
 */
public class ObjectCreatingApiVisitor implements ApiVisitor {

    private TypeScriptGeneratorContext context;
    private StringBuilder output;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ObjectCreatingApiVisitor(TypeScriptGeneratorContext context) {
        this.context = context;
        this.output = context.startOutput();
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }

        ObjectImportApiVisitor importVisitor = new ObjectImportApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser();
        traverser.traverse(type, importVisitor);
        output.append("\n");

        ObjectBodyApiVisitor bodyVisitor = new ObjectBodyApiVisitor(context);
        traverser.traverse(type, bodyVisitor);

    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }
        context.writeToFile(output.toString(), type.name());
    }
}
