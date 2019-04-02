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
package org.ops4j.ramler.openapi;

import org.junit.jupiter.api.Test;

public class SimpleObjectTest extends AbstractOpenApiTest {

    @Override
    public String getBasename() {
        return "simpleobject";
    }

    @Test
    public void shouldFindSchemas() {
        assertSchemas("Address", "Age", "Colour", "FunnyNames", "Integers",
            "Name", "Numbers", "Reference", "Temporals", "User", "UserGroup");
    }

    @Test
    public void shouldFindAddressProperties() {
        expectSchema("Address");
        assertProperties("city", "street");
        assertRequiredProperties("city", "street");
    }

    @Test
    public void shouldFindIntegersProperties() {
        expectSchema("Integers");
        assertProperties("i8", "i16", "i32", "i64", "i", "l", "i8o", "i16o", "i32o", "i64o", "io",
            "lo");
        assertRequiredProperties("i8", "i16", "i32", "i64", "i", "l");
    }

    @Test
    public void shouldFindUserGroupProperties() {
        expectSchema("UserGroup");
        assertProperties("name", "users");
        assertRequiredProperties("name", "users");
        assertStringProperty("name");
        assertArrayPropertyRef("users", "User");
    }

    @Test
    public void shouldFindNumbersProperties() {
        expectSchema("Numbers");
        assertProperties("f", "fo", "d", "dbl");
        assertRequiredProperties("f", "d");
    }

    @Test
    public void shouldFindColourValues() {
        assertEnumValues("Colour", "lightBlue", "red", "yellow", "green");
    }
}
