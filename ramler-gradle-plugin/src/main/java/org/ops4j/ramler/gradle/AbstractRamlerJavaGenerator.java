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
package org.ops4j.ramler.gradle;

import java.io.File;

import org.gradle.api.GradleException;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.ops4j.ramler.common.exc.RamlerException;
import org.ops4j.ramler.java.JavaConfiguration;
import org.ops4j.ramler.java.JavaGenerator;

/**
 * Base class for tasks generating Java code.
 *
 * @author Harald Wellmann
 *
 */
public abstract class AbstractRamlerJavaGenerator extends AbstractRamlerTask {

    private String packageName;

    private boolean discriminatorMutable;

    private String interfaceNameSuffix;

    private boolean jacksonTypeInfo;

    private boolean jacksonPropertyName;

    private boolean jacksonUnion;

    /**
     * Gets the fully qualified package name for generated Java sources. The generated classes will
     * be located in subpackages {@code model} and {@code api}.
     *
     * @return the package name for generated sources
     */
    @Input
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package name for generated sources.
     *
     * @param packageName
     *            the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Should discriminator properties be mutable?
     *
     * @return true if discriminator properties are mutable
     */
    @Input
    public boolean isDiscriminatorMutable() {
        return discriminatorMutable;
    }

    /**
     * Sets the flag for mutable discriminators.
     *
     * @param discriminatorMutable
     *            the flag to set
     */
    public void setDiscriminatorMutable(boolean discriminatorMutable) {
        this.discriminatorMutable = discriminatorMutable;
    }

    /**
     * Gets the suffix for interface names. This suffix is appended to the code name of a resource.
     * The code name is either specified explicitly by the {@code (codeName)} annotation, or
     * implicitly by the resource name, converted to camel case. The default value is
     * {@code Resource}.
     *
     * @return the interface nameSuffix
     */
    @Input
    @Optional
    public String getInterfaceNameSuffix() {
        return interfaceNameSuffix;
    }

    /**
     * Sets the interface name suffix.
     *
     * @param interfaceNameSuffix
     *            the suffix to set
     */
    public void setInterfaceNameSuffix(String interfaceNameSuffix) {
        this.interfaceNameSuffix = interfaceNameSuffix;
    }

    /**
     * Should Java classes include Jackson type information annotations for type hierarchies?
     *
     * @return true if type info annotations will be generated
     */
    @Input
    public boolean isJacksonTypeInfo() {
        return jacksonTypeInfo;
    }

    /**
     * Sets the flag for Jackson type info annotations.
     *
     * @param jacksonTypeInfo
     *            the flag to set
     */
    public void setJacksonTypeInfo(boolean jacksonTypeInfo) {
        this.jacksonTypeInfo = jacksonTypeInfo;
    }

    /**
     * Should Java classes include Jackson {@code JsonProperty} annotations for properties with
     * illegal Java names?
     *
     * @return true if {@code JsonProperty} annotations shall be generated
     */
    @Input
    public boolean isJacksonPropertyName() {
        return jacksonPropertyName;
    }

    /**
     * Sets the flag for {@code JsonProperty} annotations.
     *
     * @param jacksonPropertyName
     *            the flag to set
     */
    public void setJacksonPropertyName(boolean jacksonPropertyName) {
        this.jacksonPropertyName = jacksonPropertyName;
    }

    /**
     * Should Jackson annotations {@code @JsonSerializer} etc. be generated for union types?
     *
     * @return true if Jackson annotations and helper classes shall be generated for union types
     */
    @Input
    public boolean isJacksonUnion() {
        return jacksonUnion;
    }

    /**
     * Sets the flag enabling Jackson annotations for union types.
     *
     * @param jacksonUnion
     *            the flag to set
     */
    public void setJacksonUnion(boolean jacksonUnion) {
        this.jacksonUnion = jacksonUnion;
    }

    /**
     * Gets the default output subdirectory. This path will be appended to {@code project.buildDir}.
     *
     * @return default output subdirectory
     */
    protected abstract String getDefaultOutputSubdir();

    /**
     * Gets the source set to which sources generated by this taks will be added.
     *
     * @return source set
     */
    protected abstract String getSourceSet();

    /**
     * Generates Java sources.
     */
    @TaskAction
    public void generate() {
        getLogger().info("Generating Java sources from {}", getModel());
        String sourceFile = new File(getProject().getProjectDir(), getModel()).getPath();
        String outputDir = java.util.Optional.ofNullable(getOutputDir())
            .orElse(new File(getProject().getBuildDir(), getDefaultOutputSubdir()).getPath());
        JavaConfiguration config = new JavaConfiguration();
        config.setSourceFile(sourceFile);
        config.setBasePackage(packageName);
        config.setTargetDir(new File(outputDir));
        config.setDiscriminatorMutable(discriminatorMutable);
        config.setInterfaceNameSuffix(interfaceNameSuffix);
        config.setJacksonTypeInfo(jacksonTypeInfo);
        config.setJacksonPropertyName(jacksonPropertyName);
        config.setJacksonUnion(jacksonUnion);

        JavaPluginConvention javaPluginConvention = getProject().getConvention()
            .getPlugin(JavaPluginConvention.class);
        javaPluginConvention.getSourceSets()
            .getByName(getSourceSet())
            .getJava()
            .srcDir(outputDir);

        try {
            JavaGenerator generator = new JavaGenerator(config);
            generator.generate();
        }
        catch (RamlerException exc) {
            throw new GradleException("Code generation failed", exc);
        }
    }
}
