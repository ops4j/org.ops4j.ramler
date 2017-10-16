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
 * @author Harald Wellmann
 *
 */
public class TypescriptConfiguration {

    private String sourceFile;

    private File targetDir;

    private String templateDir;

    /**
     * @return the sourceFile
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * @param sourceFile the sourceFile to set
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * @return the targetDir
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * @param targetDir the targetDir to set
     */
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * @return the templateDir
     */
    public String getTemplateDir() {
        return templateDir;
    }

    /**
     * @param templateDir the templateDir to set
     */
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }
}
