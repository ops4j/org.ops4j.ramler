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

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.ops4j.ramler.common.exc.RamlerException;
import org.ops4j.ramler.java.JavaConfiguration;
import org.ops4j.ramler.java.JavaGenerator;

/**
 * Base class for {@code java} and {@code java-test} goals.
 *
 * @author Harald Wellmann
 *
 */
public abstract class AbstractJavaMojo extends AbstractRamlerMojo {

    /**
     * Fully qualified package name for generated Java sources. The generated classes will be
     * located in subpackages {@code model} and {@code api}.
     */
    @Parameter(name = "package", required = true)
    private String packageName;

    /**
     * Should discriminator properties be mutable?
     */
    @Parameter(defaultValue = "false")
    private boolean discriminatorMutable;

    /**
     * Suffix for interface names. This suffix is appended to the code name of a resource. The code
     * name is either specified explicitly by the {@code (codeName)} annotation, or implicitly by
     * the resource name, converted to camel case.
     */
    @Parameter(defaultValue = "Resource")
    private String interfaceNameSuffix;

    /**
     * Should Java classes include type information annotations for type hierarchies?
     */
    @Parameter(defaultValue = "false")
    private boolean jacksonTypeInfo;

    /**
     * Should Java classes include {@code JsonProperty} annotations for properties with illegal Java
     * names?
     */
    @Parameter(defaultValue = "false")
    private boolean jacksonPropertyName;

    /**
     * Should Jackson annotations {@code @JsonSerializer} etc. be used for union types?
     */
    @Parameter(defaultValue = "false")
    private boolean jacksonUnion;

    @Parameter(defaultValue = "false")
    private boolean delegators;

    @Parameter(defaultValue = "Delegator")
    private String delegatorSuffix;

    @Parameter(defaultValue = "delegate")
    private String delegateFieldName;

    @Override
    protected void generateOutput() throws MojoFailureException {
        getLog().info("Generating Java model from " + model);
        String sourceFile = new File(project.getBasedir(), model).getPath();

        JavaConfiguration config = new JavaConfiguration();
        config.setSourceFile(sourceFile);
        config.setBasePackage(packageName);
        config.setTargetDir(getOutputDir());
        config.setDiscriminatorMutable(discriminatorMutable);
        config.setInterfaceNameSuffix(interfaceNameSuffix);
        config.setJacksonTypeInfo(jacksonTypeInfo);
        config.setJacksonPropertyName(jacksonPropertyName);
        config.setJacksonUnion(jacksonUnion);

        try {
            JavaGenerator generator = new JavaGenerator(config);
            generator.generate();
        }
        catch (RamlerException exc) {
            throw new MojoFailureException("code generation failed", exc);
        }
    }

    @Override
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
