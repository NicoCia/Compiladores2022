package compiladores_demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;



public class SymbolsTable {
    // Static variable reference of single_instance
    // of type Singleton
    private static SymbolsTable single_instance = null;
    private LinkedList<HashMap<String, Id>> table;
  
    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    public SymbolsTable()
    {
        this.table = new LinkedList<HashMap<String, Id>>();
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
        table.add(new HashMap<String, Id>());

	    return ;
    }

    public void delContext()
    {   
        table.removeLast();

	    return ;
    }

    public void addSymbol(Id id)
    {
        table.getLast().put(id.getName(), id);

	    return ;
    }

    public Id findSymbol(String name){
        Iterator<HashMap<String, Id>> itr = table.descendingIterator();

        while (itr.hasNext()){
            HashMap<String, Id> temp = itr.next();

            if(temp.containsKey(name)) return temp.get(name);
        }

        
        return null;
    }

    public Id findLocalSymbol(String name)
    {   
        if(table.getLast().containsKey(name)) return table.getLast().get(name);

        return null;
    }

    public ArrayList<Id> getUnusedSymbols(){
        ArrayList<Id> ret = new ArrayList<Id>();
        Iterator<Id> itr = table.getLast().values().iterator();

        while(itr.hasNext()){
            Id temp = itr.next();
            if(!temp.getUsed()) ret.add(temp);
        }

        return ret;
    }

    @Override
    public String toString() {
        String texto = "";
        Iterator<HashMap<String, Id>> itr = table.descendingIterator();

        while (itr.hasNext()){
            Iterator <Id> temp = itr.next().values().iterator();
            while (temp.hasNext()){
                texto += temp.next().toString() + "\n";
            }

        }
        return texto;
    }

    



}