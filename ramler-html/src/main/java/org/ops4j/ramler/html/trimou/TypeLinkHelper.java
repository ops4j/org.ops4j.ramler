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
package org.ops4j.ramler.html.trimou;

import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.HelperValidator;
import org.trimou.handlebars.Options;

/**
 * Trimou helper for rendering links to type names. The helper strips brackets
 * from array type names and generates links of the form {@code #itemType}.
 *
 * @author Harald Wellmann
 *
 */
public class TypeLinkHelper extends BasicHelper {

    public static final String NAME = "typeLink";

    private void validateRuntimeParameters(Options options) {
        if (!(options.getParameters().get(0) instanceof String)) {
            throw HelperValidator.newValidationException("Parameter 0 must be a String",
                TypeLinkHelper.class, options);
        }
    }

    @Override
    public void execute(Options options) {
        validateRuntimeParameters(options);
        String typeName = (String) options.getParameters().get(0);
        typeName = typeName.replace("[]", "");
        options.append("#");
        options.append(typeName);
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 1;
    }
}
