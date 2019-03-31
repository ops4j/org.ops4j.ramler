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
package org.ops4j.ramler.openapi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.ops4j.ramler.common.exc.GeneratorException;
import org.ops4j.ramler.common.model.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context information shared by all API visitors generating OpenApi code.
 *
 * @author Harald Wellmann
 *
 */
public class OpenApiGeneratorContext {

    private static Logger log = LoggerFactory.getLogger(OpenApiGeneratorContext.class);

    private OpenApiConfiguration config;

    private ApiModel apiModel;

    /**
     * Creates a generator context for the given configuration.
     *
     * @param config
     *            OpenApi generator configuration
     */
    public OpenApiGeneratorContext(OpenApiConfiguration config) {
        this.config = config;
    }

    /**
     * Gets the generator configuration.
     *
     * @return generator configuration
     */
    public OpenApiConfiguration getConfig() {
        return config;
    }

    /**
     * Sets the generator configuration.
     *
     * @param config
     *            generator configuration
     */
    public void setConfig(OpenApiConfiguration config) {
        this.config = config;
    }

    /**
     * Gets the RAML API model.
     *
     * @return API model
     */
    public ApiModel getApiModel() {
        return apiModel;
    }

    /**
     * Sets the RAML API model.
     *
     * @param apiModel
     *            API model
     */
    public void setApiModel(ApiModel apiModel) {
        this.apiModel = apiModel;
    }

    public void writeToFile(String content, String fileName) {
        File tsFile = new File(config.getTargetDir(), fileName);
        log.debug("generating {}\n{}", fileName, content);
        try {
            Files.write(tsFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException exc) {
            throw new GeneratorException(exc);
        }
    }


}
