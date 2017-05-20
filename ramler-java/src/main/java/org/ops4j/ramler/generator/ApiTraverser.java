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

import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Traverses a RAML 1.0 API model. Takes an {@link ApiVisitor}, visiting every relevant node.
 * <p>
 * The traverser ignores any nodes that are not relevant for code generation.
 * <p>
 * Since the RAML API does not directly support the visitor pattern, e.g. with an
 * {@code accept(ApiVisitor)} method for all model classes, this class shall be used with a concrete
 * visitor implementation.
 * <p>
 * Example:
 *
 * <pre>
 * MyVisitor visitor = new MyVisitor();
 * ApiTraverser traverser = new ApiTraverser();
 * traverser.traverse(api, visitor);
 * Object result = visitor.getResult();
 * </pre>
 *
 * @author Harald Wellmann
 *
 */
public class ApiTraverser {

    /**
     * Lets the given visitor traverse the API model tree.
     *
     * @param api
     *            RAML 1.0 API model
     * @param visitor
     *            concrete visitor
     */
    public void traverse(Api api, ApiVisitor visitor) {
        visitor.visitApiStart(api);
        api.uses().forEach(lib -> traverse(lib, visitor));
        orderTypes(api).forEach((name, type) -> traverse(type, visitor));
        api.resources().forEach(resource -> traverse(resource, visitor));
        visitor.visitApiEnd(api);
    }

    /**
     * @param lib
     * @param visitor
     * @return
     */
    private void traverse(Library library, ApiVisitor visitor) {
        visitor.visitLibraryStart(library);
        library.types().forEach(type -> traverse(type, visitor));
        visitor.visitLibraryEnd(library);
    }

    /**
     * Orders defined types so that base types precede derived types. This avoids forward references
     * for the code generator.
     *
     * @param api
     *            API model
     * @return ordered map of type names to type definitions
     */
    private Map<String, TypeDeclaration> orderTypes(Api api) {
        Map<String, TypeDeclaration> orderedTypes = new LinkedHashMap<>();
        api.types().forEach(type -> storeHierarchy(orderedTypes, api, type));
        return orderedTypes;
    }

    private void storeHierarchy(Map<String, TypeDeclaration> orderedTypes, Api api,
        TypeDeclaration type) {
        for (TypeDeclaration baseType : type.parentTypes()) {
            String baseTypeName = baseType.name();
            if (!baseTypeName.equals(Constants.OBJECT)) {
                if (!orderedTypes.containsKey(baseTypeName)) {
                    storeHierarchy(orderedTypes, api, baseType);
                }
            }
        }
        orderedTypes.put(type.name(), type);
    }

    private void traverse(TypeDeclaration type, ApiVisitor visitor) {
        if (type instanceof AnyTypeDeclaration) {
            visitor.visitAnyType((AnyTypeDeclaration) type);
        }
        else if (type instanceof ArrayTypeDeclaration) {
            visitor.visitArrayType((ArrayTypeDeclaration) type);
        }
        else if (type instanceof BooleanTypeDeclaration) {
            visitor.visitBooleanType((BooleanTypeDeclaration) type);
        }
        else if (type instanceof NumberTypeDeclaration) {
            visitor.visitNumberType((NumberTypeDeclaration) type);
        }
        else if (type instanceof ObjectTypeDeclaration) {
            traverse((ObjectTypeDeclaration) type, visitor);
        }
        else if (type instanceof StringTypeDeclaration) {
            visitor.visitStringType((StringTypeDeclaration) type);
        }
    }

    private void traverse(ObjectTypeDeclaration type, ApiVisitor visitor) {
        visitor.visitObjectTypeStart(type);
        type.properties().stream().filter(p -> !isInherited(p, type))
            .forEach(property -> visitor.visitObjectTypeProperty(type, property));
        visitor.visitObjectTypeEnd(type);
    }

    private boolean isInherited(TypeDeclaration property, ObjectTypeDeclaration type) {
        for (TypeDeclaration parent : type.parentTypes()) {
            if (parent instanceof ObjectTypeDeclaration) {
                ObjectTypeDeclaration parentType = (ObjectTypeDeclaration) parent;
                if (hasProperty(parentType, property.name())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasProperty(ObjectTypeDeclaration parent, String name) {
        return parent.properties().stream().filter(p -> p.name().equals(name)).findFirst()
            .isPresent();
    }

    private void traverse(Resource resource, ApiVisitor visitor) {
        visitor.visitResourceStart(resource);
        resource.methods().forEach(method -> traverse(method, visitor));
        resource.resources().forEach(child -> traverse(child, visitor));
        visitor.visitResourceEnd(resource);
    }

    private void traverse(Method method, ApiVisitor visitor) {
        visitor.visitMethodStart(method);
        method.headers().forEach(header -> visitor.visitHeader(header));
        method.queryParameters().forEach(param -> visitor.visitQueryParameter(param));
        visitor.visitMethodEnd(method);
    }
}
