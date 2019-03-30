package org.ops4j.ramler.openapi;

import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.ops4j.ramler.generator.ApiVisitor;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import io.smallrye.openapi.api.models.ComponentsImpl;
import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.media.SchemaImpl;

public class OpenApiCreatingApiVisitor implements ApiVisitor {

    private OpenApiGeneratorContext context;

    private OpenAPI openApi;

    private Components components;

    private Schema schema;

    /**
     * Creates a visitor with the given generator context.
     *
     * @param context
     *            generator context
     */
    public OpenApiCreatingApiVisitor(OpenApiGeneratorContext context) {
        this.context = context;
        this.openApi = new OpenAPIImpl();
    }

    public OpenAPI getOpenApi() {
        return openApi;
    }

    @Override
    public void visitApiStart(Api api) {
        openApi.setOpenapi("3.0.2");
        Info info = new InfoImpl();
        info.setTitle(api.title().value());
        String version = (api.version() == null) ? "undefined" : api.version().value();
        info.setVersion(version);
        openApi.setInfo(info);

        Paths paths = new PathsImpl();
        openApi.setPaths(paths);

        components = new ComponentsImpl();
        openApi.setComponents(components);

    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }
        schema = new SchemaImpl();
        components.addSchema(type.name(), schema);
        schema.setTitle(type.name());
        schema.setType(SchemaType.OBJECT);
    }

    @Override
    public void visitObjectTypeEnd(ObjectTypeDeclaration type) {
        schema = null;
    }

    @Override
    public void visitObjectTypeProperty(ObjectTypeDeclaration type, TypeDeclaration property) {
        if (schema == null) {
            return;
        }
        SchemaImpl propertySchema = toSchema(property);

        schema.addProperty(property.name(), propertySchema);
    }

    private SchemaImpl toSchema(TypeDeclaration property) {
        SchemaImpl propertySchema = new SchemaImpl();
        if (isAdditionalProperties(property)) {
            addAdditionalProperties(propertySchema, property);
        }
        else if (property instanceof ObjectTypeDeclaration) {
            addObjectProperty(propertySchema, property);
        }
        else if (property instanceof ArrayTypeDeclaration) {
            addArrayProperty(propertySchema, (ArrayTypeDeclaration) property);
        }
        else if (property instanceof BooleanTypeDeclaration) {
            addBooleanProperty(propertySchema, (BooleanTypeDeclaration) property);
        }
        else if (property instanceof AnyTypeDeclaration) {
            addAnyProperty(propertySchema, property);
        }
        else if (property instanceof IntegerTypeDeclaration) {
            addIntegerProperty(propertySchema, property);
        }
        else if (property instanceof NumberTypeDeclaration) {
            addNumberProperty(propertySchema, property);
        }
        else if (property instanceof StringTypeDeclaration) {
            addStringProperty(propertySchema, property);
        }
        else {
            // throw new UnsupportedOperationException("unsupported type " + property.type());
        }
        return propertySchema;
    }

    private void addIntegerProperty(SchemaImpl propertySchema, TypeDeclaration property) {
        propertySchema.setType(SchemaType.INTEGER);
    }

    private void addNumberProperty(SchemaImpl propertySchema, TypeDeclaration property) {
        propertySchema.setType(SchemaType.NUMBER);
    }

    private void addStringProperty(SchemaImpl propertySchema, TypeDeclaration property) {
        propertySchema.setType(SchemaType.STRING);
    }

    private boolean isAdditionalProperties(TypeDeclaration property) {
        // TODO Auto-generated method stub
        return false;
    }

    private void addAdditionalProperties(SchemaImpl propertySchema, TypeDeclaration property) {
        // TODO Auto-generated method stub

    }

    private void addObjectProperty(SchemaImpl propertySchema, TypeDeclaration property) {
        propertySchema.setRef(property.type());
    }

    private void addArrayProperty(SchemaImpl propertySchema, ArrayTypeDeclaration property) {
        propertySchema.setType(SchemaType.ARRAY);
        Schema itemSchema = toSchema(property.items());
        propertySchema.setItems(itemSchema);
    }

    private void addBooleanProperty(SchemaImpl propertySchema, BooleanTypeDeclaration property) {
        propertySchema.setType(SchemaType.BOOLEAN);
    }

    private void addAnyProperty(SchemaImpl propertySchema, TypeDeclaration property) {
        // TODO Auto-generated method stub

    }
}
