/*
 * Copyright 2019 OPS4J Contributors
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
package org.ops4j.ramler.gradle;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.ops4j.ramler.common.exc.RamlerException;
import org.ops4j.ramler.typescript.TypeScriptConfiguration;
import org.ops4j.ramler.typescript.TypeScriptGenerator;

public class RamlerTypeScriptGenerator extends DefaultTask {

    /** RAML specification file, relative to <code>${project.basedir}</code>. */
    private String model;

    /**
     * Output directory for generated sources.
     */
    private String outputDir;

    private boolean angularService;

    private String angularBaseUrlToken;

    private String interfaceNameSuffix = "Resource";

    private String serviceNameSuffix = "Service";

    /**
     * Gets the model.
     *
     * @return the model
     */
    @Input
    public String getModel() {
        return model;
    }

    /**
     * Sets the model.
     *
     * @param model
     *            the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the outputDir.
     *
     * @return the outputDir
     */
    @Input
    @OutputDirectory
    @Optional
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Sets the outputDir.
     *
     * @param outputDir
     *            the outputDir to set
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Gets the angularService.
     *
     * @return the angularService
     */
    @Input
    @Optional
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
    @Input
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
    @Input
    @Optional
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
    @Input
    @Optional
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

    @TaskAction
    public void generate() {
        getLogger().info("Generating TypeScript sources from {}", model);
        String sourceFile = new File(getProject().getProjectDir(), model).getPath();
        String outputDir = java.util.Optional.ofNullable(getOutputDir())
            .orElse(new File(getProject().getBuildDir(), "ramler/ts").getPath());
        TypeScriptConfiguration config = new TypeScriptConfiguration();
        config.setSourceFile(sourceFile);
        config.setTargetDir(new File(outputDir));
        config.setAngularBaseUrlToken(angularBaseUrlToken);
        config.setAngularService(angularService);
        config.setInterfaceNameSuffix(interfaceNameSuffix);
        config.setServiceNameSuffix(serviceNameSuffix);

        try {
            TypeScriptGenerator generator = new TypeScriptGenerator(config);
            generator.generate();
        }
        catch (RamlerException | IOException exc) {
            throw new GradleException("Code generation failed", exc);
        }
    }
}
