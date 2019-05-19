package org.ops4j.ramler.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Input;

public class JavaRamlerPlugin implements Plugin<Project> {

    /**
     * Output directory for generated sources.
     */
    // @Parameter(defaultValue = "${project.build.directory}/generated-sources/ramler")
    private String outputDir;

    /**
     * Fully qualified package name for generated Java sources. The generated classes will be
     * located in subpackages {@code model} and {@code api}.
     */
    private String packageName;

    /**
     * Should discriminator properties be mutable?
     */
    private boolean discriminatorMutable;

    /**
     * Suffix for interface names. This suffix is appended to the code name of a resource. The code
     * name is either specified explicitly by the {@code (codeName)} annotation, or implicitly by
     * the resource name, converted to camel case.
     */
    private String interfaceNameSuffix;

    /**
     * Should Java classes include type information annotations for type hierarchies?
     */
    private boolean jacksonTypeInfo;

    /**
     * Should Java classes include {@code JsonProperty} annotations for properties with illegal Java
     * names?
     */
    private boolean jacksonPropertyName;

    /**
     * Should Jackson annotations {@code @JsonSerializer} etc. be used for union types?
     */
    private boolean jacksonUnion;

    private boolean delegators;

    private String delegatorSuffix;

    private String delegateFieldName;

    /**
     * Gets the outputDir.
     *
     * @return the outputDir
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Sets the outputDir.
     *
     * @param outputDir
     *            the outputDir to set
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Gets the packageName.
     *
     * @return the packageName
     */
    @Input
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the packageName.
     *
     * @param packageName
     *            the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Gets the discriminatorMutable.
     *
     * @return the discriminatorMutable
     */
    @Input
    public boolean isDiscriminatorMutable() {
        return discriminatorMutable;
    }

    /**
     * Sets the discriminatorMutable.
     *
     * @param discriminatorMutable
     *            the discriminatorMutable to set
     */
    public void setDiscriminatorMutable(boolean discriminatorMutable) {
        this.discriminatorMutable = discriminatorMutable;
    }

    /**
     * Gets the interfaceNameSuffix.
     *
     * @return the interfaceNameSuffix
     */
    @Input
    public String getInterfaceNameSuffix() {
        return interfaceNameSuffix;
    }

    /**
     * Sets the interfaceNameSuffix.
     *
     * @param interfaceNameSuffix
     *            the interfaceNameSuffix to set
     */
    public void setInterfaceNameSuffix(String interfaceNameSuffix) {
        this.interfaceNameSuffix = interfaceNameSuffix;
    }

    /**
     * Gets the jacksonTypeInfo.
     *
     * @return the jacksonTypeInfo
     */
    @Input
    public boolean isJacksonTypeInfo() {
        return jacksonTypeInfo;
    }

    /**
     * Sets the jacksonTypeInfo.
     *
     * @param jacksonTypeInfo
     *            the jacksonTypeInfo to set
     */
    public void setJacksonTypeInfo(boolean jacksonTypeInfo) {
        this.jacksonTypeInfo = jacksonTypeInfo;
    }

    /**
     * Gets the jacksonPropertyName.
     *
     * @return the jacksonPropertyName
     */
    @Input
    public boolean isJacksonPropertyName() {
        return jacksonPropertyName;
    }

    /**
     * Sets the jacksonPropertyName.
     *
     * @param jacksonPropertyName
     *            the jacksonPropertyName to set
     */
    public void setJacksonPropertyName(boolean jacksonPropertyName) {
        this.jacksonPropertyName = jacksonPropertyName;
    }

    /**
     * Gets the jacksonUnion.
     *
     * @return the jacksonUnion
     */
    @Input
    public boolean isJacksonUnion() {
        return jacksonUnion;
    }

    /**
     * Sets the jacksonUnion.
     *
     * @param jacksonUnion
     *            the jacksonUnion to set
     */
    public void setJacksonUnion(boolean jacksonUnion) {
        this.jacksonUnion = jacksonUnion;
    }

    /**
     * Gets the delegators.
     *
     * @return the delegators
     */
    @Input
    public boolean isDelegators() {
        return delegators;
    }

    /**
     * Sets the delegators.
     *
     * @param delegators
     *            the delegators to set
     */
    public void setDelegators(boolean delegators) {
        this.delegators = delegators;
    }

    /**
     * Gets the delegatorSuffix.
     *
     * @return the delegatorSuffix
     */
    @Input
    public String getDelegatorSuffix() {
        return delegatorSuffix;
    }

    /**
     * Sets the delegatorSuffix.
     *
     * @param delegatorSuffix
     *            the delegatorSuffix to set
     */
    public void setDelegatorSuffix(String delegatorSuffix) {
        this.delegatorSuffix = delegatorSuffix;
    }

    /**
     * Gets the delegateFieldName.
     *
     * @return the delegateFieldName
     */
    @Input
    public String getDelegateFieldName() {
        return delegateFieldName;
    }

    /**
     * Sets the delegateFieldName.
     *
     * @param delegateFieldName
     *            the delegateFieldName to set
     */
    public void setDelegateFieldName(String delegateFieldName) {
        this.delegateFieldName = delegateFieldName;
    }

    @Override
    public void apply(Project project) {
        project.getTasks()
            .withType(RamlerJavaGenerator.class, task -> {
                project.getTasks()
                    .getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
                    .dependsOn(task);
            });
    }
}
