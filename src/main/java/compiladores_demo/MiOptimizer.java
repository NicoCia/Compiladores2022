package compiladores_demo;

import java.io.BufferedReader;
import java.io.File; // Import the File class
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.util.ArrayList;
import java.util.Iterator;


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
		Integer optRounds = 1;
		if(lines!=null){
			for(int i = 0; i < optRounds.intValue(); i++){
				constantReplacement();
				writeFile(outputFile);
			}

		}
		else System.out.println("Error al leer el archivo");
	}

	private void fewerOperations(){

	}

	private void fewerAssignments(){

	}

	private void constantReplacement(){
		Iterator<String> itr = lines.iterator();
		String line;
		String regex = " [0-9]*[-+\\*/][0-9]*";//[0-9]*";
		while(itr.hasNext()){
			line = itr.next();
			if(line.contains("=")){
				String aux [] = line.split("=");
				if(aux[1].matches(regex)) {
					int a, b, r;
					char op;
					for(int i=0; i<aux[1].length(); i++){
						op = aux[1].charAt(i);
						if(op == '+'||op == '-'||op == '*'||op == '/'){
							// System.out.println("old line:" + line);
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
							
							// System.out.println("new line:" + line);
							break;
						}

					}

					
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
			while(itr.hasNext()) myWriter.append(itr.next()+"\n");
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