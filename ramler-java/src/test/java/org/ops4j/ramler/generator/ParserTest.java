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
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.ops4j.ramler.model.Metatype.ARRAY;
import static org.ops4j.ramler.model.Metatype.BOOLEAN;
import static org.ops4j.ramler.model.Metatype.INTEGER;
import static org.ops4j.ramler.model.Metatype.OBJECT;
import static org.ops4j.ramler.model.Metatype.STRING;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.ops4j.ramler.model.ApiModel;
import org.ops4j.ramler.model.Metatype;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

public class ParserTest {

    private ApiModel apiModel;
    
    private void parse(String simpleName) {
        File input = new File("src/test/resources/raml", simpleName);
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();
        apiModel = new ApiModel(api);        
    }

    @Test
    public void shouldParseRaml() {
        parse("simpleobject.raml");

        ApiTraverser traverser = new ApiTraverser();
        LoggingApiVisitor visitor = new LoggingApiVisitor();
        traverser.traverse(apiModel.api(), visitor);
    }

    @Test
    public void shouldParseObjectType() {
        parse("simpleobject.raml");

        TypeDeclaration type = apiModel.getDeclaredType("User");
        assertThat(type, instanceOf(ObjectTypeDeclaration.class));
        ObjectTypeDeclaration userType = (ObjectTypeDeclaration) type;
        assertThat(userType.name(), is("User"));
        assertThat(userType.type(), is("object"));
        ObjectTypeDeclaration address = (ObjectTypeDeclaration) userType.properties().get(3);
        assertThat(address.name(), is("address"));
        assertThat(address.type(), is("Address"));
        
        List<TypeDeclaration> props = userType.properties();
        assertMemberType(props.get(0), "firstname", "string", "string");
        assertMemberType(props.get(1), "lastname", "Name", "string");
        assertMemberType(props.get(2), "age", "Age", "integer");
        assertMemberType(props.get(3), "address", "Address", "object");
        assertMemberType(props.get(4), "favouriteColour", "Colour", "string");
        assertMemberType(props.get(5), "registered", "boolean", "boolean");
        assertMemberType(props.get(6), "dateOfBirth", "date-only", "date-only");
        assertMemberType(props.get(7), "registrationDate", "datetime", "datetime");
        
        TypeDeclaration favouriteColour = userType.properties().get(4);
        assertThat(favouriteColour.name(), is("favouriteColour"));
        assertThat(favouriteColour.required(), is(false));

        Resource resource = apiModel.api().resources().get(0);
        assertThat(resource.relativeUri().value(), is("/user"));
        Method getMethod = resource.methods().get(0);
        assertThat(getMethod.method(), is("get"));
        StringTypeDeclaration sortParam = (StringTypeDeclaration) getMethod.queryParameters().get(1);
        assertThat(sortParam.name(), is("sort"));
        assertThat(sortParam.required(), is(false));
        
    }
    
    private void assertMemberType(TypeDeclaration type, String memberName, String typeName, String baseType) {
        assertThat(type.name(), is(memberName));
        assertThat(type.type(), is(typeName));
        assertThat(apiModel.getBaseType(type), is(baseType));
    }
    
    @Test
    public void shouldParseAnnotations() {
        parse("generic.raml");

        ObjectTypeDeclaration person = (ObjectTypeDeclaration) apiModel.getDeclaredType("Person");
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

    @Test
    public void shouldParseObject() {
        parse("bracketArray.raml");
        Api api = apiModel.api();
        assertMemberTypes(api.types().get(0), "ObjectList", "list", "object[]", ARRAY);
        assertMemberTypes(api.types().get(1), "NameList", "list", "Name[]", ARRAY);
        assertMemberTypes(api.types().get(2), "PersonList", "list", "Person[]", ARRAY);
        assertMemberTypes(api.types().get(3), "StringList", "list", "string[]", ARRAY);
        assertMemberTypes(api.types().get(4), "BooleanList", "list", "BooleanArray", ARRAY);
        assertMemberTypes(api.types().get(5), "DigitList", "list", "DigitArray", ARRAY);
    }
    
    private void assertMemberTypes(TypeDeclaration type, String typeName, String memberName, String memberType, Metatype metatype) {
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.name(), is(typeName));
        TypeDeclaration member = objectType.properties().get(0);
        assertThat(member, instanceOf(ArrayTypeDeclaration.class));
        assertThat(member.name(), is(memberName));
        assertThat(member.type(), is(memberType));        
        assertThat(apiModel.metatype(member), is(metatype));
    }
    
    
    
    @Test
    public void shouldParseArrays() {
        parse("bracketArray.raml");
        Api api = apiModel.api();
        
        assertTypes(api.types().get(0), "ObjectList", "list", "object[]", "object", OBJECT);
        assertTypes(api.types().get(1), "NameList", "Name", "string", "Name", STRING);
        assertTypes(api.types().get(2), "PersonList", "Person", "object", "Person", OBJECT);
        assertTypes(api.types().get(3), "StringList", "list", "string[]", "string", STRING);
        assertTypes(api.types().get(4), "BooleanList", "BooleanArray", "boolean[]", "BooleanArray", BOOLEAN);
        assertTypes(api.types().get(5), "DigitList", "Digit", "integer", "Digit", INTEGER);
    }
    
    private void assertTypes(TypeDeclaration type, String typeName, String itemName, String itemType, String realItemType, Metatype metatype) {
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.name(), is(typeName));
        TypeDeclaration member = objectType.properties().get(0);
        assertThat(member, instanceOf(ArrayTypeDeclaration.class));
        ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) member;
        TypeDeclaration item = arrayType.items();
        assertThat(item.name(), is(itemName));
        assertThat(item.type(), is(itemType));        
        assertThat(apiModel.getItemType(member), is(realItemType));
        assertThat(apiModel.metatype(item), is(metatype));
    }
    
    @Test
    public void shouldFindItemTypeWithBrackets() {
        parse("bracketArray.raml");
        Api api = apiModel.api();
        
        assertTypes(api.types().get(0), "ObjectList",   "list",         "object[]");
        assertTypes(api.types().get(1), "NameList",     "Name",         "string");
        assertTypes(api.types().get(2), "PersonList",   "Person",       "object");
        assertTypes(api.types().get(3), "StringList",   "list",         "string[]");
        assertTypes(api.types().get(4), "BooleanList",  "BooleanArray", "boolean[]");
        assertTypes(api.types().get(5), "DigitList",    "Digit",        "integer");
    }
    
    private void assertTypes(TypeDeclaration type, String typeName, String itemName, String itemType) {
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.name(), is(typeName));
        TypeDeclaration member = objectType.properties().get(0);
        assertThat(member, instanceOf(ArrayTypeDeclaration.class));
        ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) member;
        TypeDeclaration item = arrayType.items();
        assertThat(item.name(), is(itemName));
        assertThat(item.type(), is(itemType));        
    }
    
    
    @Test
    public void shouldFindItemType() {
        parse("array.raml");
        Api api = apiModel.api();
        
        assertTypes(api.types().get(0), "ObjectList",   "items",         "object");
        assertTypes(api.types().get(1), "NameList",     "items",         "Name");
        assertTypes(api.types().get(2), "PersonList",   "items",         "Person");
        assertTypes(api.types().get(3), "StringList",   "items",         "string");
        assertTypes(api.types().get(4), "BooleanList",  "items",         "boolean");
        assertTypes(api.types().get(5), "DigitList",    "items",         "Digit");
    }   
}
