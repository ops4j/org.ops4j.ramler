/*
 * Copyright 2019 OPS4J Contributors
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

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.Collections;

import org.ops4j.ramler.common.exc.GeneratorException;
import org.ops4j.ramler.common.helper.NameFactory;
import org.ops4j.ramler.common.model.Annotations;
import org.ops4j.ramler.common.model.ApiTraverser;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.trimou.util.ImmutableMap;

/**
 * Generates a TypeScript client service for a given RAML resource.
 *
 * @author Harald Wellmann
 *
 */
public class ServiceCreatingApiVisitor implements ApiVisitor {

    private TypeScriptGeneratorContext context;
    private StringBuilder output;
    private Resource outerResource;
    private Resource innerResource;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ServiceCreatingApiVisitor(TypeScriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitResourceStart(Resource resource) {
        ApiTraverser traverser = new ApiTraverser(context.getApiModel());
        if (outerResource == null) {
            outerResource = resource;
            this.output = context.startOutput();

            String serviceName = buildServiceName(resource, context.getConfig()) + "Service";
            String resourceName = ResourceCreatingApiVisitor.buildResourceInterfaceName(resource,
                context.getConfig());

            ResourceImportApiVisitor importVisitor = new ResourceImportApiVisitor(context);
            importVisitor.addTypeToImports(resourceName);
            traverser.traverse(resource, importVisitor);
            output.append("\n");

            context.getMustache("serviceStart")
                .render(context.getOutput(), ImmutableMap.of(
                    "serviceName", serviceName,
                    "resourceName", resourceName,
                    "baseUrlToken", "CRUD_BASE_URL"));

        }
        else if (innerResource == null) {
            innerResource = resource;
        }
        else {
            throw new GeneratorException("cannot handle resources nested more than two levels");
        }

        for (Method method : resource.methods()) {
            ServiceMethodApiVisitor bodyVisitor = new ServiceMethodApiVisitor(context);
            traverser.traverse(method, bodyVisitor);
        }
    }

    @Override
    public void visitResourceEnd(Resource resource) {
        if (innerResource != null) {
            innerResource = null;
            return;
        }
        outerResource = null;
        context.getMustache("objectEnd")
            .render(context.getOutput(), Collections.emptyMap());
        context.writeToFile(output.toString(),
            buildServiceName(resource, context.getConfig()), "service");
    }

    /**
     * Builds the name of the Angular service client for the given RAML resource.
     *
     * @param resource
     *            RAML resource
     * @param config
     *            generator configuration
     * @return interface name
     */
    public static String buildServiceName(final Resource resource,
        TypeScriptConfiguration config) {
        String rawName = defaultIfBlank(Annotations.findCodeName(resource),
            resource.relativeUri()
                .value());
        String resourceInterfaceName = NameFactory.buildCodeFriendlyName(rawName);

        if (isBlank(resourceInterfaceName)) {
            resourceInterfaceName = "Root";
        }
        return resourceInterfaceName;
    }
}
