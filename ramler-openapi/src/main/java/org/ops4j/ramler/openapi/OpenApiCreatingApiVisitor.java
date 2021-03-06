package org.ops4j.ramler.openapi;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.ops4j.ramler.common.model.ApiVisitor;
import org.ops4j.ramler.common.model.CommonConstants;
import org.ops4j.ramler.common.model.EnumValue;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import io.smallrye.openapi.api.models.ComponentsImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.media.DiscriminatorImpl;
import io.smallrye.openapi.api.models.media.SchemaImpl;

/**
 * Generates an OpenAPI specification by visiting a RAML model.
 *
 * @author Harald Wellmann
 *
 */
public class OpenApiCreatingApiVisitor implements ApiVisitor {

    private OpenApiGeneratorContext context;

    private OpenAPI openApi;

    private Components components;

    private Schema objectSchema;

    private SchemaBuilder schemaBuilder;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public OpenApiCreatingApiVisitor(OpenApiGeneratorContext context) {
        this.context = context;
        this.openApi = context.getOpenApi();
        this.schemaBuilder = context.getSchemaBuilder();
    }

    @Override
    public void visitApiStart(Api api) {
        openApi.setOpenapi("3.0.2");
        Info info = new InfoImpl();
        info.setTitle(api.title()
            .value());
        String version = (api.version() == null) ? "undefined"
            : api.version()
                .value();
        info.setVersion(version);
        openApi.setInfo(info);

        Paths paths = new PathsImpl();
        openApi.setPaths(paths);

        components = new ComponentsImpl();
        openApi.setComponents(components);

    }

    @Override
    public void visitApiEnd(Api api) {
        if (schemaBuilder.isGenerateAny()) {
            Schema anySchema = new SchemaImpl();
            anySchema.setTitle("Any");
            anySchema.setDescription("Can be any simple or complex type");
            components.addSchema("Any", anySchema);
        }
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel()
            .isInternal(type)) {
            return;
        }
        objectSchema = new SchemaImpl();
        components.addSchema(type.name(), objectSchema);
        objectSchema.setTitle(type.name());
        if (type.description() != null) {
            objectSchema.setDescription(type.description()
                .value());
        }
        TypeDeclaration parentType = type.parentTypes()
            .get(0);
        if (parentType.name()
            .equals(CommonConstants.OBJECT)) {
            objectSchema.setType(SchemaType.OBJECT);
        }
        else {
            Schema base = new SchemaImpl();
            base.setRef(parentType.name());
            objectSchema.addAllOf(base);
            Schema derived = new SchemaImpl();
            derived.setType(SchemaType.OBJECT);
            objectSchema.addAllOf(derived);
            objectSchema = derived;
        }

        if (type.discriminator() != null) {
            Discriminator discriminator = new DiscriminatorImpl();
            discriminator.setPropertyName(type.discriminator());
            objectSchema.setDiscriminator(discriminator);
        }
    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        objectSchema = null;
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (objectSchema == null) {
            return;
        }
        Schema propertySchema = schemaBuilder.toSchema(property);
        if (property.description() != null) {
            propertySchema.setDescription(property.description()
                .value());
        }

        objectSchema.addProperty(property.name(), propertySchema);
        if (Boolean.TRUE.equals(property.required())) {
            objectSchema.addRequired(property.name());
        }
    }

    @Override
    public void visitUnionType(UnionTypeDeclaration type) {
        Schema schema = new SchemaImpl();
        schema.setTitle(type.name());
        if (type.description() != null) {
            schema.setDescription(type.description()
                .value());
        }

        for (TypeDeclaration variant : type.of()) {
            Schema variantSchema = new SchemaImpl();
            variantSchema.setRef(variant.name());
            schema.addOneOf(variantSchema);
        }

        components.addSchema(type.name(), schema);
    }

    @Override
    public void visitStringType(StringTypeDeclaration type) {
        SchemaImpl schema = new SchemaImpl();
        schema.setTitle(type.name());
        schema.setType(SchemaType.STRING);
        if (type.description() != null) {
            schema.setDescription(type.description()
                .value());
        }
        components.addSchema(type.name(), schema);
    }

    @Override
    public void visitNumberType(NumberTypeDeclaration type) {
        SchemaImpl schema = new SchemaImpl();
        schema.setTitle(type.name());
        if (type instanceof IntegerTypeDeclaration) {
            schema.setType(SchemaType.INTEGER);

        }
        else {
            schema.setType(SchemaType.NUMBER);
        }
        if (type.description() != null) {
            schema.setDescription(type.description()
                .value());
        }
        components.addSchema(type.name(), schema);
    }

    @Override
    public void visitEnumTypeStart(StringTypeDeclaration type) {
        SchemaImpl schema = new SchemaImpl();
        schema.setTitle(type.name());
        schema.setType(SchemaType.STRING);
        if (type.description() != null) {
            schema.setDescription(type.description()
                .value());
        }

        List<Object> enumValues = context.getApiModel()
            .getEnumValues(type)
            .stream()
            .map(EnumValue::getName)
            .collect(toList());
        schema.setEnumeration(enumValues);
        components.addSchema(type.name(), schema);
    }

}
