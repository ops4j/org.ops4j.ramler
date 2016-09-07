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
package org.ops4j.ramler.generator;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Optional;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

public class ParserTest {

    @Test
    public void shouldParseRaml() {
        File input = new File("src/test/resources/raml/simpleobject.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();
        assertThat(api, is(notNullValue()));

        ApiTraverser traverser = new ApiTraverser();
        LoggingApiVisitor visitor = new LoggingApiVisitor();
        traverser.traverse(api, visitor);
    }

    @Test
    public void shouldParseObjectType() {
        File input = new File("src/test/resources/raml/simpleobject.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();
        TypeDeclaration type = api.types().stream().
                filter(t -> t.name().equals("User")).findFirst().get();
        assertThat(type, instanceOf(ObjectTypeDeclaration.class));
        ObjectTypeDeclaration userType = (ObjectTypeDeclaration) type;
        assertThat(userType.name(), is("User"));
        assertThat(userType.type(), is("object"));
        ObjectTypeDeclaration address = (ObjectTypeDeclaration) userType.properties().get(3);
        assertThat(address.name(), is("address"));
        assertThat(address.type(), is("Address"));
        
        TypeDeclaration favouriteColour = userType.properties().get(4);
        assertThat(favouriteColour.name(), is("favouriteColour"));
        assertThat(favouriteColour.required(), is(false));

        Resource resource = api.resources().get(0);
        assertThat(resource.relativeUri().value(), is("/user"));
        Method getMethod = resource.methods().get(0);
        assertThat(getMethod.method(), is("get"));
        StringTypeDeclaration sortParam = (StringTypeDeclaration) getMethod.queryParameters().get(1);
        assertThat(sortParam.name(), is("sort"));
        assertThat(sortParam.required(), is(false));
        
    }
    
    @Test
    public void shouldParseAnnotations() {
        File input = new File("src/test/resources/raml/generic.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();
        assertThat(api, is(notNullValue()));

        Optional<TypeDeclaration> optPerson = api.types().stream().filter(t -> t.name().equals("Person")).findFirst();
        assertThat(optPerson.isPresent(), is(true));
        ObjectTypeDeclaration person = (ObjectTypeDeclaration) optPerson.get();
        TypeDeclaration lastName = person.properties().get(1);
        assertThat(lastName.name(), is("lastName"));
        assertThat(lastName.annotations().size(), is(1));
        AnnotationRef notes = lastName.annotations().get(0);
        assertThat(notes.annotation().name(), is("notes"));
        TypeInstance sv = notes.structuredValue();
        assertThat(sv.isScalar(), is(false));
        TypeInstanceProperty tip = sv.properties().get(0);
        assertThat(tip.isArray(), is(true));
        assertThat(tip.values().size(), is(3));
        assertThat(tip.values().stream().map(v -> v.value()).collect(toList()), hasItems("N1", "N2", "N3"));
    }
}
