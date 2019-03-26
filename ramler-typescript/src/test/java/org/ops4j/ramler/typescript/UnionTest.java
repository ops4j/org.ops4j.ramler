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

public class UnionTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "union";
    }

    @Test
    public void shouldFindModules() {
        assertModules("city", "dog", "favourite");
    }

    @Test
    public void shouldFindCityInterface() {
        expectInterface("City");
        assertImports();
        assertProperty("name", "string");
        assertProperty("population", "number");
        verifyInterface();
    }

    @Test
    public void shouldFindDogInterface() {
        expectInterface("Dog");
        assertImports();
        assertProperty("name", "string");
        assertProperty("furColour", "string");
        verifyInterface();
    }

    @Test
    public void shouldFindFavouriteAlias() {
        expectTypeAlias("Favourite", "City", "Dog");
        assertImports("City", "Dog");
    }
}
