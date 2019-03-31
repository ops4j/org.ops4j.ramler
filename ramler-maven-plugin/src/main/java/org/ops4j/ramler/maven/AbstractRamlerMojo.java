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
package org.ops4j.ramler.maven;

import java.io.File;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Base class for Ramler Mojos, taking care of incremental builds with m2e.
 *
 * @author Harald Wellmann
 *
 */
public abstract class AbstractRamlerMojo extends AbstractMojo {

    /** RAML specification file, relative to <code>${project.basedir}</code>. */
    @Parameter(required = true)
    protected String model;

    @Parameter(readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Inject
    private BuildContext buildContext;

    @Override
    public void execute() throws MojoFailureException {
        extendProject();
        if (buildContext.hasDelta(model)) {
            generateOutput();
            refreshOutput();
        }
        else {
            getLog().info("Skipping execution, RAML model is unchanged");
        }
    }

    private void refreshOutput() {
        getLog().debug("refreshing " + getOutputDir());
        buildContext.refresh(getOutputDir());
    }

    protected abstract void generateOutput() throws MojoFailureException;

    protected abstract File getOutputDir();

    /**
     * Override to add (test) source directories.
     */
    protected void extendProject() {
        // empty
    }
}
