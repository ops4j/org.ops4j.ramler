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
package org.ops4j.ramler.common.helper;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.ramler.common.model.Annotations;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

public abstract class NameFactory {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])[A-Z]");

    public abstract Set<String> getReservedWords();

    /**
     * <p>
     * buildResourceInterfaceName.
     * </p>
     *
     * @param resource
     *            a RAML resource
     * @param suffix
     *            Name suffix
     * @return a {@link java.lang.String} object.
     */
    public String buildResourceInterfaceName(final Resource resource, String suffix) {
        String rawName = defaultIfBlank(Annotations.findCodeName(resource),
            resource.relativeUri()
                .value());
        String resourceInterfaceName = buildJavaFriendlyName(rawName);

        if (isBlank(resourceInterfaceName)) {
            resourceInterfaceName = "Root";
        }
        return resourceInterfaceName.concat(suffix);
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
    public String buildVariableName(final String source) {
        final String name = uncapitalize(buildJavaFriendlyName(source));

        return getReservedWords().contains(name) ? ("$" + name) : name;
    }

    /**
     * Builds a variable name for the given property. The result will be a camel-case identifier
     * starting with a lower-case character. If the name would conflict with a Java keyword, a "$"
     * character is prefixed.
     *
     * @param property
     *            RAML property
     * @return legal Java identifier.
     */
    public String buildVariableName(TypeDeclaration property) {
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
        String friendlyName = buildJavaFriendlyName(source);
        Matcher m = CAMEL_CASE_PATTERN.matcher(friendlyName);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "_" + m.group());
        }
        m.appendTail(sb);

        String constantName = sb.toString()
            .toUpperCase();

        if (isDigits(left(constantName, 1))) {
            constantName = "_" + constantName;
        }

        return constantName;
    }

    /**
     * Converts a camel case name to lower-case kebab case, using hyphens as separators. E.g.
     * {@code UserGroup -> user-group}.
     *
     * @param source
     *            camel case string
     * @return hyphen-separated lower-case string
     */
    public static String buildLowerKebabCaseName(String source) {
        Matcher m = CAMEL_CASE_PATTERN.matcher(source);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "-" + m.group());
        }
        m.appendTail(sb);

        String friendlyName = sb.toString()
            .toLowerCase();

        if (isDigits(left(friendlyName, 1))) {
            friendlyName = "-" + friendlyName;
        }

        return friendlyName;
    }

    /**
     * Gets the name of the checker method for a given boolean field. Example:
     * {@code fooBar -> isFooBar}
     *
     * @param fieldName
     *            field name
     * @return checker method name
     */
    public static String getCheckerName(String fieldName) {
        return getAccessorName("is", fieldName);
    }

    /**
     * Gets the name of the getter method for a given field. Example: {@code fooBar -> getFooBar}
     *
     * @param fieldName
     *            field name
     * @return getter method name
     */
    public static String getGetterName(String fieldName) {
        return getAccessorName("get", fieldName);
    }

    /**
     * Gets the name of the setter method for a given field. Example: {@code fooBar -> setFooBar}
     *
     * @param fieldName
     *            field name
     * @return setter method name
     */
    public static String getSetterName(String fieldName) {
        return getAccessorName("set", fieldName);
    }

    /**
     * Generates an accessor method name in with the given prefix and the given field name. Any
     * leading "$" is stripped from the field name, and its first character is capitalized.
     *
     * @param prefix
     *            name prefix
     * @param fieldName
     *            name of accessed field
     * @return accessor name
     */
    public static String getAccessorName(String prefix, String fieldName) {
        int start = 0;
        if (fieldName.startsWith("$")) {
            start++;
        }
        StringBuilder buffer = new StringBuilder(prefix);
        buffer.append(fieldName.substring(start, start + 1)
            .toUpperCase());
        buffer.append(fieldName.substring(start + 1));
        return buffer.toString();
    }

    public String buildMethodName(Method method, int bodyIndex) {
        String methodName = buildMethodName(method);
        if (bodyIndex > 0) {
            TypeDeclaration responseType = method.responses()
                .get(0)
                .body()
                .get(bodyIndex);
            String codeName = Annotations.findCodeName(responseType);
            if (codeName == null) {
                methodName += Integer.toString(bodyIndex);
            }
            else {
                methodName = codeName;
            }
        }
        return methodName;
    }

    public String buildMethodName(Method method) {
        String name = Annotations.findCodeName(method);
        if (name == null) {
            name = method.displayName()
                .value();
        }
        if (name == null) {
            name = method.method();
        }
        return buildVariableName(name);
    }

}
