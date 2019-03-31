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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.ramler.common.model.Annotations;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
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

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])[A-Z]");

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
     * @param config
     *            Ramler configuration
     * @return a {@link java.lang.String} object.
     */
    public static String buildResourceInterfaceName(final Resource resource, Configuration config) {
        String rawName = defaultIfBlank(Annotations.findCodeName(resource),
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

        return Constants.JAVA_KEYWORDS.contains(name) ? ("$" + name) : name;
    }

    /**
     * Builds a variable name for the given property. The result will be a camel-case identifier
     * starting with a lower-case character. If the name would conflict with a Java keyword,
     * a "$" character is prefixed.
     *
     * @param property RAML property
     * @return legal Java identifier.
     */
    public static String buildVariableName(TypeDeclaration property) {
        return defaultIfBlank(Annotations.findCodeName(property),
            buildVariableName(property.name()));
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
     * Builds a name for a constant, converting camel case to upper snake case. Example:
     * {@code httpMethod -> HTTP_METHOD}.
     *
     * @param source
     *            any name
     * @return upper case string with underscores as word separators
     */
    public static String buildConstantName(String source) {
        Matcher m = CAMEL_CASE_PATTERN.matcher(source);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "_" + m.group());
        }
        m.appendTail(sb);

        String friendlyName = sb.toString().toUpperCase();

        if (isDigits(left(friendlyName, 1))) {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }

    /**
     * Converts a camel case name to lower-case kebab case, using hyphens as separators.
     * E.g. {@code UserGroup -> user-group}.
     *
     * @param source camel case string
     * @return hyphen-separated lower-case string
     */
    public static String buildLowerKebabCaseName(String source) {
        Matcher m = CAMEL_CASE_PATTERN.matcher(source);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "-" + m.group());
        }
        m.appendTail(sb);

        String friendlyName = sb.toString().toLowerCase();

        if (isDigits(left(friendlyName, 1))) {
            friendlyName = "-" + friendlyName;
        }

        return friendlyName;
    }


    /**
     * Get enum field name from value
     *
     * @param value
     *            string to be checked
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
     *
     * @param values
     *            list of strings to be checked
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

    public static String getCheckerName(String fieldName) {
        return getAccessorName("is", fieldName);
    }

    public static String getGetterName(String fieldName) {
        return getAccessorName("get", fieldName);
    }

    public static String getSetterName(String fieldName) {
        return getAccessorName("set", fieldName);
    }

    /**
     * Generates an accessor method name in with the given prefix and the given field name.
     * Any leading "$" is stripped from the field name, and its first character is capitalized.
     * @param prefix name prefix
     * @param fieldName name of accessed field
     * @return accessor name
     */
    public static String getAccessorName(String prefix, String fieldName) {
        int start = 0;
        if (fieldName.startsWith("$")) {
            start++;
        }
        StringBuilder buffer = new StringBuilder(prefix);
        buffer.append(fieldName.substring(start, start + 1).toUpperCase());
        buffer.append(fieldName.substring(start + 1));
        return buffer.toString();
    }
}
