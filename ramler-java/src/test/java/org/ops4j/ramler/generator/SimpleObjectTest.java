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
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

public class SimpleObjectTest {

    private static Generator generator;
    private static JCodeModel codeModel;
    private static JPackage modelPackage;
    private JDefinedClass klass;
    private Set<String> methodNames;
    private Set<String> fieldNames;

    @BeforeClass
    public static void shouldGeneratePojos() {
        File input = new File("src/test/resources/raml/simpleobject.raml");
        assertTrue(input.isFile());
        
        Configuration config = new Configuration();
        config.setSourceFile(input);
        config.setBasePackage("org.ops4j.raml.demo");
        config.setTargetDir(new File("target/generated/raml"));

        generator = new Generator(config);
        generator.generate();

        codeModel = generator.getContext().getCodeModel();
        modelPackage = codeModel._package("org.ops4j.raml.demo.model");
    }
    
    @Test
    public void shouldFindModelClasses() {
        Set<String> classNames = new HashSet<>();
        modelPackage.classes().forEachRemaining(c -> classNames.add(c.name()));
        assertThat(classNames, containsInAnyOrder("Address", "Colour", "Employee", "FileResponse", 
            "Integers", "Numbers", "Person", "Temporals", "User", "UserGroup"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindEnumValues() throws IllegalAccessException {
        klass = modelPackage._getClass("Colour");
        assertThat(klass.getClassType(), is(ClassType.ENUM));
        Map<String,JEnumConstant> enums = (Map<String, JEnumConstant>) FieldUtils.readField(klass, "enumConstantsByName", true);
        assertThat(enums.keySet(), contains("RED", "YELLOW", "GREEN"));
    }
    
    @Test
    public void shouldFindAddressMembers() {
        expectClass("Address");
        assertProperty(klass, "city", "String", "getCity", "setCity");
        assertProperty(klass, "street", "String", "getStreet", "setStreet");
        verifyClass();
    }

    private void expectClass(String className) {
        klass = modelPackage._getClass(className);
        fieldNames = new HashSet<>(klass.fields().keySet());
        methodNames = klass.methods().stream().map(m -> m.name()).collect(toSet());
        
    }
    
    private void verifyClass() {
        assertThat(fieldNames, is(empty()));
        assertThat(methodNames, is(empty()));
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
        assertThat(klass._extends().name(), is("Person"));
        assertProperty(klass, "department", "String", "getDepartment", "setDepartment");
        
        fieldNames.remove("DISCRIMINATOR");
        methodNames.remove("getObjectType");
        methodNames.remove("setObjectType");
        verifyClass();
    }


    private void assertProperty(JDefinedClass klass, String memberName, String typeName, String getterName, String setterName) {
        JFieldVar field = klass.fields().get(memberName);
        assertThat(field, is(notNullValue()));
        assertThat(field.type().name(), is(typeName));
        
        List<JMethod> getters = klass.methods().stream().filter(m -> m.name().equals(getterName)).collect(toList());
        assertThat(getters, hasSize(1));
        JMethod getter = getters.get(0);
        assertThat(getter.type().name(), is(typeName));
        assertThat(getter.hasSignature(new JType[0]), is(true));
        
        List<JMethod> setters = klass.methods().stream().filter(m -> m.name().equals(setterName)).collect(toList());
        assertThat(setters, hasSize(1));
        JMethod setter = setters.get(0);
        assertThat(setter.type(), is(codeModel.VOID));
        assertThat(setter.hasSignature(new JType[]{field.type()}), is(true));
        
        fieldNames.remove(memberName);
        methodNames.remove(getterName);
        methodNames.remove(setterName);
        
    }
}
