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
package org.ops4j.ramler.java;

import org.ops4j.ramler.common.exc.Exceptions;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * API visitor creating an empty POJO class for each defined RAML type.
 * <p>
 * The classes are defined without any details in a first pass to allow for references in the
 * following generator pass.
 *
 * @author Harald Wellmann
 *
 */
public class PojoCreatingApiVisitor implements ApiVisitor {

    private JavaGeneratorContext context;

    private JPackage pkg;

    private EnumGenerator enumGenerator;

    /**
     * Creates a visitor for the given generator context.
     *
     * @param context
     *            generator context
     */
    public PojoCreatingApiVisitor(JavaGeneratorContext context) {
        this.context = context;
        this.pkg = context.getModelPackage();
        this.enumGenerator = new EnumGenerator(context);
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel()
            .isInternal(type)) {
            return;
        }
        try {
            JDefinedClass klass = pkg._class(type.name());
            context.addType(type.type(), klass);
            context.annotateAsGenerated(klass);
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    @Override
    public void visitUnionType(UnionTypeDeclaration type) {
        String declaredName = context.getApiModel()
            .getDeclaredName(type);
        if (declaredName == null) {
            return;
        }
        try {
            JDefinedClass klass = pkg._class(type.name());
            context.addType(type.type(), klass);
            context.annotateAsGenerated(klass);
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    @Override
    public void visitEnumTypeStart(StringTypeDeclaration type) {
        enumGenerator.createEnumClass(type);
    }
}
