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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class GeneratorTest {

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

    @Test
    public void shouldGenerateArrayTypes() {
        File input = new File("src/test/resources/raml/array.raml");
        assertTrue(input.isFile());
        
        Configuration config = new Configuration();
        config.setSourceFile(input);
        config.setBasePackage("org.ops4j.raml.array");
        config.setTargetDir(new File("target/generated/raml"));

        Generator generator = new Generator(config);
        generator.generate();
    }
}
