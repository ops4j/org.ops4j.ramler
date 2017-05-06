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
package org.ops4j.ramler.html;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class HtmlGeneratorTest {

    @Test
    public void shouldRenderApi() throws IOException {
        HtmlConfiguration config = new HtmlConfiguration();
        config.setSourceFile("src/test/resources/raml/simpleobject.raml");
        config.setTargetDir("target/html/simpleobject");
        HtmlGenerator generator = new HtmlGenerator(config);
        generator.generate();
    }

    @Test
    public void shouldRenderRegistryApi() throws IOException {
        HtmlConfiguration config = new HtmlConfiguration();
        config.setSourceFile("../ramler-java/src/test/resources/raml/registry.raml");
        config.setTargetDir("target/html/registry");
        HtmlGenerator generator = new HtmlGenerator(config);
        generator.generate();
    }

    @Test
    public void shouldWalkTree() throws IOException {
        Path root = Paths.get("src/main/resources");
        List<String> paths = Files.walk(root).map(p -> root.relativize(p).toString()).collect(toList());
        assertThat(paths, hasItems("fonts", "fonts/slate.eot", "trimou", "trimou/api.trimou.html"));
    }
}
