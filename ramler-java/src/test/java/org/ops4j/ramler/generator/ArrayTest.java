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

import org.junit.Test;

public class ArrayTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "array";
    }

    @Test
    public void shouldFindModelClasses() {
        assertClasses("BooleanList", "DigitList", "NameList", "ObjectList", "Person", "PersonList",
            "StringList");
    }

    @Test
    public void shouldFindBooleanListMembers() {
        expectClass("BooleanList");
        assertProperty(klass, "list", "List<Boolean>", "getList", "setList");
        verifyClass();
    }

    @Test
    public void shouldFindDigitListMembers() {
        expectClass("DigitList");
        assertProperty(klass, "list", "List<Integer>", "getList", "setList");
        verifyClass();
    }

    @Test
    public void shouldFindNameListMembers() {
        expectClass("NameList");
        assertProperty(klass, "list", "List<String>", "getList", "setList");
        verifyClass();
    }

    @Test
    public void shouldFindObjectListMembers() {
        expectClass("ObjectList");
        assertProperty(klass, "list", "List<Map<String,Object>>", "getList", "setList");
        verifyClass();
    }

    @Test
    public void shouldFindPersonListMembers() {
        expectClass("PersonList");
        assertProperty(klass, "list", "List<Person>", "getList", "setList");
        verifyClass();
    }

    @Test
    public void shouldFindStringListMembers() {
        expectClass("StringList");
        assertProperty(klass, "list", "List<String>", "getList", "setList");
        verifyClass();
    }
}
