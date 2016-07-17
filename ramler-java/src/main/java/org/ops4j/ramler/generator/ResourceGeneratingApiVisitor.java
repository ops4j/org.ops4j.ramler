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
package org.ops4j.ramler.generator;

import static java.util.stream.Collectors.toMap;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Generated;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.ops4j.ramler.exc.Exceptions;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

public class ResourceGeneratingApiVisitor implements ApiVisitor {

    private GeneratorContext context;

    private JCodeModel codeModel;

    private JPackage pkg;

    private Map<String, Class<? extends Annotation>> httpMethodAnnotations;

    private JDefinedClass klass;

    private Resource outerResource;

    private Resource innerResource;

    public ResourceGeneratingApiVisitor(GeneratorContext context) {
        this.context = context;
        this.codeModel = context.getCodeModel();
        this.pkg = context.getApiPackage();
        httpMethodAnnotations = Constants.JAXRS_HTTP_METHODS.stream()
            .collect(toMap(Class::getSimpleName, Function.identity()));
    }

    @Override
    public void visitResourceStart(Resource resource) {
        try {
            if (outerResource == null) {
                outerResource = resource;
                klass = pkg
                    ._interface(Names.buildResourceInterfaceName(resource, context.getConfig()));
                klass.annotate(Generated.class).param("value", getClass().getName()).param("date",
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
                klass.annotate(Path.class).param("value", resource.resourcePath());
            }
            else {
                innerResource = resource;
            }

        }
        catch (JClassAlreadyExistsException exc) {
            throw Exceptions.unchecked(exc);
        }
    }

    @Override
    public void visitResourceEnd(Resource resource) {
        if (outerResource == resource) {
            klass = null;
            outerResource = null;
        }
        if (innerResource == resource) {
            innerResource = null;
        }
    }

    @Override
    public void visitMethodStart(Method method) {
        String methodName = Names.buildVariableName(method.displayName().value());
        JMethod codeMethod = klass.method(JMod.NONE, klass, methodName);
        addHttpMethodAnnotation(method.method(), codeMethod);

        if (innerResource != null) {
            codeMethod.annotate(Path.class).param("value", innerResource.relativeUri().value());
        }

        if (!method.body().isEmpty()) {
            TypeDeclaration body = method.body().get(0);
            if (body.name().equals("multipart/form-data")) {
                addFormParameters(codeMethod, body);
            }
            else {
                codeMethod.param(context.getJavaType(body), Names.buildVariableName(body.type()));
            }
        }

        for (TypeDeclaration pathParam : method.resource().uriParameters()) {
            JVar param = codeMethod.param(context.getJavaType(pathParam),
                Names.buildVariableName(pathParam.name()));
            param.annotate(PathParam.class).param("value", pathParam.name());
        }

        for (TypeDeclaration queryParam : method.queryParameters()) {
            JVar param = codeMethod.param(context.getJavaType(queryParam),
                Names.buildVariableName(queryParam.name()));
            param.annotate(QueryParam.class).param("value", queryParam.name());
            if (queryParam.defaultValue() != null) {
                param.annotate(DefaultValue.class).param("value", queryParam.defaultValue());
            }
        }

        org.raml.v2.api.model.v10.bodies.Response response = method.responses().get(0);
        if (response.body().isEmpty()) {
            codeMethod.type(codeModel.VOID);
        }
        else {
            TypeDeclaration body = response.body().get(0);

            codeMethod.type(context.getJavaType(body));
        }

        if (method.displayName() != null) {
            codeMethod.javadoc().add(method.displayName().value());
        }
    }

    private void addFormParameters(JMethod codeMethod, TypeDeclaration body) {
        ObjectTypeDeclaration type = (ObjectTypeDeclaration) body;
        for (TypeDeclaration param : type.properties()) {
            addFormParameter(codeMethod, param);
        }
    }

    private void addFormParameter(JMethod codeMethod, TypeDeclaration formParam) {
        JVar param = codeMethod.param(context.getJavaType(formParam),
            Names.buildVariableName(formParam.name()));
        param.annotate(FormDataParam.class).param("value", formParam.name());

        if (formParam instanceof FileTypeDeclaration) {
            JVar detail = codeMethod.param(codeModel._ref(FormDataContentDisposition.class),
                Names.buildVariableName(formParam.name()) + "Detail");
            detail.annotate(FormDataParam.class).param("value", formParam.name());
        }
    }

    private void addHttpMethodAnnotation(final String httpMethod, final JAnnotatable annotatable) {
        Class<? extends Annotation> annotationClass = httpMethodAnnotations
            .get(httpMethod.toUpperCase());
        if (annotationClass == null) {
            throw new IllegalArgumentException("unsupported HTTP method: " + httpMethod);
        }
        annotatable.annotate((Class<? extends Annotation>) annotationClass);
    }
}
