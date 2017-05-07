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
package org.ops4j.ramler.generator;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JStringLiteral;

public class EnumTest {

    private static Generator generator;
    private static JCodeModel codeModel;
    private static JPackage modelPackage;
    private JDefinedClass klass;

    @BeforeClass
    public static void shouldGenerateArrays() {
        Configuration config = new Configuration();
        config.setSourceFile("raml/enum.raml");
        config.setBasePackage("org.ops4j.raml.enums");
        config.setTargetDir(new File("target/generated/raml"));

        generator = new Generator(config);
        generator.generate();

        codeModel = generator.getContext().getCodeModel();
        modelPackage = codeModel._package("org.ops4j.raml.enums.model");
    }

    @Test
    public void shouldFindModelClasses() {
        Set<String> classNames = new HashSet<>();
        modelPackage.classes().forEachRemaining(c -> classNames.add(c.name()));
        assertThat(classNames, contains("Colour"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindEnumValues() throws IllegalAccessException {
        klass = modelPackage._getClass("Colour");
        assertThat(klass.getClassType(), is(ClassType.ENUM));
        Map<String,JEnumConstant> enums = (Map<String, JEnumConstant>) FieldUtils.readField(klass, "enumConstantsByName", true);
        assertThat(enums.keySet(), contains("LIGHT_BLUE", "RED"));

        JEnumConstant lightBlue = enums.get("LIGHT_BLUE");
        assertThat(lightBlue.javadoc().get(0), is("Colour of the sky"));

        List<JExpression> args = (List<JExpression>) FieldUtils.readField(lightBlue, "args", true);
        assertThat(args, hasSize(1));

        assertThat(args.get(0), instanceOf(JStringLiteral.class));

        String literal = (String) FieldUtils.readField(args.get(0), "str", true);
        assertThat(literal, is("lightBlue"));
    }
}
