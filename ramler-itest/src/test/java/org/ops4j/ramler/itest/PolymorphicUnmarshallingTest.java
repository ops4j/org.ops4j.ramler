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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;
import org.ops4j.ramler.itest.model.Address;
import org.ops4j.ramler.itest.model.Employee;
import org.ops4j.ramler.itest.model.Manager;
import org.ops4j.ramler.itest.model.Person;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PolymorphicUnmarshallingTest {

    @Test
    public void shouldMarshalPerson() throws IOException {
        Person person = new Person();
        person.setFirstname("Anna");
        person.setLastname("Blume");
        person.setAge(31);
        Address address = new Address();
        address.setCity("Hamburg");
        address.setStreet("Colonnaden");
        person.setAddress(address);

        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(sw, person);
        String json = sw.toString();

        Person p = mapper.readerFor(Person.class).readValue(json);
        assertThat(p, instanceOf(Person.class));
    }

    @Test
    public void shouldMarshalEmployee() throws IOException {
        Employee person = new Employee();
        person.setFirstname("Anna");
        person.setLastname("Blume");
        person.setAge(31);
        person.setDepartment("Sales");
        Address address = new Address();
        address.setCity("Hamburg");
        address.setStreet("Colonnaden");
        person.setAddress(address);

        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(sw, person);
        String json = sw.toString();

        Person p = mapper.readValue(json, Person.class);
        assertThat(p, instanceOf(Employee.class));
        Employee e = (Employee) p;
        assertThat(e.getDepartment(), is("Sales"));
    }

    @Test
    public void shouldManager() throws IOException {
        Manager person = new Manager();
        person.setFirstname("Anna");
        person.setLastname("Blume");
        person.setAge(31);
        person.setDepartment("Sales");
        person.setNumEmployees(5);
        Address address = new Address();
        address.setCity("Hamburg");
        address.setStreet("Colonnaden");
        person.setAddress(address);

        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(sw, person);
        String json = sw.toString();

        Person p = mapper.readValue(json, Person.class);
        assertThat(p, instanceOf(Manager.class));
        Manager m = (Manager) p;
        assertThat(m.getDepartment(), is("Sales"));
        assertThat(m.getNumEmployees(), is(5));
    }

}
