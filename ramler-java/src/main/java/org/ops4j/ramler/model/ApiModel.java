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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

public class ApiModel {

    private Api api;
    
    private Map<String, TypeDeclaration> types = new HashMap<>();
    
    public ApiModel(Api api) {
        this.api = api;
        mapTypes();
    }
    
    public Api api() {
        return api;
    }

    private void mapTypes() {
        api.types().forEach(t -> types.put(t.name(), t));
    }
    
    public TypeDeclaration getDeclaredType(String typeName) {
        return types.get(typeName);
    }
    
    public String getDeclaredName(TypeDeclaration type) {
        TypeDeclaration declaredType = types.get(type.name());
        if (declaredType == null) {
            return null;
        }
        return type.name();
    }
    
    public boolean isArray(TypeDeclaration type) {
        return type instanceof ArrayTypeDeclaration;
    }
    
    public String getItemType(TypeDeclaration type) {
        if (!isArray(type)) {
            return null;
        }
        ArrayTypeDeclaration array = (ArrayTypeDeclaration) type;
        TypeDeclaration item = array.items();
        if (getDeclaredType(item.name()) != null) {
            return item.name();
        }
        return item.type().replace("[]", "");
    }
    
    public boolean isPrimitive(TypeDeclaration type) {
        switch (metatype(type)){
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

    public boolean isStructured(TypeDeclaration type) {
        switch (metatype(type)){
            case ARRAY:
            case OBJECT:
            case UNION:
                return true;
            
            default:
                return false;
        }
    }
    
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
        throw new IllegalArgumentException("cannot determine metatype: " + "name=" + type.name() +", type=" + type.type());
    }
    
    public List<String> getStringAnnotations(TypeDeclaration decl, String annotationName) {
        return decl.annotations().stream()
                .filter(a -> a.annotation().name().equals(annotationName))
                .flatMap(a -> findAnnotationValues(a)).collect(toList());
    }

    private Stream<String> findAnnotationValues(AnnotationRef ref) {
        TypeInstanceProperty tip = ref.structuredValue().properties().get(0);
        return tip.values().stream().map(ti -> ti.value()).map(String.class::cast);
    }
}
