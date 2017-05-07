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

import java.io.File;
import java.util.Optional;

/**
 * Configuration of the RAML Java code generator.
 *
 * @author Harald Wellmann
 *
 */
public class Configuration {

    private String basePackage;

    private String modelPackage;

    private String apiPackage;

    private String sourceFile;

    private File targetDir;

    private String interfaceNameSuffix;

    private boolean discriminatorMutable;

    private boolean jacksonTypeInfo;

    /**
     * Gets the name of the base package for all subpackages created by the code generator.
     *
     * @return the base package (e.g. com.example.myapi)
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Sets the base package name.
     *
     * @param basePackage
     *            base package name
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Gets the name of the subpackage with POJO model classes generated from type definitions.
     * <p>
     * Example: Given the base package {@code com.example.myapi} and the model package
     * {@code gen.model}, the model classes will be generated in package
     * {@code com.example.myapi.gen.model}.
     *
     * @return the model package, defaulting to {@code model}.
     */
    public String getModelPackage() {
        return Optional.ofNullable(modelPackage).orElse("model");
    }

    /**
     * Gets the name of the subpackage with POJO model classes generated from type definitions.
     *
     * @param modelPackage
     *            name of model subpackage
     */
    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    /**
     * Gets the name of the subpackage with JAX-RS resource interfaces generated from resource
     * definitions.
     * <p>
     * Example: Given the base package {@code com.example.myapi} and the API package
     * {@code gen.api}, the resource interfaces will be generated in package
     * {@code com.example.myapi.gen.api}.
     *
     * @return the resource package, defaulting to {@code api}.
     */
    public String getApiPackage() {
        return Optional.ofNullable(apiPackage).orElse("api");
    }

    /**
     * Gets the name of the subpackage with JAX-RS interfaces generated from resource definitions.
     *
     * @param apiPackage
     *            name of API subpackage
     */
    public void setApiPackage(String apiPackage) {
        this.apiPackage = apiPackage;
    }

    /**
     * Gets the top-level RAML source file.
     *
     * @return the source file
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the top-level RAML source file.
     *
     * @param sourceFile
     *            top-level RAML source file
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the target directory for generated code. Generated classes will be located in
     * subdirectories according to the package structure. Any parent directories will be created if
     * needed.
     *
     * @return the target directory
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * Sets the target directory for generated code.
     *
     * @param targetDir
     *            the target directory to set
     */
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * Gets the interface name suffix for JAX-RS resource interfaces. The default is
     * {@code Resource}.
     * <p>
     * Example: For a resource named {@code /shoppingCart}, the corresponding interface will be
     * named {@code ShoppingCartResource}.
     *
     * @return the interfaceNameSuffix
     */
    public String getInterfaceNameSuffix() {
        return Optional.ofNullable(interfaceNameSuffix).orElse("Resource");
    }

    /**
     * Sets the interface name suffix for JAX-RS resource interfaces.
     *
     * @param interfaceNameSuffix
     *            the interfaceNameSuffix to set
     */
    public void setInterfaceNameSuffix(String interfaceNameSuffix) {
        this.interfaceNameSuffix = interfaceNameSuffix;
    }


    public boolean isDiscriminatorMutable() {
        return discriminatorMutable;
    }


    public void setDiscriminatorMutable(boolean discriminatorMutable) {
        this.discriminatorMutable = discriminatorMutable;
    }


    public boolean isJacksonTypeInfo() {
        return jacksonTypeInfo;
    }


    public void setJacksonTypeInfo(boolean jacksonTypeInfo) {
        this.jacksonTypeInfo = jacksonTypeInfo;
    }
}
