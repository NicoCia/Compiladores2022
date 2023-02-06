 grammar compilador;

@header {
package compiladores_demo;
}

fragment MAYUS	: [A-Z] ;
fragment LETRA 	: [A-Za-zñ] ;
fragment VOCALES : [AEIUOaeiou] ;
fragment NUMBER	: [0-9] ;
fragment MINUTES: [0-5][0-9] ;
fragment DIAS	: ([0-2][0-9])|'3'[01] ;
fragment MESES	: ('0'[0-9])|('1'[0-2]) ;
fragment AÑOS	: NUMBER NUMBER NUMBER NUMBER ;
fragment INT: 'int';
fragment CHAR : 'char';
fragment DOUBLE : 'double' ;
fragment FLOAT : 'float';
EQUALS : '=' ;
//fragment OPERADOR : [-+*/];

PA : '(' ;
PC : ')' ;
LLA : '{' ;
LLC : '}' ;
CA : '[';
CC : ']';

WHILE : 'while' ;
FOR : 'for' ;
IF : 'if' ;
ELSE : 'else' ;
RET : 'return' ;
BOOLEAN : 'true' | 'false' ;
TIPO : INT | CHAR | DOUBLE | FLOAT ;
COMA : ',' ;
PYC: ';' ; 
ID : (LETRA | '_')(LETRA | NUMBER | '_')* ; //empieza con ltra o _ y sigue con letra numero o _
ARRAY : ID (CA (ID|ENTERO) CC)*;
ENTERO	: '-'? NUMBER+;
//FLOAT	: [+-]?(NUMBER*[.])?[0-9]+;
//CUENTA	: (NATURAL|ENTERO)+ OPERADOR+ (NATURAL|ENTERO)+;

//ASIGNACION : EQUALS ' '? ('-'? NUMBER+|ID) ;

/*Operaciones aritmetico logicas */
SUMA : '+';
RESTA: '-';
MULT : '*';
DIV  : '/';
MOD  : '%';
L_AND : '&&' ;
L_OR  : '||' ;
L_NOT : '!' ;
C_EQUALS : '==' ;
C_NOT_EQUALS : '!=';
C_LESS : '<' ;
C_LESS_OR_EQUAL : '<=' ;
C_GREATER : '>' ;
C_GREATER_OR_EQUAL : '>=' ;
LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ; // Match "//" stuff '\n'
COMMENT : '/*' .*? '*/' -> skip ; // Match "/*" stuff "*/"
WS	: [ \t\n\r] -> skip;
OTRO : . ;

programa : instrucciones EOF ;

instrucciones 	: instruccion instrucciones
	      	| 
	      	;

instruccion	: declaracion PYC
		| asignacion PYC
		| functionDecl PYC?
		| functionCall PYC
		| oal PYC
		| inst_while
		| inst_for
		| inst_if
		| ireturn
		| bloque
		;

inst_while	: WHILE PA oal PC instruccion 
		;

inst_for	: FOR PA (declaracion|asignacion) PYC oal PYC (oal|asignacion) PC instruccion 
		;

inst_if		: IF PA oal PC instruccion (ELSE instruccion)? 
		;

ireturn		: RET (ID|ENTERO|BOOLEAN|oal)? PYC
		;

bloque		: LLA instrucciones LLC
		;

formalParameters: formalParameter (COMA formalParameter)*
		;

formalParameter	: TIPO ID
		;

declaracion	: TIPO expr asignacion secvar
		;

asignacion	: expr? EQUALS (expr | oal | asignacion)
		|
		;

secvar		: COMA (ID|ARRAY) asignacion secvar
		|
		;

oal		: (op_arit | op_logic)
		;

op_arit		: term t
		;

term 		: factor f
		;

t		: SUMA term t
		| RESTA term t
		|
		;

factor		: ENTERO 
		| '-'? ID
		| PA oal PC
		| L_NOT factor
		| functionCall
		;

f		: MULT factor f
		| DIV factor f
		| MOD factor f
		|
		;

op_logic	: l_term l_t
		;

l_term		: factor l_f
		;

l_t		: C_EQUALS l_term l_t
		| C_NOT_EQUALS l_term l_t
		| C_LESS l_term l_t
		| C_LESS_OR_EQUAL l_term l_t
		| C_GREATER l_term l_t
		| C_GREATER_OR_EQUAL l_term l_t
		|
		;

l_f		: L_AND factor l_f
		| L_OR factor l_f
		|
		;

functionDecl	: TIPO ID PA formalParameters? PC (bloque|PYC) // "void f(int x) {...}"
		;

functionCall	: ID PA exprList? PC //func call like f(), f(x), f(1,2)
		;

exprList 	: (expr|oal) (COMA exprList)* 
		;

expr		: functionCall
		| ARRAY
		| '-'? ID
		| ENTERO
		| PA expr PC
		;