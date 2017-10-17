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
package org.ops4j.ramler.typescript;

import org.ops4j.ramler.model.ApiModel;
import org.ops4j.ramler.typescript.trimou.TypescriptTemplateEngine;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * @author Harald Wellmann
 *
 */
public class TypescriptGeneratorContext {

    private TypescriptConfiguration config;
    private ApiModel apiModel;
    private TypescriptTemplateEngine templateEngine;
    private Appendable output;

    /**
     *
     */
    public TypescriptGeneratorContext(TypescriptConfiguration config) {
        this.config = config;
    }

    /**
     * @return the config
     */
    public TypescriptConfiguration getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(TypescriptConfiguration config) {
        this.config = config;
    }

    /**
     * @return the apiModel
     */
    public ApiModel getApiModel() {
        return apiModel;
    }

    /**
     * @param apiModel the apiModel to set
     */
    public void setApiModel(ApiModel apiModel) {
        this.apiModel = apiModel;
    }

    /**
     * @return the templateEngine
     */
    public TypescriptTemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    /**
     * @param templateEngine the templateEngine to set
     */
    public void setTemplateEngine(TypescriptTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }




    /**
     * @return the output
     */
    public Appendable getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(Appendable output) {
        this.output = output;
    }

    public String getTypescriptType(TypeDeclaration type) {
        if (type instanceof StringTypeDeclaration) {
            return "string";
        }
        if (type instanceof NumberTypeDeclaration) {
            return "number";
        }
        if (type instanceof BooleanTypeDeclaration) {
            return "number";
        }
        if (type instanceof DateTimeTypeDeclaration) {
            return "string";
        }
        if (type instanceof DateTimeOnlyTypeDeclaration) {
            return "string";
        }
        if (type instanceof DateTypeDeclaration) {
            return "string";
        }
        if (type instanceof TimeOnlyTypeDeclaration) {
            return "string";
        }
        if (type instanceof ObjectTypeDeclaration) {
            return type.type();
        }
        if (type instanceof ArrayTypeDeclaration) {
            return apiModel.getItemType(type) + "[]";
        }
        return "__UNDEFINED__";
    }
}
