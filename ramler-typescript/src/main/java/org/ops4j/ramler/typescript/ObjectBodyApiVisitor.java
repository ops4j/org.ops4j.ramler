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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.ops4j.ramler.generator.Constants.TYPE_ARGS;
import static org.ops4j.ramler.generator.Constants.TYPE_VARS;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ops4j.ramler.generator.ApiVisitor;
import org.ops4j.ramler.generator.Constants;
import org.ops4j.ramler.generator.Names;
import org.ops4j.ramler.model.Annotations;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 * Creates the exported interface declaration of a Typescript module corresponding to a RAML object
 * type.
 *
 * @author Harald Wellmann
 *
 */
public class ObjectBodyApiVisitor implements ApiVisitor {

    private TypescriptGeneratorContext context;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ObjectBodyApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        List<String> baseClasses = type.parentTypes().stream()
            .filter(t -> !t.name().equals(Constants.OBJECT)).map(t -> this.typeWithArgs(type, t))
            .collect(toList());

        List<String> typeVars = Annotations.getStringAnnotations(type, TYPE_VARS);

        Map<String, Object> contextObject = ImmutableMap.of("name", type.name(), "baseClasses",
            baseClasses, "typeVars", typeVars);

        MustacheEngine engine = context.getTemplateEngine().getEngine();
        engine.getMustache("objectStart").render(context.getOutput(), contextObject);
    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        engine.getMustache("objectEnd").render(context.getOutput(), Collections.emptyMap());
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (property instanceof ArrayTypeDeclaration) {
            generateArrayProperty((ArrayTypeDeclaration) property);
        }
        else {
            generateProperty(property);
        }
    }

    /**
     * @param type
     * @param property
     */
    private void generateArrayProperty(ArrayTypeDeclaration property) {
        String fieldName = Names.buildVariableName(property);
        String itemTypeName = context.getApiModel().getItemType(property);
        String typeVar = Annotations.findTypeVar(property);
        String tsItemType;
        if (typeVar != null) {
            tsItemType = typeVar;
        }
        else {
            tsItemType = itemTypeName;
        }
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, Object> contextObject = ImmutableMap.of("name", fieldName, "tsPropType",
            tsItemType + "[]", "optional", !property.required());
        engine.getMustache("property").render(context.getOutput(), contextObject);
    }

    /**
     * @param type
     * @param property
     */
    private void generateProperty(TypeDeclaration property) {
        String fieldName = Names.buildVariableName(property);
        String tsPropType;
        String typeVar = Annotations.findTypeVar(property);
        if (typeVar != null) {
            tsPropType = typeVar;
        }
        else {
            tsPropType = propertyTypeWithArgs(property);
        }
        MustacheEngine engine = context.getTemplateEngine().getEngine();
        Map<String, Object> contextObject = ImmutableMap.of("name", fieldName, "tsPropType",
            tsPropType, "optional", !property.required());
        engine.getMustache("property").render(context.getOutput(), contextObject);
    }

    /**
     * @param property
     * @return
     */
    private String propertyTypeWithArgs(TypeDeclaration property) {
        String tsPropType;
        tsPropType = context.getTypescriptPropertyType(property);
        List<String> typeArgs = Annotations.getStringAnnotations(property, TYPE_ARGS);
        if (!typeArgs.isEmpty()) {
            StringBuilder builder = new StringBuilder(tsPropType);
            builder.append("<");
            builder.append(typeArgs.stream().collect(joining(", ")));
            builder.append(">");
            tsPropType = builder.toString();
        }
        return tsPropType;
    }

    private String typeWithArgs(TypeDeclaration annotated, TypeDeclaration type) {
        String tsPropType;
        tsPropType = context.getTypescriptType(type);
        List<String> typeArgs = Annotations.getStringAnnotations(annotated, TYPE_ARGS);
        if (!typeArgs.isEmpty()) {
            StringBuilder builder = new StringBuilder(tsPropType);
            builder.append("<");
            builder.append(typeArgs.stream().collect(joining(", ")));
            builder.append(">");
            tsPropType = builder.toString();
        }
        return tsPropType;
    }

}
