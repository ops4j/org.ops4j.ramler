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

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Generated;

import org.ops4j.ramler.model.ApiModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class GeneratorContext {

    private static Logger log = LoggerFactory.getLogger(GeneratorContext.class);

    private static final String POM_PROPERTIES = "/META-INF/maven/org.ops4j.ramler/ramler-java/pom.properties";

    private Configuration config;

    private JCodeModel codeModel;

    private JPackage modelPackage;

    private JPackage apiPackage;

    private Map<String, JType> typeMap;

    private ApiModel apiModel;

    private String ramlerVersion;

    /**
     * Creates a generator context for the given configuration.
     *
     * @param config
     *            code generator configuration
     */
    public GeneratorContext(Configuration config) {
        this.config = config;
        this.codeModel = new JCodeModel();
        this.typeMap = new HashMap<>();
        JPackage basePackage = codeModel._package(config.getBasePackage());
        modelPackage = basePackage.subPackage(config.getModelPackage());
        apiPackage = basePackage.subPackage(config.getApiPackage());
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
            jtype = typeMap.get(decl.type());
            if (jtype == null) {
                jtype = codeModel.ref(String.class);
            }
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
            // special case for nested arrays
            String ref = decl.name().equals("object") ? decl.type() : decl.name();
            jtype = getModelPackage()._getClass(ref);
        }
        else if (decl instanceof ArrayTypeDeclaration) {
            ArrayTypeDeclaration array = (ArrayTypeDeclaration) decl;
            JType itemType = getReferencedJavaType(array.items());
            jtype = codeModel.ref(List.class).narrow(itemType);
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

    private JType getBooleanType(BooleanTypeDeclaration decl) {
        if (decl.required()) {
            return codeModel.BOOLEAN;
        }
        else {
            return codeModel.ref(Boolean.class);
        }
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
            if (decl.type().equals(Constants.OBJECT)) {
                return codeModel.ref(Map.class).narrow(String.class, Object.class);
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
        if (type.equals(Constants.OBJECT)) {
            return codeModel.ref(Map.class).narrow(String.class, Object.class);
        }
        if (type.equals("string")) {
            return codeModel.ref(String.class);
        }
        if (type.equals("integer")) {
            return codeModel.ref(Integer.class);
        }
        if (type.equals("boolean")) {
            return codeModel.ref(Boolean.class);
        }
        if (type.endsWith("[]")) {
            String itemTypeName = type.substring(0, type.length() - 2);
            JType itemType = getJavaType(itemTypeName);
            return codeModel.ref(List.class).narrow(itemType);
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
            .param("date", ZonedDateTime.now().truncatedTo(SECONDS).format(ISO_OFFSET_DATE_TIME))
            .param("comments", "version " + getRamlerVersion());
    }

    private String getRamlerVersion() {
        if (ramlerVersion == null) {
            Properties props = new Properties();
            try (InputStream is = GeneratorContext.class.getResourceAsStream(POM_PROPERTIES)) {
                // The resource may not be available when running from the IDE
                if (is != null) {
                    props.load(is);
                }
            }
            catch (IOException exc) {
                log.debug("Error loading pom.properties", exc);
            }
            ramlerVersion = props.getProperty("version", "UNKNOWN");
        }
        return ramlerVersion;
    }

    /**
     * Gets the configuration of this generator.
     *
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Sets the configuration of this generator.
     *
     * @param config
     *            the config to set
     */
    public void setConfig(Configuration config) {
        this.config = config;
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
     * Sets the Java code model produced by this generator.
     *
     * @param codeModel
     *            the codeModel to set
     */
    public void setCodeModel(JCodeModel codeModel) {
        this.codeModel = codeModel;
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
}
