package org.ops4j.ramler.itest;

import org.junit.Test;
import org.ops4j.ramler.itest.model.Employee;
import org.ops4j.ramler.itest.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that fluent setters are working
 */
public class FluentSetterTest {

    @Test
    public void testFluentSetters() {

        Person person = new Person().withAge(12).withFirstname("My Firstname");
        assertThat(person.getAge()).isEqualTo(12);
        assertThat(person.getFirstname()).isEqualTo("My Firstname");

        person.setFirstname("Changed Name");
        assertThat(person.getFirstname()).isEqualTo("Changed Name");
    }

    /**
     * Tests that inherited fields also properly accessible by fluent api
     */
    @Test
    public void testFluentSettersForInherited() {

        Employee employee = new Employee().withAge(12).withDepartment("My Department");
        assertThat(employee.getAge()).isEqualTo(12);
        assertThat(employee.getDepartment()).isEqualTo("My Department");
    }
}