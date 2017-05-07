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

import org.ops4j.ramler.html.render.ExampleSpecJsonRenderer;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.HelperValidator;
import org.trimou.handlebars.Options;

/**
 * Trimou helper for rendering RAML examples as pretty-printed JSON strings.
 *
 * @author Harald Wellmann
 *
 */
public class ExampleHelper extends BasicHelper {

    public static final String NAME = "example";

    private ExampleSpecJsonRenderer renderer = new ExampleSpecJsonRenderer();

    private void validateRuntimeParameters(Options options) {
        if (!(options.getParameters().get(0) instanceof TypeDeclaration)) {
            throw HelperValidator.newValidationException("Parameter 0 must be a TypeDeclaration",
                ExampleHelper.class, options);
        }

        if (!(options.getParameters().get(1) instanceof ExampleSpec)) {
            throw HelperValidator.newValidationException("Parameter 1 must be an ExampleSpec",
                ExampleHelper.class, options);
        }
    }

    @Override
    public void execute(Options options) {
        validateRuntimeParameters(options);
        TypeDeclaration type = (TypeDeclaration) options.getParameters().get(0);
        ExampleSpec example = (ExampleSpec) options.getParameters().get(1);
        String json = renderer.prettyPrint(type, example);
        options.append(json);

    }

    @Override
    protected int numberOfRequiredParameters() {
        return 2;
    }
}
