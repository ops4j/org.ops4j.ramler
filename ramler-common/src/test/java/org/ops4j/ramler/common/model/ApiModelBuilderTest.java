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
package org.ops4j.ramler.common.model;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.ops4j.ramler.common.exc.ParserException;

public class ApiModelBuilderTest {

    @Test
    public void shouldLogParserErrors() {
        assertThatExceptionOfType(ParserException.class)
            .isThrownBy(() -> new ApiModelBuilder().buildApiModel("raml/syntaxError.raml"))
            .withMessageContaining("RAML syntax errors")
            .withMessageContaining("/types/City/properties/population/type")
            .withMessageContaining("Invalid reference 'int'");
    }
}
