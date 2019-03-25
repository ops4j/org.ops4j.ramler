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
package org.ops4j.ramler.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonUnionTest {

    public static class City {
        public String name;
        public int population;
    }

    public static class Dog {
        public String name;
        public String furColour;
    }

    @JsonDeserialize(using = FavouriteDeserializer.class)
    @JsonSerialize(using = FavouriteSerializer.class)
    public static class Favourite {

        private Object value;

        public boolean isCity() {
            return value instanceof City;
        }

        public boolean isDog() {
            return value instanceof Dog;
        }

        public City getCity() {
            return (City) value;
        }

        public void setCity(City city) {
            this.value = city;
        }

        public Dog getDog() {
            return (Dog) value;
        }

        public void setDog(Dog dog) {
            this.value = dog;
        }

        public Object value() {
            return value;
        }
    }

    public static class FavouriteSerializer extends StdSerializer<Favourite> {

        private static final long serialVersionUID = 1L;

        public FavouriteSerializer() {
            super(Favourite.class);
        }

        @Override
        public void serialize(Favourite value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
            gen.writeObject(value.value());
        }
    }

    public static class FavouriteDeserializer extends StdDeserializer<Favourite> {

        private static final long serialVersionUID = 1L;

        public FavouriteDeserializer() {
            super(Favourite.class);
        }

        @Override
        public Favourite deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
            ObjectMapper mapper  = (ObjectMapper) p.getCodec();

            JsonNode node = mapper.readTree(p);

            Favourite favourite = new Favourite();
            if (looksLikeCity(node)) {
                favourite.value = mapper.convertValue(node, City.class);
                return favourite;
            }

            if (looksLikeDog(node)) {
                favourite.value = mapper.convertValue(node, Dog.class);
                return favourite;
            }
            throw new IOException("Cannot determine type of object" + node);
        }

        private boolean looksLikeCity(JsonNode node) {
            return node.has("population");
        }

        private boolean looksLikeDog(JsonNode node) {
            return node.has("furColour");
        }
    }

    @JsonDeserialize(using = StringOrNumberDeserializer.class)
    @JsonSerialize(using = StringOrNumberSerializer.class)
    public static class StringOrNumber {

        private Object value;

        public boolean isString() {
            return value instanceof String;
        }

        public boolean isNumber() {
            return value instanceof Integer;
        }

        public String getString() {
            return (String) value;
        }

        public void setString(String string) {
            this.value = string;
        }

        public Integer getNumber() {
            return (Integer) value;
        }

        public void setNumber(Integer number) {
            this.value = number;
        }
    }

    public static class Container {
        public StringOrNumber content;
    }

    public static class StringOrNumberSerializer extends StdSerializer<StringOrNumber> {


        private static final long serialVersionUID = 1L;

        public StringOrNumberSerializer() {
            super(StringOrNumber.class);
        }

        @Override
        public void serialize(StringOrNumber value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
            gen.writeObject(value.value);
        }
    }

    public static class StringOrNumberDeserializer extends StdDeserializer<StringOrNumber> {

        private static final long serialVersionUID = 1L;

        public StringOrNumberDeserializer() {
            super(StringOrNumber.class);
        }

        @Override
        public StringOrNumber deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            JsonNode node = mapper.readTree(p);

            StringOrNumber stringOrNumber = new StringOrNumber();
            if (looksLikeString(node)) {
                stringOrNumber.value = node.asText();
                return stringOrNumber;
            }

            if (looksLikeNumber(node)) {
                stringOrNumber.value = node.asInt();
                return stringOrNumber;
            }
            throw new IOException("Cannot determine type of object" + node);
        }

        private boolean looksLikeString(JsonNode node) {
            return node.isTextual();
        }

        private boolean looksLikeNumber(JsonNode node) {
            return node.isInt();
        }
    }



    @Test
    public void shouldSerializeAndDeserializeCity() throws IOException {
        City city = new City();
        city.name = "Hamburg";
        city.population = 1_800_000;
        Favourite favourite = new Favourite();
        favourite.setCity(city);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(favourite);
        assertThat(json).isEqualTo("{\"name\":\"Hamburg\",\"population\":1800000}");

        Favourite deserialized = mapper.readValue(json, Favourite.class);
        assertThat(deserialized.isCity()).isTrue();
        assertThat(deserialized.isDog()).isFalse();
        assertThat(deserialized.getCity().name).isEqualTo("Hamburg");
        assertThat(deserialized.getCity().population).isEqualTo(1_800_000);
    }

    @Test
    public void shouldSerializeAndDeserializeDog() throws IOException {
        Dog dog = new Dog();
        dog.name = "Watson";
        dog.furColour = "black";
        Favourite favourite = new Favourite();
        favourite.setDog(dog);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(favourite);
        assertThat(json).isEqualTo("{\"name\":\"Watson\",\"furColour\":\"black\"}");

        Favourite deserialized = mapper.readValue(json, Favourite.class);
        assertThat(deserialized.isCity()).isFalse();
        assertThat(deserialized.isDog()).isTrue();
        assertThat(deserialized.getDog().name).isEqualTo("Watson");
        assertThat(deserialized.getDog().furColour).isEqualTo("black");
    }

    @Test
    public void shouldSerializeAndDeserializeStringInContainer() throws IOException {
        StringOrNumber stringOrNumber = new StringOrNumber();
        stringOrNumber.value = "something";

        Container container = new Container();
        container.content = stringOrNumber;
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(container);
        assertThat(json).isEqualTo("{\"content\":\"something\"}");

        Container deserialized = mapper.readValue(json, Container.class);
        assertThat(deserialized.content.isString()).isTrue();
        assertThat(deserialized.content.isNumber()).isFalse();
        assertThat(deserialized.content.value).isEqualTo("something");
    }

    @Test
    public void shouldSerializeAndDeserializeNumberInContainer() throws IOException {
        StringOrNumber stringOrNumber = new StringOrNumber();
        stringOrNumber.value = 123;

        Container container = new Container();
        container.content = stringOrNumber;
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(container);
        assertThat(json).isEqualTo("{\"content\":123}");

        Container deserialized = mapper.readValue(json, Container.class);
        assertThat(deserialized.content.isString()).isFalse();
        assertThat(deserialized.content.isNumber()).isTrue();
        assertThat(deserialized.content.value).isEqualTo(123);
    }

    @Test
    public void shouldSerializeAndDeserializeString() throws IOException {
        StringOrNumber stringOrNumber = new StringOrNumber();
        stringOrNumber.value = "something";

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(stringOrNumber);
        assertThat(json).isEqualTo("\"something\"");

        StringOrNumber deserialized = mapper.readValue(json, StringOrNumber.class);
        assertThat(deserialized.isString()).isTrue();
        assertThat(deserialized.isNumber()).isFalse();
        assertThat(deserialized.value).isEqualTo("something");
    }

    @Test
    public void shouldSerializeAndDeserializeNumber() throws IOException {
        StringOrNumber stringOrNumber = new StringOrNumber();
        stringOrNumber.value = 123;

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(stringOrNumber);
        assertThat(json).isEqualTo("123");

        StringOrNumber deserialized = mapper.readValue(json, StringOrNumber.class);
        assertThat(deserialized.isString()).isFalse();
        assertThat(deserialized.isNumber()).isTrue();
        assertThat(deserialized.value).isEqualTo(123);
    }
}
