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

import java.util.Map;

import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JEnumConstant;

public class SimpleObjectTest extends AbstractGeneratorTest {

    @Override
    public String getBasename() {
        return "simpleobject";
    }

    @Test
    public void shouldFindModelClasses() {
        assertClasses("Address", "Colour", "Employee", "FileResponse", "FunnyNames",
            "Integers", "Manager", "Numbers", "Person", "Reference", "Temporals", "User", "UserGroup");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindEnumValues() throws IllegalAccessException {
        klass = modelPackage._getClass("Colour");
        assertThat(klass.getClassType()).isEqualTo(ClassType.ENUM);
        Map<String,JEnumConstant> enums = (Map<String, JEnumConstant>) FieldUtils.readField(klass, "enumConstantsByName", true);
        assertThat(enums.keySet()).containsExactly("LIGHT_BLUE", "RED", "YELLOW", "GREEN");
    }

    @Test
    public void shouldFindAddressMembers() {
        expectClass("Address");
        assertProperty(klass, "city", "String", "getCity", "setCity");
        assertProperty(klass, "street", "String", "getStreet", "setStreet");
        verifyClass();
    }

    @Test
    public void shouldFindDiscriminator() {
        TypeDeclaration type = generator.getContext().getApiModel().getDeclaredType("Manager");
        ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) type;
        assertThat(objectType.discriminator()).isEqualTo("objectType");
    }

    @Test
    public void shouldFindIntegersMembers() {
        expectClass("Integers");
        assertProperty(klass, "i8", "int", "getI8", "setI8");
        assertProperty(klass, "i8o", "Integer", "getI8o", "setI8o");
        assertProperty(klass, "i16", "int", "getI16", "setI16");
        assertProperty(klass, "i16o", "Integer", "getI16o", "setI16o");
        assertProperty(klass, "i32", "int", "getI32", "setI32");
        assertProperty(klass, "i32o", "Integer", "getI32o", "setI32o");
        assertProperty(klass, "i", "int", "getI", "setI");
        assertProperty(klass, "io", "Integer", "getIo", "setIo");
        assertProperty(klass, "i64", "long", "getI64", "setI64");
        assertProperty(klass, "i64o", "Long", "getI64o", "setI64o");
        assertProperty(klass, "l", "long", "getL", "setL");
        assertProperty(klass, "lo", "Long", "getLo", "setLo");
        verifyClass();
    }

    @Test
    public void shouldFindNumbersMembers() {
        expectClass("Numbers");
        assertProperty(klass, "f", "float", "getF", "setF");
        assertProperty(klass, "fo", "Float", "getFo", "setFo");
        assertProperty(klass, "d", "double", "getD", "setD");
        assertProperty(klass, "dbl", "Double", "getDbl", "setDbl");
        verifyClass();
    }

    @Test
    public void shouldFindTemporalsMembers() {
        expectClass("Temporals");
        assertProperty(klass, "date", "LocalDate", "getDate", "setDate");
        assertProperty(klass, "to", "LocalTime", "getTo", "setTo");
        assertProperty(klass, "dto", "LocalDateTime", "getDto", "setDto");
        assertProperty(klass, "dt", "ZonedDateTime", "getDt", "setDt");
        verifyClass();
    }

    @Test
    public void shouldFindUserMembers() {
        expectClass("User");
        assertProperty(klass, "address", "Address", "getAddress", "setAddress");
        assertProperty(klass, "age", "int", "getAge", "setAge");
        assertProperty(klass, "dateOfBirth", "LocalDate", "getDateOfBirth", "setDateOfBirth");
        assertProperty(klass, "favouriteColour", "Colour", "getFavouriteColour", "setFavouriteColour");
        assertProperty(klass, "firstname", "String", "getFirstname", "setFirstname");
        assertProperty(klass, "lastname", "String", "getLastname", "setLastname");
        assertProperty(klass, "registered", "boolean", "isRegistered", "setRegistered");
        assertProperty(klass, "registrationDate", "ZonedDateTime", "getRegistrationDate", "setRegistrationDate");
        verifyClass();
    }

    @Test
    public void shouldFindEmployeeMembers() {
        expectClass("Employee");
        assertThat(klass._extends().name()).isEqualTo("Person");
        assertProperty(klass, "department", "String", "getDepartment", "setDepartment");

        fieldNames.remove("DISCRIMINATOR");
        methodNames.remove("getObjectType");
        methodNames.remove("setObjectType");
        verifyClass();
    }

    @Test
    public void shouldFindFunnyNamesMembers() {
        expectClass("FunnyNames");
        assertProperty(klass, "$static", "boolean", "isStatic", "setStatic");
        assertProperty(klass, "customerName", "String", "getCustomerName", "setCustomerName");
        assertProperty(klass, "$interface", "int", "getInterface", "setInterface");
        assertProperty(klass, "someOtherName", "String", "getSomeOtherName", "setSomeOtherName");
        verifyClass();
    }
}
