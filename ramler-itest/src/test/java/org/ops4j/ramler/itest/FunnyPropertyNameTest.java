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
package org.ops4j.ramler.itest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.ops4j.ramler.itest.inheritance.model.FunnyNames;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FunnyPropertyNameTest {

    @Test
    public void shouldMarshalFunnyNames() throws IOException {
        FunnyNames funnyNames = new FunnyNames();
        funnyNames.setCustomerName("Donald Duck");
        funnyNames.setInterface(-1);
        funnyNames.setSomeOtherName("Voldemort");
        funnyNames.setStatic(true);

        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.writer()
            .writeValue(sw, funnyNames);
        String json = sw.toString();

        assertThat(json).isEqualTo(
            "{\"customer.name\":\"Donald Duck\",\"interface\":-1,\"rawName\":\"Voldemort\",\"static\":true}");
    }

    @Test
    public void shouldUnmarshalFunnyNames() throws IOException {
        String json = "{\"static\":true,\"interface\":-1,\"customer.name\":\"Donald Duck\",\"rawName\":\"Voldemort\"}";
        ObjectMapper mapper = new ObjectMapper();
        FunnyNames funnyNames = mapper.readValue(json, FunnyNames.class);
        assertThat(funnyNames.getCustomerName()).isEqualTo("Donald Duck");
        assertThat(funnyNames.getInterface()).isEqualTo(-1);
        assertThat(funnyNames.getSomeOtherName()).isEqualTo("Voldemort");
        assertThat(funnyNames.isStatic()).isTrue();
    }
}
