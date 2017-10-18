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
package org.ops4j.ramler.typescript;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractGeneratorTest {

    protected TypescriptGenerator generator;
    protected Set<String> methodNames;
    protected Set<String> fieldNames;

    @BeforeEach
    public void generateJavaModel() {
        TypescriptConfiguration config = new TypescriptConfiguration();
        config.setSourceFile(String.format("raml/%s.raml", getBasename()));
        config.setTargetDir(new File("target/generated/ts/" + getBasename()));

        generator = new TypescriptGenerator(config);
        generator.generate();

    }

    public abstract String getBasename();

}
