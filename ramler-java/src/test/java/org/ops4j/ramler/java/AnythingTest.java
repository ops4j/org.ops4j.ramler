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

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.junit.jupiter.api.Test;

import com.sun.codemodel.JMethod;

public class AnythingTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "anything";
    }

    @Test
    public void shouldFindApiClasses() {
        assertApiClasses("AnythingResource");
    }

    @Test
    public void shouldFindApiMethods() {
        assertApiMethods("AnythingResource", "get", "getCsv", "post");
    }

    @Test
    public void shouldFindGetMethod() {
        JMethod get = findApiMethod("AnythingResource", "get");
        assertReturnType(get, "Map<String,Object>");
        assertSignature(get);
        assertNoArgAnnotation(get, GET.class);
    }

    @Test
    public void shouldFindGetCsvMethod() {
        JMethod getCsv = findApiMethod("AnythingResource", "getCsv");
        assertReturnType(getCsv, "String");
        assertSignature(getCsv);
        assertNoArgAnnotation(getCsv, GET.class);
        assertSimpleAnnotation(getCsv, "Produces", "\"text/csv\"");
    }

    @Test
    public void shouldFindPostMethod() {
        JMethod post = findApiMethod("AnythingResource", "post");
        assertReturnType(post, "void");
        assertSignature(post, "Map<String,Object>");
        assertNoArgAnnotation(post, POST.class);
    }
}
