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

import org.junit.Test;

public class MultipleInheritanceTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "multipleInheritance";
    }
    @Test
    public void shouldFindModelClasses() {
        assertClasses("A", "B", "C");
    }

    @Test
    public void shouldFindAMembers() {
        expectClass("A");
        assertProperty(klass, "a1", "String", "getA1", "setA1");
        assertProperty(klass, "a2", "String", "getA2", "setA2");
        assertDiscriminator(klass, "DISCRIMINATOR", "String", "getDisc");
        verifyClass();
    }

    @Test
    public void shouldFindBMembers() {
        expectClass("B");
        assertProperty(klass, "b1", "int", "getB1", "setB1");
        assertProperty(klass, "b2", "int", "getB2", "setB2");
        verifyClass();
    }

    @Test
    public void shouldFindCMembers() {
        expectClass("C");
        assertProperty(klass, "c1", "String", "getC1", "setC1");
        assertProperty(klass, "b1", "int", "getB1", "setB1");
        assertProperty(klass, "b2", "int", "getB2", "setB2");
        assertDiscriminator(klass, "DISCRIMINATOR", "String", "getDisc");
        verifyClass();
    }
}
