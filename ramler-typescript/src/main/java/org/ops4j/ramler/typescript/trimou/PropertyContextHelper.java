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
package org.ops4j.ramler.typescript.trimou;

import java.util.Map;

import org.ops4j.ramler.generator.Names;
import org.ops4j.ramler.typescript.TypescriptGeneratorContext;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.HelperValidator;
import org.trimou.handlebars.Options;
import org.trimou.util.ImmutableMap;

/**
 * @author Harald Wellmann
 *
 */
public class PropertyContextHelper extends BasicHelper {

    public static final String NAME = "propertyContext";

    @Override
    public void execute(Options options) {
        validateRuntimeParameters(options);
        TypescriptGeneratorContext tsContext = (TypescriptGeneratorContext) options.getValue("_context");

        TypeDeclaration type = (TypeDeclaration) options.peek();
        String tsPropType = tsContext.getTypescriptType(type);
        String tsType = tsPropType;
        while (tsType.endsWith("[]")) {
            tsType = tsPropType.substring(0, tsType.length() - 2);
        }
        String tsFile = null;
        if (Character.isUpperCase(tsPropType.charAt(0))) {
            tsFile = Names.buildLowerKebabCaseName(tsType);
        }
        Map<String, String> context = ImmutableMap.of("name", type.name(), "tsPropType", tsPropType, "tsType", tsType, "tsFile",
                tsFile);
        options.push(context);
        options.fn();
        options.pop();
    }

    /**
     * @param options
     */
    private void validateRuntimeParameters(Options options) {
        if (!(options.peek() instanceof TypeDeclaration)) {
            throw HelperValidator.newValidationException("Context object must be a TypeDeclaration",
                    PropertyContextHelper.class, options);
        }
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

}
