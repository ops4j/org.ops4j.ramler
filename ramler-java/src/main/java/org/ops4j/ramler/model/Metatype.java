package org.ops4j.ramler.model;

/**
 * Metatype of RAML types. Each type has a metatype. For primitive types, there is a one-to-one
 * correspondence between types and metatypes. For structured types, the metatype indicates the kind
 * of structure, i.e. array, union or object.
 * 
 * @author Harald Wellmann
 *
 */
public enum Metatype {

    /** Any type. */
    ANY, 
    
    /** Null type. */
    NULL, 
    
    /** Boolean type. */
    BOOLEAN, 
    
    /** Number type. */
    NUMBER, 
    
    /** Integer type. */
    INTEGER, 
    
    /** String type. */
    STRING, 
    
    /** File type. */
    FILE, 
    
    /** Time type. */
    TIME_ONLY, 
    
    /** Datetime type with timezone. */
    DATETIME, 
    
    /** Datetime type without timezone. */
    DATETIME_ONLY, 
    
    /** Date type. */
    DATE_ONLY, 
    
    /** Object type. */
    OBJECT, 
    
    /** Array type. */
    ARRAY, 
    
    /** Union type. */
    UNION
}
