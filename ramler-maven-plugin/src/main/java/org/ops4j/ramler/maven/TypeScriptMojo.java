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
import java.io.IOException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.ops4j.ramler.common.exc.RamlerException;
import org.ops4j.ramler.typescript.TypeScriptConfiguration;
import org.ops4j.ramler.typescript.TypeScriptGenerator;

/**
 * Generates TypeScript code from a RAML model.
 *
 * @author Harald Wellmann
 *
 */
@Mojo(name = "typescript", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class TypeScriptMojo extends AbstractRamlerMojo {

    /**
     * Output directory for generated TypeScript sources.
     */
    @Parameter(defaultValue = "${project.build.directory}/ramler/ts")
    private File outputDir;

    @Parameter(defaultValue = "false")
    private boolean angularService;

    @Parameter
    private String angularBaseUrlToken;

    @Parameter(defaultValue = "Resource")
    private String interfaceNameSuffix;

    @Parameter(defaultValue = "Service")
    private String serviceNameSuffix;

    @Override
    protected void generateOutput() throws MojoFailureException {
        getLog().info("Generating TypeScript sources from " + model);
        String sourceFile = new File(project.getBasedir(), model).getPath();

        TypeScriptConfiguration config = new TypeScriptConfiguration();
        config.setSourceFile(sourceFile);
        config.setTargetDir(getOutputDir());
        config.setAngularBaseUrlToken(angularBaseUrlToken);
        config.setAngularService(angularService);
        config.setInterfaceNameSuffix(interfaceNameSuffix);
        config.setServiceNameSuffix(serviceNameSuffix);

        TypeScriptGenerator generator = new TypeScriptGenerator(config);
        try {
            generator.generate();
        }
        catch (RamlerException | IOException exc) {
            throw new MojoFailureException("HTML generation failed", exc);
        }
    }

    @Override
    public File getOutputDir() {
        return outputDir;
    }
}
