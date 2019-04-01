package org.ops4j.ramler.openapi;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.ops4j.ramler.common.model.CommonConstants;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import io.smallrye.openapi.api.models.media.SchemaImpl;

/**
 * Builds OpenAPI schemas from RAML type declarations.
 * @author hwellmann
 *
 */
public class SchemaBuilder {

    private OpenApiGeneratorContext context;
    private boolean generateAny;

    /**
     * Creates a schema builder with the given context.
     * @param context OpenAPI generator context
     */
    public SchemaBuilder(OpenApiGeneratorContext context) {
        this.context = context;
    }

    /**
     * Build an OpenAPI schema for the given RAML type declaration.
     * @param type type declaration
     * @return OpenAPI schema
     */
    public Schema toSchema(TypeDeclaration type) {
        Schema schema = new SchemaImpl();
        if (isAdditionalProperties(type)) {
            addAdditionalProperties(schema, type);
        }
        else if (type instanceof ObjectTypeDeclaration) {
            addObjectProperty(schema, type);
        }
        else if (type instanceof ArrayTypeDeclaration) {
            addArrayProperty(schema, (ArrayTypeDeclaration) type);
        }
        else if (type instanceof BooleanTypeDeclaration) {
            addBooleanProperty(schema);
        }
        else if (type instanceof AnyTypeDeclaration) {
            addAnyProperty(schema);
        }
        else if (type instanceof IntegerTypeDeclaration) {
            addIntegerProperty(schema, (IntegerTypeDeclaration) type);
        }
        else if (type instanceof NumberTypeDeclaration) {
            addNumberProperty(schema, (NumberTypeDeclaration) type);
        }
        else if (type instanceof StringTypeDeclaration) {
            addStringProperty(schema);
        }
        else if (type instanceof DateTimeOnlyTypeDeclaration) {
            addDateTimeOnlyProperty(schema);
        }
        else if (type instanceof DateTimeTypeDeclaration) {
            addDateTimeProperty(schema);
        }
        else if (type instanceof DateTypeDeclaration) {
            addDateProperty(schema);
        }
        else if (type instanceof TimeOnlyTypeDeclaration) {
            addTimeOnlyProperty(schema);
        }
        else if (type instanceof FileTypeDeclaration) {
            addFileProperty(schema);
        }
        else {
            throw new UnsupportedOperationException("unsupported type " + type.type());
        }
        return schema;
    }

    /**
     * Indicates whether a synthetic "Any" schema should be generated.
     * @return true of "Any" is used
     */
    public boolean isGenerateAny() {
        return generateAny;
    }

    private void addFileProperty(Schema schema) {
        schema.setType(SchemaType.STRING);
        schema.setFormat("binary");
    }

    private void addDateTimeOnlyProperty(Schema schema) {
        schema.setType(SchemaType.STRING);
    }

    private void addDateTimeProperty(Schema schema) {
        schema.setType(SchemaType.STRING);
        schema.setFormat("date-time");
    }

    private void addDateProperty(Schema schema) {
        schema.setType(SchemaType.STRING);
        schema.setFormat("date");
    }

    private void addTimeOnlyProperty(Schema schema) {
        schema.setType(SchemaType.STRING);
    }

    private void addIntegerProperty(Schema schema, IntegerTypeDeclaration property) {
        schema.setType(SchemaType.INTEGER);
        if (property.format() == null) {
            return;
        }
        switch (property.format()) {
        case "int64":
        case "long":
            schema.setFormat("int64");
            break;
        case "int32":
            schema.setFormat("int32");
            break;
        default:
            // ignore
        }
    }

    private void addNumberProperty(Schema schema, NumberTypeDeclaration property) {
        schema.setType(SchemaType.NUMBER);
        if (property.format() == null) {
            return;
        }
        switch (property.format()) {
        case "float":
            schema.setFormat("float");
            break;
        case "double":
            schema.setFormat("double");
            break;
        default:
            // ignore
        }
    }

    private void addStringProperty(Schema schema) {
        schema.setType(SchemaType.STRING);
    }

    private boolean isAdditionalProperties(TypeDeclaration property) {
        return property.name().startsWith("/");
    }

    private void addAdditionalProperties(Schema schema, TypeDeclaration property) {
        String pattern = property.name().substring(1, property.name().length() - 1);
        Schema additionalPropertiesSchema = new SchemaImpl();
        additionalPropertiesSchema.setPattern(pattern);
        schema.additionalPropertiesSchema(additionalPropertiesSchema);
    }

    private void addObjectProperty(Schema schema, TypeDeclaration property) {
        if (property.type().equals(CommonConstants.OBJECT)) {
            schema.setType(SchemaType.OBJECT);
        } else {
            schema.setRef(property.type());
        }
    }

    private void addArrayProperty(Schema schema, ArrayTypeDeclaration property) {
        schema.setType(SchemaType.ARRAY);
        String itemType = context.getApiModel().getItemType(property);
        Schema itemSchema = new SchemaImpl();
        itemSchema.setRef(itemType);
        schema.setItems(itemSchema);
    }

    private void addBooleanProperty(Schema schema) {
        schema.setType(SchemaType.BOOLEAN);
    }

    private void addAnyProperty(Schema schema) {
        generateAny = true;
        schema.setRef("Any");
    }
}
