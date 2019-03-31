package org.ops4j.ramler.openapi;

import java.io.IOException;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.ops4j.ramler.common.helper.FileHelper;
import org.ops4j.ramler.common.model.ApiModel;
import org.ops4j.ramler.common.model.ApiModelBuilder;
import org.ops4j.ramler.common.model.ApiTraverser;

import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.io.OpenApiSerializer.Format;

public class OpenApiGenerator {

    private OpenApiConfiguration config;
    private OpenApiGeneratorContext context;

    /**
     * Creates a generator with the given configuration.
     *
     * @param config OpenApi generator configuration
     */
    public OpenApiGenerator(OpenApiConfiguration config) {
        this.config = config;
        this.context = new OpenApiGeneratorContext(config);
    }

    /**
     * Generates OpenApi spec.
     *
     * @throws IOException
     */
    public void generate() throws IOException {
        ApiModel apiModel = new ApiModelBuilder().buildApiModel(config.getSourceFile());
        context.setApiModel(apiModel);
        FileHelper.createDirectoryIfNeeded(config.getTargetDir());

        OpenApiCreatingApiVisitor visitor = new OpenApiCreatingApiVisitor(context);
        ApiTraverser traverser = new ApiTraverser(apiModel);
        traverser.traverse(apiModel.getApi(), visitor);

        OpenAPI openApi = visitor.getOpenApi();

        if (context.getConfig().isGenerateYaml()) {
            String yaml = OpenApiSerializer.serialize(openApi, Format.YAML);
            context.writeToFile(yaml, "openapi.yaml");
        }

        if (context.getConfig().isGenerateJson()) {
            String json = OpenApiSerializer.serialize(openApi, Format.JSON);
            context.writeToFile(json, "openapi.json");
        }
    }
}
