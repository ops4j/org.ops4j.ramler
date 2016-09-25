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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.json.JsonValue;

import org.junit.Test;
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
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();
        apiModel = new ApiModel(api);        
    }

    private ExampleSpec getExample(TypeDeclaration type) {
        return type.examples().isEmpty() ? type.example() : type.examples().get(0);
    }
    

    @Test
    public void shouldParseObjectExample() throws IOException {
        parse("simpleobject.raml");
        TypeDeclaration userGroup = apiModel.getDeclaredType("UserGroup");
        assertThat(userGroup, is(notNullValue()));
        ExampleSpec exampleSpec = getExample(userGroup);
        TypeInstance instance = exampleSpec.structuredValue();
        assertThat(instance.isScalar(), is(false));
        TypeInstanceProperty users = instance.properties().get(1);
        assertThat(users.isArray(), is(true));
        TypeInstance user = users.values().get(0);
        assertThat(user.properties().get(0).name(), is("firstname"));
        assertThat(user.properties().get(0).value().value(), is("Anna"));

        ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();
        JsonValue jsonValue = renderer.toJsonValue(userGroup, exampleSpec);
        
        System.out.println(jsonValue);
    }
    
    @Test
    public void shouldParseListExample() throws IOException {
        parse("simpleobject.raml");
        TypeDeclaration nameList = apiModel.getDeclaredType("NameList");
        assertThat(nameList, is(notNullValue()));
        ExampleSpec exampleSpec = getExample(nameList);
        List<TypeInstanceProperty> props = exampleSpec.structuredValue().properties();
        TypeInstanceProperty p0 = props.get(0);
        assertThat(p0.isArray(), is(true));
        //assertThat(p0.name(), is("value"));
        
        ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();
        JsonValue jsonValue = renderer.toJsonValue(nameList, exampleSpec);
        
        System.out.println(jsonValue);
    }
    
    @Test
    public void shouldParseNumberExample() throws IOException {
        parse("simpleobject.raml");
        TypeDeclaration age = apiModel.getDeclaredType("Age");
        assertThat(age, is(notNullValue()));
        ExampleSpec exampleSpec = getExample(age);
        List<TypeInstanceProperty> props = exampleSpec.structuredValue().properties();
        TypeInstanceProperty p0 = props.get(0);
        assertThat(p0.isArray(), is(false));
        assertThat(p0.name(), is("value"));
        assertThat(p0.value().isScalar(), is(true));
        Object scalar = p0.value().value();
        assertThat(scalar, is(37));
        ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();
        JsonValue jsonValue = renderer.toJsonValue(age, exampleSpec);
        
        System.out.println(jsonValue);
    }
    
}
