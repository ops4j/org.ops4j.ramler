/*
 * Copyright 2016 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.ramler.model;

import static java.util.stream.Collectors.toList;
import static org.ops4j.ramler.model.Metatype.ANY;
import static org.ops4j.ramler.model.Metatype.ARRAY;
import static org.ops4j.ramler.model.Metatype.BOOLEAN;
import static org.ops4j.ramler.model.Metatype.DATETIME;
import static org.ops4j.ramler.model.Metatype.DATETIME_ONLY;
import static org.ops4j.ramler.model.Metatype.DATE_ONLY;
import static org.ops4j.ramler.model.Metatype.FILE;
import static org.ops4j.ramler.model.Metatype.INTEGER;
import static org.ops4j.ramler.model.Metatype.NULL;
import static org.ops4j.ramler.model.Metatype.NUMBER;
import static org.ops4j.ramler.model.Metatype.OBJECT;
import static org.ops4j.ramler.model.Metatype.STRING;
import static org.ops4j.ramler.model.Metatype.TIME_ONLY;
import static org.ops4j.ramler.model.Metatype.UNION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.raml.v2.api.model.v08.parameters.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NullTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Model of an API defined by a RAML specification.
 * <p>
 * This model wraps the basic {@link Api} model created by the RAML parser and adds some convenience
 * methods, e.g. for directy accessing types by name.
 *
 * @author Harald Wellmann
 */
public class ApiModel {

    private Api api;

    private Map<String, TypeDeclaration> types = new LinkedHashMap<>();
    private Map<String, List<String>> derivedTypes = new HashMap<>();

    /**
     * Creates an enhanced model for the given API.
     *
     * @param api
     *            API model provided by parser
     */
    public ApiModel(Api api) {
        this.api = api;
        mapTypes();
        mapDerivedTypes();
    }

    /**
     * Gets the underlying API model.
     *
     * @return API model provided by parser
     */
    public Api getApi() {
        return api;
    }

    private void mapTypes() {
        api.types().stream().sorted((l, r) -> l.name().compareTo(r.name()))
            .forEach(t -> types.put(t.name(), t));
    }

    private void mapDerivedTypes() {
        for (TypeDeclaration type : api.types()) {
            if (type instanceof ObjectTypeDeclaration && !type.type().equals("object")) {
                String baseTypeName = type.type();
                derivedTypes.merge(baseTypeName, Collections.singletonList(type.name()),
                    this::join);
            }
        }
    }

    private <T> List<T> join(List<T> head, List<T> tail) {
        List<T> result = new ArrayList<>(head);
        result.addAll(tail);
        return result;
    }

    /**
     * Finds the types directly derived from the given type.
     *
     * @param typeName
     *            type name
     * @return list of derived types (never null)
     */
    public List<String> findDerivedTypes(String typeName) {
        List<String> derived = derivedTypes.get(typeName);
        return derived == null ? Collections.emptyList() : derived;
    }

    /**
     * Gets all types defined in this API.
     *
     * @return collection of types (never null)
     */
    public Collection<TypeDeclaration> getTypes() {
        return types.values();
    }

    /**
     * Returns the title of this API.
     *
     * @return API title
     */
    public String getTitle() {
        return api.title().value();
    }

    /**
     * Gets all resources defined in this API, sorted alphabetically.
     *
     * @return collection of resources (never null)
     */
    public Collection<Resource> getResources() {
        return api.resources().stream()
            .sorted((l, r) -> l.displayName().value().compareTo(r.displayName().value()))
            .collect(toList());
    }

    /**
     * Returns the type declaration for the given name.
     *
     * @param typeName
     *            name of an API type
     * @return type declaration, or null if no such type exists
     */
    public TypeDeclaration getDeclaredType(String typeName) {
        return types.get(typeName);
    }

    /**
     * Gets the name of the given type, if the type is a declared type.
     * <p>
     * A built-in type like {@code string} does not have a declared name. An inline type like
     * {@code Foo[]} does not have a declared name.
     *
     * @param type
     *            type declaration
     * @return type name, or null
     */
    public String getDeclaredName(TypeDeclaration type) {
        TypeDeclaration declaredType = types.get(type.name());
        if (declaredType == null) {
            return null;
        }
        return type.name();
    }

    /**
     * Checks if the given type is an array type.
     *
     * @param type
     *            type declaration
     * @return true if type is an array type
     */
    public boolean isArray(TypeDeclaration type) {
        return type instanceof ArrayTypeDeclaration;
    }

    /**
     * Gets the item type name of the given array type.
     *
     * @param type
     *            type declaration
     * @return item type, or null if type is not an array type
     */
    public String getItemType(TypeDeclaration type) {
        if (!isArray(type)) {
            return null;
        }
        ArrayTypeDeclaration array = (ArrayTypeDeclaration) type;
        TypeDeclaration item = array.items();
        if (item.type() == null) {
            return item.name().replaceFirst("\\[\\]", "");
        }
        if ("object".equals(item.type()) && item.name() != null) {
            return item.name();
        }
        if ("array".equals(item.type()) && item.name() != null) {
            return item.name();
        }
        return item.type();
    }

    /**
     * Checks if the given type is primitive. A type is primitive if it is not {@code any} and not
     * structured.
     *
     * @param type
     *            type declaration
     * @return true if the type is primitive
     */
    public boolean isPrimitive(TypeDeclaration type) {
        switch (metatype(type)) {
            case STRING:
            case INTEGER:
            case NUMBER:
            case BOOLEAN:
            case DATE_ONLY:
            case DATETIME:
            case DATETIME_ONLY:
            case TIME_ONLY:
            case FILE:
            case NULL:
                return true;

            default:
                return false;
        }
    }

    /**
     * Checks if the given type is structured. A type is structured if it is an array type, an
     * object type, or a union type.
     *
     * @param type
     *            type declaration
     * @return true if the type is structured
     */
    public boolean isStructured(TypeDeclaration type) {
        switch (metatype(type)) {
            case ARRAY:
            case OBJECT:
            case UNION:
                return true;

            default:
                return false;
        }
    }

    /**
     * Gets the metatype of the given type declaration.
     *
     * @param type
     *            type declaration
     * @return metatype
     */
    public Metatype metatype(TypeDeclaration type) {
        if (type instanceof ObjectTypeDeclaration) {
            return OBJECT;
        }
        if (type instanceof StringTypeDeclaration) {
            return STRING;
        }
        if (type instanceof IntegerTypeDeclaration) {
            return INTEGER;
        }
        if (type instanceof NumberTypeDeclaration) {
            return NUMBER;
        }
        if (type instanceof BooleanTypeDeclaration) {
            return BOOLEAN;
        }
        if (type instanceof ArrayTypeDeclaration) {
            return ARRAY;
        }
        if (type instanceof DateTimeOnlyTypeDeclaration) {
            return DATETIME_ONLY;
        }
        if (type instanceof TimeOnlyTypeDeclaration) {
            return TIME_ONLY;
        }
        if (type instanceof DateTimeTypeDeclaration) {
            return DATETIME;
        }
        if (type instanceof DateTypeDeclaration) {
            return DATE_ONLY;
        }
        if (type instanceof FileTypeDeclaration) {
            return FILE;
        }
        if (type instanceof NullTypeDeclaration) {
            return NULL;
        }
        if (type instanceof UnionTypeDeclaration) {
            return UNION;
        }
        if (type instanceof AnyTypeDeclaration) {
            return ANY;
        }
        throw new IllegalArgumentException(
            "cannot determine metatype: " + "name=" + type.name() + ", type=" + type.type());
    }

    /**
     * Gets the value list of a string valued annotation of a given name on the given type.
     *
     * @param decl
     *            type declaration
     * @param annotationName
     *            name of annotation with value type {@code string[]}
     * @return list of annotation values (never null)
     */
    public List<String> getStringAnnotations(TypeDeclaration decl, String annotationName) {
        return decl.annotations().stream().filter(a -> a.annotation().name().equals(annotationName))
            .flatMap(a -> findStringAnnotationValues(a)).collect(toList());
    }

    private Stream<String> findStringAnnotationValues(AnnotationRef ref) {
        TypeInstanceProperty tip = ref.structuredValue().properties().get(0);
        return tip.values().stream().map(ti -> ti.value()).map(String.class::cast);
    }

    /**
     * Checks if the given type is an enumeration type. This is true if it has an {@code (enum)}
     * annotation, or an {@code enum} facet. Note that the latter is specified by RAML, while the
     * former is a Ramler extension.
     *
     * @param decl
     *            type declaration
     * @return true is type is an enumeration type
     */
    public boolean isEnum(TypeDeclaration decl) {
        if (findEnumAnnotation(decl).isPresent()) {
            return true;
        }

        if (decl instanceof StringTypeDeclaration) {
            StringTypeDeclaration stringType = (StringTypeDeclaration) decl;
            return !stringType.enumValues().isEmpty();
        }

        return false;
    }

    private Optional<AnnotationRef> findEnumAnnotation(TypeDeclaration decl) {
        return decl.annotations().stream().filter(a -> a.annotation().name().equals("enum"))
            .findFirst();
    }

    /**
     * Returns the list of enumeration values of the given type declaration.
     *
     * @param decl
     *            type declaration
     * @return enumeration values
     */
    public List<EnumValue> getEnumValues(TypeDeclaration decl) {
        Optional<AnnotationRef> annotationRef = findEnumAnnotation(decl);
        if (annotationRef.isPresent()) {
            TypeInstance annotationValue = annotationRef.get().structuredValue();
            TypeInstanceProperty valuesProperty = annotationValue.properties().get(0);
            return valuesProperty.values().stream().map(this::toEnumValue).collect(toList());
        }
        else {
            if (decl instanceof StringTypeDeclaration) {
                StringTypeDeclaration type = (StringTypeDeclaration) decl;
                return type.enumValues().stream().map(e -> new EnumValue(e, null))
                    .collect(toList());
            }
            return Collections.emptyList();
        }
    }

    /**
     * Returns the list of enumeration values of type with the given name.
     *
     * @param typeName
     *            type name
     * @return enumeration values, or null if no such type exists
     */
    public List<EnumValue> findEnumValues(String typeName) {
        TypeDeclaration decl = getDeclaredType(typeName);
        if (decl instanceof StringTypeDeclaration) {
            return getEnumValues(decl);
        }
        return Collections.emptyList();
    }

    private EnumValue toEnumValue(TypeInstance ti) {
        Object name = getPropertyValue(ti, "name");
        Object description = getPropertyValue(ti, "description");

        return new EnumValue((String) name, (String) description);
    }

    private Object getPropertyValue(TypeInstance ti, String propertyName) {
        Optional<TypeInstanceProperty> tip = ti.properties().stream()
            .filter(p -> p.name().equals(propertyName)).findFirst();
        if (tip.isPresent()) {
            return tip.get().value().value();
        }
        return null;
    }
}
