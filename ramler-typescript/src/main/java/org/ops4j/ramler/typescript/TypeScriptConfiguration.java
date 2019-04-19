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
package org.ops4j.ramler.typescript;

import java.io.File;

/**
 * Configuration of the TypeScript code generator.
 *
 * @author Harald Wellmann
 *
 */
public class TypeScriptConfiguration {

    private String sourceFile;

    private File targetDir;

    private boolean angularService;

    private String angularBaseUrlToken;

    private String interfaceNameSuffix;

    private String serviceNameSuffix;

    /**
     * Gets the RAML source file name.
     *
     * @return source file name
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the RAML source file name.
     *
     * @param sourceFile
     *            source file name
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the target directory for the generated TypeScript files.
     *
     * @return target directory
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * Sets the target directory.
     *
     * @param targetDir
     *            target directory
     */
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * Gets the angularService.
     *
     * @return the angularService
     */
    public boolean isAngularService() {
        return angularService;
    }

    /**
     * Sets the angularService.
     *
     * @param angularService
     *            the angularService to set
     */
    public void setAngularService(boolean angularService) {
        this.angularService = angularService;
    }

    /**
     * Gets the angularBaseUrlToken.
     *
     * @return the angularBaseUrlToken
     */
    public String getAngularBaseUrlToken() {
        return angularBaseUrlToken;
    }

    /**
     * Sets the angularBaseUrlToken.
     *
     * @param angularBaseUrlToken
     *            the angularBaseUrlToken to set
     */
    public void setAngularBaseUrlToken(String angularBaseUrlToken) {
        this.angularBaseUrlToken = angularBaseUrlToken;
    }

    /**
     * Gets the interfaceNameSuffix.
     *
     * @return the interfaceNameSuffix
     */
    public String getInterfaceNameSuffix() {
        return interfaceNameSuffix;
    }

    /**
     * Sets the interfaceNameSuffix.
     *
     * @param interfaceNameSuffix
     *            the interfaceNameSuffix to set
     */
    public void setInterfaceNameSuffix(String interfaceNameSuffix) {
        this.interfaceNameSuffix = interfaceNameSuffix;
    }

    /**
     * Gets the serviceNameSuffix.
     *
     * @return the serviceNameSuffix
     */
    public String getServiceNameSuffix() {
        return serviceNameSuffix;
    }

    /**
     * Sets the serviceNameSuffix.
     *
     * @param serviceNameSuffix
     *            the serviceNameSuffix to set
     */
    public void setServiceNameSuffix(String serviceNameSuffix) {
        this.serviceNameSuffix = serviceNameSuffix;
    }
}
