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

import java.util.Collections;
import java.util.Map;

import org.ops4j.ramler.generator.ApiVisitor;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 * @author Harald Wellmann
 *
 */
public class ObjectBodyApiVisitor implements ApiVisitor {

    private TypescriptGeneratorContext context;

    public ObjectBodyApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, String> contextObject = ImmutableMap.of("name", type.name());
        engine.getMustache("objectStart").render(context.getOutput(), contextObject);
    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        engine.getMustache("objectEnd").render(context.getOutput(), Collections.emptyMap());
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        String tsPropType = context.getTypescriptType(property);
        String name = property.name();


        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, String> contextObject = ImmutableMap.of("name", name, "tsPropType", tsPropType);
        engine.getMustache("property").render(context.getOutput(), contextObject);
    }

}
