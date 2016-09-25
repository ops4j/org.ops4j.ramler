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
public class IdHelper extends BasicHelper {

    @Override
    public void execute(Options options) {
        String resourcePath = (String) options.getParameters().get(0);
        options.append(resourcePath.substring(1));

    }

    @Override
    protected int numberOfRequiredParameters() {
        return 1;
    }
}
