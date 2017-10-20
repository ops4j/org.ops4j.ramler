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
package org.ops4j.ramler.typescript;

import org.junit.jupiter.api.Test;

public class GenericTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "generic";
    }

    @Test
    public void shouldFindModules() {
        assertModules("animal-response", "animal", "list-result", "person", "response", "result", "status");
    }

    @Test
    public void shouldFindAnimalInterface() {
        expectInterface("Animal");
        assertImports();
        assertProperty("species", "string");
        assertProperty("numLegs", "number");
        verifyInterface();
    }

    @Test
    public void shouldFindAnimalResponseInterface() {
        expectInterface("AnimalResponse", "Response<Animal>");
        assertImports("Animal", "Response");
        verifyInterface();
    }

    @Test
    public void shouldFindListResultInterface() {
        expectInterface("ListResult");
        assertImports();
        assertProperty("result", "T[]");
        verifyInterface();
    }

    @Test
    public void shouldFindResponseInterface() {
        expectInterface("Response");
        assertImports("Result", "Status");
        assertProperty("data", "Result<T>");
        assertProperty("status", "Status");
        assertProperty("success", "boolean");
        verifyInterface();
    }

    @Test
    public void shouldFindResultInterface() {
        expectInterface("Result");
        assertImports();
        assertProperty("result", "T");
        verifyInterface();
    }

    @Test
    public void shouldFindStatusInterface() {
        expectInterface("Status");
        assertImports();
        assertProperty("code", "number");
        assertProperty("httpStatus", "number");
        assertProperty("requestId", "string");
        assertProperty("text", "string");
        assertProperty("success", "boolean");
        verifyInterface();
    }

}
