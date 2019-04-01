package org.ops4j.ramler.openapi;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.Parameter.In;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.ops4j.ramler.common.exc.GeneratorException;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.media.ContentImpl;
import io.smallrye.openapi.api.models.media.MediaTypeImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;
import io.smallrye.openapi.api.models.parameters.RequestBodyImpl;
import io.smallrye.openapi.api.models.responses.APIResponseImpl;
import io.smallrye.openapi.api.models.responses.APIResponsesImpl;

public class OpenApiResourceVisitor implements ApiVisitor {

    private OpenAPI openApi;

    private Resource outerResource;
    private Resource innerResource;
    private PathItem pathItem;
    private Operation operation;
    private SchemaBuilder schemaBuilder;



    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public OpenApiResourceVisitor(OpenApiGeneratorContext context) {
        this.openApi = context.getOpenApi();
        this.schemaBuilder = context.getSchemaBuilder();
    }

    @Override
    public void visitResourceStart(Resource resource) {
        if (outerResource == null) {
            outerResource = resource;
        }
        else if (innerResource == null) {
            innerResource = resource;
        }
        else {
            throw new GeneratorException("cannot handle resources nested more than two levels");
        }

        pathItem = new PathItemImpl();
        if (resource.description() != null) {
            pathItem.setDescription(resource.description().value());
        }
        pathItem.setSummary(resource.displayName().value());
        openApi.getPaths().addPathItem(resource.resourcePath(), pathItem);
    }

    @Override
    public void visitResourceEnd(Resource resource) {
        if (resource.equals(outerResource)) {
            outerResource = null;
        }
        if (resource.equals(innerResource)) {
            innerResource = null;
        }
    }

    @Override
    public void visitMethodStart(Method method) {
        operation = new OperationImpl();
        switch (method.method()) {
        case "delete":
            pathItem.setDELETE(operation);
            break;
        case "get":
            pathItem.setGET(operation);
            break;
        case "head":
            pathItem.setHEAD(operation);
            break;
        case "options":
            pathItem.setOPTIONS(operation);
            break;
        case "patch":
            pathItem.setPATCH(operation);
            break;
        case "put":
            pathItem.setPUT(operation);
            break;
        case "post":
            pathItem.setPOST(operation);
            break;
        case "trace":
            pathItem.setTRACE(operation);
            break;
        default:
            throw new IllegalArgumentException("unknown HTTP method: " + method.method());
        }

        pathItem.setSummary(method.displayName().value());
        if (method.description() != null) {
            pathItem.setDescription(method.description().value());
        }

        List<TypeDeclaration> pathParams = new ArrayList<>();
        if (method.resource().parentResource() != null) {
            pathParams.addAll(method.resource().parentResource().uriParameters());
        }
        pathParams.addAll(method.resource().uriParameters());

        for (TypeDeclaration pathParam : pathParams) {
            Parameter parameter = new ParameterImpl();
            parameter.setName(pathParam.name());
            parameter.setIn(In.PATH);
            parameter.setRequired(true);
            parameter.setSchema(schemaBuilder.toSchema(pathParam));
            operation.addParameter(parameter);
        }

        for (TypeDeclaration queryParam : method.queryParameters()) {
            Parameter parameter = new ParameterImpl();
            parameter.setName(queryParam.name());
            parameter.setIn(In.QUERY);
            parameter.setRequired(queryParam.required());
            parameter.setSchema(schemaBuilder.toSchema(queryParam));
            operation.addParameter(parameter);
        }

        if (!method.body().isEmpty()) {
            TypeDeclaration body = method.body().get(0);
            if (body.name().equals(MediaType.MULTIPART_FORM_DATA)) {
                // addFormParameters(codeMethod, body);
            }
            else {
                RequestBody requestBody = new RequestBodyImpl();
                requestBody.setRequired(body.required());
                Content content = new ContentImpl();
                MediaTypeImpl mediaType = new MediaTypeImpl();
                mediaType.setSchema(schemaBuilder.toSchema(body));
                content.addMediaType(MediaType.APPLICATION_JSON, mediaType);
                requestBody.setContent(content);
                operation.setRequestBody(requestBody);
            }
        }

        APIResponses responses = new APIResponsesImpl();
        operation.setResponses(responses);
        for (Response response : method.responses()) {
            responses.addAPIResponse(response.code().value(), convertResponse(response));
        }
    }

    private APIResponse convertResponse(Response response) {
        APIResponse apiResponse = new APIResponseImpl();
        if (response.description() == null) {
            apiResponse.setDescription("No description");
        } else {
            apiResponse.setDescription(response.description().value());
        }

        if (!response.body().isEmpty()) {
            Content content = new ContentImpl();
            MediaTypeImpl mediaType = new MediaTypeImpl();
            mediaType.setSchema(schemaBuilder.toSchema(response.body().get(0)));
            content.addMediaType(MediaType.APPLICATION_JSON, mediaType);
            apiResponse.setContent(content);
        }
        return apiResponse;
    }
}
