package compiladores_demo;

import java.util.ArrayList;
import java.util.List;

public class Funcion extends Id{

	public List<DataType> args;

	public Funcion(String name, DataType type, Boolean initialized, Boolean used){
		//super();
		setName(name);
		setInitialized(initialized);
		setType(type);
		setUsed(used);

		this.args = new ArrayList<DataType>();  
	}

	public boolean addArg(DataType type){
		return args.add(type);
	}
	
}
