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
package org.ops4j.ramler.html;

/**
 * Context for the HTML generator.
 * 
 * @author Harald Wellmann
 *
 */
public class HtmlContext {

    private HtmlConfiguration config;

    /**
     * Constructs a context for the HTML generator from the given configuration.
     * 
     * @param config
     *            HTML generator configuration
     */
    public HtmlContext(HtmlConfiguration config) {
        this.config = config;
    }

    /**
     * Gets the HTML generator configuration.
     * 
     * @return generator
     */
    public HtmlConfiguration getConfig() {
        return config;
    }
}
