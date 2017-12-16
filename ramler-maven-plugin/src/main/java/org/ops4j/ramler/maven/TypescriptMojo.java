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
package org.ops4j.ramler.maven;

import java.io.File;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ops4j.ramler.exc.RamlerException;
import org.ops4j.ramler.typescript.TypescriptConfiguration;
import org.ops4j.ramler.typescript.TypescriptGenerator;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates HTML documentation from a RAML model.
 *
 * @author Harald Wellmann
 *
 */
@Mojo(name = "typescript", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class TypescriptMojo extends AbstractMojo {

    /** RAML specification file. */
    @Parameter(required = true)
    protected String model;

    /**
     * Output directory for generated Typescript sources.
     */
    @Parameter(defaultValue = "${project.build.directory}/ramler/ts")
    private File outputDir;

    @Parameter(readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Inject
    private BuildContext buildContext;

    protected void generateSources() throws MojoFailureException {
        if (buildContext.hasDelta(model)) {
            getLog().info("Generating Typescript sources from " + model);

            TypescriptConfiguration config = new TypescriptConfiguration();
            config.setSourceFile(model);
            config.setTargetDir(getOutputDir());

            TypescriptGenerator generator = new TypescriptGenerator(config);
            try {
                generator.generate();
            }
            catch (RamlerException exc) {
                throw new MojoFailureException("HTML generation failed", exc);
            }
            refreshGeneratedSources();
        }
        else {
            getLog().info("Typescript model is up-to-date");
        }
    }

    private void refreshGeneratedSources() {
        getLog().debug("refreshing " + getOutputDir());
        buildContext.refresh(getOutputDir());
    }

    public File getOutputDir() {
        return outputDir;
    }

    @Override
    public void execute() throws MojoFailureException {
        generateSources();
    }
}
