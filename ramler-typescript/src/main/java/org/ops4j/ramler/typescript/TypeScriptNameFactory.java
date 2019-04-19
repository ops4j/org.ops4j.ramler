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

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.ops4j.ramler.common.helper.NameFactory;
import org.ops4j.ramler.common.model.Annotations;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * A name factory which respects TypeScript reserved words.
 *
 * @author Harald Wellmann
 *
 */
public class TypeScriptNameFactory extends NameFactory {

    private static final Set<String> TYPESCRIPT_KEYWORDS = Collections
        .unmodifiableSet(new HashSet<String>(Arrays.asList("abstract", "assert", "boolean", "break",
            "byte", "case", "catch", "char", "class", "const", "continue", "default", "do",
            "double", "else", "enum", "extends", "false", "final", "finally", "float", "for",
            "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public", "return", "short",
            "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "true", "try", "void", "volatile", "while")));

    private static final String UNNAMED_RESOURCE = "Root";

    @Override
    public Set<String> getReservedWords() {
        return TYPESCRIPT_KEYWORDS;
    }

    /**
     * Builds the name of the TypeScript interface corresponding to the given RAML resource.
     *
     * @param resource
     *            RAML resource
     * @param config
     *            generator configuration
     * @return interface name
     */
    public static String buildResourceInterfaceName(final Resource resource,
        TypeScriptConfiguration config) {
        String rawName = defaultIfBlank(Annotations.findCodeName(resource),
            resource.relativeUri()
                .value());
        String resourceInterfaceName = NameFactory.buildCodeFriendlyName(rawName);

        if (isBlank(resourceInterfaceName)) {
            resourceInterfaceName = UNNAMED_RESOURCE;
        }
        return resourceInterfaceName.concat("Resource");
    }

    /**
     * Builds the name of the Angular service client for the given RAML resource.
     *
     * @param resource
     *            RAML resource
     * @param config
     *            generator configuration
     * @return interface name
     */
    public static String buildServiceName(final Resource resource,
        TypeScriptConfiguration config) {
        String rawName = defaultIfBlank(Annotations.findCodeName(resource),
            resource.relativeUri()
                .value());
        String resourceInterfaceName = NameFactory.buildCodeFriendlyName(rawName);

        if (isBlank(resourceInterfaceName)) {
            resourceInterfaceName = UNNAMED_RESOURCE;
        }
        return resourceInterfaceName;
    }
}
