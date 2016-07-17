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
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

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

    }

    default void visitResourceEnd(Resource resource) {

    }

    default void visitMethodStart(Method method) {

    }

    default void visitMethodEnd(Method method) {

    }

    default void visitHeader(TypeDeclaration header) {

    }

    default void visitQueryParameter(TypeDeclaration param) {

    }
}
