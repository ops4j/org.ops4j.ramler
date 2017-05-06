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
package org.ops4j.ramler.model;

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
    ANY,

    /** Null type. */
    NULL,

    /** Boolean type. */
    BOOLEAN,

    /** Number type. */
    NUMBER,

    /** Integer type. */
    INTEGER,

    /** String type. */
    STRING,

    /** File type. */
    FILE,

    /** Time type. */
    TIME_ONLY,

    /** Datetime type with timezone. */
    DATETIME,

    /** Datetime type without timezone. */
    DATETIME_ONLY,

    /** Date type. */
    DATE_ONLY,

    /** Object type. */
    OBJECT,

    /** Array type. */
    ARRAY,

    /** Union type. */
    UNION
}
