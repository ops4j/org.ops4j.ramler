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

public class Configuration {

    private String basePackage;

    private String modelPackage;

    private String apiPackage;

    private File sourceFile;

    private File targetDir;

    private String interfaceNameSuffix;

    /**
     * @return the basePackage
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * @param basePackage
     *            the basePackage to set
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * @return the modelPackage
     */
    public String getModelPackage() {
        return modelPackage;
    }

    /**
     * @param modelPackage
     *            the modelPackage to set
     */
    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    /**
     * @return the apiPackage
     */
    public String getApiPackage() {
        return apiPackage;
    }

    /**
     * @param apiPackage
     *            the apiPackage to set
     */
    public void setApiPackage(String apiPackage) {
        this.apiPackage = apiPackage;
    }

    /**
     * @return the sourceDir
     */
    public File getSourceFile() {
        return sourceFile;
    }

    /**
     * @param sourceDir
     *            the sourceDir to set
     */
    public void setSourceFile(File sourceDir) {
        this.sourceFile = sourceDir;
    }

    /**
     * @return the targetDir
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * @param targetDir
     *            the targetDir to set
     */
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * @return the interfaceNameSuffix
     */
    public String getInterfaceNameSuffix() {
        return Optional.ofNullable(interfaceNameSuffix).orElse("Resource");
    }

    /**
     * @param interfaceNameSuffix
     *            the interfaceNameSuffix to set
     */
    public void setInterfaceNameSuffix(String interfaceNameSuffix) {
        this.interfaceNameSuffix = interfaceNameSuffix;
    }

}
