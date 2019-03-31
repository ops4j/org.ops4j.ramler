/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.ops4j.ramler.java;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * <p>
 * Abstract Constants class.
 * </p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JavaConstants {

    /** Constant <code>JAVA_KEYWORDS</code> */
    public static final Set<String> JAVA_KEYWORDS = Collections
        .unmodifiableSet(new HashSet<String>(Arrays.asList("abstract", "assert", "boolean", "break",
            "byte", "case", "catch", "char", "class", "const", "continue", "default", "do",
            "double", "else", "enum", "extends", "false", "final", "finally", "float", "for",
            "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public", "return", "short",
            "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "true", "try", "void", "volatile", "while")));

    /** Constant <code>JAXRS_HTTP_METHODS</code> */
    public static final List<Class<? extends Annotation>> JAXRS_HTTP_METHODS = Collections
        .unmodifiableList(Arrays.asList(DELETE.class, GET.class, HEAD.class, OPTIONS.class,
            POST.class, PUT.class));

    /**
     * Name of value property of annotation types.
     */
    public static final String VALUE = "value";

    /**
     * Name of Ramler {@code typeArgs} annotation.
     */
    public static final String TYPE_ARGS = "typeArgs";

    /**
     * Name of Ramler {@code typeVar} annotation.
     */
    public static final String TYPE_VAR = "typeVar";

    /**
     * Name of Ramler {@code typeVars} annotation.
     */
    public static final String TYPE_VARS = "typeVars";

    /**
     * Name of discriminator field in generated POJOs.
     */
    public static final String DISCRIMINATOR = "DISCRIMINATOR";


    /**
     * Name of built-in RAML object type.
     */
    public static final String OBJECT = "object";

    /**
     * Name of built-in RAML integer type.
     */
    public static final String INTEGER = "integer";

    /**
     * Name of built-in RAML integer type.
     */
    public static final String STRING = "string";

    private JavaConstants() {
        throw new UnsupportedOperationException();
    }
}
