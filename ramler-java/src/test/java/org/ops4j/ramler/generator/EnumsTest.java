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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStringLiteral;

public class EnumsTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "enums";
    }

    @Test
    public void shouldFindModelClasses() {
        assertClasses("Colour");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindEnumValues() throws IllegalAccessException {
        klass = modelPackage._getClass("Colour");
        assertThat(klass.getClassType()).isEqualTo(ClassType.ENUM);
        Map<String,JEnumConstant> enums = (Map<String, JEnumConstant>) FieldUtils.readField(klass, "enumConstantsByName", true);
        assertThat(enums.keySet()).contains("LIGHT_BLUE", "RED");

        JEnumConstant lightBlue = enums.get("LIGHT_BLUE");
        assertThat(lightBlue.javadoc().get(0)).isEqualTo("Colour of the sky");

        List<JExpression> args = (List<JExpression>) FieldUtils.readField(lightBlue, "args", true);
        assertThat(args).hasSize(1);

        assertThat(args.get(0)).isInstanceOf(JStringLiteral.class);

        String literal = (String) FieldUtils.readField(args.get(0), "str", true);
        assertThat(literal).isEqualTo("lightBlue");
    }
}
