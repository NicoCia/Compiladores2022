 grammar id;

@header {
package compiladores_demo;
}

fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;
fragment OPERADOR : [-+*/];

NOMBRE	: 'nico';
ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;
NATURAL : DIGITO+ ;
ENTERO	: '-'? NATURAL;
CUENTA	: (NATURAL|ENTERO)+ OPERADOR+ (NATURAL|ENTERO)+;
WS	: [ \t\n\r] -> skip;
OTRO : . ;


s :	NOMBRE { System.out.println("NOMBRE ->" + $NOMBRE.getText() + "<--"); } s 	
	| ID { System.out.println("ID ->" + $ID.getText() + "<--"); } s
	| NATURAL { System.out.println("NATURAL ->" + $NATURAL.getText() + "<--"); } s
	| ENTERO { System.out.println("ENTERO ->" + $ENTERO.getText() + "<--"); } s
	| CUENTA { System.out.println("CUENTA ->" + $CUENTA.getText() + "<--"); } s
	| OTRO { System.out.println("Otro -> |" + $OTRO.getText() + "| <--"); } s
	| EOF
	;