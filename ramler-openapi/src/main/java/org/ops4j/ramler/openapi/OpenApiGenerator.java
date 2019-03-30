package org.ops4j.ramler.openapi;

import java.io.IOException;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.ops4j.ramler.common.helper.FileHelper;
import org.ops4j.ramler.model.ApiModel;
import org.ops4j.ramler.model.ApiModelBuilder;
import org.ops4j.ramler.model.ApiTraverser;

import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.io.OpenApiSerializer.Format;

public class OpenApiGenerator {

    private OpenApiConfiguration config;
    private OpenApiGeneratorContext context;

    /**
     * Creates a generator with the given configuration.
     *
     * @param config
     *            OpenApi generator configuration
     */
    public OpenApiGenerator(OpenApiConfiguration config) {
        this.config = config;
        this.context = new OpenApiGeneratorContext(config);
    }

    /**
     * Generates OpenApi spec.
     * @throws IOException
     */
    public void generate() throws IOException {
        ApiModel apiModel = new ApiModelBuilder().buildApiModel(config.getSourceFile());
        context.setApiModel(apiModel);
        FileHelper.createDirectoryIfNeeded(config.getTargetDir());

        OpenApiCreatingApiVisitor visitor = new OpenApiCreatingApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser();
        traverser.traverse(context.getApiModel().getApi(), visitor);

        OpenAPI openApi = visitor.getOpenApi();

        String content = OpenApiSerializer.serialize(openApi, Format.YAML);
        context.writeToFile(content, "openapi.yaml");

    }
}