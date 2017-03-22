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
package org.ops4j.ramler.generator;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

import org.raml.v2.api.model.v10.resources.Resource;

/**
 * <p>
 * Names class.
 * </p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Names {

    /** Constant <code>GENERIC_PAYLOAD_ARGUMENT_NAME="entity"</code> */
    public static final String GENERIC_PAYLOAD_ARGUMENT_NAME = "entity";

    /** Constant <code>MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME="headers"</code> */
    public static final String MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME = "headers";

    /** Constant <code>EXAMPLE_PREFIX=" e.g. "</code> */
    public static final String EXAMPLE_PREFIX = " e.g. ";

    private Names() {
        // hidden utility class constructor
    }
    /**
     * <p>
     * buildResourceInterfaceName.
     * </p>
     *
     * @param resource
     *            a RAML resource
     * @param config Ramler configuration
     * @return a {@link java.lang.String} object.
     */
    public static String buildResourceInterfaceName(final Resource resource, Configuration config) {
        String rawName = defaultIfBlank(resource.displayName().value(),
            resource.relativeUri().value());
        String resourceInterfaceName = buildJavaFriendlyName(rawName);

        if (isBlank(resourceInterfaceName)) {
            resourceInterfaceName = "Root";
        }
        return resourceInterfaceName.concat(config.getInterfaceNameSuffix());
    }

    /**
     * <p>
     * buildVariableName.
     * </p>
     *
     * @param source
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildVariableName(final String source) {
        final String name = uncapitalize(buildJavaFriendlyName(source));

        return Constants.JAVA_KEYWORDS.contains(name) ? "$" + name : name;
    }

    /**
     * <p>
     * buildJavaFriendlyName.
     * </p>
     *
     * @param source
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildJavaFriendlyName(final String source) {
        final String baseName = source.replaceAll("[\\W_]", " ");

        String friendlyName = capitalize(baseName).replaceAll("[\\W_]", "");

        if (isDigits(left(friendlyName, 1))) {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }

    /**
     * Get enum field name from value
     *
     * @param value string to be checked
     * @return a {@link java.lang.String} object.
     */
    public static boolean canBeEnumConstantName(final String value) {
        boolean res = !value.isEmpty();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (i == 0) {
                res &= Character.isJavaIdentifierStart(c);
            }
            else {
                res &= Character.isJavaIdentifierPart(c);
            }
            if (!res) {
                break;
            }
        }
        return res;
    }

    /**
     * Checks if the given strings can be used as enum values.
     * @param values list of strings to be checked
     * @return true if this list of strings can be used as names for enum
     */
    public static boolean isValidEnumValues(java.util.List<String> values) {
        if (values.isEmpty()) {
            return false;
        }
        for (String v : values) {
            if (!canBeEnumConstantName(v)) {
                return false;
            }
        }
        return true;
    }

}
