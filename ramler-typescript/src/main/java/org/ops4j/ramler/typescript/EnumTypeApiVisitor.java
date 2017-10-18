/*
 * Copyright 2017 OPS4J Contributors
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

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.ops4j.ramler.exc.GeneratorException;
import org.ops4j.ramler.generator.ApiVisitor;
import org.ops4j.ramler.generator.Names;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.util.ImmutableMap;

/**
 * @author Harald Wellmann
 *
 */
public class EnumTypeApiVisitor implements ApiVisitor {

    private static Logger log = LoggerFactory.getLogger(EnumTypeApiVisitor.class);

    private TypescriptGeneratorContext context;

    private StringBuilder output;

    static class EnumSymbol {
        public String symbol;

        public String value;

        EnumSymbol(String symbol, String value) {
            this.symbol = symbol;
            this.value = value;
        }
    }

    public EnumTypeApiVisitor(TypescriptGeneratorContext context) {
        this.context = context;
        this.output = new StringBuilder();
    }

    @Override
    public void visitStringType(StringTypeDeclaration type) {
        String name = type.name();
        List<EnumSymbol> enumValues = context.getApiModel().getEnumValues(type).stream()
                .map(v -> new EnumSymbol(Names.buildConstantName(v.getName()), v.getName())).collect(toList());

        Map<String, Object> contextObject = ImmutableMap.of("name", name, "enumValues", enumValues);

        MustacheEngine engine = context.getTemplateEngine().getEngine();
        engine.getMustache("enum").render(output, contextObject);

        String moduleName = Names.buildLowerKebabCaseName(type.name());
        writeToFile(output.toString(), moduleName);
    }

    private void writeToFile(String content, String moduleName) {
        String tsFileName = moduleName + ".ts";
        File tsFile = new File(context.getConfig().getTargetDir(), tsFileName);
        log.debug("generating {}\n{}", tsFileName, content);
        try {
            Files.write(tsFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exc) {
            throw new GeneratorException(exc);
        }
    }

}