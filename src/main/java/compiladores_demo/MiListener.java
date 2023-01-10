package compiladores_demo;

import java.util.ArrayList;
import java.util.Iterator;

import compiladores_demo.compiladorParser.AsignacionContext;
import compiladores_demo.compiladorParser.BloqueContext;
import compiladores_demo.compiladorParser.DeclaracionContext;
import compiladores_demo.compiladorParser.FactorContext;
import compiladores_demo.compiladorParser.Inst_forContext;
import compiladores_demo.compiladorParser.ProgramaContext;
import compiladores_demo.compiladorParser.SecvarContext;

public class MiListener extends compiladorBaseListener {
	// private Integer tokens = 0;
	// private Integer decl = 0;
	// private Integer vars = 0;
	SymbolsTable symbolsTable; //tabla de simbolos
	ArrayList<String> vars;	//lista de variables declaradas a agregar en la tabla de simbolos
	ArrayList<String> varsaux; //lista de variables declaras para test
	ArrayList<Boolean> init; //flag de init para las variables declaradas a agregar en la tabla de simbolos
	Integer initCount = -1; //contador de variables declaradas a agregar en la tabla de simbolos

	// @Override
	// public void visitTerminal(TerminalNode node) {
	// 	tokens++;
	// 	//System.out.println("|" + node.getText() + "|");
	// 	// TODO Auto-generated method stub
	// }

	

	// @Override
	// public void enterDeclaracion(DeclaracionContext ctx) {
	// 	// TODO Auto-generated method stub
	// 	System.out.print("Inicio declaracion ->" + ctx.getText());
	// 	System.out.println("<- | start |" + ctx.getStart() + "| stop |" + ctx.
	// 	getStop() + "|");
	// 	decl++;
	// }
	
	
	
	@Override
	public void enterSecvar(SecvarContext ctx) {
		// TODO Auto-generated method stub
		// System.out.println("Inicio secvar");
		//init.add(false);

		//System.out.println("initCount = " + initCount);
		//
	}


	@Override
	public void exitSecvar(SecvarContext ctx) {
		// TODO Auto-generated method stub
		// TerminalNode temp = ctx.getToken(15, 0);
		// System.out.println("--> |" + temp.getText() + "|");
		if(ctx.ID() != null) {
			vars.add(ctx.ID().getText());
			varsaux.add(ctx.ID().getText());
		}
	}



	@Override
	public void enterAsignacion(AsignacionContext ctx) {
		// TODO Auto-generated method stub
		// System.out.println("Inicio asignacion");
		// init.add(0,false);
		// initCount++;
		//System.out.println("initCount = " + initCount);
	}



	@Override
	public void exitAsignacion(AsignacionContext ctx) {
		// TODO Auto-generated method stub
		//init.set(initCount, true);
		// if(ctx.getToken(15, 0) == null) init.set(initCount, true);
		boolean newFlag = true;
		
		if(ctx.ID() != null){
			if(symbolsTable.findSymbol(ctx.ID().getText())!=null){
				symbolsTable.findSymbol(ctx.ID().getText()).setInitialized(true);
				newFlag = false;
			}
		// // 	if(symbolsTable.findSymbol(ctx.getToken(15, 0).getText())!=null)
		}
		else{
			init.add(0,false);
		}
		
		if((ctx.EQUALS() != null)&&newFlag) init.set(0,true);
	}



	@Override
	public void enterDeclaracion(DeclaracionContext ctx) {
		// TODO Auto-generated method stub
		// System.out.println("Inicio declaracion");
		initCount = -1;
		vars.clear();
		init.clear();
	}


	@Override
	public void exitDeclaracion(DeclaracionContext ctx) {
		// TODO Auto-generated method stub
		// System.out.println("variable " + ctx.ID().getText());
		vars.add(ctx.ID().getText());
		varsaux.add(ctx.ID().getText());
		Iterator<String> itr = vars.iterator();
		Integer i = 0;

		while (itr.hasNext()){
			String temp = itr.next();

			// System.out.println("variable: " + temp + " tipo: " + ctx.TIPO().getText().toUpperCase() + " declarada: " + init.get(i));
			if(symbolsTable.findLocalSymbol(temp) == null){
				Variable var = new Variable(temp, DataType.valueOf(ctx.TIPO().getText().toUpperCase()), init.get(i), false);

				symbolsTable.addSymbol(var);
			}
			else {
				System.out.println("ERROR: Ya existe una variable definida con el nombre " + temp);
			}
		// 	// System.out.println("|" + temp + "|");
			i++;
		}

		// System.out.println("Fin declaracion ->" + ctx.getText());
		// System.out.println("<- | start |" + ctx.getStart() + "| stop |" + ctx.
		// getStop() + "|");
	}



	@Override
	public void enterPrograma(ProgramaContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("Comienza compilacion");
		symbolsTable = new SymbolsTable();
		symbolsTable.addContext();
		vars = new ArrayList<String>();
		varsaux = new ArrayList<String>();
		init = new ArrayList<Boolean>();
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("-\nFin compilacion\n-\n-");

		// Iterator<String> itr = varsaux.iterator();
		// Integer i = 0;

		// while (itr.hasNext()){
		// 	String temp = itr.next();

		// 	System.out.println("variable: " + temp);// + " tipo: " + ctx.TIPO().getText().toUpperCase() + " declarada: " + init.get(i));

		// // 	Variable var = new Variable(temp, DataType.valueOf(ctx.TIPO().getText().toUpperCase()), init.get(i), false);

		// // 	symbolsTable.addSymbol(var);

		// // 	// System.out.println("|" + temp + "|");
		// 	i++;
		// }

		
		for(String a : varsaux){
			Id id = symbolsTable.findSymbol(a);
			if(id!=null)System.out.println("variable: " + id.getName() + " tipo: "
			+ id.getType() + " initializaed: " + id.getInitialized() + " used: "
			+ id.getUsed());
		}
		
		// System.out.println("Se encontraron " + tokens + " tokens");
		// System.out.println("Se realizaron " + decl + " declaraciones");
		// System.out.println("Se declararon " + vars + " variables");
	}

	@Override
	public void enterBloque(BloqueContext ctx) {
		// TODO Auto-generated method stub
		symbolsTable.addContext();

		System.out.println("\nantes del bloque: ");
		for(String a : varsaux){
			Id id = symbolsTable.findSymbol(a);
			if(id!=null)System.out.println("variable: " + id.getName() + " tipo: "
			+ id.getType() + " initializaed: " + id.getInitialized() + " used: "
			+ id.getUsed());
		}
	}

	@Override
	public void exitBloque(BloqueContext ctx) {
		// TODO Auto-generated method stub
		

		System.out.println("----------------------");
		System.out.println("despues del bloque: ");
		for(String a : varsaux){
			Id id = symbolsTable.findSymbol(a);
			if(id!=null)System.out.println("variable: " + id.getName() + " tipo: "
			+ id.getType() + " initializaed: " + id.getInitialized() + " used: "
			+ id.getUsed());
		}
		
		symbolsTable.delContext();

	}


	@Override
	public void enterInst_for(Inst_forContext ctx) {
		// TODO Auto-generated method stub
		symbolsTable.addContext();//super.enterInst_for(ctx);
	}


	@Override
	public void exitInst_for(Inst_forContext ctx) {
		// TODO Auto-generated method stub
		symbolsTable.delContext();//super.exitInst_for(ctx);
	}


	@Override
	public void enterFactor(FactorContext ctx) {
		// TODO Auto-generated method stub
		//super.enterFactor(ctx);
	}


	@Override
	public void exitFactor(FactorContext ctx) {
		// TODO Auto-generated method stub
		if(ctx.ID()!=null){
			if(symbolsTable.findSymbol(ctx.ID().getText())==null){
				System.out.println("ERROR: no existe la variable " + ctx.ID().getText());
				ctx.exitRule(null);
			}
		} //super.exitFactor(ctx);
	}

	

	
}
