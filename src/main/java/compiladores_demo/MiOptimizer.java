package compiladores_demo;

import java.io.BufferedReader;
import java.io.File; // Import the File class
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MiOptimizer{

	String outputFile;
	Boolean fileFlag;
	ArrayList<String> lines;
	//constructor
	public MiOptimizer(String inputFile){
		outputFile = inputFile + "Optimizado";
		createFile(outputFile+".txt");
		lines = readFile(inputFile+".txt");
		fileFlag = false;

	}

	public void optimizeCode(){
		Integer optRounds = 2;
		if(lines!=null){
			for(int i = 0; i < optRounds.intValue(); i++){

				constantReplacement();
				fewerAssignments();
				writeFile(outputFile);
			}

		}
		else System.out.println("Error al leer el archivo");
	}

	private void fewerOperations(){

	}

	private void fewerAssignments(){
		Iterator<String> itr = lines.iterator();
		String line;
		String regexId = "t[0-9]*";//" ([A-Za-zñ]|'_')([A-Za-zñ]|[0-9]*|'_')* "; //" [0-9]*[-+\\*/][0-9]*";
		String newLineText;
		while(itr.hasNext()){
			line = itr.next();
			int lineIndex = lines.indexOf(line);
			if(line.contains("=")){
				String aux [] = line.replace(" ", "").split("=");
				if(aux[1].matches(regexId)) {
					if(lines.get(lineIndex-1).replace(" ", "").contains(aux[1] + "=")) {
						newLineText = lines.get(lineIndex-1).replace(aux[1], aux[0]);
						lines.set(lineIndex-1, newLineText);
						lines.set(lineIndex, "");
					}
					
				}
			}
		}
	}

	private void constantReplacement(){
		Iterator<String> itr = lines.iterator();
		List<String> replaceTempVariablesList = new ArrayList<>();
		List<String> replaceValuesList = new ArrayList<>();
		String line;
		String regexConstantOal = " [0-9]*[-+\\*/][0-9]*";
		String regexConstantAssign = " ([0-9]*)";
		while(itr.hasNext()){
			line = itr.next();
			if(line.contains("=")){
				String aux [] = line.split("=");
				if(aux[1].matches(regexConstantOal)) {
					int a, b, r;
					char op;
					for(int i=0; i<aux[1].length(); i++){
						op = aux[1].charAt(i);
						if(op == '+'||op == '-'||op == '*'||op == '/'){
							a = Integer.parseInt(aux[1].substring(1, i));
							b = Integer.parseInt(aux[1].substring(i+1));
							switch(op){
						
								case '+': r=a+b; break;
								case '-': r=a-b; break;
								case '/': r=a/b; break;
								case '*': r=a*b; break;
								default: r=0;break;
							}
							lines.set(lines.indexOf(line), (aux[0] + "= "+r));
							
							break;
						}

					}

					
				}
				else if(aux[1].matches(regexConstantAssign)) {
					replaceTempVariablesList.add(aux[0].replace(" ", ""));
					replaceValuesList.add(aux[1].replace(" ", ""));
					lines.set(lines.indexOf(line), "");
				}
				else {
					for(int i = 0; i<replaceTempVariablesList.size(); i++){
						if(aux[1].contains(replaceTempVariablesList.get(i))){
							aux[1] = aux[1].replace(replaceTempVariablesList.get(i), replaceValuesList.get(i));
						}
						
						
						
					}
					lines.set(lines.indexOf(line), (aux[0] + "=" + aux[1]));
				}
			}

		}
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
			FileWriter myWriter = new FileWriter(outputFile+".txt",true);
			Iterator<String> itr = lines.iterator();
			while(itr.hasNext()) {
				String line = itr.next();
				if(!line.equals("")) myWriter.append(line+"\n");
			}
			myWriter.close();
			// fileFlag = true;
		// writeFile("Successfully wrote to the file.");
		} catch (IOException e) {
		// writeFile("An error occurred.");
			e.printStackTrace();
		}
	}

	private ArrayList<String> readFile(String fileName){
		File file = new File(fileName);
		BufferedReader br;
		String st;
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(file));
			while ((st = br.readLine()) != null) ret.add(st);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
}