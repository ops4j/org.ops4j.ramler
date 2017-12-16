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

import java.nio.charset.StandardCharsets;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.engine.locator.FileSystemTemplateLocator;

/**
 * A Trimou template engine configured for Typescript generation.
 *
 * @author Harald Wellmann
 *
 */
public class TypescriptTemplateEngine {

    public static final String TEMPLATE_SUFFIX = "trimou.ts";

    public static final String TEMPLATE_PATH = "trimou";

    private static final int PRIO_CLASS_PATH = 100;

    private static final int PRIO_FILE_SYSTEM = 200;

    private MustacheEngine engine;

    private String templateDir;

    /**
     * Gets the template directory.
     *
     * @return template directory
     */
    public String getTemplateDir() {
        return templateDir;
    }

    /**
     * Set the template directory.
     *
     * @param templateDir
     *            template directory
     */
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    /**
     * Constructs a template engine with some additional helpers and lambdas for Typescript
     * generation.
     */
    public MustacheEngine getEngine() {
        if (engine == null) {
            ClassPathTemplateLocator genericLocator = new ClassPathTemplateLocator(PRIO_CLASS_PATH,
                TEMPLATE_PATH, TEMPLATE_SUFFIX);
            MustacheEngineBuilder builder = MustacheEngineBuilder.newBuilder()
                .setProperty(EngineConfigurationKey.DEFAULT_FILE_ENCODING,
                    StandardCharsets.UTF_8.name())
                .addTemplateLocator(genericLocator);
            if (templateDir != null) {
                builder.addTemplateLocator(
                    new FileSystemTemplateLocator(PRIO_FILE_SYSTEM, templateDir, TEMPLATE_SUFFIX));
            }
            engine = builder.build();
        }
        return engine;
    }
}
