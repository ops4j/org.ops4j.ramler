package org.ops4j.ramler.html.trimou;

import org.ops4j.ramler.html.ExampleSpecJsonRenderer;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.HelperValidator;
import org.trimou.handlebars.Options;

/**
 * Trimou helper for rendering RAML examples as pretty-printed JSON strings.
 * @author hwellmann
 *
 */
public class ExampleHelper extends BasicHelper {

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
