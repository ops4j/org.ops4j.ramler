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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

public class GeneratorTest {

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
        ObjectTypeDeclaration userType = (ObjectTypeDeclaration) api.types().stream().
                filter(t -> t.name().equals("User")).findFirst().get();
        System.out.println(userType.name());
        System.out.println(userType.type());
        ObjectTypeDeclaration address = (ObjectTypeDeclaration) userType.properties().get(3);
        System.out.println(address.name());
        System.out.println(address.type());
        
        Resource resource = api.resources().get(0);
        System.out.println(resource.relativeUri().value());
    }
    
    @Test
    public void shouldGeneratePojos() {
        File input = new File("src/test/resources/raml/simpleobject.raml");
        assertTrue(input.isFile());
        
        Configuration config = new Configuration();
        config.setSourceFile(input);
        config.setBasePackage("org.ops4j.raml.demo");
        config.setTargetDir(new File("target/generated/raml"));

        Generator generator = new Generator(config);
        generator.generate();
    }

    @Test
    public void shouldGenerateGenericPojos() {
        File input = new File("src/test/resources/raml/generic.raml");
        assertTrue(input.isFile());
        
        Configuration config = new Configuration();
        config.setSourceFile(input);
        config.setBasePackage("org.ops4j.raml.generic");
        config.setTargetDir(new File("target/generated/raml"));

        Generator generator = new Generator(config);
        generator.generate();
    }

}
