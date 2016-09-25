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
import java.io.IOException;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ops4j.ramler.exc.Exceptions;
import org.ops4j.ramler.html.HtmlConfiguration;
import org.ops4j.ramler.html.HtmlGenerator;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates HTML documentation from a RAML model.
 * 
 * @author hwellmann
 *
 */
@Mojo(name = "html", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class HtmlMojo extends AbstractMojo {

    @Parameter(required = true)
    protected String model;

    @Parameter(defaultValue = "${project.build.directory}/ramler/html")
    private File outputDir;
    
    /**
     * Directory with web resources to be used instead of the built-in resources.
     */
    @Parameter
    private File webResourceDir;
    
    /**
     * Directory with Trimou templates which take precedence over the built-in templates.
     * The entry template is named {@code api.trimou.html}.
     */
    @Parameter
    private File templateDir;

    @Parameter(readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Inject
    private BuildContext buildContext;

    /**
     * @throws MojoFailureException
     * @throws IOException
     */
    protected void generateWebResources() throws MojoFailureException {
        if (buildContext.hasDelta(model)) {
            getLog().info("Generating HTML documentation from " + model);

            HtmlConfiguration config = new HtmlConfiguration();
            config.setSourceFile(model);
            config.setTargetDir(getOutputDir().getAbsolutePath());
            if (templateDir != null) {
                config.setTemplateDir(templateDir.getAbsolutePath());
            }
            if (webResourceDir != null) {
                config.setWebResourceDir(webResourceDir.getAbsolutePath());
            }

            HtmlGenerator generator = new HtmlGenerator(config);
            try {
                generator.generate();
            }
            catch (IOException exc) {
                throw Exceptions.unchecked(exc);
            }
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

    public File getOutputDir() {
        return outputDir;
    }

    @Override
    public void execute() throws MojoFailureException {
        generateWebResources();
    }
}
