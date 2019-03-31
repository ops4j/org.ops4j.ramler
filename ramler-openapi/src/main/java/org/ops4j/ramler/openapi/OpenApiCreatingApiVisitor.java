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

    private boolean generateAny;

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
    public void visitApiEnd(Api api) {
        if (generateAny) {
            Schema anySchema = new SchemaImpl();
            anySchema.setTitle("Any");
            anySchema.setDescription("Can be any simple or complex type");
            components.addSchema("Any", anySchema);
        }
    }

    @Override
    public void visitObjectTypeStart(ObjectTypeDeclaration type) {
        if (context.getApiModel().isInternal(type)) {
            return;
        }
        objectSchema = new SchemaImpl();
        components.addSchema(type.name(), objectSchema);
        objectSchema.setTitle(type.name());
        if (type.description() != null) {
            objectSchema.setDescription(type.description().value());
        }
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
        Schema propertySchema = toSchema(property);
        if (property.description() != null) {
            propertySchema.setDescription(property.description().value());
        }

        objectSchema.addProperty(property.name(), propertySchema);
        if (Boolean.TRUE.equals(property.required())) {
            objectSchema.addRequired(property.name());
        }
    }

    @Override
    public void visitStringType(StringTypeDeclaration type) {
        SchemaImpl schema = new SchemaImpl();
        schema.setTitle(type.name());
        schema.setType(SchemaType.STRING);
        if (type.description() != null) {
            schema.setDescription(type.description().value());
        }
        components.addSchema(type.name(), schema);
    }

    @Override
    public void visitNumberType(NumberTypeDeclaration type) {
        SchemaImpl schema = new SchemaImpl();
        schema.setTitle(type.name());
        if (type instanceof IntegerTypeDeclaration) {
            schema.setType(SchemaType.INTEGER);

        } else {
            schema.setType(SchemaType.NUMBER);
        }
        if (type.description() != null) {
            schema.setDescription(type.description().value());
        }
        components.addSchema(type.name(), schema);
    }

    @Override
    public void visitEnumTypeStart(StringTypeDeclaration type) {
        SchemaImpl schema = new SchemaImpl();
        schema.setTitle(type.name());
        schema.setType(SchemaType.STRING);
        if (type.description() != null) {
            schema.setDescription(type.description().value());
        }

        List<Object> enumValues = context.getApiModel().getEnumValues(type).stream()
                .map(EnumValue::getName).collect(toList());
        schema.setEnumeration(enumValues);
        components.addSchema(type.name(), schema);
    }

    private Schema toSchema(TypeDeclaration property) {
        Schema propertySchema = new SchemaImpl();
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
            addBooleanProperty(propertySchema);
        }
        else if (property instanceof AnyTypeDeclaration) {
            addAnyProperty(propertySchema);
        }
        else if (property instanceof IntegerTypeDeclaration) {
            addIntegerProperty(propertySchema, (IntegerTypeDeclaration) property);
        }
        else if (property instanceof NumberTypeDeclaration) {
            addNumberProperty(propertySchema, (NumberTypeDeclaration) property);
        }
        else if (property instanceof StringTypeDeclaration) {
            addStringProperty(propertySchema);
        }
        else if (property instanceof DateTimeOnlyTypeDeclaration) {
            addDateTimeOnlyProperty(propertySchema);
        }
        else if (property instanceof DateTimeTypeDeclaration) {
            addDateTimeProperty(propertySchema);
        }
        else if (property instanceof DateTypeDeclaration) {
            addDateProperty(propertySchema);
        }
        else if (property instanceof TimeOnlyTypeDeclaration) {
            addTimeOnlyProperty(propertySchema);
        }
        else {
            throw new UnsupportedOperationException("unsupported type " + property.type());
        }
        return propertySchema;
    }

    private void addDateTimeOnlyProperty(Schema propertySchema) {
        propertySchema.setType(SchemaType.STRING);
    }

    private void addDateTimeProperty(Schema propertySchema) {
        propertySchema.setType(SchemaType.STRING);
        propertySchema.setFormat("date-time");
    }

    private void addDateProperty(Schema propertySchema) {
        propertySchema.setType(SchemaType.STRING);
        propertySchema.setFormat("date");
    }

    private void addTimeOnlyProperty(Schema propertySchema) {
        propertySchema.setType(SchemaType.STRING);
    }

    private void addIntegerProperty(Schema propertySchema, IntegerTypeDeclaration property) {
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
        default:
            // ignore
        }
    }

    private void addNumberProperty(Schema propertySchema, NumberTypeDeclaration property) {
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
        default:
            // ignore
        }
    }

    private void addStringProperty(Schema propertySchema) {
        propertySchema.setType(SchemaType.STRING);
    }

    private boolean isAdditionalProperties(TypeDeclaration property) {
        return property.name().startsWith("/");
    }

    private void addAdditionalProperties(Schema propertySchema, TypeDeclaration property) {
        String pattern = property.name().substring(1, property.name().length() - 1);
        Schema additionalPropertiesSchema = new SchemaImpl();
        additionalPropertiesSchema.setPattern(pattern);
        propertySchema.additionalPropertiesSchema(additionalPropertiesSchema);
    }

    private void addObjectProperty(Schema propertySchema, TypeDeclaration property) {
        if (property.type().equals(CommonConstants.OBJECT)) {
            propertySchema.setType(SchemaType.OBJECT);
        } else {
            propertySchema.setRef(property.type());
        }
    }

    private void addArrayProperty(Schema propertySchema, ArrayTypeDeclaration property) {
        propertySchema.setType(SchemaType.ARRAY);
        String itemType = context.getApiModel().getItemType(property);
        Schema itemSchema = new SchemaImpl();
        itemSchema.setRef(itemType);
        propertySchema.setItems(itemSchema);
    }

    private void addBooleanProperty(Schema propertySchema) {
        propertySchema.setType(SchemaType.BOOLEAN);
    }

    private void addAnyProperty(Schema propertySchema) {
        generateAny = true;
        propertySchema.setRef("Any");
    }
}
