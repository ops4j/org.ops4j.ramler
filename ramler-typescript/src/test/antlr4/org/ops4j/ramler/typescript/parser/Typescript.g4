grammar Typescript;

// The EOF token is required to make sure we parse the complete input
module
    : imports? exports? EOF
    ;

imports
    : importDecl+
    ;

exports
    : export+    
    ;
    
importDecl
    : IMPORT LBRACE identifiers RBRACE FROM STRING SEMICOLON   
    ;
    
identifiers
    : ID (COMMA ID)*
    ;

export
    : EXPORT declaration
    ;
    
declaration
    : interfaceDecl
    | typeAlias
    | enumDecl
    ;
    
interfaceDecl
    : INTERFACE typeDecl (extendsDecl)? members
    ;
    
members
    :  LBRACE (member)* RBRACE
    ;
    
typeDecl
    : ID typeVars?
    ;            

typeVars
    : LT identifiers GT
    ;
    
typeArgs
    : LT typeRefs GT
    ;
    
extendsDecl
    : EXTENDS baseTypes    
    ;
    
baseTypes
    : baseType (COMMA baseType)*
    ;
    
member
    : ID QUESTION? COLON typeRef SEMICOLON
    ;
    
typeRefs
    : typeRefElem (COMMA typeRefElem)*    
    ;
    
typeRefElem
    : typeRef
    ;    
    
typeRef
    : simpleType
    | arrayType
    | paramType
    | unionType
    ;
    
baseType
    : simpleType
    | paramType
    ;
    
arrayType
    : ID LBRACKET RBRACKET
    ;

paramType
    : ID typeArgs
    ;

simpleType
    : ID
    ;

unionType
    : variant (BAR variant)+
    ;

variant
    : simpleType
    ;

typeAlias
    : TYPE ID EQ typeRef SEMICOLON
    ;
    
enumDecl
    : ENUM ID LBRACE enumMember (COMMA enumMember)* RBRACE
    ;
    
enumMember
    : ID (EQ STRING)?
    ;

// -------- end of parser, start of lexer    

LPAREN
    : '('
    ;

RPAREN
    : ')'
    ;
    
LBRACKET
    : '['
    ;    
  
RBRACKET
    : ']'
    ;    
  
LBRACE
    : '{'
    ;    
  
RBRACE
    : '}'
    ;
    
COLON
    : ':'
    ;

COMMA
    : ','
    ;
    
SEMICOLON        
    : ';'
    ;
    
LT
    : '<'
    ;    
  
GT
    : '>'
    ;
    
EQ
    : '='
    ;
    
BAR
    : '|'
    ;

QUESTION
    : '?'
    ;
    
IMPORT
    : 'import'
    ;
    
ENUM
    : 'enum'
    ;
        
FROM
    : 'from'
    ;

EXPORT
    : 'export'
    ;


EXTENDS
    : 'extends'
    ;


TYPE
    : 'type'
    ;


INTERFACE            
    : 'interface'
    ;

  
STRING
    : '\'' (~['])* '\''
    ;
    
ID
    : [A-Za-z][A-Za-z0-9_]*
    ;
    
WHITESPACE
    : [ \t\n\r]+ -> skip
    ;
    
LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;    
   