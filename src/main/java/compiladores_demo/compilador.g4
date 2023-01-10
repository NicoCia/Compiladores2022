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

/*Horas entre las 03:12 y 11:27 */
//HORARIO :  ; //[03-11] ':' [12-27]; ESTA MAL


NOMBRE	: 'nico';
ENDW	: LETRA+ '.'	;
MAYUSW	: MAYUS+ LETRA+ ;
PLURALVW	: VOCALES LETRA+ 's';
WORD	: LETRA+ -> skip;
HOUR1	: ('1'[3-5])':'(MINUTES);
HOUR2	: ('0''3'':'('1'[2-9]|[2-5][0-9])) 
	| (('0'[4-9]|'1''0') ':' MINUTES) 
	| ('1''1' ':' ([01][0-9]|'2'[0-7]));

FECHA1	: DIAS'/'MESES'/''2''0'([01][0-9]|'2''0') ;
FECHA2	: DIAS'/'('0'[2468]|('1'('0'|'2')))'/' AÑOS ;
FECHA3	: (('1'[2-9])|('2'[0-3]))'/'MESES'/'AÑOS ;

//NATURAL : NUMBER+ ;
ENTERO	: '-'? NUMBER+;
//CUENTA	: (NATURAL|ENTERO)+ OPERADOR+ (NATURAL|ENTERO)+;
WS	: [ \t\n\r] -> skip;
OTRO : . ;


/*horas : HOUR2 { System.out.println("HOUR2 ->" + $HOUR2.getText() + "<--"); } horas
	| OTRO horas
	| EOF
	;*/

s :	NOMBRE { System.out.println("NOMBRE ->" + $NOMBRE.getText() + "<--"); } s 	
	| ENDW { System.out.println("ENDW ->" + $ENDW.getText() + "<--"); } s
	| MAYUSW { System.out.println("MAYUSW ->" + $MAYUSW.getText() + "<--"); } s
	| PLURALVW { System.out.println("PLURALVW ->" + $PLURALVW.getText() + "<--"); } s
	| HOUR1 { System.out.println("HOUR1 ->" + $HOUR1.getText() + "<--"); } s
	| HOUR2 { System.out.println("HOUR2 ->" + $HOUR2.getText() + "<--"); } s
	| FECHA1 { System.out.println("FECHA1 ->" + $FECHA1.getText() + "<--"); } s
	| FECHA2 { System.out.println("FECHA2 ->" + $FECHA2.getText() + "<--"); } s
	| FECHA3 { System.out.println("FECHA3 ->" + $FECHA3.getText() + "<--"); } s	
	//| ID { System.out.println("ID ->" + $ID.getText() + "<--"); } s
	//| NATURAL { System.out.println("NATURAL ->" + $NATURAL.getText() + "<--"); } s
	//| ENTERO { System.out.println("ENTERO ->" + $ENTERO.getText() + "<--"); } s
	//| CUENTA { System.out.println("CUENTA ->" + $CUENTA.getText() + "<--"); } s
	| OTRO { System.out.println("Otro -> |" + $OTRO.getText() + "| <--"); } s
	| EOF
	;

bp: PA bp PC bp
  | 
  ;


programa : instrucciones EOF ;

instrucciones 	: instruccion instrucciones
	      	| 
	      	;

instruccion	: inst_simple PYC
		| inst_while
		| inst_for
		| inst_if
		| ireturn
		| bloque
		;

inst_simple	: declaracion 
		| asignacion 
		| oal
		;

inst_while	: WHILE oal instruccion 
		;

inst_for	: FOR PA (declaracion|asignacion) PYC oal PYC inst_simple PC instruccion 
		;

inst_if		: IF oal instruccion ELSE? instruccion? 
		;

ireturn		: RET (ID|ENTERO|BOOLEAN)? PYC
		;

bloque		: LLA instrucciones LLC
		;

declaracion	: TIPO secvar
		;

asignacion	: ID? EQUALS (ENTERO | ID | inst_simple)
		;

secvar		: ID COMA secvar
		| ID asignacion COMA secvar
		| ID asignacion
		| ID
		;

test : oal;

oal		: op_arit | op_logic
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
		| ID
		| PA oal PC
		| L_NOT factor
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