/*
 * Copyright 2016 OPS4J Contributors
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.engine.resolver.CombinedIndexResolver;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.engine.resolver.ThisResolver;
import org.trimou.handlebars.HelpersBuilder;

public class TemplateEngine {

    private static Logger log = LoggerFactory.getLogger(TemplateEngine.class);

    private MustacheEngine engine;

    /**
     * Constructs a template engine for the given subprotocol.
     * @param subprotocol JDBC subprotocol
     */
    public TemplateEngine() {
        // generic SQL templates
        ClassPathTemplateLocator genericLocator = new ClassPathTemplateLocator(100, "trimou",
            "trimou.html");
        engine = MustacheEngineBuilder.newBuilder()
            .setProperty(EngineConfigurationKey.DEFAULT_FILE_ENCODING, "UTF-8")
            .addTemplateLocator(genericLocator)
            .registerHelpers(HelpersBuilder.empty()
                .addEach()
                .addIf()
                .addSwitch()
                .addUnless()
                .build())
            // manually add default extension to avoid META-INF/service classloader issues
            // when running under OSGi
            .omitServiceLoaderConfigurationExtensions()
            .addResolver(new ReflectionResolver())
            .addResolver(new ThisResolver()).addResolver(new MapResolver())
            .addResolver(new CombinedIndexResolver())
            .addGlobalData("markdown", new MarkdownLambda())
            .build();
    }

    /**
     * Renders a template of the given name with the given action, using the parameter name
     * {@code action}.
     *
     * @param templateName
     *            template name
     * @param api
     *            Warp action JAXB model
     * @return rendered template
     */
    public String renderTemplate(String templateName, Object api) {
        Mustache mustache = engine.getMustache(templateName);
        String result = mustache.render(api);
        log.debug(result);
        return result;
    }

    /**
     * Renders a template of the given name with the given string argument. The argument
     * is referenced in the template as {@code {{this}}}.
     *
     * @param templateName
     *            template name
     * @param argument
     *            single template argument
     * @return rendered template
     */
    public String renderTemplate(String templateName, String argument) {
        Mustache mustache = engine.getMustache(templateName);
        String result = mustache.render(argument);
        log.debug(result);
        return result;
    }
}
