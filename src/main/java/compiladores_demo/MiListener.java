package compiladores_demo;

import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores_demo.compiladorParser.ProgramaContext;

public class MiListener extends compiladorBaseListener {
	private Integer tokens = 0;

	@Override
	public void visitTerminal(TerminalNode node) {
		tokens++;
		System.out.println("|" + node.getText() + "|");
		// TODO Auto-generated method stub
	}

	@Override
	public void enterPrograma(ProgramaContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("Comienza compilacion");
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("Fin compilacion");
		System.out.println("Se encontraron" + tokens + "tokens");
	}

	
}
