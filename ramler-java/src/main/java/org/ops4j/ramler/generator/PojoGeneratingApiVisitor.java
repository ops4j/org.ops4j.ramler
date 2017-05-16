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

import static org.ops4j.ramler.generator.Constants.DISCRIMINATOR;
import static org.ops4j.ramler.generator.Constants.OBJECT;
import static org.ops4j.ramler.generator.Constants.TYPE_ARGS;
import static org.ops4j.ramler.generator.Constants.TYPE_VARS;
import static org.ops4j.ramler.generator.Constants.VALUE;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.ops4j.ramler.model.Annotations;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * API visitor adding the members to each POJO class created by {@code PojoCreatingApiVisitor}.
 *
 * @author Harald Wellmann
 *
 */
public class PojoGeneratingApiVisitor implements ApiVisitor {

    private GeneratorContext context;

    private JCodeModel codeModel;

    private JPackage pkg;

    private EnumGenerator enumGenerator;

    /**
     * Creates a visitor for the given generator context.
     *
     * @param context
     *            generator context
     */
    public PojoGeneratingApiVisitor(GeneratorContext context) {
        this.context = context;
        this.codeModel = context.getCodeModel();
        this.pkg = context.getModelPackage();
        this.enumGenerator = new EnumGenerator(context);
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }
        JDefinedClass klass = pkg._getClass(type.name());
        addJavadoc(klass, type);
        addTypeParameters(klass, type);
        addBaseClass(klass, type);
        addDiscriminator(klass, type);
        addJsonTypeInfo(klass, type);
    }

    private void addJavadoc(JDefinedClass klass, ObjectTypeDeclaration type) {
        if (type.description() == null) {
            klass.javadoc().add("Generated from a RAML specification.");
        }
        else {
            klass.javadoc().add(type.description().value());
        }
    }

    private void addBaseClass(JDefinedClass klass, ObjectTypeDeclaration type) {
        if (!type.type().equals(OBJECT)) {
            JClass baseClass = pkg._getClass(type.type());

            List<String> typeArgs = Annotations.getStringAnnotations(type, TYPE_ARGS);
            if (!typeArgs.isEmpty()) {
                baseClass = baseClass
                    .narrow(typeArgs.stream().map(this::toJavaClass).toArray(JClass[]::new));
            }
            klass._extends(baseClass);
        }
    }

    private JClass toJavaClass(String typeName) {
        JType javaType = context.getJavaType(typeName);
        if (javaType instanceof JClass) {
            return (JClass) javaType;
        }
        throw new IllegalArgumentException("no class for " + typeName);
    }

    private void addDiscriminator(JDefinedClass klass, ObjectTypeDeclaration type) {
        if (type.discriminator() == null) {
            return;
        }
        String discriminatorValue = type.discriminatorValue();
        if (discriminatorValue == null) {
            discriminatorValue = type.name();
        }

        JFieldVar field = klass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
            codeModel._ref(String.class), DISCRIMINATOR);
        field.init(JExpr.lit(discriminatorValue));

        if (context.getConfig().isDiscriminatorMutable()) {
            klass.constructor(JMod.PUBLIC).body().invoke(getSetterName(type.discriminator()))
                .arg(field);
        }
        else {
            generateDiscriminatorGetter(type, klass, type.discriminator());
        }
    }

    private void addJsonTypeInfo(JDefinedClass klass, ObjectTypeDeclaration type) {
        if (!context.getConfig().isJacksonTypeInfo()) {
            return;
        }
        if (type.discriminator() == null) {
            return;
        }
        List<String> derivedTypes = context.getApiModel().findDerivedTypes(type.name());
        if (derivedTypes.isEmpty()) {
            return;
        }
        JAnnotationUse typeInfo = klass.annotate(JsonTypeInfo.class);
        typeInfo.param("use", Id.NAME);
        typeInfo.param("include", As.EXISTING_PROPERTY);
        typeInfo.param("property", type.discriminator());

        JAnnotationUse subTypes = klass.annotate(JsonSubTypes.class);
        JAnnotationArrayMember typeArray = subTypes.paramArray(VALUE);

        for (String derivedType : derivedTypes) {
            JDefinedClass subtype = pkg._getClass(derivedType);
            typeArray.annotate(Type.class).param(VALUE, subtype);
        }
    }

    private void addTypeParameters(JDefinedClass klass, ObjectTypeDeclaration type) {
        List<String> typeVars = Annotations.getStringAnnotations(type, TYPE_VARS);
        typeVars.forEach(klass::generify);
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }
        JDefinedClass klass = pkg._getClass(type.name());
        if (!context.getConfig().isDiscriminatorMutable()
            && property.name().equals(type.discriminator())) {
            return;
        }
        if (!isInherited(type, property)) {
            generateFieldAndAccessors(klass, property);
        }
    }

    private boolean isInherited(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (type.type().equals(OBJECT)) {
            return false;
        }
        JDefinedClass baseClass = pkg._getClass(type.type());
        return baseClass.fields().containsKey(property.name());
    }

    @Override
    public void visitStringType(StringTypeDeclaration type) {
        if (context.getApiModel().isEnum(type)) {
            enumGenerator.generateEnumClass(type);
        }
    }

    private void generateFieldAndAccessors(JDefinedClass klass, TypeDeclaration property) {
        if (property instanceof ObjectTypeDeclaration) {
            generateObjectFieldAndAccessors(klass, property);
        }
        else if (property instanceof ArrayTypeDeclaration) {
            generateListFieldAndAccessors(klass, (ArrayTypeDeclaration) property);
        }
        else if (property instanceof BooleanTypeDeclaration) {
            generateBooleanFieldAndAccessors(klass, (BooleanTypeDeclaration) property);
        }
        else if (property instanceof AnyTypeDeclaration) {
            generateAnyFieldAndAccessors(klass, property);
        }
        else {
            generateSimpleFieldAndAccessor(klass, property);
        }
    }

    private void generateSimpleFieldAndAccessor(JDefinedClass klass, TypeDeclaration property) {
        String fieldName = property.name();
        JType jtype = context.getJavaType(property);
        JFieldVar field = klass.field(JMod.PRIVATE, jtype, fieldName);

        generateGetter(property, klass, field, this::getGetterName);
        generateSetter(klass, jtype, fieldName);
    }

    private void generateListFieldAndAccessors(JDefinedClass klass, ArrayTypeDeclaration property) {
        String fieldName = property.name();
        String itemTypeName = context.getApiModel().getItemType(property);
        JType elementType = findTypeVar(klass, property).orElse(context.getJavaType(itemTypeName));
        JClass listType = codeModel.ref(List.class).narrow(elementType);
        JFieldVar field = klass.field(JMod.PRIVATE, listType, fieldName);

        JMethod getter = klass.method(JMod.PUBLIC, listType, getGetterName(fieldName));
        getter.body()._return(field);

        if (property.description() != null) {
            getter.javadoc().add(property.description().value());
        }
        generateSetter(klass, listType, fieldName);
    }

    private void generateObjectFieldAndAccessors(JDefinedClass klass, TypeDeclaration property) {
        String fieldName = property.name();

        JType jtype = findTypeVar(klass, property).orElse(context.getJavaType(property));
        List<String> args = Annotations.getStringAnnotations(property, TYPE_ARGS);
        if (!args.isEmpty()) {
            JClass jclass = (JClass) jtype;
            for (String arg : args) {
                JType typeArg = findTypeParam(klass, arg).get();
                jclass = jclass.narrow(typeArg);
            }
            jtype = jclass;
        }
        JFieldVar field = klass.field(JMod.PRIVATE, jtype, fieldName);

        generateGetter(property, klass, field, this::getGetterName);
        generateSetter(klass, jtype, fieldName);
    }

    private void generateAnyFieldAndAccessors(JDefinedClass klass, TypeDeclaration property) {
        generateObjectFieldAndAccessors(klass, property);
    }

    private Optional<JType> findTypeVar(JDefinedClass klass, TypeDeclaration property) {
        return property.annotations().stream().filter(a -> a.annotation().name().equals("typeVar"))
            .findFirst().flatMap(t -> findTypeParam(klass, t));
    }

    private Optional<JType> findTypeParam(JDefinedClass klass, AnnotationRef typeVar) {
        String paramName = typeVar.structuredValue().value().toString();
        return Stream.of(klass.typeParams()).map(JType.class::cast)
            .filter(t -> t.name().equals(paramName)).findFirst();
    }

    private Optional<JType> findTypeParam(JClass klass, String paramName) {
        return Stream.of(klass.typeParams()).map(JType.class::cast)
            .filter(t -> t.name().equals(paramName)).findFirst();
    }

    private void generateGetter(TypeDeclaration type, JDefinedClass klass, JFieldVar field,
        UnaryOperator<String> op) {
        JMethod getter = klass.method(JMod.PUBLIC, field.type(), op.apply(field.name()));
        getter.body()._return(field);
        if (type.description() != null) {
            getter.javadoc().add(type.description().value());
        }
    }

    private void generateSetter(JDefinedClass klass, JType fieldType, String fieldName) {
        JMethod setter = klass.method(JMod.PUBLIC, codeModel.VOID, getSetterName(fieldName));
        JVar p1 = setter.param(fieldType, fieldName);
        setter.body().assign(JExpr._this().ref(fieldName), p1);
    }

    private void generateBooleanFieldAndAccessors(JDefinedClass klass,
        BooleanTypeDeclaration property) {
        String fieldName = property.name();
        JType jtype = context.getJavaType(property);
        JFieldVar field = klass.field(JMod.PRIVATE, jtype, fieldName);

        generateGetter(property, klass, field, this::getCheckerName);
        generateSetter(klass, jtype, fieldName);
    }

    private void generateDiscriminatorGetter(TypeDeclaration type, JDefinedClass klass,
        String property) {
        JMethod getter = klass.method(JMod.PUBLIC, codeModel._ref(String.class),
            getGetterName(property));
        getter.body()._return(klass.staticRef(DISCRIMINATOR));
        if (type.description() != null) {
            getter.javadoc().add(type.description().value());
        }
    }

    private String getCheckerName(String fieldName) {
        return getAccessorName("is", fieldName);
    }

    private String getGetterName(String fieldName) {
        return getAccessorName("get", fieldName);
    }

    private String getSetterName(String fieldName) {
        return getAccessorName("set", fieldName);
    }

    private String getAccessorName(String prefix, String fieldName) {
        StringBuilder buffer = new StringBuilder(prefix);
        buffer.append(fieldName.substring(0, 1).toUpperCase());
        buffer.append(fieldName.substring(1));
        return buffer.toString();
    }
}
