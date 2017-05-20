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
package org.ops4j.ramler.html;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.json.JsonValue;

import org.junit.Test;
import org.ops4j.ramler.html.render.ExampleSpecJsonRenderer;
import org.ops4j.ramler.model.ApiModel;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;

public class ExampleSpecTest {

    private ApiModel apiModel;

    private void parse(String simpleName) {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi("raml/" + simpleName);
        assertThat(ramlModelResult.hasErrors()).isFalse();
        Api api = ramlModelResult.getApiV10();
        apiModel = new ApiModel(api);
    }

    private ExampleSpec getExample(TypeDeclaration type) {
        return type.examples().isEmpty() ? type.example() : type.examples().get(0);
    }

    @Test
    public void shouldParseObjectExample() {
        parse("simpleobject.raml");
        TypeDeclaration userGroup = apiModel.getDeclaredType("UserGroup");
        assertThat(userGroup).isNotNull();
        ExampleSpec exampleSpec = getExample(userGroup);
        TypeInstance instance = exampleSpec.structuredValue();
        assertThat(instance.isScalar()).isFalse();
        TypeInstanceProperty users = instance.properties().get(1);
        assertThat(users.isArray()).isTrue();
        TypeInstance user = users.values().get(0);
        assertThat(user.properties().get(0).name()).isEqualTo("firstname");
        assertThat(user.properties().get(0).value().value()).isEqualTo("Anna");

        ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();
        JsonValue jsonValue = renderer.toJsonValue(userGroup, exampleSpec);

        assertThat(jsonValue.toString()).
            isEqualTo("{\"name\":\"Editors\",\"users\":["
                + "{\"firstname\":\"Anna\",\"lastname\":\"Walter\",\"age\":32,"
                + "\"address\":{\"city\":\"Hamburg\",\"street\":\"Colonnaden\"},"
                + "\"registered\":true,\"dateOfBirth\":\"1985-04-30\","
                + "\"registrationDate\":\"2016-02-28T16:41:41.090Z\"}]}");
    }

    @Test
    public void shouldParseListExample() {
        parse("simpleobject.raml");
        TypeDeclaration nameList = apiModel.getDeclaredType("NameList");
        assertThat(nameList).isNotNull();
        ExampleSpec exampleSpec = getExample(nameList);
        List<TypeInstanceProperty> props = exampleSpec.structuredValue().properties();
        TypeInstanceProperty p0 = props.get(0);
        assertThat(p0.isArray()).isTrue();

        ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();
        JsonValue jsonValue = renderer.toJsonValue(nameList, exampleSpec);

        assertThat(jsonValue.toString()).isEqualTo("[\"foo\",\"bar\"]");
    }

    @Test
    public void shouldParseNumberExample() {
        parse("simpleobject.raml");
        TypeDeclaration age = apiModel.getDeclaredType("Age");
        assertThat(age).isNotNull();
        ExampleSpec exampleSpec = getExample(age);
        List<TypeInstanceProperty> props = exampleSpec.structuredValue().properties();
        TypeInstanceProperty p0 = props.get(0);
        assertThat(p0.isArray()).isFalse();
        assertThat(p0.name()).isEqualTo("value");
        assertThat(p0.value().isScalar()).isTrue();
        Object scalar = p0.value().value();
        assertThat(scalar).isEqualTo(37L);

        ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();
        JsonValue jsonValue = renderer.toJsonValue(age, exampleSpec);
        assertThat(jsonValue.toString()).isEqualTo("37");
    }
}
