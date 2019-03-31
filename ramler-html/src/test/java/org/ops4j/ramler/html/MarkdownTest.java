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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownTest {

    private static final String SOURCE_DIR = "src/test/resources/markdown";

    private static Logger log = LoggerFactory.getLogger(MarkdownTest.class);

    @Test
    public void shouldHighlightMarkdown() throws IOException {
        PegDownProcessor processor = new PegDownProcessor(Extensions.FENCED_CODE_BLOCKS);
        String markdown = readAllAsUtf8String(Paths.get(SOURCE_DIR, "java1.md"));
        String html = processor.markdownToHtml(markdown);
        log.debug(html);

        String expectedHtml = readAllAsUtf8String(Paths.get(SOURCE_DIR, "java1.html"));

        assertThat(html).isEqualTo(expectedHtml);
    }

    private String readAllAsUtf8String(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
