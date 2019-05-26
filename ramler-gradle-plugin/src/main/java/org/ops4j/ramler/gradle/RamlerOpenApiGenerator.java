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
import org.ops4j.ramler.openapi.OpenApiConfiguration;
import org.ops4j.ramler.openapi.OpenApiGenerator;

public class RamlerOpenApiGenerator extends DefaultTask {

    /** RAML specification file, relative to <code>${project.basedir}</code>. */
    private String model;

    /**
     * Output directory for generated sources.
     */
    private String outputDir;

    /**
     * Generate YAML output.
     */
    private boolean yaml = true;

    /**
     * Generate JSON output.
     */
    private boolean json;

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
     * Gets the yaml.
     *
     * @return the yaml
     */
    public boolean isYaml() {
        return yaml;
    }

    /**
     * Sets the yaml.
     *
     * @param yaml
     *            the yaml to set
     */
    public void setYaml(boolean yaml) {
        this.yaml = yaml;
    }

    /**
     * Gets the json.
     *
     * @return the json
     */
    public boolean isJson() {
        return json;
    }

    /**
     * Sets the json.
     *
     * @param json
     *            the json to set
     */
    public void setJson(boolean json) {
        this.json = json;
    }

    @TaskAction
    public void generate() {
        getLogger().info("Generating OpenAPI from {}", model);
        String sourceFile = new File(getProject().getProjectDir(), model).getPath();
        String outputDir = java.util.Optional.ofNullable(getOutputDir())
            .orElse(new File(getProject().getBuildDir(), "ramler/openapi").getPath());
        OpenApiConfiguration config = new OpenApiConfiguration();
        config.setSourceFile(sourceFile);
        config.setTargetDir(new File(outputDir));
        config.setGenerateJson(json);
        config.setGenerateYaml(yaml);


        try {
            OpenApiGenerator generator = new OpenApiGenerator(config);
            generator.generate();
        }
        catch (RamlerException | IOException exc) {
            throw new GradleException("OpenAPI generation failed", exc);
        }
    }
}
