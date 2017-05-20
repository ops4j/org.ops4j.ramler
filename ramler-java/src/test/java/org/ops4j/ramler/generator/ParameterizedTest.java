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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;

public class ParameterizedTest {

    private static Generator generator;
    private static JCodeModel codeModel;
    private static JPackage modelPackage;
    private JDefinedClass klass;
    private Set<String> methodNames;
    private Set<String> fieldNames;

    @BeforeClass
    public static void shouldGenerateArrays() {
        String input = "raml/parameterized.raml";

        Configuration config = new Configuration();
        config.setSourceFile(input);
        config.setBasePackage("org.ops4j.raml.generic");
        config.setTargetDir(new File("target/generated/raml"));

        generator = new Generator(config);
        generator.generate();

        codeModel = generator.getContext().getCodeModel();
        modelPackage = codeModel._package("org.ops4j.raml.generic.model");
    }

    @Test
    public void shouldFindModelClasses() {
        Set<String> classNames = new HashSet<>();
        modelPackage.classes().forEachRemaining(c -> classNames.add(c.name()));
        assertThat(classNames, containsInAnyOrder("Animal", "AnimalResponse", "IntegerResponse", "ListResult", "Person",
            "Response", "Result", "Status", "StringResponse"));
    }

    @Test
    public void shouldFindResultMembers() {
        expectClass("Result", "T");
        assertProperty(klass, "result", "T", "getResult", "setResult");
        verifyClass();
    }

    @Test
    public void shouldFindResponseMembers() {
        expectClass("Response", "T");
        assertProperty(klass, "data", "Result<T>", "getData", "setData");
        assertProperty(klass, "status", "Status", "getStatus", "setStatus");
        assertProperty(klass, "success", "boolean", "isSuccess", "setSuccess");
        verifyClass();
    }

    @Test
    public void shouldFindAnimalResponseMembers() {
        expectClass("AnimalResponse");
        expectBaseclass("Response<Animal>");
        verifyClass();
    }

    private void expectClass(String className) {
        klass = modelPackage._getClass(className);
        fieldNames = new HashSet<>(klass.fields().keySet());
        methodNames = klass.methods().stream().map(m -> m.name()).collect(toSet());
    }

    private void expectClass(String className, String... typeParams) {
        expectClass(className);
        JTypeVar[] typeVars = klass.typeParams();
        assertThat(typeVars.length, is(typeParams.length));
        for (int i = 0; i < typeVars.length; i++) {
            assertThat(typeVars[i].name(), is(typeParams[i]));
        }
    }

    private void expectBaseclass(String className) {
        JClass baseclass = klass._extends();
        assertThat(baseclass.name(), is(className));
    }

    private void verifyClass() {
        assertThat(fieldNames, is(empty()));
        assertThat(methodNames, is(empty()));
    }

    private void assertProperty(JDefinedClass klass, String memberName, String typeName, String getterName, String setterName) {
        JFieldVar field = klass.fields().get(memberName);
        assertThat(field, is(notNullValue()));
        assertThat(field.type().name(), is(typeName));

        List<JMethod> getters = klass.methods().stream().filter(m -> m.name().equals(getterName)).collect(toList());
        assertThat(getters, hasSize(1));
        JMethod getter = getters.get(0);
        assertThat(getter.type().name(), is(typeName));
        assertThat(getter.hasSignature(new JType[0]), is(true));

        List<JMethod> setters = klass.methods().stream().filter(m -> m.name().equals(setterName)).collect(toList());
        assertThat(setters, hasSize(1));
        JMethod setter = setters.get(0);
        assertThat(setter.type(), is(codeModel.VOID));
        assertThat(setter.hasSignature(new JType[]{field.type()}), is(true));

        fieldNames.remove(memberName);
        methodNames.remove(getterName);
        methodNames.remove(setterName);
    }
}
