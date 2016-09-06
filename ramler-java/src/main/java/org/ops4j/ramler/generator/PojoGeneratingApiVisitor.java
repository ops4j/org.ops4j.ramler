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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.Generated;

import org.ops4j.ramler.exc.Exceptions;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class PojoGeneratingApiVisitor implements ApiVisitor {

    private GeneratorContext context;

    private JCodeModel codeModel;

    private JPackage pkg;

    public PojoGeneratingApiVisitor(GeneratorContext context) {
        this.context = context;
        this.codeModel = context.getCodeModel();
        this.pkg = context.getModelPackage();
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        try {
            JDefinedClass klass = pkg._class(type.name());
            context.addType(type.type(), klass);
            klass.annotate(Generated.class).param("value", getClass().getName()).param("date",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());

            addTypeParameters(klass, type);
            addBaseClass(klass, type);

            klass.javadoc().add("This is a generated class.");

        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    private void addBaseClass(JDefinedClass klass, ObjectTypeDeclaration type) {
        if (!type.type().equals("object")) {
            JClass baseClass = pkg._getClass(type.type());

            Optional<AnnotationRef> typeArgs = type.annotations().stream()
                .filter(a -> a.annotation().name().equals("typeArgs")).findFirst();
            if (typeArgs.isPresent()) {
                TypeInstanceProperty prop = typeArgs.get().structuredValue().properties().get(0);
                JClass[] args = prop.values().stream().map(v -> pkg._getClass(v.value().toString()))
                    .toArray(JClass[]::new);
                baseClass = baseClass.narrow(args);
            }
            klass._extends(baseClass);
        }
    }

    private void addTypeParameters(JDefinedClass klass, ObjectTypeDeclaration type) {
        Optional<AnnotationRef> typeParams = type.annotations().stream()
            .filter(a -> a.annotation().name().equals("typeParams")).findFirst();
        if (typeParams.isPresent()) {
            TypeInstanceProperty prop = typeParams.get().structuredValue().properties().get(0);
            prop.values().forEach(i -> klass.generify(i.value().toString()));
        }
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        JDefinedClass klass = pkg._getClass(type.name());
        if (!isInherited(type, property)) {
            generateFieldAndAccessors(klass, property);
        }
    }

    private boolean isInherited(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (type.type().equals("object")) {
            return false;
        }
        JDefinedClass baseClass = pkg._getClass(type.type());
        return baseClass.fields().containsKey(property.name());
    }

    @Override
    public void visitStringType(StringTypeDeclaration type) {
        if (type.enumValues().isEmpty()) {
            return;
        }

        try {
            JDefinedClass klass = pkg._enum(type.name());
            context.addType(type.name(), klass);
            for (String enumValue : type.enumValues()) {
                klass.enumConstant(enumValue.toUpperCase());
            }
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    private void generateFieldAndAccessors(JDefinedClass klass, TypeDeclaration property) {
        if (property instanceof ObjectTypeDeclaration) {
            generateObjectFieldAndAccessors(klass, (ObjectTypeDeclaration) property);
        }
        else if (property instanceof ArrayTypeDeclaration) {
            generateListFieldAndAccessors(klass, (ArrayTypeDeclaration) property);
        }
        else if (property instanceof BooleanTypeDeclaration) {
            generateBooleanFieldAndAccessors(klass, (BooleanTypeDeclaration) property);
        }
        else {
            generateSimpleFieldAndAccessor(klass, property);
        }
    }

    private void generateSimpleFieldAndAccessor(JDefinedClass klass, TypeDeclaration property) {
        String fieldName = property.name();
        JType jtype = context.getJavaType(property);
        JFieldVar field = klass.field(JMod.PRIVATE, jtype, fieldName);

        generateGetter(klass, field, this::getGetterName);
        generateSetter(klass, jtype, fieldName);
    }

    private void generateListFieldAndAccessors(JDefinedClass klass, ArrayTypeDeclaration property) {
        String fieldName = property.name();
        JType elementType = context.getReferencedJavaType(property.items());
        JClass listType = codeModel.ref(List.class).narrow(elementType);
        JFieldVar field = klass.field(JMod.PRIVATE, listType, fieldName);

        JMethod getter = klass.method(JMod.PUBLIC, listType, getGetterName(fieldName));
        getter.body()._if(field.eq(JExpr._null()))._then().assign(field,
            JExpr._new(codeModel.ref(ArrayList.class).narrow(elementType)));
        getter.body()._return(field);
    }

    private void generateObjectFieldAndAccessors(JDefinedClass klass,
        ObjectTypeDeclaration property) {
        String fieldName = property.name();

        JType jtype = findTypeVar(klass, property).orElse(context.getJavaType(property));
        JFieldVar field = klass.field(JMod.PRIVATE, jtype, fieldName);

        generateGetter(klass, field, this::getGetterName);
        generateSetter(klass, jtype, fieldName);
    }

    private Optional<JType> findTypeVar(JDefinedClass klass, ObjectTypeDeclaration property) {
        return property.annotations().stream()
            .filter(a -> a.annotation().name().equals("typeParam")).findFirst()
            .flatMap(t -> findTypeParam(klass, t));
    }

    private Optional<JType> findTypeParam(JDefinedClass klass, AnnotationRef typeParam) {
        String paramName = typeParam.structuredValue().value().toString();
        return Stream.of(klass.typeParams()).map(JType.class::cast)
            .filter(t -> t.name().equals(paramName)).findFirst();
    }

    private void generateGetter(JDefinedClass klass, JFieldVar field, UnaryOperator<String> op) {
        JMethod getter = klass.method(JMod.PUBLIC, field.type(), op.apply(field.name()));
        getter.body()._return(field);
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

        generateGetter(klass, field, this::getCheckerName);
        generateSetter(klass, jtype, fieldName);
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
