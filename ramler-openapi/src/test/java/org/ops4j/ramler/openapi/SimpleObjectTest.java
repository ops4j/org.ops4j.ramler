package org.ops4j.ramler.openapi;

import org.junit.jupiter.api.Test;

public class SimpleObjectTest extends AbstractOpenApiTest {

    @Override
    public String getBasename() {
        return "simpleobject";
    }

    @Test
    public void shouldFindSchemas() {
        assertSchemas("Address", "Age", "Colour", "Employee", "FileResponse", "FunnyNames", "Integers", "Manager",
                "Name", "Numbers", "Person", "Reference", "Temporals", "User", "UserGroup");
    }
}
