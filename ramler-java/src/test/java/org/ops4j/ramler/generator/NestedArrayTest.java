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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class NestedArrayTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "nestedArray";
    }

    @Test
    public void shouldFindModelClasses() {
        Set<String> classNames = new HashSet<>();
        modelPackage.classes().forEachRemaining(c -> classNames.add(c.name()));
        assertThat(classNames, containsInAnyOrder("PersonArrayList", "Person", "PersonList"));
    }

    @Test
    public void shouldFindPersonListMembers() {
        expectClass("PersonList");
        assertProperty(klass, "list", "List<Person>", "getList", "setList");
        verifyClass();
    }

    @Test
    public void shouldFindPersonArrayListMembers() {
        expectClass("PersonArrayList");
        assertProperty(klass, "list", "List<List<Person>>", "getList", "setList");
        verifyClass();
    }
}
