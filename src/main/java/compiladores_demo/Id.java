package compiladores_demo;

public abstract class Id {
	private String name;
	private DataType type;
	private Boolean initialized;
	private Boolean used;

	public void setName(String name){
		this.name = name;
	}


	public void setType(DataType type){
		this.type = type;
	}

	public void setInitialized(Boolean initialized){
		this.initialized = initialized;
	}

	public void setUsed(Boolean used){
		this.used = used;
	}

	public String getName(){
		return name;
	}

	public DataType getType(){
		return type;
	}

	public Boolean getInitialized(){
		return initialized;
	}

	public Boolean getUsed(){
		return used;
	}
}
