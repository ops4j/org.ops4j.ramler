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

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.ops4j.ramler.common.helper.Version;
import org.ops4j.ramler.common.model.ApiModel;
import org.ops4j.ramler.common.model.CommonConstants;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

/**
 * Stores the configuration and other intermediate information for a code generator run.
 *
 * @author Harald Wellmann
 *
 */
public class JavaGeneratorContext {

    private JavaConfiguration config;

    private JCodeModel codeModel;

    private JPackage modelPackage;

    private JPackage apiPackage;

    private JPackage delegatorPackage;

    private Map<String, JType> typeMap;

    private ApiModel apiModel;

    /**
     * Creates a generator context for the given configuration.
     *
     * @param config
     *            code generator configuration
     */
    public JavaGeneratorContext(JavaConfiguration config) {
        this.config = config;
        this.codeModel = new JCodeModel();
        this.typeMap = new HashMap<>();
        JPackage basePackage = codeModel._package(config.getBasePackage());
        modelPackage = basePackage.subPackage(config.getModelPackage());
        apiPackage = basePackage.subPackage(config.getApiPackage());
        delegatorPackage = basePackage.subPackage(config.getDelegatorPackage());
    }

    /**
     * Stores the Java type for a given RAML type.
     *
     * @param typeName
     *            RAML type name
     * @param type
     *            corresponding Java type
     */
    public void addType(String typeName, JType type) {
        typeMap.put(typeName, type);
    }

    /**
     * Finds the Java type for a given RAML type. Prerequisite: The type has been stored with
     * {@link #addType(String, JType)}.
     *
     * @param typeName
     *            RAML type name
     * @return corresponding Java type
     */
    public JType findType(String typeName) {
        return typeMap.get(typeName);
    }

    private JType getReferencedJavaType(TypeDeclaration decl) {
        JType jtype = null;
        if (decl instanceof StringTypeDeclaration) {
            jtype = getStringType((StringTypeDeclaration) decl);
        }
        else if (decl instanceof IntegerTypeDeclaration) {
            jtype = getNumberType((IntegerTypeDeclaration) decl);
        }
        else if (decl instanceof NumberTypeDeclaration) {
            jtype = getNumberType((NumberTypeDeclaration) decl);
        }
        else if (decl instanceof BooleanTypeDeclaration) {
            jtype = getBooleanType((BooleanTypeDeclaration) decl);
        }
        else if (decl instanceof ObjectTypeDeclaration) {
            jtype = getObjectType((ObjectTypeDeclaration) decl);
        }
        else if (decl instanceof ArrayTypeDeclaration) {
            jtype = getArrayType((ArrayTypeDeclaration) decl);
        }
        else if (decl instanceof DateTypeDeclaration) {
            jtype = codeModel.ref(LocalDate.class);
        }
        else if (decl instanceof DateTimeOnlyTypeDeclaration) {
            jtype = codeModel.ref(LocalDateTime.class);
        }
        else if (decl instanceof TimeOnlyTypeDeclaration) {
            jtype = codeModel.ref(LocalTime.class);
        }
        else if (decl instanceof DateTimeTypeDeclaration) {
            jtype = codeModel.ref(ZonedDateTime.class);
        }
        else if (decl instanceof FileTypeDeclaration) {
            jtype = codeModel.ref(InputStream.class);
        }
        else if (decl instanceof AnyTypeDeclaration) {
            jtype = codeModel.ref(Object.class);
        }
        return jtype;
    }

    private JType getObjectType(ObjectTypeDeclaration decl) {
        // special case for nested arrays
        String ref = decl.name()
            .equals("object") ? decl.type() : decl.name();
        return getModelPackage()._getClass(ref);
    }

    private JType getArrayType(ArrayTypeDeclaration decl) {
        JType itemType = getReferencedJavaType(decl.items());
        return codeModel.ref(List.class)
            .narrow(itemType);
    }

    private JType getBooleanType(BooleanTypeDeclaration decl) {
        if (decl.required()) {
            return codeModel.BOOLEAN;
        }
        else {
            return codeModel.ref(Boolean.class);
        }
    }

    private JType getStringType(StringTypeDeclaration decl) {
        JType jtype = typeMap.get(decl.type());
        if (jtype == null) {
            jtype = codeModel.ref(String.class);
        }
        return jtype;
    }

    private JType getNumberType(NumberTypeDeclaration decl) {
        if (decl.format() == null) {
            return getIntegerType((IntegerTypeDeclaration) decl);
        }
        switch (decl.format()) {
            case "long":
            case "int64":
                return getLongType((IntegerTypeDeclaration) decl);
            case "float":
                return getFloatType(decl);
            case "double":
                return getDoubleType(decl);
            default:
                return getIntegerType((IntegerTypeDeclaration) decl);
        }
    }

    private JType getLongType(IntegerTypeDeclaration decl) {
        if (decl.required()) {
            return codeModel.LONG;
        }
        else {
            return codeModel.ref(Long.class);
        }
    }

    private JType getFloatType(NumberTypeDeclaration decl) {
        if (decl.required()) {
            return codeModel.FLOAT;
        }
        else {
            return codeModel.ref(Float.class);
        }
    }

    private JType getDoubleType(NumberTypeDeclaration decl) {
        if (decl.required()) {
            return codeModel.DOUBLE;
        }
        else {
            return codeModel.ref(Double.class);
        }
    }

    private JType getIntegerType(IntegerTypeDeclaration decl) {
        if (decl.required()) {
            return codeModel.INT;
        }
        else {
            return codeModel.ref(Integer.class);
        }
    }

    /**
     * Gets the Java type for the given RAML type declaration.
     *
     * @param decl
     *            type declaration
     * @return corresponding Java type
     */
    public JType getJavaType(TypeDeclaration decl) {
        if (decl instanceof ObjectTypeDeclaration) {
            if (decl.type()
                .equals(CommonConstants.OBJECT)) {
                return codeModel.ref(Map.class)
                    .narrow(String.class, Object.class);
            }
            return getModelPackage()._getClass(decl.type());
        }
        else {
            return getReferencedJavaType(decl);
        }
    }

    /**
     * Gets the Java type for the RAML type with the given name.
     *
     * @param type
     *            RAML type name
     * @return corresponding Java type
     */
    public JType getJavaType(String type) {
        if (type.equals(CommonConstants.OBJECT)) {
            return codeModel.ref(Map.class)
                .narrow(String.class, Object.class);
        }
        if (type.equals(CommonConstants.STRING)) {
            return codeModel.ref(String.class);
        }
        if (type.equals(CommonConstants.INTEGER)) {
            return codeModel.ref(Integer.class);
        }
        if (type.equals(CommonConstants.BOOLEAN)) {
            return codeModel.ref(Boolean.class);
        }
        if (type.endsWith("[]")) {
            String itemTypeName = type.substring(0, type.length() - 2);
            JType itemType = getJavaType(itemTypeName);
            return codeModel.ref(List.class)
                .narrow(itemType);
        }
        return getReferencedJavaType(apiModel.getDeclaredType(type));
    }

    /**
     * Adds the {@code @Generated} annotation to the given class.
     *
     * @param klass
     *            generated Java class
     */
    public void annotateAsGenerated(JDefinedClass klass) {
        klass.annotate(Generated.class)
            .param("value", "org.ops4j.ramler")
            .param("date", ZonedDateTime.now()
                .truncatedTo(SECONDS)
                .format(ISO_OFFSET_DATE_TIME))
            .param("comments", "version " + Version.getRamlerVersion());
    }

    /**
     * Adds RAML description as Javadoc to the given class. A default text is added when to
     * description is present.
     *
     * @param klass
     *            generated Java class
     * @param type
     *            RAML type
     */
    public void addJavadoc(JDefinedClass klass, TypeDeclaration type) {
        if (type.description() == null) {
            klass.javadoc()
                .add("Generated from a RAML specification.");
        }
        else {
            klass.javadoc()
                .add(type.description()
                    .value());
        }
    }

    /**
     * Gets the configuration of this generator.
     *
     * @return the config
     */
    public JavaConfiguration getConfig() {
        return config;
    }

    /**
     * Gets the Java code model produced by this generator.
     *
     * @return the codeModel
     */
    public JCodeModel getCodeModel() {
        return codeModel;
    }

    /**
     * Gets the RAML API model this generator is working on.
     *
     * @return the api
     */
    public ApiModel getApiModel() {
        return apiModel;
    }

    /**
     * Sets the RAML API model this generator is working on.
     *
     * @param apiModel
     *            the api to set
     */
    public void setApiModel(ApiModel apiModel) {
        this.apiModel = apiModel;
    }

    /**
     * Gets the Java package for the generated model (POJO) classes.
     *
     * @return the model package
     */
    public JPackage getModelPackage() {
        return modelPackage;
    }

    /**
     * Gets the Java package for the generated resource Java interfaces.
     *
     * @return the API package
     */
    public JPackage getApiPackage() {
        return apiPackage;
    }

    /**
     * Gets the Java package for the generated delegator classes.
     *
     * @return the delegator package
     */
    public JPackage getDelegatorPackage() {
        return delegatorPackage;
    }
}
