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
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import io.smallrye.openapi.api.models.media.SchemaImpl;

public class SchemaBuilder {

    private OpenApiGeneratorContext context;
    private boolean generateAny;

    public SchemaBuilder(OpenApiGeneratorContext context) {
        this.context = context;
    }

    public Schema toSchema(TypeDeclaration property) {
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

    public boolean isGenerateAny() {
        return generateAny;
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
