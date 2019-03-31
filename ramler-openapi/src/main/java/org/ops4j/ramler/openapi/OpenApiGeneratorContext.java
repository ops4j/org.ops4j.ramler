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

import org.ops4j.ramler.common.helper.FileHelper;
import org.ops4j.ramler.common.model.ApiModel;

/**
 * Context information shared by all API visitors generating OpenApi code.
 *
 * @author Harald Wellmann
 *
 */
public class OpenApiGeneratorContext {

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

    /**
     * Writes the given content to the file with the given name in the target directory.
     *
     * @param content
     *            string content
     * @param fileName
     *            file name
     */
    public void writeToFile(String content, String fileName) {
        File file = new File(config.getTargetDir(), fileName);
        FileHelper.writeToFile(content, file);
    }
}
