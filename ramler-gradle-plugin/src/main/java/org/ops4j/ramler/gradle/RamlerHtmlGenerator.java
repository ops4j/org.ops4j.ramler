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

import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.ops4j.ramler.common.exc.RamlerException;
import org.ops4j.ramler.html.HtmlConfiguration;
import org.ops4j.ramler.html.HtmlGenerator;

public class RamlerHtmlGenerator extends AbstractRamlerTask {

    /**
     * Directory with web resources to be used instead of the built-in resources.
     */
    private String webResourceDir;

    /**
     * Directory with Trimou templates which take precedence over the built-in templates. The entry
     * template is named {@code api.trimou.html}.
     */
    private String templateDir;

    /**
     * Gets the webResourceDir.
     *
     * @return the webResourceDir
     */
    @Input
    @Optional
    public String getWebResourceDir() {
        return webResourceDir;
    }

    /**
     * Sets the webResourceDir.
     *
     * @param webResourceDir
     *            the webResourceDir to set
     */
    public void setWebResourceDir(String webResourceDir) {
        this.webResourceDir = webResourceDir;
    }

    /**
     * Gets the templateDir.
     *
     * @return the templateDir
     */
    @Input
    @Optional
    public String getTemplateDir() {
        return templateDir;
    }

    /**
     * Sets the templateDir.
     *
     * @param templateDir
     *            the templateDir to set
     */
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    @TaskAction
    public void generate() {
        getLogger().info("Generating static HTML from {}", getModel());
        String sourceFile = new File(getProject().getProjectDir(), getModel()).getPath();
        String outputDir = java.util.Optional.ofNullable(getOutputDir())
            .orElse(new File(getProject().getBuildDir(), "ramler/html").getPath());
        HtmlConfiguration config = new HtmlConfiguration();
        config.setSourceFile(sourceFile);
        config.setTargetDir(outputDir);
        config.setTemplateDir(templateDir);
        config.setWebResourceDir(webResourceDir);

        try {
            HtmlGenerator generator = new HtmlGenerator(config);
            generator.generate();
        }
        catch (RamlerException | IOException exc) {
            throw new GradleException("HTML generation failed", exc);
        }
    }
}
