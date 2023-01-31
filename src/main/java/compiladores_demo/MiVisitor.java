package compiladores_demo;

import java.io.File; // Import the File class
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.RuleContext;

import compiladores_demo.compiladorParser.AsignacionContext;
import compiladores_demo.compiladorParser.BloqueContext;
import compiladores_demo.compiladorParser.DeclaracionContext;
import compiladores_demo.compiladorParser.ExprContext;
import compiladores_demo.compiladorParser.ExprListContext;
import compiladores_demo.compiladorParser.FContext;
import compiladores_demo.compiladorParser.FactorContext;
import compiladores_demo.compiladorParser.FormalParameterContext;
import compiladores_demo.compiladorParser.FormalParametersContext;
import compiladores_demo.compiladorParser.FunctionCallContext;
import compiladores_demo.compiladorParser.FunctionDeclContext;
import compiladores_demo.compiladorParser.Inst_forContext;
import compiladores_demo.compiladorParser.Inst_ifContext;
import compiladores_demo.compiladorParser.Inst_whileContext;
import compiladores_demo.compiladorParser.InstruccionContext;
import compiladores_demo.compiladorParser.IreturnContext;
import compiladores_demo.compiladorParser.L_fContext;
import compiladores_demo.compiladorParser.L_tContext;
import compiladores_demo.compiladorParser.L_termContext;
import compiladores_demo.compiladorParser.OalContext;
import compiladores_demo.compiladorParser.Op_aritContext;
import compiladores_demo.compiladorParser.Op_logicContext;
import compiladores_demo.compiladorParser.SecvarContext;
import compiladores_demo.compiladorParser.TContext;
import compiladores_demo.compiladorParser.TermContext;

public class MiVisitor extends compiladorBaseVisitor<String> {
	Integer varsCount;
	Integer labelsCount;
	Integer factorCount;
	String texto, tipo, outputFile;
	ArrayList<String> tempVarsList;
	ArrayList<String> labelsList;
	HashMap<String, String> funcLabels;
	Boolean fileFlag;

	public MiVisitor(String inputFile) {
		varsCount = 0;
		labelsCount = 0;
		factorCount = 0;
		tempVarsList = new ArrayList<String>();
		labelsList = new ArrayList<String>();
		funcLabels = new HashMap<String, String>();
		outputFile = "output/" + inputFile + "_codIntermedio";
		createFile(outputFile+".txt");
		fileFlag = false;
	}

	@Override
	public String visitAsignacion(AsignacionContext ctx) {
		
		String asigVar;
		Integer exprPos;

		if (ctx.getChildCount() > 0) {
			if(ctx.getChild(0).getClass().equals(ExprContext.class)){
				asigVar = ctx.getChild(0).getText();
				exprPos = 2;
				
			}
			else { 
				asigVar = tempVarsList.get(0);
				tempVarsList.remove(0);
				exprPos = 1;

			}

			if (ctx.getChild(OalContext.class, 0)!=null) {
				visit(ctx.getChild(OalContext.class, 0));
				texto = asigVar + " = " + tempVarsList.get(0);
				tempVarsList.remove(0);
				writeFile(texto);
			}
			else if(ctx.getChild(exprPos.intValue()).getClass().equals(ExprContext.class)){
				// String auxTempVar = tempVariableGenerator();
				// if(ctx.getChild(exprPos.intValue()).getChild(0).getClass().equals(FunctionCallContext.class)){
				// 	visit(ctx.getChild(exprPos.intValue()).getChild(0));
				// 	texto = asigVar + " = pop";//asigno y con el valor de retorno
				// 	writeFile(texto);
				// }
				// else {
					texto = asigVar + " = " + visit(ctx.getChild(exprPos.intValue()));
					writeFile(texto);
				// }	
			}//else if ctx.
		}
	
		return "";
	}

	@Override
	public String visitBloque(BloqueContext ctx) {
		return super.visitBloque(ctx);
	}
	
	
	@Override
	public String visitSecvar(SecvarContext ctx) {
		if(ctx.getChildCount()>0){
			texto = tipo + ctx.getChild(1).getText();
			writeFile(texto);
			if(ctx.getChild(SecvarContext.class, 0)!=null){
				visit(ctx.getChild(SecvarContext.class, 0));
			}
			if(ctx.getChild(AsignacionContext.class, 0).getChildCount()>0){
				tempVarsList.add(ctx.getChild(1).getText());
				visit(ctx.getChild(AsignacionContext.class, 0));
			}
			
		}
		return "";
	}

	@Override
	public String visitDeclaracion(DeclaracionContext ctx) {
		texto = ctx.TIPO().getText() + " ";
		texto += visit(ctx.getChild(ExprContext.class, 0));
		writeFile(texto);
		if(ctx.getChild(SecvarContext.class, 0)!=null){
			tipo = ctx.TIPO().getText() + " ";
			visit(ctx.getChild(SecvarContext.class, 0));
		}
		if(ctx.getChild(AsignacionContext.class, 0).getChildCount()>0){
			tempVarsList.add(ctx.getChild(ExprContext.class, 0).getText());
			visit(ctx.getChild(AsignacionContext.class, 0));
		}

		

		return "";
	}

	@Override
	public String visitOal(OalContext ctx) {

		if (!ctx.getParent().getClass().equals(FactorContext.class))
			tempVarsList.clear();
		return super.visitOal(ctx);
	}

	@Override
	public String visitOp_arit(Op_aritContext ctx) {
		
		visitAllHijos(ctx);
		return "";
	}

	@Override
	public String visitT(TContext ctx) {
		if (ctx.getChildCount() > 0) {
			visit(ctx.getChild(TermContext.class, 0));
			String tempVar = tempVariableGenerator();
			Integer len = tempVarsList.size() - 1;
			texto = tempVar + " = ";
			texto += tempVarsList.get(len - 1) + ctx.getChild(0).getText() + tempVarsList.get(len);
			writeFile(texto);
			tempVarsList.remove(len.intValue());
			tempVarsList.remove(len.intValue() - 1);
			tempVarsList.add(tempVar);
			if (ctx.getChild(TContext.class, 0).getChildCount() > 0)
				visit(ctx.getChild(TContext.class, 0));
		}
		
		return "";
	}

	@Override
	public String visitTerm(TermContext ctx) {
		
		
		String tempVar, factorText, fText;

		if (ctx.getChild(FContext.class, 0).getChildCount()>0) {
			factorText = visitFactor(ctx.getChild(FactorContext.class, 0));
			fText = visitF(ctx.getChild(FContext.class, 0));
			tempVar = tempVariableGenerator();
			texto = tempVar + " = " + factorText + fText;
			writeFile(texto);
			tempVarsList.add(0, tempVar);
		}
		else {
			tempVar = visit(ctx.getChild(FactorContext.class, 0));
			tempVarsList.add(tempVar);
		}



		return "";
	}

	

	@Override
	public String visitFactor(FactorContext ctx) {
		String retText = "";
		if(ctx.getChild(FunctionCallContext.class, 0)!=null) visit(ctx.getChild(FunctionCallContext.class, 0));
		else if(ctx.getChild(OalContext.class, 0)!=null){
			visit(ctx.getChild(OalContext.class, 0));
			retText = tempVarsList.get(0);
			tempVarsList.remove(0);
		}
		else if(ctx.getChild(FactorContext.class, 0)!=null){
			retText = visit(ctx.getChild(FactorContext.class, 0));
		}
		else retText = ctx.getText();
		
		return retText;
	}

	@Override
	public String visitF(FContext ctx) {
		String ret="";
		if(ctx.getChildCount()>0){

			String text, tempVar;

			if(ctx.getChild(FContext.class, 0).getChildCount()>0){
				String factorText, fText;
				factorText = visitFactor(ctx.getChild(FactorContext.class, 0));
				fText = visitF(ctx.getChild(FContext.class, 0));
				tempVar = tempVariableGenerator();
				text = tempVar + " = " + factorText + fText;
				writeFile(text);
				ret = ctx.getChild(0).getText() + tempVar;

			}
			else ret = ctx.getChild(0).getText() + visit(ctx.getChild(FactorContext.class, 0));
		}

		return ret;
	}

	@Override
	public String visitFormalParameter(FormalParameterContext ctx) {
		//texto = ctx.ID().getText() + " = pop"; // saco los parametros de la funcion
		texto = "pop " + ctx.ID().getText();
		writeFile(texto);
		return "";
	}

	@Override
	public String visitFormalParameters(FormalParametersContext ctx) {
		Integer i = 0;
		while(ctx.getChild(FormalParameterContext.class, i.intValue())!=null){ 
			visit(ctx.getChild(FormalParameterContext.class, i.intValue()));// visito todos los parametros
			i++;
		}
		return "";
	}


	@Override
	public String visitIreturn(IreturnContext ctx) {
		if(ctx.getChild(OalContext.class, 0)!=null){
			visit(ctx.getChild(OalContext.class, 0)); //opero
			texto = "push " + tempVarsList.get(0); //guardo en la pila el valor de retorno 
			writeFile(texto);
			tempVarsList.remove(0);
		}
		else if (!ctx.getChild(1).getText().equals(";")) {
			if(ctx.ID() != null){
				texto = "push " + ctx.ID().getText(); //guardo en la pila el valor de retorno 
				writeFile(texto);
			}
			else {
				String tempVarAux = tempVariableGenerator();
				texto = tempVarAux + " = " + ctx.getChild(1).getText();
				writeFile(texto);
				texto = "push "  + tempVarAux; //guardo en la pila el valor de retorno 
				writeFile(texto);
			}
		}

		texto = "jmp d"; //retorno
		writeFile(texto);
		return "";
	}

	@Override
	public String visitFunctionCall(FunctionCallContext ctx) {
		String tempLabel;
		if(funcLabels.containsKey(ctx.ID().getText())){
			if(ctx.getChild(ExprListContext.class, 0)!=null){
				visit(ctx.getChild(ExprListContext.class, 0));
			}
			tempLabel = labelsGenerator();
			texto = "push " + tempLabel; //guardo la posicion a retornar
			writeFile(texto);
			texto = "jmp " + funcLabels.get(ctx.ID().getText()); //salto a la funcion
			writeFile(texto);
			texto = "label " + tempLabel; // etiqueta donde retornar de la funcion
			writeFile(texto);
		}
		return "";
	}

	@Override
	public String visitFunctionDecl(FunctionDeclContext ctx) {
		String tempLabel;
		if(ctx.getChild(BloqueContext.class, 0)!=null){
			if(funcLabels.containsKey(ctx.ID().getText())) tempLabel = funcLabels.get(ctx.ID().getText());
			else {
				tempLabel = labelsGenerator();
				funcLabels.put(ctx.ID().getText(), tempLabel);
			}
			// labelsList.add(0,tempLabel);
			texto = "label " + tempLabel; // etiqueta comienzo de funcion
			writeFile(texto);
			//texto = "d = pop";
			texto = "pop d";
			writeFile(texto);
			if(ctx.getChild(FormalParametersContext.class, 0)!=null){
				visit(ctx.getChild(FormalParametersContext.class, 0));
			}
			visit(ctx.getChild(BloqueContext.class, 0));
		}
		else {
			tempLabel = labelsGenerator();
			funcLabels.put(ctx.ID().getText(), tempLabel);
		}
		return "";
	}

	@Override
	public String visitExpr(ExprContext ctx) {
		if (ctx.getChild(FunctionCallContext.class, 0)!=null){
			visit(ctx.getChild(FunctionCallContext.class, 0));
			texto = "pop";
		}
		else if(ctx.getChild(ExprContext.class, 0)!=null) texto = visit(ctx.getChild(ExprContext.class, 0));
		else texto = ctx.getText();
		return texto;
	}

	@Override
	public String visitExprList(ExprListContext ctx) {
		if(ctx.getChild(OalContext.class, 0)!=null){
			visit(ctx.getChild(OalContext.class, 0)); //opero
			texto = "push " + tempVarsList.get(0); //guardo en la pila el valor de retorno 
			writeFile(texto);
			tempVarsList.remove(0);
		}
		else {
			String aux; 
			if(ctx.getChild(ExprContext.class, 0).ENTERO()!=null){
				aux = tempVariableGenerator();
				texto = aux + " = " + visit(ctx.getChild(ExprContext.class, 0));
				writeFile(texto);
			}
			else {
				aux = visit(ctx.getChild(ExprContext.class, 0));
				if(aux.equals("pop")){
					aux = tempVariableGenerator();
					//texto = aux + " = pop";
					texto = "pop " + aux;
					writeFile(texto);
				}
			}
			texto = "push " + aux;
			writeFile(texto);
		}

		if(ctx.getChild(ExprListContext.class, 0)!=null) visit(ctx.getChild(ExprListContext.class, 0));

		return "";
	}

	@Override
	public String visitInst_for(Inst_forContext ctx) {

		String tempLabel = labelsGenerator();
		labelsList.add(0,tempLabel);
		visit(ctx.getChild(2)); // resuelvo la asignacion o declaracion de la variable de iteracion
		texto = "label " + labelsList.get(0);
		writeFile(texto);
		tempLabel = labelsGenerator();
		visit(ctx.getChild(4)); // resuelvo condicion de stop/control
		texto = "ifnot " + tempVarsList.get(0) + " jmp " + tempLabel;
		tempVarsList.remove(0);
		writeFile(texto);
		visit(ctx.getChild(InstruccionContext.class, 0)); // resuelvo tarea a iterar
		visit(ctx.getChild(6)); // resuelvo expresion final
		texto = "jmp " + labelsList.get(0); // vuelvo a iterar
		writeFile(texto);
		texto = "label " + tempLabel; // etiqueta de salida
		labelsList.clear();
		writeFile(texto);
		
		return "";
	}

	@Override
	public String visitInst_if(Inst_ifContext ctx) {
		String tempLabel = labelsGenerator();
		labelsList.add(tempLabel);
		visit(ctx.getChild(OalContext.class, 0));
		texto = "ifnot " + tempVarsList.get(0) + " jmp " + tempLabel;
		tempVarsList.remove(0);
		writeFile(texto);
		visit(ctx.getChild(InstruccionContext.class, 0));
		if (ctx.ELSE() != null) {
			tempLabel = labelsGenerator();
			texto = "jmp " + tempLabel;
			writeFile(texto);
			texto = "label " + labelsList.get(0);
			writeFile(texto);
			visit(ctx.getChild(InstruccionContext.class, 1));
		}

		texto = "label " + tempLabel;
		labelsList.clear();
		writeFile(texto);

		return "";// super.visitInst_if(ctx);
	}

	@Override
	public String visitInst_while(Inst_whileContext ctx) {

		String tempLabel = labelsGenerator();
		labelsList.add(tempLabel);
		texto = "label " + labelsList.get(0); // etiqueta para iterar
		writeFile(texto);
		visit(ctx.getChild(OalContext.class, 0)); // resuelvo condicion de stop/control
		tempLabel = labelsGenerator();
		texto = "ifnot " + tempVarsList.get(0) + " jmp " + tempLabel; // si no se cumple termino
		tempVarsList.remove(0);
		writeFile(texto);
		visit(ctx.getChild(InstruccionContext.class, 0)); // resuelvo tarea a iterar
		texto = "jmp " + labelsList.get(0); // vuelvo a iterar
		writeFile(texto);
		texto = "label " + tempLabel; // etiqueta de salida
		labelsList.clear();
		writeFile(texto);

		return "";
	}

	@Override
	public String visitL_f(L_fContext ctx) {
		String ret="";
		if(ctx.getChildCount()>0){

			String text, tempVar;

			if(ctx.getChild(L_fContext.class, 0).getChildCount()>0){
				String factorText, fText;
				factorText = visitFactor(ctx.getChild(FactorContext.class, 0));
				fText = visit(ctx.getChild(L_fContext.class, 0));
				tempVar = tempVariableGenerator();
				text = tempVar + " = " + factorText + fText;
				writeFile(text);
				ret = ctx.getChild(0).getText() + tempVar;

			}
			else ret = ctx.getChild(0).getText() + visit(ctx.getChild(FactorContext.class, 0));
		}

		return ret;
	}

	@Override
	public String visitL_t(L_tContext ctx) {
		if (ctx.getChildCount() > 0) {
			visit(ctx.getChild(L_termContext.class, 0));
			String tempVar = tempVariableGenerator();
			Integer len = tempVarsList.size() - 1;
			texto = tempVar + " = ";
			texto += tempVarsList.get(len - 1) + ctx.getChild(0).getText() + tempVarsList.get(len);
			writeFile(texto);
			tempVarsList.remove(len.intValue());
			tempVarsList.remove(len.intValue() - 1);
			tempVarsList.add(tempVar);
			if (ctx.getChild(L_tContext.class, 0).getChildCount() > 0)
				visit(ctx.getChild(L_tContext.class, 0));
		}

		return "";
	}

	@Override
	public String visitL_term(L_termContext ctx) {
		String tempVar, factorText, l_fText;

		if (ctx.getChild(L_fContext.class, 0).getChildCount()>0) {
			factorText = visitFactor(ctx.getChild(FactorContext.class, 0));
			l_fText = visit(ctx.getChild(L_fContext.class, 0));
			tempVar = tempVariableGenerator();
			texto = tempVar + " = " + factorText + l_fText;
			writeFile(texto);
			tempVarsList.add(0, tempVar);
		}
		else {
			tempVar = visit(ctx.getChild(FactorContext.class, 0));
			tempVarsList.add(tempVar);
		}



		return "";
	}

	@Override
	public String visitOp_logic(Op_logicContext ctx) {
		visitAllHijos(ctx);
		return "";
	}

	private String tempVariableGenerator() {
		String ret = "t" + varsCount;

		varsCount++;
		return ret;
	}

	private String labelsGenerator() {
		String ret = "l" + labelsCount;
		labelsCount++;
		return ret;
	}

	private void createFile(String fileName) {
		try {
			File myObj = new File(fileName);
			myObj.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeFile(String text) {
		// System.out.println(text);
		try {
			FileWriter myWriter = new FileWriter(outputFile+".txt",fileFlag);
			myWriter.append(text+"\n");
			myWriter.close();
			fileFlag = true;
		// writeFile("Successfully wrote to the file.");
		} catch (IOException e) {
		// writeFile("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Visita todos los nodos hijo.
	 * 
	 * @param ctx Contexto del nodo donde estamos parados
	 */
	public String visitAllHijos(RuleContext ctx) {
		for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
			visit(ctx.getChild(hijo));
		}
		return texto;
	}

	public String getOutputFile(){
		return outputFile;
	}

	@Override
	public String visitInstruccion(InstruccionContext ctx) {

		return super.visitInstruccion(ctx);
	}

	

}
