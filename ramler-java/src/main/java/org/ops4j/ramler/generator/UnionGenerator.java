/*
 * Copyright 2019 OPS4J Contributors
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

import static org.ops4j.ramler.generator.Constants.VALUE;

import java.io.IOException;

import org.ops4j.ramler.common.exc.Exceptions;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

public class UnionGenerator {

    private GeneratorContext context;
    private JPackage pkg;
    private JCodeModel codeModel;
    private boolean jacksonEnabled;

    /**
     * Creates a union generator with the given context.
     *
     * @param context
     *            generator context
     */
    public UnionGenerator(GeneratorContext context) {
        this.context = context;
        this.codeModel = context.getCodeModel();
        this.pkg = context.getModelPackage();
        this.jacksonEnabled = context.getConfig().isJacksonUnion();
    }

    public void generateUnionClass(UnionTypeDeclaration type) {
        if (jacksonEnabled) {
            generateSerializer(type);
            generateDeserializer(type);
        }
        JDefinedClass klass = pkg._getClass(type.name());
        context.addJavadoc(klass, type);
        if (jacksonEnabled) {
            addJacksonAnnotations(klass, type);
        }
        addValueField(klass);
        addValueGetter(klass);
        for (TypeDeclaration variant : type.of()) {
            addVariantChecker(klass, variant);
            addVariantGetter(klass, variant);
            addVariantSetter(klass, variant);
        }
    }

    private void generateSerializer(UnionTypeDeclaration type) {
        JDefinedClass serializer = null;
        try {
            serializer = pkg._class(type.name() + "Serializer");
            context.annotateAsGenerated(serializer);
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }

        JDefinedClass unionClass = pkg._getClass(type.name());
        JClass baseClass = codeModel.ref(StdSerializer.class).narrow(unionClass);
        serializer._extends(baseClass);

        addDefaultSerialVersionUid(serializer);

        JMethod constructor = serializer.constructor(JMod.PUBLIC);
        JInvocation sup = constructor.body().invoke("super");
        sup.arg(unionClass.dotclass());


        JMethod serialize = serializer.method(JMod.PUBLIC, codeModel.VOID, "serialize");
        serialize.annotate(Override.class);
        JVar value = serialize.param(unionClass, VALUE);
        JVar gen = serialize.param(JsonGenerator.class, "gen");
        serialize.param(SerializerProvider.class, "provider");
        serialize._throws(IOException.class);

        serialize.body().invoke(gen, "writeObject").arg(value.invoke("value"));
    }

    private void addDefaultSerialVersionUid(JDefinedClass klass) {
        JFieldVar uid = klass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, codeModel.LONG,
            "serialVersionUID");
        uid.init(JExpr.lit(1L));
    }

    private void generateDeserializer(UnionTypeDeclaration type) {
        JDefinedClass deserializer = null;
        try {
            deserializer = pkg._class(type.name() + "Deserializer");
            context.annotateAsGenerated(deserializer);
        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }

        JDefinedClass unionClass = pkg._getClass(type.name());
        JClass baseClass = codeModel.ref(StdDeserializer.class).narrow(unionClass);
        deserializer._extends(baseClass);

        addDefaultSerialVersionUid(deserializer);

        JMethod constructor = deserializer.constructor(JMod.PUBLIC);
        JInvocation sup = constructor.body().invoke("super");
        sup.arg(unionClass.dotclass());

        for (TypeDeclaration variant : type.of()) {
            generateLooksLikeMethod(deserializer, variant);
        }

        JMethod deserialize = deserializer.method(JMod.PUBLIC, unionClass, "deserialize");
        deserialize.annotate(Override.class);
        JVar parser = deserialize.param(JsonParser.class, "parser");
        deserialize.param(DeserializationContext.class, "context");
        deserialize._throws(IOException.class);

        JClass objectMapper = codeModel.ref(ObjectMapper.class);
        JClass jsonNode = codeModel.ref(JsonNode.class);
        JBlock body = deserialize.body();

        JVar mapper = body.decl(objectMapper, "mapper", JExpr.cast(objectMapper, parser.invoke("getCodec")));
        JVar node = body.decl(jsonNode, "node", mapper.invoke("readTree").arg(parser));
        JVar result = body.decl(unionClass, "result", JExpr._new(unionClass));

        for (TypeDeclaration variant : type.of()) {
            tryVariant(body, result, node, mapper, variant);
        }

        body._throw(JExpr._new(codeModel.ref(IOException.class))
            .arg(JExpr.lit("Cannot determine type of ").plus(node)));

    }

    private void tryVariant(JBlock body, JVar result, JVar node, JVar mapper,
        TypeDeclaration variant) {
        JBlock then = body._if(JExpr.invoke("looksLike" + variant.name()).arg(node))._then();
        then.invoke(result, "set" + variant.name()).arg(JExpr.invoke(mapper, "convertValue")
            .arg(node).arg(pkg._getClass(variant.name()).dotclass()));
        then._return(result);
    }

    private void generateLooksLikeMethod(JDefinedClass deserializer, TypeDeclaration variant) {
        JMethod method = deserializer.method(JMod.PRIVATE, codeModel.BOOLEAN,
            "looksLike" + variant.name());
        JVar node = method.param(JsonNode.class, "node");
        if (variant instanceof ObjectTypeDeclaration) {
            ObjectTypeDeclaration objectType = (ObjectTypeDeclaration) variant;
            JExpression conjunction = buildRequiredPropertiesConjunction(objectType, node);
            method.body()._return(conjunction);
        }
        else {
            method.body()._return(JExpr.FALSE);
        }
    }

    private JExpression buildRequiredPropertiesConjunction(ObjectTypeDeclaration objectType, JVar node) {
        JExpression conjunction = null;
        for (TypeDeclaration prop : objectType.properties()) {
            if (prop.required()) {
                JInvocation hasProp = JExpr.invoke(node, "has").arg(prop.name());
                if (conjunction == null) {
                    conjunction = hasProp;
                }
                else {
                    conjunction = conjunction.cand(hasProp);
                }
            }
        }
        return conjunction;
    }

    private void addJacksonAnnotations(JDefinedClass klass, UnionTypeDeclaration type) {
        JDefinedClass serializer = pkg._getClass(type.name() + "Serializer");
        klass.annotate(JsonSerialize.class).param("using", serializer);
        JDefinedClass deserializer = pkg._getClass(type.name() + "Deserializer");
        klass.annotate(JsonDeserialize.class).param("using", deserializer);
    }

    private void addValueField(JDefinedClass klass) {
        klass.field(JMod.PRIVATE, codeModel._ref(Object.class), VALUE);
    }

    private void addValueGetter(JDefinedClass klass) {
        JMethod getter = klass.method(JMod.PUBLIC, codeModel._ref(Object.class), VALUE);
        getter.body()._return(klass.fields().get(VALUE));
    }

    private void addVariantChecker(JDefinedClass klass, TypeDeclaration variant) {
        String methodName = Names.getCheckerName(variant.name());
        JDefinedClass variantClass = pkg._getClass(variant.name());
        JMethod checker = klass.method(JMod.PUBLIC, codeModel.BOOLEAN, methodName);
        checker.body()._return(klass.fields().get(VALUE)._instanceof(variantClass));
    }

    private void addVariantGetter(JDefinedClass klass, TypeDeclaration variant) {
        String methodName = Names.getGetterName(variant.name());
        JDefinedClass variantClass = pkg._getClass(variant.name());
        JMethod getter = klass.method(JMod.PUBLIC, variantClass, methodName);
        getter.body()._return(JExpr.cast(variantClass, klass.fields().get(VALUE)));
    }

    private void addVariantSetter(JDefinedClass klass, TypeDeclaration variant) {
        String methodName = Names.getSetterName(variant.name());
        String paramName = Names.buildVariableName(variant.name());
        JDefinedClass variantClass = pkg._getClass(variant.name());
        JMethod setter = klass.method(JMod.PUBLIC, codeModel.VOID, methodName);
        JVar param = setter.param(variantClass, paramName);
        setter.body().assign(JExpr._this().ref(VALUE), param);
    }
}
