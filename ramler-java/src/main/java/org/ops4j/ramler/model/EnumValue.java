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
package org.ops4j.ramler.model;

/**
 * Model of complex {@code (enum)} annotation, used by Ramler to enrich and replace the simple
 * {@code enum} facet of RAML.
 *
 * @author Harald Wellmann
 *
 */
public class EnumValue {

    private String name;
    private String description;

    /**
     * Creates an enumeration value with the given name and the given description.
     *
     * @param name
     *            value name
     * @param description
     *            textual description
     */
    public EnumValue(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the name of this value.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this value.
     *
     * @return
     */
    public String getDescription() {
        return description;
    }
}
