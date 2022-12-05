package compiladores_demo;

public class SymbolsTable {
    // Static variable reference of single_instance
    // of type Singleton
    private static SymbolsTable single_instance = null;
    private DoublyLinkedList table;
  
    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private SymbolsTable()
    {
        this.table = new DoublyLinkedList();
    }
  
    // Static method
    // Static method to create instance of Singleton class
    public static SymbolsTable getInstanceOf()
    {
        if (single_instance == null)
            single_instance = new SymbolsTable();
  
        return single_instance;
    }

    public void addContext()
    {   
        table.addNode();

	    return ;
    }

    public void delContext()
    {   
        table.delLastNode();

	    return ;
    }

    public void addSymbol(Id id)
    {
        table.tail.data.put(id.getName(), id);

	    return ;
    }

    public Id finSymbol(String name){
        Node temp = table.tail;

        while (temp != table.head){
            temp = temp.previous;

            if(temp.data.containsKey(name)) return temp.data.get(name);
        }

        
        return null;
    }

    public Id findLocalSymbol(String name)
    {   
        if(table.tail.data.containsKey(name)) return table.tail.data.get(name);

        return null;
    }



}