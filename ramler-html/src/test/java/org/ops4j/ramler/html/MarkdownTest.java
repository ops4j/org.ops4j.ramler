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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

public class MarkdownTest {

    @Test
    public void shouldHighlightMarkdown() throws IOException {
        PegDownProcessor processor = new PegDownProcessor(Extensions.FENCED_CODE_BLOCKS);
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/markdown", "java1.md"));
        String markdown = new String(bytes, StandardCharsets.UTF_8);
        String html = processor.markdownToHtml(markdown);
        System.out.println(html);
    }
}
