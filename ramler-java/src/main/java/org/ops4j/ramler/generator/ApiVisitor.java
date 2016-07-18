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
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Visits nodes of a RAML 1.0 API model. Implementations of this interface can be used to
 * create code or documentation generators.
 * <p>
 * All methods of this interface have an empty default implementation. Thus, implementation
 * classes only need to implement the methods they actually need.
 *
 * @author Harald Wellmann
 *
 */
public interface ApiVisitor {

    default void visitAnyType(AnyTypeDeclaration type) {
        // empty
    }

    default void visitApiStart(Api api) {
        // empty
    }

    default void visitApiEnd(Api api) {
        // empty
    }

    default void visitArrayType(ArrayTypeDeclaration type) {
        // empty
    }

    default void visitBooleanType(BooleanTypeDeclaration type) {
        // empty
    }

    default void visitNumberType(NumberTypeDeclaration type) {
        // empty
    }

    default void visitObjectType(ObjectTypeDeclaration type) {
        // empty
    }

    default void visitStringType(StringTypeDeclaration type) {
        // empty
    }

    default void visitObjectTypeStart(ObjectTypeDeclaration type) {
        // empty
    }

    default void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        // empty
    }

    default void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        // empty
    }

    default void visitResourceStart(Resource resource) {
        // empty
    }

    default void visitResourceEnd(Resource resource) {
        // empty
    }

    default void visitMethodStart(Method method) {
        // empty
    }

    default void visitMethodEnd(Method method) {
        // empty
    }

    default void visitHeader(TypeDeclaration header) {
        // empty
    }

    default void visitQueryParameter(TypeDeclaration param) {
        // empty
    }

    /**
     * @param library
     */
    default void visitLibraryStart(Library library) {
        
    }

    /**
     * @param library
     */
    default void visitLibraryEnd(Library library) {
        
    }
}
