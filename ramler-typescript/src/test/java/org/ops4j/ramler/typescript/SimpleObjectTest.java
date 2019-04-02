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

public class SimpleObjectTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "simpleobject";
    }

    @Test
    public void shouldFindModules() {
        assertModules("address", "age", "colour", "employee", "integers", "manager", "name",
            "numbers",
            "person", "persons", "temporals", "user-group", "user");
    }

    @Test
    public void shouldFindAddressInterface() {
        expectInterface("Address");
        assertImports();
        assertProperty("city", "string");
        assertProperty("street", "string");
        verifyInterface();
    }

    @Test
    public void shouldFindAgeAlias() {
        expectTypeAlias("Age", "number");
        assertImports();
    }

    @Test
    public void shouldFindColourEnum() {
        expectEnum("Colour");
        assertImports();
        assertEnumMember("LIGHT_BLUE", "'lightBlue'");
        assertEnumMember("RED", "'red'");
        assertEnumMember("YELLOW", "'yellow'");
        assertEnumMember("GREEN", "'green'");
        verifyEnum();
    }

    @Test
    public void shouldFindEmployeeInterface() {
        expectInterface("Employee", "Person");
        assertImports("Person");
        assertProperty("department", "string");
        verifyInterface();
    }

    @Test
    public void shouldFindIntegersInterface() {
        expectInterface("Integers");
        assertImports();
        assertProperty("i8", "number");
        assertProperty("i8o", "number");
        assertProperty("i16", "number");
        assertProperty("i16o", "number");
        assertProperty("i32", "number");
        assertProperty("i32o", "number");
        assertProperty("i64", "number");
        assertProperty("i64o", "number");
        assertProperty("i", "number");
        assertProperty("io", "number");
        assertProperty("l", "number");
        assertProperty("lo", "number");
        verifyInterface();
    }

    @Test
    public void shouldFindManagerInterface() {
        expectInterface("Manager", "Employee");
        assertImports("Employee");
        assertProperty("numEmployees", "number");
        verifyInterface();
    }

    @Test
    public void shouldFindNumbersInterface() {
        expectInterface("Numbers");
        assertImports();
        assertProperty("f", "number");
        assertProperty("fo", "number");
        assertProperty("d", "number");
        assertProperty("dbl", "number");
        verifyInterface();
    }

    @Test
    public void shouldFindPersonInterface() {
        expectInterface("Person");
        assertImports("Address", "Age");
        assertProperty("objectType", "string");
        assertProperty("firstname", "string");
        assertProperty("lastname", "string");
        assertProperty("address", "Address");
        assertProperty("age", "Age");
        verifyInterface();
    }

    @Test
    public void shouldFindPersonsAlias() {
        expectTypeAlias("Persons", "Person[]");
        assertImports("Person");
    }

    @Test
    public void shouldFindTemporalsInterface() {
        expectInterface("Temporals");
        assertImports();
        assertProperty("date", "string");
        assertProperty("to", "string");
        assertProperty("dto", "string");
        assertProperty("dt", "string");
        verifyInterface();
    }

    public void shouldFindUserGroupInterface() {
        expectInterface("UserGroup");
        assertImports("User");
        assertProperty("name", "string");
        assertProperty("users", "User[]");
        verifyInterface();
    }

    public void shouldFindUserInterface() {
        expectInterface("User");
        assertImports("Age", "Address", "Colour", "Name");
        assertProperty("firstname", "string");
        assertProperty("lastname", "Name");
        assertProperty("age", "Age");
        assertProperty("address", "Address");
        assertProperty("registered", "boolean");
        assertProperty("favouriteColour", "Colour");
        assertProperty("dateOfBirth", "string");
        assertProperty("registrationDate", "string");
        verifyInterface();
    }
}
