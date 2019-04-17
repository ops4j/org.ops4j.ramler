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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ops4j.ramler.common.helper.NameFactory;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.trimou.util.ImmutableMap;

/**
 * Creates the methods of a TypeScript client service corresponding to a RAML resource.
 *
 * @author Harald Wellmann
 *
 */
public class ServiceMethodApiVisitor implements ApiVisitor {

    private TypeScriptGeneratorContext context;
    private NameFactory nameFactory;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public ServiceMethodApiVisitor(TypeScriptGeneratorContext context) {
        this.context = context;
        this.nameFactory = new TypeScriptNameFactory();
    }

    @Override
    public void visitMethodStart(Method method) {
        List<TypeDeclaration> bodies = method.body();
        TypeDeclaration body = bodies.isEmpty() ? null : bodies.get(0);
        String name = nameFactory.buildMethodName(method, -1);
        Response response = method.responses()
            .get(0);
        String returnType = "void";
        List<TypeDeclaration> responseBody = response.body();
        if (!responseBody.isEmpty()) {
            returnType = context.typeWithArgs(responseBody.get(0));
        }
        List<Parameter> parameters = new ArrayList<>();
        addBodyParameters(body, parameters);
        addPathParameters(method, parameters);
        addQueryParameters(method, parameters);

        String bodyArg = (body == null) ? "" : ", body";
        String path = method.resource()
            .resourcePath()
            .replace("{", "$" + "{");
        String url = "`${this.baseUrl}" + path + "`";
        if (path.isEmpty() || path.equals("/")) {
            url = "this.baseUrl";
        }

        String options = "";
        if (!method.queryParameters()
            .isEmpty()) {
            options = ", { params: {";
            boolean first = true;
            for (TypeDeclaration param : method.queryParameters()) {
                if (first) {
                    first = false;
                }
                else {
                    options += ", ";
                }
                options += param.name();
            }
            options += "} }";
        }

        Map<String, Object> contextObject = ImmutableMap.<String, Object> builder()
            .put("name", name)
            .put("returnType", returnType)
            .put("parameters", parameters)
            .put("httpMethod", method.method())
            .put("url", url)
            .put("body", bodyArg)
            .put("options", options)
            .build();
        context.getMustache("serviceMethod")
            .render(context.getOutput(), contextObject);
    }

    private void addBodyParameters(TypeDeclaration body, List<Parameter> parameters) {
        if (body != null) {
            parameters.add(new Parameter("body", context.typeWithArgs(body)));
        }
    }

    private void addPathParameters(Method method, List<Parameter> parameters) {
        context.getApiModel()
            .findAllUriParameters(method)
            .stream()
            .map(p -> new Parameter(p.name(), context.typeWithArgs(p)))
            .forEach(parameters::add);
    }

    private void addQueryParameters(Method method, List<Parameter> parameters) {
        method.queryParameters()
            .stream()
            .map(p -> new Parameter(p.name(), context.typeWithArgs(p)))
            .forEach(parameters::add);
    }
}
