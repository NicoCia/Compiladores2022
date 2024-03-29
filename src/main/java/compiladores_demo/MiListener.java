package compiladores_demo;

import java.util.ArrayList;
import java.util.Iterator;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;

import compiladores_demo.compiladorParser.AsignacionContext;
import compiladores_demo.compiladorParser.BloqueContext;
import compiladores_demo.compiladorParser.DeclaracionContext;
import compiladores_demo.compiladorParser.ExprContext;
import compiladores_demo.compiladorParser.ExprListContext;
import compiladores_demo.compiladorParser.FactorContext;
import compiladores_demo.compiladorParser.FormalParameterContext;
import compiladores_demo.compiladorParser.FunctionCallContext;
import compiladores_demo.compiladorParser.FunctionDeclContext;
import compiladores_demo.compiladorParser.Inst_forContext;
import compiladores_demo.compiladorParser.IreturnContext;
import compiladores_demo.compiladorParser.ProgramaContext;
import compiladores_demo.compiladorParser.SecvarContext;

public class MiListener extends compiladorBaseListener {

	SymbolsTable symbolsTable; //tabla de simbolos
	ArrayList<ArrayList<String>> funcParams; //lista de parametros de funcion a agregar en la tabla de simbolos
	ArrayList<String> prevEquals; //lista de variables del lado izquierdo de la asignacion
	ArrayList<String> postEquals; //lista de variables del lado derecho de la asinacion
	String calledFuncName;
	int paramsCallCount;
	Boolean declFlag;
	Boolean funcCallFlag;
	Boolean errorFlag;
	Boolean genericErrorFlag;
	ArrayList<String> vars;	//lista de variables declaradas a agregar en la tabla de simbolos
	// ArrayList<String> varsaux; //lista de variables declaras para test
	ArrayList<Boolean> init; //flag de init para las variables declaradas a agregar en la tabla de simbolos
	// Integer initCount = -1; //contador de variables declaradas a agregar en la tabla de simbolos

	
	
	@Override
	public void enterSecvar(SecvarContext ctx) {
		
		// System.out.println("Inicio secvar");
		//init.add(false);

		//System.out.println("initCount = " + initCount);
		//
	}


	@Override
	public void exitSecvar(SecvarContext ctx) {
		
		// TerminalNode temp = ctx.getToken(15, 0);
		// System.out.println("--> |" + temp.getText() + "|");
		if(ctx.COMA()!=null){
			if(ctx.ID() != null) {
				vars.add(ctx.ID().getText());
				// varsaux.add(ctx.ID().getText());
			}
			else {
				String parts[] = ctx.ARRAY().getText().split("\\[");
				vars.add(parts[0]);
			}
		}
	}



	@Override
	public void enterAsignacion(AsignacionContext ctx) {
		
		// System.out.println("Inicio asignacion");
		// init.add(0,false);
		// initCount++;
		//System.out.println("initCount = " + initCount);
		postEquals.clear();
	}



	@Override
	public void exitAsignacion(AsignacionContext ctx) {
		
		//init.set(initCount, true);
		// if(ctx.getToken(15, 0) == null) init.set(initCount, true);
		boolean newFlag = false;

		if(prevEquals.size()>0){
			if((symbolsTable.findSymbol(prevEquals.get(0))!=null)){
				if(symbolsTable.findLocalSymbol(prevEquals.get(0))!=null){
					if(!declFlag) symbolsTable.findLocalSymbol(prevEquals.get(0)).setInitialized(true);
					else {
						System.out.println("ERROR: ya existe la variable '" + prevEquals.get(0) + "'");
						errorFlag = true;
						genericErrorFlag = true;
					}
				}
				else if(declFlag && ctx.EQUALS() != null) init.add(0,true);
				else symbolsTable.findSymbol(prevEquals.get(0)).setInitialized(true);

				if(postEquals.size()>0 && !errorFlag){
					if(symbolsTable.findSymbol(postEquals.get(0))!=null){
						// if(symbolsTable.findSymbol(ctx.ID(0).getText()).getType() == symbolsTable.findSymbol(ctx.ID(0).getText()).getType()){
							
							symbolsTable.findSymbol(postEquals.get(0)).setUsed(true);
						// }
						// else System.out.println("ERROR: ");
					}
					else {
						System.out.println("ERROR: no existe la variable " + postEquals.get(0));
						genericErrorFlag = true;
					}
					
				}
			}
			else if(!declFlag) {
				System.out.println("ERROR: no existe la variable " + prevEquals.get(0));
				genericErrorFlag = true;
			}
			else if(ctx.EQUALS() != null)init.add(0,true);
			else init.add(0,false);
			

			if(!declFlag) prevEquals.clear();
		}
		else{
			if(postEquals.size()>0){

				if(declFlag){
					if(symbolsTable.findSymbol(postEquals.get(0))!=null){
						symbolsTable.findSymbol(postEquals.get(0)).setUsed(true);
						newFlag=true;
					}
					// else if(vars.contains(ctx.ID(0).getText())) newFlag=true;
					else {
						System.out.println("ERROR: no existe la variable " + postEquals.get(0));
						genericErrorFlag = true;
					}

					
				}
				else {
					if(symbolsTable.findSymbol(postEquals.get(0))!=null){
						symbolsTable.findSymbol(postEquals.get(0)).setInitialized(true);
					}
					else {
						System.out.println("ERROR: no existe la variable " + postEquals.get(0));
						genericErrorFlag = true;
					}
					
				}
			}
			else if(ctx.expr()!=null || ctx.asignacion()!=null) newFlag=true;
			else{
				init.add(0,false);
			}
			
			if((ctx.EQUALS() != null)&&newFlag) init.add(0,true);
		}
		
	}

	

	@Override
	public void enterFunctionDecl(FunctionDeclContext ctx) {
		
		// System.out.println("Voy a declarar una funcion");
		funcParams.clear();
		symbolsTable.addContext();
	}


	@Override
	public void exitFunctionDecl(FunctionDeclContext ctx) {
		
		// System.out.print("Declare la funcion -> " + ctx.TIPO() + " " + ctx.ID() + "( ");
		boolean errorFlag = false;
		Iterator<Id> unused = symbolsTable.getUnusedSymbols().iterator();

		while(unused.hasNext()&&(ctx.getChild(BloqueContext.class, paramsCallCount)!=null)){
			System.out.println("WARNING: simbolo `" + unused.next().getName() + "` declarado pero no utilizado");
		}
		symbolsTable.delContext();
		if(symbolsTable.findLocalSymbol(ctx.ID().getText()) == null){

			Funcion func = new Funcion(ctx.ID().getText(), DataType.valueOf(ctx.TIPO().getText().toUpperCase()), (ctx.bloque()!=null), false);

			for (ArrayList<String> p : funcParams){
				func.addArg(p);
			}

			symbolsTable.addSymbol(func);
			
			// System.out.println(func);
		}
		else {
			if(symbolsTable.findLocalSymbol(ctx.ID().getText()).getInitialized() == false ){
				if(ctx.bloque()!=null) {
					symbolsTable.findLocalSymbol(ctx.ID().getText()).setInitialized(true);
					//System.out.println(symbolsTable.findLocalSymbol(ctx.ID().getText()));
				}
				else errorFlag = true;
			}
			else errorFlag = true;
		}

		if(errorFlag) {
			System.out.println("ERROR: `" + ctx.ID().getText() + "` redeclarado como otro tipo de símbolo");
			genericErrorFlag = true;
		}


		// for (ArrayList<String> p : funcParams){
		// 	for (String s : p){
		// 		System.out.print(s + " ");
		// 	}
		// 	if(funcParams.indexOf(p)<funcParams.size()-1)System.out.print(", ");
		// }
		

		// if(ctx.bloque()!=null) System.out.println("esta inicializada");
	}
	

	@Override
	public void enterFormalParameter(FormalParameterContext ctx) {
		
		// super.enterFormalParameter(ctx);
	}


	@Override
	public void exitFormalParameter(FormalParameterContext ctx) {
		
		ArrayList<String> temp = new ArrayList<String>();
		if((ctx.TIPO() != null)&&(ctx.ID() != null)){
			temp.add(ctx.TIPO().getText().toUpperCase());
			temp.add(ctx.ID().getText());
			funcParams.add(temp);
			Variable var = new Variable(ctx.ID().getText(), DataType.valueOf(ctx.TIPO().getText().toUpperCase()), false, false);
			symbolsTable.addSymbol(var);
		}
	}


	

	@Override
	public void enterFunctionCall(FunctionCallContext ctx) {
		
		// super.enterFunctionCall(ctx);
		paramsCallCount = 0;
		funcCallFlag = true;
	}


	@Override
	public void exitFunctionCall(FunctionCallContext ctx) {
		
		// Boolean errorFlag
		if(symbolsTable.findSymbol(ctx.ID().getText()) != null){
			if(symbolsTable.findSymbol(ctx.ID().getText()) instanceof Funcion){

				
				Funcion func = (Funcion)symbolsTable.findSymbol(ctx.ID().getText());
				// System.out.println("tengo: " + paramsCallCount + " necesito: " + func.getArgsSize());
				if(paramsCallCount > func.getArgsSize()) {
					System.out.println("ERROR: demasiados argumentos para la funcion `" + ctx.ID().getText() + "`");
					genericErrorFlag = true;
				}
				else if(paramsCallCount < func.getArgsSize()) {
					System.out.println("ERROR: argumentos insuficientes para la funcion `" + ctx.ID().getText() + "`");
					genericErrorFlag = true;
				}
				else symbolsTable.findSymbol(ctx.ID().getText()).setUsed(true);
				// }
				// else System.out.println("ERROR: la funcion `" + ctx.ID().getText() + "` no esta inicializada");
			}
			else {
				System.out.println("ERROR: el simbolo `" + ctx.ID().getText() + "` no es una funcion");
				genericErrorFlag = true;
			}
		}
		else {
			System.out.println("ERROR: `" + ctx.ID().getText() + "` no es una funcion declarada");
			genericErrorFlag = true;
		}

		funcCallFlag = false;
	}


	

	@Override
	public void enterExprList(ExprListContext ctx) {
		if(ctx.getParent().getClass().equals(FunctionCallContext.class)){
			calledFuncName = ctx.getParent().getChild(0).getText();
		//System.out.println("llame a " + calledFuncName);
		}
		paramsCallCount++;
		// super.enterExprList(ctx);
	}


	@Override
	public void exitExprList(ExprListContext ctx) {
		// super.exitExprList(ctx);
	}

	

	@Override
	public void enterExpr(ExprContext ctx) {
		
		// super.enterExpr(ctx);
	}


	@Override
	public void exitExpr(ExprContext ctx) {
		
		String id="";
		if(ctx.ID()!=null){
			id+=ctx.ID().getText();
		}
		else if(ctx.ARRAY()!=null){
			String parts[] = ctx.ARRAY().getText().split("\\[");
			id += parts[0];
		}
		else if(ctx.functionCall()!=null){
			id+=ctx.functionCall().getChild(0).getText();
		}

		if(!id.equals("")){
			if(funcCallFlag){
				if(symbolsTable.findSymbol(id)!=null){
					DataType passedType = symbolsTable.findSymbol(id).getType();
					if(symbolsTable.findSymbol(calledFuncName) instanceof Funcion){
						DataType expectedType =((Funcion) symbolsTable.findSymbol(calledFuncName)).getParamType(paramsCallCount-1);

						if(passedType == expectedType){
							if(symbolsTable.findSymbol(id).getInitialized()==true) symbolsTable.findSymbol(id).setUsed(true);
							else {
								System.out.println("WARNING: simbolo `" + symbolsTable.findSymbol(id).getName() + "` utilizado sin inicializar");
							}
						}
						else {
							System.out.println("ERROR: parametros para llamada a funcion no validos. pass " + passedType + " expect " + expectedType);
							genericErrorFlag = true;
						}
					
						
					}
				}
			}
			else{
				if(prevEquals.size()>0) postEquals.add(id);
				else prevEquals.add(id);
			}
		}
	}


	@Override
	public void enterDeclaracion(DeclaracionContext ctx) {
		
		// System.out.println("Inicio declaracion");
		// initCount = -1;
		declFlag = true;
		errorFlag = false;
		vars.clear();
		init.clear();
		prevEquals.clear();
	}


	@Override
	public void exitDeclaracion(DeclaracionContext ctx) {
		
		// System.out.println("variable " + ctx.ID().getText());
		if(!errorFlag){
			if(prevEquals.size()>0){
				vars.add(prevEquals.get(0));
				// varsaux.add(ctx.ID().getText());
				Iterator<String> itr = vars.iterator();
				Integer i = 0;

				while (
					itr.hasNext()){
					String temp = itr.next();

					// System.out.println("variable: " + temp + " tipo: " + ctx.TIPO().getText().toUpperCase() + " declarada: " + init.get(i));
					if(symbolsTable.findLocalSymbol(temp) == null){
						Variable var = new Variable(temp, DataType.valueOf(ctx.TIPO().getText().toUpperCase()), init.get(i), false);

						symbolsTable.addSymbol(var);

						// System.out.println(var);
					}
					else {
						System.out.println("ERROR: `" + temp + "` redeclarado como otro tipo de símbolo");
						genericErrorFlag = true;
					}
				// 	// System.out.println("|" + temp + "|");
					i++;
				}
			}
			else {
				System.out.println("ERROR: falta nombre de simbolo");
				genericErrorFlag = true;
			}
		}
		declFlag=false;

		// System.out.println("Fin declaracion ->" + ctx.getText());
		// System.out.println("<- | start |" + ctx.getStart() + "| stop |" + ctx.
		// getStop() + "|");
		prevEquals.clear();
	}



	@Override
	public void enterPrograma(ProgramaContext ctx) {
		
		System.out.println("-\nComienza compilacion\n-");
		symbolsTable = new SymbolsTable();
		symbolsTable.addContext();
		funcParams = new ArrayList<ArrayList<String>>();
		prevEquals = new ArrayList<String>();
		postEquals = new ArrayList<String>();
		vars = new ArrayList<String>();
		// varsaux = new ArrayList<String>();
		init = new ArrayList<Boolean>();
		declFlag = false;
		funcCallFlag = false;
		genericErrorFlag = false;
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		
		
		// System.out.println("---- Salgo programa ----");
		// System.out.println(symbolsTable);
		Iterator<Id> unused = symbolsTable.getUnusedSymbols().iterator();

		while(unused.hasNext()){
			System.out.println("WARNING: simbolo `" + unused.next().getName() + "` declarado pero no utilizado");
		}

		

		// System.out.println("---- Salgo programa ----");
		//System.out.println(symbolsTable);
		
		// symbolsTable.delContext();
		System.out.println("-\nFin compilacion\n-");
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

		
		// for(String a : varsaux){
		// 	Id id = symbolsTable.findSymbol(a);
		// 	if(id!=null)System.out.println("variable: " + id.getName() + " tipo: "
		// 	+ id.getType() + " initializaed: " + id.getInitialized() + " used: "
		// 	+ id.getUsed());
		// }
		
		// System.out.println("Se encontraron " + tokens + " tokens");
		// System.out.println("Se realizaron " + decl + " declaraciones");
		// System.out.println("Se declararon " + vars + " variables");

	}

	@Override
	public void enterBloque(BloqueContext ctx) {
		
		symbolsTable.addContext();

		// System.out.println("\nantes del bloque: ");
		// for(String a : varsaux){
		// 	Id id = symbolsTable.findSymbol(a);
		// 	if(id!=null)System.out.println("variable: " + id.getName() + " tipo: "
		// 	+ id.getType() + " initializaed: " + id.getInitialized() + " used: "
		// 	+ id.getUsed());
		// }
	}

	@Override
	public void exitBloque(BloqueContext ctx) {
		Iterator<Id> notinitialized = symbolsTable.getNotInitializedSymbols().iterator();
		
		while(notinitialized.hasNext()){
			System.out.println("ERROR: simbolo `" + notinitialized.next().getName() + "` utilizado pero no inicializado");
			genericErrorFlag = true;
		}

		// System.out.println("----------------------");
		// System.out.println("despues del bloque: ");
		// for(String a : varsaux){
		// 	Id id = symbolsTable.findSymbol(a);
		// 	if(id!=null)System.out.println("variable: " + id.getName() + " tipo: "
		// 	+ id.getType() + " initializaed: " + id.getInitialized() + " used: "
		// 	+ id.getUsed());
		// }
		// System.out.println("---- Salgo bloque ----");
		// System.out.println(symbolsTable);

		// Iterator<Id> unused = symbolsTable.getUnusedSymbols().iterator();

		// while(unused.hasNext()){
		// 	System.out.println("WARNING: simbolo `" + unused.next().getName() + "` declarado pero no utilizado");
		// }

		
		
		symbolsTable.delContext();

	}


	@Override
	public void enterInst_for(Inst_forContext ctx) {
		
		symbolsTable.addContext();//super.enterInst_for(ctx);
	}


	@Override
	public void exitInst_for(Inst_forContext ctx) {
		
		symbolsTable.delContext();//super.exitInst_for(ctx);
	}


	@Override
	public void enterFactor(FactorContext ctx) {
		
		//super.enterFactor(ctx);
	}


	@Override
	public void exitFactor(FactorContext ctx) {
		
		if(ctx.ID()!=null){
			if(symbolsTable.findSymbol(ctx.ID().getText())==null){
				System.out.println("ERROR: no existe la variable " + ctx.ID().getText());
				genericErrorFlag = true;
				// ctx.exitRule(null);
			}
			else {
				symbolsTable.findSymbol(ctx.ID().getText()).setUsed(true);
			}
		}
		//super.exitFactor(ctx);
	}


	@Override
	public void exitIreturn(IreturnContext ctx) {
		if(ctx.ID()!=null){
			if(symbolsTable.findSymbol(ctx.ID().getText())==null){
				System.out.println("ERROR: no existe la variable " + ctx.ID().getText());
				genericErrorFlag = true;
				// ctx.exitRule(null);
			}
			else {
				symbolsTable.findSymbol(ctx.ID().getText()).setUsed(true);
			}
		}
	}


	@Override
	public void exitEveryRule(ParserRuleContext ctx) {

		if(ctx.getChild(ErrorNode.class,0)!=null) genericErrorFlag = true;

	}


	public Boolean getErrorFlag(){
		return genericErrorFlag;
	}	
	
}
