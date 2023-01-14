package compiladores_demo;

import java.util.ArrayList;
import java.util.List;

public class Funcion extends Id{

	public List<ArrayList<String>> args;
	//public List<DataType> args;

	public Funcion(String name, DataType type, Boolean initialized, Boolean used){
		//super();
		setName(name);
		setInitialized(initialized);
		setType(type);
		setUsed(used);

		this.args = new ArrayList<ArrayList<String>>();  
	}

	public boolean addArg(ArrayList<String> params){
		return args.add(params);
	}

	public Integer getArgsSize(){
		return args.size();
	}

	@Override
	public String toString(){ 
		String texto = "";

		texto += this.getType() + " " + this.getName() + " (";

		for (ArrayList<String> p : args){
			for (String s : p){
				texto += s + " ";
			}
			if(args.indexOf(p)<args.size()-1) texto += ", ";
		}

		texto += ") initialized: " + this.getInitialized() + " used: " + this.getUsed();

		return texto;
	}
	
}
