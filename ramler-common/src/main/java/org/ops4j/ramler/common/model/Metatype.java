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
package org.ops4j.ramler.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Metatype of RAML types. Each type has a metatype. For primitive types, there is a one-to-one
 * correspondence between types and metatypes. For structured types, the metatype indicates the kind
 * of structure, i.e. array, union or object.
 *
 * @author Harald Wellmann
 *
 */
public enum Metatype {

    /** Any type. */
    ANY("any"),

    /** Null type. */
    NULL("null"),

    /** Boolean type. */
    BOOLEAN("boolean"),

    /** Number type. */
    NUMBER("number"),

    /** Integer type. */
    INTEGER("integer"),

    /** String type. */
    STRING("string"),

    /** File type. */
    FILE("file"),

    /** Time type. */
    TIME_ONLY("time-only"),

    /** Datetime type with timezone. */
    DATETIME("datetime"),

    /** Datetime type without timezone. */
    DATETIME_ONLY("datetime-only"),

    /** Date type. */
    DATE_ONLY("date"),

    /** Object type. */
    OBJECT("object"),

    /** Array type. */
    ARRAY("array"),

    /** Union type. */
    UNION("union");

    private static Map<String, Metatype> literalToTypeMap;

    private String literal;

    static {
        literalToTypeMap = new HashMap<>();
        for (Metatype metatype : values()) {
            literalToTypeMap.put(metatype.getLiteral(), metatype);
        }
    }

    Metatype(String literal) {
        this.literal = literal;
    }

    /**
     * @return the literal
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Checks if the given type is a built-in type.
     *
     * @param typeName
     *            type name
     * @return true if type is built-in
     */
    public static boolean isBuiltIn(String typeName) {
        return literalToTypeMap.containsKey(typeName);
    }
}
