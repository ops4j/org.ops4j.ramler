package org.ops4j.ramler.html.trimou;

import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.Options;

/**
 * Trimou helper for producing a HTML ID from a resource path by stripping
 * the leading slash.
 * 
 * @author hwellmann
 *
 */
public class MethodIdHelper extends BasicHelper {

    @Override
    public void execute(Options options) {
        String methodName = (String) options.getParameters().get(0);
        options.append(methodName.toLowerCase().replaceAll(" ", ""));

    }

    @Override
    protected int numberOfRequiredParameters() {
        return 1;
    }
}
