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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.ops4j.ramler.exc.Exceptions;
import org.ops4j.ramler.generator.Names;
import org.ops4j.ramler.typescript.parser.JsonGeneratingListener;
import org.ops4j.ramler.typescript.parser.TypeScriptLexer;
import org.ops4j.ramler.typescript.parser.TypeScriptParser;
import org.ops4j.ramler.typescript.parser.TypeScriptParser.ModuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractGeneratorTest {

    protected static Logger log = LoggerFactory.getLogger(AbstractGeneratorTest.class);

    protected TypeScriptGenerator generator;

    protected Set<String> methodNames;

    protected Set<String> fieldNames;

    private TypeScriptConfiguration config;

    private JsonGeneratingListener listener;

    private List<JsonObject> members;

    private Set<String> memberNames;

    private JsonObject module;

    private List<JsonObject> enumMembers;

    private Iterator<JsonObject> enumMemberIter;

    @BeforeAll
    public void generateJavaModel() throws IOException {
        config = new TypeScriptConfiguration();
        config.setSourceFile(String.format("raml/%s.raml", getBasename()));
        config.setTargetDir(new File("target/generated/ts/" + getBasename()));

        generator = new TypeScriptGenerator(config);
        generator.generate();

    }

    public abstract String getBasename();

    public void parseTypeScriptModule(String baseName) {
        File source = new File(config.getTargetDir(), baseName + ".ts");
        try {
            TypeScriptParser parser = buildParser(source);
            ModuleContext module = parser.module();

            listener = new JsonGeneratingListener();
            ParseTreeWalker.DEFAULT.walk(listener, module);
            log.debug(listener.getJson());

        } catch (IOException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    private TypeScriptParser buildParser(File source) throws IOException {
        CharStream inputCharStream = CharStreams.fromPath(source.toPath());
        TokenSource tokenSource = new TypeScriptLexer(inputCharStream);
        TokenStream inputTokenStream = new CommonTokenStream(tokenSource);
        TypeScriptParser parser = new TypeScriptParser(inputTokenStream);

        // make parser throw exception on first error
        parser.setErrorHandler(new BailErrorStrategy());

        // print detailed error messages to System.err
        parser.addErrorListener(new ConsoleErrorListener());

        return parser;
    }

    protected void assertModules(String... modules) {
        List<String> actual = Stream.of(config.getTargetDir().list()).sorted().map(m -> m.replace(".ts", "")).collect(toList());
        assertThat(actual).containsExactly(modules);
    }

    protected void expectInterface(String interfaceName, String... baseClasses) {
        String baseName = Names.buildLowerKebabCaseName(interfaceName);
        parseTypeScriptModule(baseName);
        String json = listener.getJson();

        module = Json.createReader(new StringReader(json)).readObject();
        JsonArray exports = module.getJsonArray("exports");
        assertThat(exports).hasSize(1);

        JsonObject export = exports.getJsonObject(0);
        assertThat(export.getString("discriminator")).isEqualTo("interface");
        assertThat(export.getJsonObject("type").getString("name")).isEqualTo(interfaceName);

        assertBaseClasses(export.getJsonArray("extends"), baseClasses);

        members = export.getJsonArray("members").getValuesAs(JsonObject.class);
        memberNames = members.stream().map(m -> m.getString("name")).collect(toSet());
    }

    protected void assertImports(String... importNames) {
        JsonArray importsArray = module.getJsonArray("imports");
        List<JsonObject> imports = importsArray == null ? Collections.emptyList() : importsArray.getValuesAs(JsonObject.class);
        assertThat(imports).hasSize(importNames.length);
        int pos = 0;
        for (JsonObject actualImport : imports) {
            assertImport(actualImport, importNames[pos]);
            pos++;
        }
    }

    /**
     * @param jsonArray
     * @param baseClasses
     */
    private void assertBaseClasses(JsonArray extendsArray, String[] baseClasses) {
        List<JsonObject> extended = extendsArray == null ? Collections.emptyList() : extendsArray.getValuesAs(JsonObject.class);
        assertThat(extended).hasSize(baseClasses.length);
        int pos = 0;
        for (JsonObject actualBaseClass : extended) {
            assertBaseClass(actualBaseClass, baseClasses[pos]);
            pos++;
        }
    }



    private void assertBaseClass(JsonObject actualBaseClass, String baseClass) {
        String discriminator = actualBaseClass.getString("discriminator");
        String name = actualBaseClass.getString("name");
        if (discriminator.equals("simple")) {
            assertThat(name).isEqualTo(baseClass);
        }
        else if (discriminator.equals("param")) {
            String actualType = buildParamTypeName(name, actualBaseClass.getJsonArray("types"));
            assertThat(actualType).isEqualTo(baseClass);
        }
    }

    private String buildParamTypeName(String name, JsonArray args) {
        return args.getValuesAs(JsonObject.class).stream()
                .map(a -> a.getString("name"))
                .collect(joining(", ", name + "<", ">"));
    }

    private void assertImport(JsonObject actualImport, String identifier) {
        List<JsonString> identifiers = actualImport.getJsonArray("identifiers").getValuesAs(JsonString.class);
        assertThat(identifiers).hasSize(1);
        String actualId = identifiers.get(0).getString();
        assertThat(actualId).isEqualTo(identifier);
    }

    protected void assertProperty(String memberName, String typeName) {
        memberNames.remove(memberName);

        JsonObject type = members.stream().filter(m -> m.getString("name").equals(memberName)).findFirst()
                .map(m -> m.getJsonObject("type"))
                .orElseThrow(() -> memberNotFound(memberName));
        assertType(type, typeName);
    }

    private void assertType(JsonObject type, String typeName) {
        String actualType = type.getString("name");
        String discriminator = type.getString("discriminator");
        if (discriminator.equals("array")) {
            actualType += "[]";
        }
        else if (discriminator.equals("param")) {
            actualType = buildParamTypeName(actualType, type.getJsonArray("types"));
        }
        assertThat(actualType).isEqualTo(typeName);
    }

    /**
     * @param memberName
     * @return
     */
    private AssertionError memberNotFound(String memberName) {
        throw new AssertionError("member not found: " + memberName);
    }

    protected void verifyInterface() {
        assertThat(memberNames).isEmpty();
    }

    protected void expectEnum(String enumName) {
        String baseName = Names.buildLowerKebabCaseName(enumName);
        parseTypeScriptModule(baseName);
        String json = listener.getJson();

        module = Json.createReader(new StringReader(json)).readObject();
        JsonArray exports = module.getJsonArray("exports");
        assertThat(exports).hasSize(1);

        JsonObject export = exports.getJsonObject(0);
        assertThat(export.getString("discriminator")).isEqualTo("enum");
        assertThat(export.getString("name")).isEqualTo(enumName);
        enumMembers = export.getJsonArray("members").getValuesAs(JsonObject.class);
        enumMemberIter = enumMembers.iterator();
    }

    protected void assertEnumMember(String name, String value) {
        assertThat(enumMemberIter.hasNext()).isTrue();
        JsonObject enumMember = enumMemberIter.next();
        assertThat(enumMember.getString("name")).isEqualTo(name);
        assertThat(enumMember.getString("value")).isEqualTo(value);
    }

    protected void verifyEnum() {
        assertThat(enumMemberIter.hasNext()).isFalse();
    }

    protected void expectTypeAlias(String aliasName, String type, String... types) {
        String baseName = Names.buildLowerKebabCaseName(aliasName);
        parseTypeScriptModule(baseName);
        String json = listener.getJson();

        module = Json.createReader(new StringReader(json)).readObject();
        JsonArray exports = module.getJsonArray("exports");
        assertThat(exports).hasSize(1);

        JsonObject export = exports.getJsonObject(0);
        assertThat(export.getString("discriminator")).isEqualTo("alias");
        assertThat(export.getString("name")).isEqualTo(aliasName);

        if (types.length == 0) {
            assertType(export.getJsonObject("type"), type);
        } else {
            JsonObject union = export.getJsonObject("type");
            assertThat(union.getString("discriminator")).isEqualTo("union");
            JsonArray variants = union.getJsonArray("variants");
            List<String> typeNames = new ArrayList<>();
            typeNames.add(type);
            typeNames.addAll(Arrays.asList(types));
            for (int i = 0; i < typeNames.size(); i++) {
                JsonObject variant = variants.getJsonObject(i);
                assertThat(variant.getString("name")).isEqualTo(typeNames.get(i));
            }
        }
    }
}
