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
package org.ops4j.ramler.java;

import org.junit.jupiter.api.Test;

public class UnionTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "union";
    }

    @Test
    public void shouldFindModelClasses() {
        assertClasses("City", "Dog", "Favourite", "FavouriteDeserializer", "FavouriteSerializer");
    }

    @Test
    public void shouldFindCityMembers() {
        expectClass("City");
        assertProperty(klass, "name", "String", "getName", "setName");
        assertProperty(klass, "population", "int", "getPopulation", "setPopulation");
        verifyClass();
    }

    @Test
    public void shouldFindDogMembers() {
        expectClass("Dog");
        assertProperty(klass, "name", "String", "getName", "setName");
        assertProperty(klass, "furColour", "String", "getFurColour", "setFurColour");
        verifyClass();
    }

    @Test
    public void shouldFindFavouriteMembers() {
        expectClass("Favourite");
        assertField(klass, "value", "Object");
        assertMethod(klass, "value", "Object");
        assertVariant(klass, "city", "City", "isCity", "getCity", "setCity");
        assertVariant(klass, "dog", "Dog", "isDog", "getDog", "setDog");
        verifyClass();
    }

}
