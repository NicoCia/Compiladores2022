package compiladores_demo;

import java.util.HashMap;
import java.util.Map;


//Represent a node of the doubly linked list  
public class Node{
	Map<String, Id> data;  
        Node previous;  
        Node next;  
  
        public Node() {  
            this.data = new HashMap<String, Id>();  
	}  
}  
