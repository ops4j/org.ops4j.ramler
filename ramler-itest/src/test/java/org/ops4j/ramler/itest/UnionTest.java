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
package org.ops4j.ramler.itest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.ops4j.ramler.itest.union.model.City;
import org.ops4j.ramler.itest.union.model.Dog;
import org.ops4j.ramler.itest.union.model.Favourite;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UnionTest {

    @Test
    public void shouldSerializeAndDeserializeCity() throws IOException {
        City city = new City();
        city.setName("Hamburg");
        city.setPopulation(1_800_000);
        Favourite favourite = new Favourite();
        favourite.setCity(city);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(favourite);
        assertThat(json).isEqualTo("{\"name\":\"Hamburg\",\"population\":1800000}");

        Favourite deserialized = mapper.readValue(json, Favourite.class);
        assertThat(deserialized.isCity()).isTrue();
        assertThat(deserialized.isDog()).isFalse();
        assertThat(deserialized.getCity().getName()).isEqualTo("Hamburg");
        assertThat(deserialized.getCity().getPopulation()).isEqualTo(1_800_000);
    }

    @Test
    public void shouldSerializeAndDeserializeDog() throws IOException {
        Dog dog = new Dog();
        dog.setName("Watson");
        dog.setFurColour("black");
        Favourite favourite = new Favourite();
        favourite.setDog(dog);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(favourite);
        assertThat(json).isEqualTo("{\"name\":\"Watson\",\"furColour\":\"black\"}");

        Favourite deserialized = mapper.readValue(json, Favourite.class);
        assertThat(deserialized.isCity()).isFalse();
        assertThat(deserialized.isDog()).isTrue();
        assertThat(deserialized.getDog().getName()).isEqualTo("Watson");
        assertThat(deserialized.getDog().getFurColour()).isEqualTo("black");
    }
}
