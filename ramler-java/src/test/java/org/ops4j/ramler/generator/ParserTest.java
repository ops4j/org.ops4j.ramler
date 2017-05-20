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

import static org.assertj.core.api.Assertions.assertThat;
import static org.ops4j.ramler.model.Metatype.ARRAY;
import static org.ops4j.ramler.model.Metatype.BOOLEAN;
import static org.ops4j.ramler.model.Metatype.INTEGER;
import static org.ops4j.ramler.model.Metatype.OBJECT;
import static org.ops4j.ramler.model.Metatype.STRING;

import java.util.List;

import org.junit.Test;
import org.ops4j.ramler.model.ApiModel;
import org.ops4j.ramler.model.EnumValue;
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
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi("raml/" + simpleName);
        if (ramlModelResult.hasErrors()) {
            System.out.println(ramlModelResult.getValidationResults());
        }
        assertThat(ramlModelResult.hasErrors()).isFalse();
        Api api = ramlModelResult.getApiV10();
        apiModel = new ApiModel(api);
    }

    @Test
    public void shouldParseRaml() {
        parse("simpleobject.raml");

        ApiTraverser traverser = new ApiTraverser();
        LoggingApiVisitor visitor = new LoggingApiVisitor();
        traverser.traverse(apiModel.getApi(), visitor);
    }

    @Test
    public void shouldParseObjectType() {
        parse("simpleobject.raml");

        TypeDeclaration type = apiModel.getDeclaredType("User");
        assertThat(type).isInstanceOf(ObjectTypeDeclaration.class);
        ObjectTypeDeclaration userType = (ObjectTypeDeclaration) type;
        assertThat(userType.name()).isEqualTo("User");
        assertThat(userType.type()).isEqualTo("object");
        ObjectTypeDeclaration address = (ObjectTypeDeclaration) userType.properties().get(3);
        assertThat(address.name()).isEqualTo("address");
        assertThat(address.type()).isEqualTo("Address");

        List<TypeDeclaration> props = userType.properties();
        assertMemberType(props.get(0), "firstname", "string", "string");
        assertMemberType(props.get(1), "lastname", "Name", "string");
        assertMemberType(props.get(2), "age", "Age", "integer");
        assertMemberType(props.get(3), "address", "Address", "object");
        assertMemberType(props.get(4), "favouriteColour", "Colour", "string");
        assertMemberType(props.get(5), "registered", "boolean", "boolean");
        assertMemberType(props.get(6), "dateOfBirth", "date-only", "date_only");
        assertMemberType(props.get(7), "registrationDate", "datetime", "datetime");

        TypeDeclaration favouriteColour = userType.properties().get(4);
        assertThat(favouriteColour.name()).isEqualTo("favouriteColour");
        assertThat(favouriteColour.required()).isFalse();

        Resource resource = apiModel.getApi().resources().get(0);
        assertThat(resource.relativeUri().value()).isEqualTo("/user");
        Method getMethod = resource.methods().get(0);
        assertThat(getMethod.method()).isEqualTo("get");
        StringTypeDeclaration sortParam = (StringTypeDeclaration) getMethod.queryParameters().get(1);
        assertThat(sortParam.name()).isEqualTo("sort");
        assertThat(sortParam.required()).isFalse();

    }

    private void assertMemberType(TypeDeclaration type, String memberName, String typeName, String baseType) {
        assertThat(type.name()).isEqualTo(memberName);
        assertThat(type.type()).isEqualTo(typeName);
        assertThat(apiModel.metatype(type).toString()).isEqualToIgnoringCase(baseType.toLowerCase());
    }

    @Test
    public void shouldParseAnnotations() {
        parse("generic.raml");

        ObjectTypeDeclaration person = (ObjectTypeDeclaration) apiModel.getDeclaredType("Person");
        TypeDeclaration lastName = person.properties().get(1);
        assertThat(lastName.name()).isEqualTo("lastName");
        assertThat(lastName.annotations().size()).isEqualTo(1);
        AnnotationRef notes = lastName.annotations().get(0);
        assertThat(notes.annotation().name()).isEqualTo("notes");
        TypeInstance sv = notes.structuredValue();
        assertThat(sv.isScalar()).isEqualTo(false);
        TypeInstanceProperty tip = sv.properties().get(0);
        assertThat(tip.isArray()).isEqualTo(true);
        assertThat(tip.values().size()).isEqualTo(3);
        assertThat(tip.values().stream().map(v -> v.value())).containsExactly("N1", "N2", "N3");
    }

    @Test
    public void shouldParseSimpleAnnotation() {
        parse("generic.raml");

        ObjectTypeDeclaration animal = (ObjectTypeDeclaration) apiModel.getDeclaredType("Animal");
        assertThat(animal.annotations().size()).isEqualTo(1);
        AnnotationRef note = animal.annotations().get(0);
        assertThat(note.annotation().name()).isEqualTo("note");
        TypeInstance sv = note.structuredValue();
        assertThat(sv.isScalar()).isTrue();
        assertThat(sv.value()).isEqualTo("This is a note");
    }

    @Test
    public void shouldParseObject() {
        parse("bracketArray.raml");
        Api api = apiModel.getApi();
        assertMemberTypes(api.types().get(0), "ObjectList", "list", "object[]", ARRAY);
        assertMemberTypes(api.types().get(1), "NameList", "list", "Name[]", ARRAY);
        assertMemberTypes(api.types().get(2), "PersonList", "list", "Person[]", ARRAY);
        assertMemberTypes(api.types().get(3), "StringList", "list", "string[]", ARRAY);
        assertMemberTypes(api.types().get(4), "BooleanList", "list", "BooleanArray", ARRAY);
        assertMemberTypes(api.types().get(5), "DigitList", "list", "DigitArray", ARRAY);
    }

    private void assertMemberTypes(TypeDeclaration type, String typeName, String memberName, String memberType, Metatype metatype) {
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.name()).isEqualTo(typeName);
        TypeDeclaration member = objectType.properties().get(0);
        assertThat(member).isInstanceOf(ArrayTypeDeclaration.class);
        assertThat(member.name()).isEqualTo(memberName);
        assertThat(member.type()).isEqualTo(memberType);
        assertThat(apiModel.metatype(member)).isEqualTo(metatype);
    }



    @Test
    public void shouldParseArrays() {
        parse("bracketArray.raml");
        Api api = apiModel.getApi();

        assertTypes(api.types().get(0), "ObjectList", "object[]", null, "object", OBJECT);
        assertTypes(api.types().get(1), "NameList", "Name", "string", "Name", STRING);
        assertTypes(api.types().get(2), "PersonList", "Person", "object", "Person", OBJECT);
        assertTypes(api.types().get(3), "StringList", "string[]", null, "string", STRING);
        assertTypes(api.types().get(4), "BooleanList", "boolean[]", null, "boolean", BOOLEAN);
        assertTypes(api.types().get(5), "DigitList", "Digit", "integer", "Digit", INTEGER);
    }

    @Test
    public void shouldParseNestedArrays() {
        parse("nestedBracketArray.raml");
        Api api = apiModel.getApi();

        assertTypes(api.types().get(0), "PersonList", "Person", "object", "Person", OBJECT);
        assertTypes(api.types().get(1), "PersonArrayList", "PersonArray", "array", "PersonArray", ARRAY);
        assertTypes(api.types().get(2), "PersonArrayList2", "Person[][]", null, "Person[]", ARRAY);
    }

    private void assertTypes(TypeDeclaration type, String typeName, String itemName, String itemType, String realItemType, Metatype metatype) {
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.name()).isEqualTo(typeName);
        TypeDeclaration member = objectType.properties().get(0);
        assertThat(member).isInstanceOf(ArrayTypeDeclaration.class);
        ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) member;
        TypeDeclaration item = arrayType.items();
        assertThat(item.name()).isEqualTo(itemName);
        assertThat(item.type()).isEqualTo(itemType);
        assertThat(apiModel.getItemType(member)).isEqualTo(realItemType);
        assertThat(apiModel.metatype(item)).isEqualTo(metatype);
    }

    @Test
    public void shouldFindItemTypeWithBrackets() {
        parse("bracketArray.raml");
        Api api = apiModel.getApi();

        assertTypes(api.types().get(0), "ObjectList",   "object[]",     null);
        assertTypes(api.types().get(1), "NameList",     "Name",         "string");
        assertTypes(api.types().get(2), "PersonList",   "Person",       "object");
        assertTypes(api.types().get(3), "StringList",   "string[]",     null);
        assertTypes(api.types().get(4), "BooleanList",  "boolean[]",    null);
        assertTypes(api.types().get(5), "DigitList",    "Digit",        "integer");
        assertTypes(api.types().get(6), "EmployeeList", "Employee",     "Person");
    }

    private void assertTypes(TypeDeclaration type, String typeName, String itemName, String itemType) {
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.name()).isEqualTo(typeName);
        TypeDeclaration member = objectType.properties().get(0);
        assertThat(member).isInstanceOf(ArrayTypeDeclaration.class);
        ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) member;
        TypeDeclaration item = arrayType.items();
        assertThat(item.name()).isEqualTo(itemName);
        assertThat(item.type()).isEqualTo(itemType);
    }


    @Test
    public void shouldFindItemType() {
        parse("array.raml");
        Api api = apiModel.getApi();

        assertTypes(api.types().get(0), "ObjectList",   "object",         "object");
        assertTypes(api.types().get(1), "NameList",     "string",         "Name");
        assertTypes(api.types().get(2), "PersonList",   "object",         "Person");
        assertTypes(api.types().get(3), "StringList",   "string",         "string");
        assertTypes(api.types().get(4), "BooleanList",  "boolean",         "boolean");
        assertTypes(api.types().get(5), "DigitList",    "integer",         "Digit");
    }

    @Test
    public void shouldFindNestedItemType() {
        parse("nestedArray.raml");
        Api api = apiModel.getApi();

        assertTypes(api.types().get(0), "PersonList",       "object",   "Person");
        assertTypes(api.types().get(1), "PersonArrayList",  "array",    "PersonArray");
    }

    @Test
    public void shouldParseEnums() {
        parse("enums.raml");

        TypeDeclaration decl = apiModel.getDeclaredType("Colour");

        AnnotationRef annotationRef = decl.annotations().stream().filter(a -> a.annotation().name().equals("enum")).findFirst().get();
        TypeInstance typeInstance = annotationRef.structuredValue();
        TypeInstanceProperty tip = typeInstance.properties().get(0);
        assertThat(tip.values()).hasSize(2);
        TypeInstance ti0 = tip.values().get(0);
        assertThat(ti0.properties().size()).isEqualTo(2);
        assertThat(ti0.properties().get(0).name()).isEqualTo("name");
        assertThat(ti0.properties().get(0).value().value()).isEqualTo("lightBlue");
        assertThat(ti0.properties().get(1).name()).isEqualTo("description");
        assertThat(ti0.properties().get(1).value().value()).isEqualTo("Colour of the sky");
    }

    @Test
    public void shouldGetEnumValues() {
        parse("enums.raml");

        TypeDeclaration decl = apiModel.getDeclaredType("Colour");
        assertThat(apiModel.isEnum(decl)).isEqualTo(true);

        List<EnumValue> enumValues = apiModel.getEnumValues(decl);
        assertThat(enumValues).hasSize(2);

        assertThat(enumValues.get(0).getName()).isEqualTo("lightBlue");
        assertThat(enumValues.get(0).getDescription()).isEqualTo("Colour of the sky");
        assertThat(enumValues.get(1).getName()).isEqualTo("red");
        assertThat(enumValues.get(1).getDescription()).isEqualTo("Colour of tomatos");
    }
}
