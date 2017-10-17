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

import org.junit.jupiter.api.Test;


public class NamesTest {

    @Test
    public void shouldBuildConstantName() {
        assertThat(Names.buildConstantName("oneTwoThree")).isEqualTo("ONE_TWO_THREE");
    }

    @Test
    public void shouldBuildVariableNameForJavaKeyword() {
        assertThat(Names.buildVariableName("static")).isEqualTo("$static");
    }

    @Test
    public void shouldBuildVariableNameForDottedProperty() {
        assertThat(Names.buildVariableName("customer.name")).isEqualTo("customerName");
    }
}
