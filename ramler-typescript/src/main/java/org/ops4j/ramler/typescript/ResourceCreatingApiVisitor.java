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
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.trimou.util.ImmutableMap;

/**
 * Generates a TypeScript interface for a given RAML resource.
 *
 * @author Harald Wellmann
 *
 */
public class ResourceCreatingApiVisitor implements ApiVisitor {

    private TypeScriptGeneratorContext context;
    private StringBuilder output;
    private Resource outerResource;
    private Resource innerResource;
    private int numResources;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ResourceCreatingApiVisitor(TypeScriptGeneratorContext context) {
        this.context = context;
    }

    @Override
    public void visitApiEnd(Api api) {
        if (numResources > 0) {
            context.startOutput();
            context.getMustache("restResponse")
                .render(context.getOutput(), Collections.emptyMap());
            context.writeToFile(context.getOutput()
                .toString(), "RestResponse");
        }
    }

    @Override
    public void visitResourceStart(Resource resource) {
        numResources++;
        ApiTraverser traverser = new ApiTraverser(context.getApiModel());
        if (outerResource == null) {
            outerResource = resource;
            this.output = context.startOutput();

            ResourceImportApiVisitor importVisitor = new ResourceImportApiVisitor(context);
            traverser.traverse(resource, importVisitor);
            output.append("\n");

            String interfaceName = buildResourceInterfaceName(resource, context.getConfig());
            context.getMustache("resourceStart")
                .render(context.getOutput(), ImmutableMap.of("name", interfaceName));

        }
        else if (innerResource == null) {
            innerResource = resource;
        }
        else {
            throw new GeneratorException("cannot handle resources nested more than two levels");
        }

        for (Method method : resource.methods()) {
            ResourceMethodApiVisitor bodyVisitor = new ResourceMethodApiVisitor(context);
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
            buildResourceInterfaceName(resource, context.getConfig()));
    }

    public static String buildResourceInterfaceName(final Resource resource,
        TypeScriptConfiguration config) {
        String rawName = defaultIfBlank(Annotations.findCodeName(resource),
            resource.relativeUri()
                .value());
        String resourceInterfaceName = NameFactory.buildCodeFriendlyName(rawName);

        if (isBlank(resourceInterfaceName)) {
            resourceInterfaceName = "Root";
        }
        return resourceInterfaceName.concat("Resource");
    }
}
