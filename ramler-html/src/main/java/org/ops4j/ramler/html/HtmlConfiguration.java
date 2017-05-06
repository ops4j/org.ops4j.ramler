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
package org.ops4j.ramler.html;

/**
 * Configuration of HTML generator.
 *
 * @author Harald Wellmann
 *
 */
public class HtmlConfiguration {

    private String sourceFile;

    private String targetDir;

    private String templateDir;

    private String webResourceDir;

    /**
     * Gets the top-level RAML source file.
     *
     * @return source file
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the top-level RAML source file.
     *
     * @param sourceFile
     *            RAML source file
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the target directory for generated HTML.
     *
     * @return target directory
     */
    public String getTargetDir() {
        return targetDir;
    }

    /**
     * Sets the target directory.
     *
     * @param targetDir
     *            target directory
     */
    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * Gets the directory with Trimou templates for the HTML generator.
     *
     * @return template directory
     */
    public String getTemplateDir() {
        return templateDir;
    }

    /**
     * Sets the template directory.
     *
     * @param templateDir
     *            template directory
     */
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    /**
     * Gets the directory with additional web resources for the HTML generator.
     *
     * @return web resource directory
     */
    public String getWebResourceDir() {
        return webResourceDir;
    }

    /**
     * Sets the web resource directory.
     *
     * @param webResourceDir
     *            web resource directory
     */
    public void setWebResourceDir(String webResourceDir) {
        this.webResourceDir = webResourceDir;
    }
}
