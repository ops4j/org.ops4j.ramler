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
package org.ops4j.ramler.generator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;

public abstract class AbstractGeneratorTest {

    protected Generator generator;
    protected JPackage modelPackage;
    protected JDefinedClass klass;
    protected Set<String> methodNames;
    protected Set<String> fieldNames;
    protected JCodeModel codeModel;
    private JPackage apiPackage;

    @Before
    public void generateJavaModel() {

        generator = new Generator(getConfiguration());
        generator.generate();

        codeModel = generator.getContext().getCodeModel();
        modelPackage = codeModel._package(String.format("org.ops4j.raml.%s.model", getBasename()));
        apiPackage = codeModel._package(String.format("org.ops4j.raml.%s.api", getBasename()));
    }

    public abstract String getBasename();

    protected Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.setSourceFile(String.format("raml/%s.raml", getBasename()));
        config.setBasePackage(String.format("org.ops4j.raml.%s", getBasename()));
        config.setTargetDir(new File("target/generated/raml"));
        return config;
    }

    protected void assertClasses(String... classNames) {
        assertThat(modelPackage.classes()).extracting(JDefinedClass::name)
            .containsExactlyInAnyOrder(classNames);
    }

    protected void assertApiClasses(String... classNames) {
        assertThat(apiPackage.classes()).extracting(JDefinedClass::name)
            .containsExactlyInAnyOrder(classNames);
    }

    protected void assertApiMethods(String className, String... methodNames) {
        JDefinedClass klass = apiPackage._getClass(className);
        assertThat(klass.methods()).extracting(JMethod::name).containsExactlyInAnyOrder(methodNames);
    }

    protected JMethod findApiMethod(String className, String methodName) {
        JDefinedClass klass = apiPackage._getClass(className);
        return klass.methods().stream().filter(m -> m.name().equals(methodName)).findFirst().get();
    }

    protected void assertReturnType(JMethod method, String returnType) {
        assertThat(method.type().name()).isEqualTo(returnType);
    }

    protected void assertSignature(JMethod method, String... parameterTypes) {
        assertThat(method.listParamTypes()).hasSize(parameterTypes.length)
            .extracting(JType::name).containsExactly(parameterTypes);
    }

    protected void assertSimpleAnnotation(JMethod method, String annotation, String argument) {
        JAnnotationUse annotationUse = method.annotations().stream()
            .filter(a -> a.getAnnotationClass().name().equals(annotation)).findFirst().get();
        JAnnotationValue value = annotationUse.getAnnotationMembers().get("value");
        StringWriter writer = new StringWriter();
        JFormatter formatter = new JFormatter(writer);
        value.generate(formatter);
        assertThat(writer.toString()).isEqualTo(argument);
    }

    protected void assertNoArgAnnotation(JMethod method, Class<?> annotation) {
        JAnnotationUse annotationUse = method.annotations().stream()
            .filter(a -> a.getAnnotationClass().name().equals(annotation.getSimpleName())).findFirst().get();
        StringWriter writer = new StringWriter();
        JFormatter formatter = new JFormatter(writer);
        annotationUse.generate(formatter);
        assertThat(writer.toString()).isEqualTo("@" + annotation.getName());
    }

    protected void expectClass(String className) {
        klass = modelPackage._getClass(className);
        fieldNames = new HashSet<>(klass.fields().keySet());
        methodNames = klass.methods().stream().map(m -> m.name()).collect(toSet());
    }

    protected void expectClass(String className, String... typeParams) {
        expectClass(className);
        JTypeVar[] typeVars = klass.typeParams();
        assertThat(typeVars.length).isEqualTo(typeParams.length);
        for (int i = 0; i < typeVars.length; i++) {
            assertThat(typeVars[i].name()).isEqualTo(typeParams[i]);
        }
    }

    protected void expectBaseclass(String className) {
        JClass baseclass = klass._extends();
        assertThat(baseclass.name()).isEqualTo(className);
    }

    protected void verifyClass() {
        assertThat(fieldNames).isEmpty();
        assertThat(methodNames).isEmpty();
    }

    protected void assertProperty(JDefinedClass klass, String memberName, String typeName, String getterName, String setterName) {
        JFieldVar field = klass.fields().get(memberName);
        assertThat(field).isNotNull();
        assertThat(field.type().name()).isEqualTo(typeName);

        List<JMethod> getters = klass.methods().stream().filter(m -> m.name().equals(getterName)).collect(toList());
        assertThat(getters).hasSize(1);
        JMethod getter = getters.get(0);
        assertThat(getter.type().name()).isEqualTo(typeName);
        assertThat(getter.hasSignature(new JType[0])).isEqualTo(true);

        List<JMethod> setters = klass.methods().stream().filter(m -> m.name().equals(setterName)).collect(toList());
        assertThat(setters).hasSize(1);
        JMethod setter = setters.get(0);
        assertThat(setter.type()).isEqualTo(codeModel.VOID);
        assertThat(setter.hasSignature(new JType[]{field.type()})).isEqualTo(true);

        fieldNames.remove(memberName);
        methodNames.remove(getterName);
        methodNames.remove(setterName);
    }

    protected void assertDiscriminator(JDefinedClass klass, String memberName, String typeName, String getterName) {
        JFieldVar field = klass.fields().get(memberName);
        assertThat(field).isNotNull();
        assertThat(field.type().name()).isEqualTo(typeName);

        List<JMethod> getters = klass.methods().stream().filter(m -> m.name().equals(getterName)).collect(toList());
        assertThat(getters).hasSize(1);
        JMethod getter = getters.get(0);
        assertThat(getter.type().name()).isEqualTo(typeName);
        assertThat(getter.hasSignature(new JType[0])).isTrue();

        fieldNames.remove(memberName);
        methodNames.remove(getterName);
    }
}
