package org.ops4j.ramler.openapi;

import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.ops4j.ramler.model.ApiVisitor;
import org.ops4j.ramler.model.CommonConstants;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
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

    private Schema objectSchema;

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
        objectSchema = new SchemaImpl();
        components.addSchema(type.name(), objectSchema);
        objectSchema.setTitle(type.name());
        TypeDeclaration parentType = type.parentTypes().get(0);
        if (parentType.name().equals(CommonConstants.OBJECT)) {
            objectSchema.setType(SchemaType.OBJECT);
        } else {
            Schema base = new SchemaImpl();
            base.setRef(parentType.name());
            objectSchema.addAllOf(base);
            Schema derived = new SchemaImpl();
            derived.setType(SchemaType.OBJECT);
            objectSchema.addAllOf(derived);
            objectSchema = derived;
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
        SchemaImpl propertySchema = toSchema(property);

        objectSchema.addProperty(property.name(), propertySchema);
        if (Boolean.TRUE.equals(property.required())) {
            objectSchema.addRequired(property.name());
        }
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
            addIntegerProperty(propertySchema, (IntegerTypeDeclaration) property);
        }
        else if (property instanceof NumberTypeDeclaration) {
            addNumberProperty(propertySchema, (NumberTypeDeclaration) property);
        }
        else if (property instanceof StringTypeDeclaration) {
            addStringProperty(propertySchema, property);
        }
        else if (property instanceof DateTimeOnlyTypeDeclaration) {
            addDateTimeOnlyProperty(propertySchema, (DateTimeOnlyTypeDeclaration) property);
        }
        else if (property instanceof DateTimeTypeDeclaration) {
            addDateTimeProperty(propertySchema, (DateTimeTypeDeclaration) property);
        }
        else if (property instanceof DateTypeDeclaration) {
            addDateProperty(propertySchema, (DateTypeDeclaration) property);
        }
        else if (property instanceof TimeOnlyTypeDeclaration) {
            addTimeOnlyProperty(propertySchema, (TimeOnlyTypeDeclaration) property);
        }
        else {
            // throw new UnsupportedOperationException("unsupported type " + property.type());
        }
        return propertySchema;
    }

    private void addDateTimeOnlyProperty(SchemaImpl propertySchema, DateTimeOnlyTypeDeclaration property) {
        propertySchema.setType(SchemaType.STRING);
    }

    private void addDateTimeProperty(SchemaImpl propertySchema, DateTimeTypeDeclaration property) {
        propertySchema.setType(SchemaType.STRING);
        propertySchema.setFormat("date-time");
    }

    private void addDateProperty(SchemaImpl propertySchema, DateTypeDeclaration property) {
        propertySchema.setType(SchemaType.STRING);
        propertySchema.setFormat("date");
    }

    private void addTimeOnlyProperty(SchemaImpl propertySchema, TimeOnlyTypeDeclaration property) {
        propertySchema.setType(SchemaType.STRING);
    }

    private void addIntegerProperty(SchemaImpl propertySchema, IntegerTypeDeclaration property) {
        propertySchema.setType(SchemaType.INTEGER);
        if (property.format() == null) {
            return;
        }
        switch (property.format()) {
        case "int64":
        case "long":
            propertySchema.setFormat("int64");
            break;
        case "int32":
            propertySchema.setFormat("int32");
            break;
        }
    }

    private void addNumberProperty(SchemaImpl propertySchema, NumberTypeDeclaration property) {
        propertySchema.setType(SchemaType.NUMBER);
        if (property.format() == null) {
            return;
        }
        switch (property.format()) {
        case "float":
            propertySchema.setFormat("float");
            break;
        case "double":
            propertySchema.setFormat("double");
            break;
        }
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
        String itemType = context.getApiModel().getItemType(property);
        Schema itemSchema = new SchemaImpl();
        itemSchema.setRef(itemType);
        propertySchema.setItems(itemSchema);
    }

    private void addBooleanProperty(SchemaImpl propertySchema, BooleanTypeDeclaration property) {
        propertySchema.setType(SchemaType.BOOLEAN);
    }

    private void addAnyProperty(SchemaImpl propertySchema, TypeDeclaration property) {
        // TODO Auto-generated method stub

    }
}
