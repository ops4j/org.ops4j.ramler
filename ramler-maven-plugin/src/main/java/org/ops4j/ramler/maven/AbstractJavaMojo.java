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

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ops4j.ramler.generator.Configuration;
import org.ops4j.ramler.generator.Generator;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates Java test sources from a RAML model.
 * 
 * @author hwellmann
 *
 */
public abstract class AbstractJavaMojo extends AbstractMojo {

    @Parameter(required = true)
    protected String model;

    @Parameter(name = "package", required = true)
    private String packageName;

    @Parameter(readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Inject
    private BuildContext buildContext;

    /**
     * @throws MojoFailureException
     */
    protected void generateJavaSources() throws MojoFailureException {
        if (buildContext.hasDelta(model)) {
            getLog().info("Generating Java model from " + model);

            Configuration config = new Configuration();
            config.setSourceFile(model);
            config.setBasePackage(packageName);
            config.setTargetDir(getOutputDir());

            Generator generator = new Generator(config);
            generator.generate();
            refreshGeneratedSources();
        }
        else {
            getLog().info("Java model is up-to-date");
        }
    }

    private void refreshGeneratedSources() {
        getLog().debug("refreshing " + getOutputDir());
        buildContext.refresh(getOutputDir());
    }

    public abstract File getOutputDir();

    /**
     * Gets the package name for the generated sources.
     *
     * @return the package name
     */
    public String getPackage() {
        return packageName;
    }

    /**
     * Gets the package name for the generated sources.
     *
     * @param packageName
     *            the genPackage to set
     */
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }
}
