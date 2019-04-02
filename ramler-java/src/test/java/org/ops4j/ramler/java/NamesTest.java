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
package org.ops4j.ramler.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class NamesTest {

    @ParameterizedTest
    @MethodSource
    public void shouldBuildConstantName(String ramlName, String javaName) {
        assertThat(Names.buildConstantName(ramlName)).isEqualTo(javaName);
    }

    static Stream<Arguments> shouldBuildConstantName() {
        return Stream.of(
            arguments("oneTwoThree", "ONE_TWO_THREE"),
            arguments("one-two-three", "ONE_TWO_THREE"),
            arguments("one two three", "ONE_TWO_THREE"),
            arguments("1twoThree", "_1TWO_THREE"));
    }

    @ParameterizedTest
    @MethodSource
    public void shouldBuildVariableNameForJavaKeyword(String ramlName, String javaName) {
        assertThat(Names.buildVariableName(ramlName)).isEqualTo(javaName);
    }

    static Stream<Arguments> shouldBuildVariableNameForJavaKeyword() {
        return Stream.of(
            arguments("static", "$static"),
            arguments("class", "$class"),
            arguments("int", "$int"),
            arguments("Static", "$static"));
    }

    @ParameterizedTest
    @MethodSource
    public void shouldBuildVariableNameForDottedProperty(String ramlName, String javaName) {
        assertThat(Names.buildVariableName(ramlName)).isEqualTo(javaName);
    }

    static Stream<Arguments> shouldBuildVariableNameForDottedProperty() {
        return Stream.of(
            arguments("customer.name", "customerName"),
            arguments("one.two.three", "oneTwoThree"),
            arguments(".net", "net"),
            arguments("double..dot", "doubleDot"));
    }
}
