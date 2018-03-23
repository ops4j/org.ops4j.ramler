/*
 * Copyright 2018 OPS4J Contributors
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

import static org.ops4j.ramler.generator.Constants.CONSTANTS;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_BOOLEAN;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_DATE;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_DATETIME;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_DATETIME_ONLY;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_INTEGER;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_LONG;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_STRING;
import static org.ops4j.ramler.generator.Constants.UNDEFINED_TIME_ONLY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.ops4j.ramler.exc.Exceptions;
import org.ops4j.ramler.exc.GeneratorException;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

/**
 * API visitor adding the members to each POJO class created by {@code PojoCreatingApiVisitor}.
 *
 * @author Harald Wellmann
 *
 */
public class ConstantsGenerator {

    private GeneratorContext context;

    private JCodeModel codeModel;

    private JPackage pkg;

    private JDefinedClass klass;

    public ConstantsGenerator(GeneratorContext context) {
        this.context = context;
        this.codeModel = context.getCodeModel();
        this.pkg = context.getModelPackage();
    }

    public JDefinedClass createConstantsClass() {
        try {
            JDefinedClass klass = pkg._class(CONSTANTS);
            context.annotateAsGenerated(klass);
            return klass;
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    public void generateConstantsClass() {
        klass = pkg._getClass(CONSTANTS);

        generateUndefinedDatetime();
        generateUndefinedDatetimeOnly();
        generateUndefinedDate();
        generateUndefinedTimeOnly();

        generateUndefinedBoolean();
        generateUndefinedNumberType(Integer.class);
        generateUndefinedNumberType(Long.class);
        generateUndefinedNumberType(Float.class);
        generateUndefinedNumberType(Double.class);
        generateUndefinedString();

        generatePrivateConstructor();
    }


    private void generateUndefinedDatetime() {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            ZonedDateTime.class, UNDEFINED_DATETIME);
        JInvocation defaultZone = codeModel.ref(ZoneId.class).staticInvoke("systemDefault");
        valueField.init(codeModel.ref(ZonedDateTime.class).staticInvoke("of").arg(Constants.ZERO).arg(Constants.ONE)
            .arg(Constants.ONE).arg(Constants.ZERO).arg(Constants.ZERO).arg(Constants.ZERO).arg(Constants.ZERO).arg(defaultZone));
    }

    private void generateUndefinedDatetimeOnly() {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            LocalDateTime.class, UNDEFINED_DATETIME_ONLY);
        valueField.init(codeModel.ref(LocalDateTime.class).staticInvoke("of").arg(Constants.ZERO).arg(Constants.ONE)
            .arg(Constants.ONE).arg(Constants.ZERO).arg(Constants.ZERO));
    }

    private void generateUndefinedDate() {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            LocalDate.class, UNDEFINED_DATE);
        valueField.init(codeModel.ref(LocalDate.class).staticInvoke("of").arg(Constants.ZERO).arg(Constants.ONE)
            .arg(Constants.ONE));
    }

    private void generateUndefinedTimeOnly() {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            LocalTime.class, UNDEFINED_TIME_ONLY);
        valueField.init(codeModel.ref(LocalTime.class).staticRef("MAX"));
    }

    private void generateUndefinedBoolean() {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            Boolean.class, UNDEFINED_BOOLEAN);
        valueField.init(JExpr._new(codeModel.ref(Boolean.class)).arg(JExpr.FALSE));
    }

    private void generateUndefinedNumberType(Class<? extends Number> numberClass) {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            numberClass, "UNDEFINED_" + numberClass.getSimpleName().toUpperCase());
        valueField.init(codeModel.ref(numberClass).staticRef("MIN_VALUE"));
    }

    private void generateUndefinedString() {
        JFieldVar valueField = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            String.class, UNDEFINED_STRING);
        valueField.init(JExpr.lit("__UNDEFINED__"));
    }

    private void generatePrivateConstructor() {
        klass.constructor(JMod.PRIVATE);
    }


    public JExpression getUndefinedValue(TypeDeclaration decl) {
        JDefinedClass klass = pkg._getClass(CONSTANTS);
        JExpression expr = null;
        if (decl instanceof StringTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_STRING);
        }
        else if (decl instanceof IntegerTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_INTEGER);
        }
        else if (decl instanceof NumberTypeDeclaration) {
            if (context.getJavaType(decl).equals(codeModel.ref(Long.class))) {
                expr = klass.staticRef(UNDEFINED_LONG);
            }
        }
        else if (decl instanceof BooleanTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_BOOLEAN);
        }
        else if (decl instanceof DateTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_DATE);
        }
        else if (decl instanceof DateTimeOnlyTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_DATETIME_ONLY);
        }
        else if (decl instanceof TimeOnlyTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_TIME_ONLY);
        }
        else if (decl instanceof DateTimeTypeDeclaration) {
            expr = klass.staticRef(UNDEFINED_DATETIME);
        }
        if (expr == null) {
            throw new GeneratorException("no undefined value for " + decl);
        }
        return expr;
    }
}

