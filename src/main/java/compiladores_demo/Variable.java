package compiladores_demo;

public class Variable extends Id {
	
	public Variable(String name, DataType type, Boolean initialized, Boolean used){
		super();
		setName(name);
		setInitialized(initialized);
		setType(type);
		setUsed(used);
	}

	@Override
	public String toString(){ 
		String texto = "";

		texto += "variable: " + this.getName() + " tipo: " + this.getType() + " initializaed: " + this.getInitialized() + " used: " + this.getUsed();

		return texto;
	}
}
