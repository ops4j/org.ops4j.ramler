/*
 * Copyright 2019 OPS4J Contributors
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

import static org.ops4j.ramler.java.JavaConstants.TYPE_ARGS;

import java.util.Map;
import java.util.TreeMap;

import org.ops4j.ramler.common.model.Annotations;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.ops4j.ramler.common.model.Metatype;
import org.ops4j.ramler.java.Names;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.trimou.util.ImmutableMap;

/**
 * Creates the import statements of a TypeScript module corresponding to a RAML resource.
 *
 * @author Harald Wellmann
 *
 */
public class ResourceImportApiVisitor implements ApiVisitor {

    private TypeScriptGeneratorContext context;
    private Map<String, String> typeToModuleMap = new TreeMap<>();
    private Resource outerResource;
    private Resource innerResource;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ResourceImportApiVisitor(TypeScriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitResourceStart(Resource resource) {
        if (outerResource == null) {
            outerResource = resource;
        }
        else {
            innerResource = resource;
        }
        resource.uriParameters()
            .stream()
            .map(TypeDeclaration::type)
            .forEach(this::addTypeToImports);
    }

    @Override
    public void visitMethodStart(Method method) {
        method.queryParameters()
            .stream()
            .map(TypeDeclaration::type)
            .forEach(this::addTypeToImports);

        for (TypeDeclaration body : method.body()) {
            addTypeToImports(body.type());
            Annotations.getStringAnnotations(body, TYPE_ARGS)
                .forEach(this::addTypeToImports);
        }
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
    public void visitResourceEnd(Resource resource) {
        if (innerResource != null) {
            innerResource = null;
        }
        else {
            outerResource = null;
            typeToModuleMap.forEach(this::generateImport);
        }
    }

    private void generateImport(String type, String module) {
        Map<String, String> contextObject = ImmutableMap.of("tsType", type, "tsFile", module);
        context.getMustache("import")
            .render(context.getOutput(), contextObject);
    }
}
