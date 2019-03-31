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

import static org.ops4j.ramler.generator.Constants.TYPE_ARGS;

import java.util.Map;
import java.util.TreeMap;

import org.ops4j.ramler.common.model.Annotations;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.ops4j.ramler.common.model.Metatype;
import org.ops4j.ramler.generator.Names;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 * Creates the import statements of a TypeScript module corresponding to a RAML object type.
 *
 * @author Harald Wellmann
 *
 */
public class ObjectImportApiVisitor implements ApiVisitor {

    private TypeScriptGeneratorContext context;
    private Map<String, String> typeToModuleMap = new TreeMap<>();

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ObjectImportApiVisitor(TypeScriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        type.parentTypes().stream().map(TypeDeclaration::name).forEach(this::addTypeToImports);
        Annotations.getStringAnnotations(type, TYPE_ARGS).forEach(this::addTypeToImports);
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        String tsPropType = context.getTypeScriptPropertyType(property);
        if (typeToModuleMap.containsKey(tsPropType) || Annotations.findTypeVar(property) != null) {
            return;
        }

        addTypeToImports(tsPropType);
    }

    private void addTypeToImports(String typeName) {
        String tsType = typeName;
        while (tsType.endsWith("[]")) {
            tsType = tsType.substring(0, tsType.length() - 2);
        }
        if (!Metatype.isBuiltIn(tsType)) {
            String tsFile = Names.buildLowerKebabCaseName(tsType);
            typeToModuleMap.put(tsType, tsFile);
        }
    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration objectType) {
        typeToModuleMap.forEach(this::generateImport);
    }

    private void generateImport(String type, String module) {
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, String> contextObject = ImmutableMap.of("tsType", type, "tsFile", module);
        engine.getMustache("import").render(context.getOutput(), contextObject);
    }

}
