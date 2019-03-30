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
package org.ops4j.ramler.maven;

import java.io.File;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Generates Java sources from a RAML model.
 *
 * @author Harald Wellmann
 *
 */
@Mojo(name = "java", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JavaMojo extends AbstractJavaMojo {

    /**
     * Output directory for generated sources.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/ramler")
    private File outputDir;

    @Override
    protected void extendProject() {
        String outputRoot = getOutputDir().getAbsolutePath();
        getLog().info("Adding source directory " + outputRoot);
        project.addCompileSourceRoot(outputRoot);
    }

    @Override
    public File getOutputDir() {
        return outputDir;
    }
}
