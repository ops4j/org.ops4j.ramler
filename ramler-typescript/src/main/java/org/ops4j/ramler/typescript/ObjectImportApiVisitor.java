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

import static org.ops4j.ramler.generator.Constants.OBJECT;

import java.util.Map;
import java.util.TreeMap;

import org.ops4j.ramler.generator.ApiVisitor;
import org.ops4j.ramler.generator.Names;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 * @author Harald Wellmann
 *
 */
public class ObjectImportApiVisitor implements ApiVisitor {

    private TypescriptGeneratorContext context;
    private Map<String, String> typeToModuleMap = new TreeMap<>();

    public ObjectImportApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        for (TypeDeclaration parentType : type.parentTypes()) {
            String typeName = parentType.name();
            if (!typeName.equals(OBJECT)) {
                String tsFile = Names.buildLowerKebabCaseName(typeName);
                typeToModuleMap.put(typeName, tsFile);
            }
        }
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        String tsPropType = context.getTypescriptType(property);
        if (typeToModuleMap.containsKey(tsPropType)) {
            return;
        }

        String tsType = tsPropType;
        while (tsType.endsWith("[]")) {
            tsType = tsPropType.substring(0, tsType.length() - 2);
        }
        if (Character.isUpperCase(tsPropType.charAt(0))) {
            String tsFile = Names.buildLowerKebabCaseName(tsType);
            typeToModuleMap.put(tsType, tsFile);
        }
    }


    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration objectType) {
        typeToModuleMap.forEach((type, module) -> generateImport(type, module));
    }


    private void generateImport(String type, String module) {
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, String> contextObject = ImmutableMap.of("tsType", type, "tsFile", module);
        engine.getMustache("import").render(context.getOutput(), contextObject);
    }

}
