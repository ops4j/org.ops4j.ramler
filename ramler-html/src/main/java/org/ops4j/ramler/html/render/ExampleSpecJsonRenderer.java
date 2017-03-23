package org.ops4j.ramler.html.render;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;

/**
 * Produces a JsonValue or a pretty-printed JSON string from a RAML ExampleSpec.
 *
 * @author Harald Wellmann
 *
 */
public class ExampleSpecJsonRenderer {

    /**
     * Converts an example of the given type to a JSON value.
     * 
     * @param type
     *            RAML type declaration
     * @param example
     *            example instance
     * @return JSON value
     */
    public JsonValue toJsonValue(TypeDeclaration type, ExampleSpec example) {
        TypeInstance instance = example.structuredValue();
        if (instance == null) {
            return JsonValue.NULL;
        }
        JsonObjectBuilder builder = createObject(instance);
        JsonObject jsonObject = builder.build();
        if (isArray(type)) {
            return jsonObject.entrySet().iterator().next().getValue();
        }
        if (!isObject(type) && jsonObject.containsKey("value")) {
            return jsonObject.get("value");
        }
        return jsonObject;
    }

    /**
     * Returns a pretty-printed JSON string for the given example instance of the given RAML type.
     * 
     * @param type
     *            RAML type declaration
     * @param example
     *            example instance
     * @return pretty-printed JSON string
     */
    public String prettyPrint(TypeDeclaration type, ExampleSpec example) {
        JsonValue json = toJsonValue(type, example);

        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jwf = Json.createWriterFactory(config);
        StringWriter sw = new StringWriter();
        JsonWriter writer = jwf.createWriter(sw);
        if (json instanceof JsonStructure) {
            writer.write((JsonStructure) json);
            return sw.toString();
        }
        else {
            return json.toString();
        }
    }

    private boolean isObject(TypeDeclaration type) {
        return type instanceof ObjectTypeDeclaration;
    }

    private boolean isArray(TypeDeclaration type) {
        return type instanceof ArrayTypeDeclaration;
    }

    private boolean isObject(TypeInstanceProperty tip) {
        return !tip.isArray() && !tip.value().isScalar();
    }

    private boolean isArray(TypeInstanceProperty tip) {
        return tip.isArray();
    }

    private JsonArrayBuilder createArray(TypeInstanceProperty tip) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (TypeInstance instance : tip.values()) {
            if (isObject(instance)) {
                builder.add(createObject(instance));
            }
            else if (isArray(instance)) {
                builder.add(createArray(instance));
            }
            else {
                addScalarOrNull(builder, instance);
            }
        }

        return builder;
    }

    private void addScalarOrNull(JsonArrayBuilder builder, TypeInstance instance) {
        Object value = instance.value();
        if (value instanceof Integer) {
            builder.add((Integer) value);
        }
        else if (value instanceof String) {
            builder.add((String) value);
        }
        else if (value instanceof Boolean) {
            builder.add((Boolean) value);
        }
        else if (value == null) {
            builder.addNull();
        }
    }

    private void addScalarOrNull(JsonObjectBuilder builder, TypeInstanceProperty tip) {
        String name = tip.name();
        Object value = tip.value().value();
        if (value instanceof Integer) {
            builder.add(name, (Integer) value);
        }
        else if (value instanceof String) {
            builder.add(name, (String) value);
        }
        else if (value instanceof Boolean) {
            builder.add(name, (Boolean) value);
        }
        else if (value == null) {
            builder.addNull(name);
        }
    }

    private boolean isObject(TypeInstance instance) {
        return !instance.isScalar() && !instance.properties().get(0).isArray();
    }

    private boolean isArray(TypeInstance instance) {
        return !instance.isScalar() && instance.properties().get(0).isArray();
    }

    private JsonObjectBuilder createObject(TypeInstance instance) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (TypeInstanceProperty tip : instance.properties()) {
            if (isObject(tip)) {
                builder.add(tip.name(), createObject(tip.value()));
            }
            else if (isArray(tip)) {
                builder.add(tip.name(), createArray(tip));
            }
            else {
                addScalarOrNull(builder, tip);
            }
        }
        return builder;
    }

    private JsonArrayBuilder createArray(TypeInstance instance) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (TypeInstanceProperty tip : instance.properties()) {
            if (isObject(tip)) {
                builder.add(createObject(tip.value()));
            }
            else if (isArray(tip)) {
                builder.add(createArray(tip));
            }
            else {
                addScalarOrNull(builder, tip.value());
            }
        }
        return builder;
    }
}
