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
package org.ops4j.ramler.html.trimou;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.trimou.lambda.InputProcessingLambda;

/**
 * Converts markdown text to HTML.
 *
 * @author Harald Wellmann
 *
 */
public class MarkdownLambda extends InputProcessingLambda {

    private PegDownProcessor processor = new PegDownProcessor(
        Extensions.TABLES | Extensions.FENCED_CODE_BLOCKS);

    @Override
    public String invoke(String text) {
        return processor.markdownToHtml(text);
    }
}
