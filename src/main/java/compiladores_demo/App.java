package compiladores_demo;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

// Las diferentes entradas se explicaran oportunamente
public class App {
    public static void main(String[] args) throws Exception {
        // System.out.println("Hello, World!!!");
        // create a CharStream that reads from file
        String inputFile = "funcion";
        CharStream input = CharStreams.fromFileName("input/" + inputFile + ".txt");//"input/entrada.txt");

        // create a lexer that feeds off of input CharStream
        compiladorLexer lexer = new compiladorLexer(input);
        
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // create a parser that feeds off the tokens buffer
        compiladorParser parser = new compiladorParser(tokens);
                
        // create Listener
        MiListener escucha = new MiListener();

        // Conecto el objeto con Listeners al parser
        parser.addParseListener(escucha);

        // Solicito al parser que comience indicando una regla gramatical
        // En este caso la regla es el simbolo inicial
        // parser.programa();
        ParseTree tree = parser.programa();
        // Conectamos el visitor
        if(!escucha.getErrorFlag()){
            MiVisitor visitor = new MiVisitor(inputFile);
            visitor.visit(tree);

            MiOptimizer optimizer = new MiOptimizer(visitor.getOutputFile());
            optimizer.optimizeCode();
        }
        // // System.out.println(visitor);
        // // System.out.println(visitor.getErrorNodes());
        // // Imprime el arbol obtenido
        // // System.out.println(tree.toStringTree(parser));
        // // System.out.println(escucha);


        // Caminante walker = new Caminante();
        // walker.visit(tree);
        // System.out.println(walker);

        // System.out.println(walker.getErrorNodes());
    }
}