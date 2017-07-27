package org.ops4j.ramler.generator;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests fluent setters
 * @see Configuration#isFluentSetters()
 */
public class FluentSetterTest extends AbstractGeneratorTest {

    @Test
    public void shouldFindFluentSetters() {
        expectClass("Person");
        assertProperty(klass, "firstName", "String", "getFirstName", "setFirstName");
        assertFluentSetter(klass,  "String", "withFirstName");

        assertProperty(klass, "lastName", "String", "getLastName", "setLastName");
        assertFluentSetter(klass,  "String", "withLastName");

        assertProperty(klass, "duplicateProperty", "int", "getDuplicateProperty", "setDuplicateProperty");
        assertFluentSetter(klass,  "int", "withDuplicateProperty");

        // expect read only fields / getter available but that there is NO fluent setter
        assertFieldAvailable("DISCRIMINATOR", "String");
        assertGetterAvailable("getObjectType", "String");

        verifyClass();
    }

    /**
     * Tests that fluent setters will also be generated for inherited properties
     */
    @Test
    public void shouldFindInheritedFluentSetters() {

        expectClass("Employee");

        // expect current fields to be added
        assertProperty(klass, "employeeId", "int", "getEmployeeId", "setEmployeeId");
        assertFluentSetter(klass, "int", "withEmployeeId");

        assertProperty(klass, "colleagues", "List<Employee>", "getColleagues", "setColleagues");
        assertFluentSetter(klass, "List<Employee>", "withColleagues");


        // expect inherited fields to be added as well
        assertFluentSetter(klass, "String", "withFirstName");
        assertFluentSetter(klass, "String", "withLastName");
        assertFluentSetter(klass,  "int", "withDuplicateProperty");

        // expect read only fields / getter available but that there is NO fluent setter
        assertFieldAvailable("DISCRIMINATOR", "String");
        assertGetterAvailable("getObjectType", "String");

        // expect duplicateProperty not to be listed again
        verifyClass();
    }

    @Override
    public String getBasename() {
        return "fluentSetters";
    }

    @Override
    protected Configuration getConfiguration() {
        Configuration result = super.getConfiguration();
        result.setFluentSetterPrefix("with");
        return result;
    }

    /**
     * Assert that there is a fluent setter for given property name
     */
    private void assertFluentSetter(JDefinedClass klass, String typeName, String methodName) {

        List<JMethod> methods = klass.methods().stream().filter(m -> m.name().equals(methodName)).collect(toList());
        assertThat(methods).hasSize(1);
        JMethod method = methods.get(0);
        
        assertThat(method.type()).isEqualTo(klass);
        assertThat(method.params().size()).isEqualTo(1);
        assertThat(method.params().get(0).type().name()).isEqualTo(typeName);

        methodNames.remove(methodName);
    }

    private void assertFieldAvailable(String fieldName, String typeName) {

        JFieldVar field = klass.fields().get(fieldName);
        assertThat(field).isNotNull();
        assertThat(field.type().name()).isEqualTo(typeName);

        fieldNames.remove(fieldName);
    }

    private void assertGetterAvailable(String getterMethodName, String typeName) {

        List<JMethod> getters = klass.methods().stream().filter(m -> m.name().equals(getterMethodName)).collect(toList());
        assertThat(getters).hasSize(1);
        JMethod getter = getters.get(0);
        assertThat(getter.type().name()).isEqualTo(typeName);
        assertThat(getter.hasSignature(new JType[0])).isEqualTo(true);

        methodNames.remove(getterMethodName);
    }
}
