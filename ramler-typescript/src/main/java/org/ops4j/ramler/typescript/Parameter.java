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
package org.ops4j.ramler.typescript;

/**
 * Parameter name and type for a method signature.
 *
 * @author Harald Wellmann
 *
 */
public class Parameter {

    private String name;

    private String type;

    /**
     * Creates a parameter with the given name and type.
     *
     * @param name
     *            parameter name
     * @param type
     *            parameter type
     */
    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }
}
