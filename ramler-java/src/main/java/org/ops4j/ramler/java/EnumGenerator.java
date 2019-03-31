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
package org.ops4j.ramler.java;

import static org.ops4j.ramler.java.JavaConstants.VALUE;

import org.ops4j.ramler.common.exc.Exceptions;
import org.ops4j.ramler.common.model.EnumValue;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

/**
 * API visitor adding the members to each POJO class created by {@code PojoCreatingApiVisitor}.
 *
 * @author Harald Wellmann
 *
 */
public class EnumGenerator {

    private JavaGeneratorContext context;

    private JCodeModel codeModel;

    private JPackage pkg;

    private JDefinedClass klass;

    /**
     * Creates a visitor for the given generator context.
     *
     * @param context
     *            generator context
     */
    public EnumGenerator(JavaGeneratorContext context) {
        this.context = context;
        this.codeModel = context.getCodeModel();
        this.pkg = context.getModelPackage();
    }

    /**
     * Selects the enum class for the given type.
     * @param type enumeration type declaration
     */
    public void generateEnumClassStart(StringTypeDeclaration type) {
        klass = pkg._getClass(type.name());
    }

    /**
     * Fills the current enum class with values and methods.
     */
    public void generateEnumClassEnd() {
        JFieldVar valueField = klass.field(JMod.PRIVATE | JMod.FINAL, String.class, VALUE);

        generateEnumConstructor(klass, valueField);
        generateEnumValueMethod(klass, valueField);
        generateEnumFromStringMethod(klass, valueField);
        generateEnumToStringMethod(klass, valueField);
    }

    /**
     * Creates an empty enum class for the given type.
     * @param type enumeration type declaration
     * @return enum class
     */
    public JDefinedClass createEnumClass(StringTypeDeclaration type) {
        try {
            JDefinedClass enumClass = pkg._enum(type.name());
            context.addType(type.name(), enumClass);
            context.annotateAsGenerated(enumClass);
            return enumClass;
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    /**
     * Generates an enum constant for the given value.
     * @param enumValue enum value
     */
    public void generateEnumConstant(EnumValue enumValue) {
        JEnumConstant constant = klass.enumConstant(Names.buildConstantName(enumValue.getName()))
            .arg(JExpr.lit(enumValue.getName()));

        if (context.getConfig().isJacksonPropertyName()) {
            constant.annotate(JsonProperty.class).param(VALUE, enumValue.getName());
        }

        if (enumValue.getDescription() != null) {
            constant.javadoc().add(enumValue.getDescription());
        }
    }

    private void generateEnumConstructor(JDefinedClass klass, JFieldVar valueField) {
        JMethod constructor = klass.constructor(JMod.PRIVATE);
        JVar p1 = constructor.param(String.class, VALUE);
        constructor.body().assign(JExpr._this().ref(valueField), p1);
    }

    private void generateEnumValueMethod(JDefinedClass klass, JFieldVar valueField) {
        JMethod getter = klass.method(JMod.PUBLIC, codeModel._ref(String.class), VALUE);
        getter.body()._return(valueField);
    }

    private void generateEnumFromStringMethod(JDefinedClass klass, JFieldVar valueField) {
        JMethod converter = klass.method(JMod.PUBLIC | JMod.STATIC, klass, "fromString");
        JVar param = converter.param(String.class, VALUE);

        JBlock body = converter.body();
        JForEach forEach = body.forEach(klass, "v", klass.staticInvoke("values"));
        JBlock loopBlock = forEach.body();
        loopBlock._if(forEach.var().ref(valueField).invoke("equals").arg(param))._then()
            ._return(forEach.var());
        body._throw(JExpr._new(codeModel._ref(IllegalArgumentException.class)).arg(param));
    }

    private void generateEnumToStringMethod(JDefinedClass klass, JFieldVar valueField) {
        JMethod converter = klass.method(JMod.PUBLIC, String.class, "toString");

        JBlock body = converter.body();
        body._return(valueField);
    }
}
