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

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Visits nodes of a RAML 1.0 API model. Implementations of this interface can be used to create
 * code or documentation generators.
 * <p>
 * All methods of this interface have an empty default implementation. Thus, implementation classes
 * only need to implement the methods they actually need.
 *
 * @author Harald Wellmann
 *
 */
public interface ApiVisitor {

    /**
     * Called for each {@code any} type declaration.
     *
     * @param type
     *            any type
     */
    default void visitAnyType(AnyTypeDeclaration type) {
        // empty
    }

    /**
     * Called before traversing the first node of this API.
     *
     * @param api
     *            RAML API
     */
    default void visitApiStart(Api api) {
        // empty
    }

    /**
     * Called after traversing the last node of this API.
     *
     * @param api
     *            RAML API
     */
    default void visitApiEnd(Api api) {
        // empty
    }

    /**
     * Called for each array type declaration.
     *
     * @param type
     *            array type
     */
    default void visitArrayType(ArrayTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each Boolean type declaration.
     *
     * @param type
     *            boolean type
     */
    default void visitBooleanType(BooleanTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each number type declaration.
     *
     * @param type
     *            boolean type
     */
    default void visitNumberType(NumberTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each object type declaration.
     *
     * @param type
     *            boolean type
     */
    default void visitObjectType(ObjectTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each string type declaration.
     *
     * @param type
     *            boolean type
     */
    default void visitStringType(StringTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each object type declaration before visiting the first property.
     *
     * @param type
     *            object type
     */
    default void visitObjectTypeStart(ObjectTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each object type declaration after visiting the last property.
     *
     * @param type
     *            object type
     */
    default void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each property of an object type.
     *
     * @param type
     *            object type
     * @param property
     *            property of the given object type
     */
    default void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        // empty
    }

    /**
     * Called for each union type declaration.
     *
     * @param type
     *            union type
     */
    default void visitUnionType(UnionTypeDeclaration type) {
        // empty
    }

    /**
     * Called for each resource before visiting the first method.
     *
     * @param resource
     *            RAML resource
     */
    default void visitResourceStart(Resource resource) {
        // empty
    }

    /**
     * Called for each resource after visiting the last method.
     *
     * @param resource
     *            RAML resource
     */
    default void visitResourceEnd(Resource resource) {
        // empty
    }

    /**
     * Called for each method before visiting its headers and parameters.
     *
     * @param method
     *            RAML method
     */
    default void visitMethodStart(Method method) {
        // empty
    }

    /**
     * Called for each method after visiting its headers and parameters.
     *
     * @param method
     *            RAML method
     */
    default void visitMethodEnd(Method method) {
        // empty
    }

    /**
     * Called for each request header.
     *
     * @param header
     *            request header
     */
    default void visitHeader(TypeDeclaration header) {
        // empty
    }

    /**
     * Called for each query parameter.
     *
     * @param param
     *            query parameter
     */
    default void visitQueryParameter(TypeDeclaration param) {
        // empty
    }

    /**
     * Called for each library before visiting its declarations.
     *
     * @param library
     *            RAML library
     */
    default void visitLibraryStart(Library library) {
        // empty
    }

    /**
     * Called for each library after visiting its declarations.
     *
     * @param library
     *            RAML library
     */
    default void visitLibraryEnd(Library library) {
        // empty
    }
}
