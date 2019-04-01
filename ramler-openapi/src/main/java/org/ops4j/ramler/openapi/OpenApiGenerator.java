package org.ops4j.ramler.openapi;

import java.io.File;
import java.io.IOException;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.ops4j.ramler.common.helper.FileHelper;
import org.ops4j.ramler.common.model.ApiModel;
import org.ops4j.ramler.common.model.ApiModelBuilder;
import org.ops4j.ramler.common.model.ApiTraverser;

import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.io.OpenApiSerializer.Format;

/**
 * Generates an OpenAPI specification from a RAML model.
 *
 * @author Harald Wellmann
 *
 */
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
     * @throws IOException when output cannot be written
     */
    public void generate() throws IOException {
        ApiModel apiModel = new ApiModelBuilder().buildApiModel(config.getSourceFile());
        context.setApiModel(apiModel);
        FileHelper.createDirectoryIfNeeded(config.getTargetDir());

        OpenApiCreatingApiVisitor schemaVisitor = new OpenApiCreatingApiVisitor(context);
        OpenApiResourceVisitor resourceVisitor = new OpenApiResourceVisitor(context);

        ApiTraverser traverser = new ApiTraverser(apiModel);
        traverser.traverse(apiModel.getApi(), schemaVisitor);
        traverser.traverse(apiModel.getApi(), resourceVisitor);

        OpenAPI openApi = context.getOpenApi();

        String fileName = new File(config.getSourceFile()).getName();
        String baseName = fileName;
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            baseName = fileName.substring(0, lastDot);
        }
        if (context.getConfig().isGenerateYaml()) {
            String yaml = OpenApiSerializer.serialize(openApi, Format.YAML);
            context.writeToFile(yaml, baseName + ".yaml");
        }

        if (context.getConfig().isGenerateJson()) {
            String json = OpenApiSerializer.serialize(openApi, Format.JSON);
            context.writeToFile(json, baseName + ".json");
        }
    }
}
