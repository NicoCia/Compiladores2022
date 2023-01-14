package compiladores_demo;

import org.antlr.runtime.tree.ParseTree;

import compiladores_demo.compiladorParser.InstruccionContext;
import compiladores_demo.compiladorParser.InstruccionesContext;
import compiladores_demo.compiladorParser.ProgramaContext;

public class MiVisitor extends compiladorBaseVisitor<ParseTree> {

	@Override
	public ParseTree visitPrograma(ProgramaContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("Visitando primer nodo");
		return super.visitPrograma(ctx);
	}

	@Override
	public ParseTree visitInstrucciones(InstruccionesContext ctx) {
		// TODO Auto-generated method stub
		System.out.println(" -- Instrucciones");
		System.out.println(" --  tengo " + ctx.getChildCount() + " hijos");
		System.out.println(" --   |" + ctx.getText() + "|");
		return super.visitInstrucciones(ctx);
	}
	
	@Override
	public ParseTree visitInstruccion(InstruccionContext ctx) {
		// TODO Auto-generated method stub
		System.out.println(" ## Instruccion");
		System.out.println(" ##  tengo " + ctx.getChildCount() + " hijos");
		System.out.println(" ##   |" + ctx.getText() + "|");
		return super.visitInstruccion(ctx);
	}
	
}
