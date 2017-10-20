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
 * Configuration of the Typescript code generator.
 *
 * @author Harald Wellmann
 *
 */
public class TypescriptConfiguration {

    private String sourceFile;

    private File targetDir;

    /**
     * Gets the RAML source file name.
     * @return source file name
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the RAML source file name.
     * @param sourceFile source file name
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the target directory for the generated Typescript files.
     * @return target directory
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * Sets the target directory.
     * @param targetDir
     *            target directory
     */
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }
}
