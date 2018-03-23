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

import org.junit.jupiter.api.Test;

public class ParameterizedTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "parameterized";
    }

    @Test
    public void shouldFindModelClasses() {
        assertClasses("Animal", "AnimalResponse", "IntegerResponse", "ListResult", "Person",
            "Response", "Result", "Status", "StringResponse");
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
}
