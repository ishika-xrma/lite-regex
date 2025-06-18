package lite_regex;
import java.util.List;
import java.util.Scanner;
public class RegexEngine {
    private final NFA nfa;
    
    public RegexEngine(String pattern) {
        lexer lexer = new lexer(pattern);
        List<RegexToken> tokens = lexer.tokenize();
        for(RegexToken token : tokens) {
        	System.out.println(token);
        }
        
        parser parser = new parser(tokens);
        RegexNode ast = parser.parse();
        
        System.out.println("\nParsed AST:");
        System.out.println(	);
        
        
        NFABuilder builder = new NFABuilder();
        this.nfa = builder.build(ast);
    }
    
    public boolean matches(String text) {
        NFAMatcher matcher = new NFAMatcher(nfa);
        return matcher.matches(text);
    }
    
    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);

        System.out.print("Enter regex pattern: ");
        String pattern = scanner.nextLine();

        RegexEngine engine = new RegexEngine(pattern);

        while (true) {
            System.out.print("Enter text to test (or type 'exit' to quit): ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            boolean result = engine.matches(input);
            
            if(result) {
            	System.out.println("'" + input + "' matches pattern '" + pattern + "': " + result);
            }
            else {
            	System.out.println("'" + input + "' doesn't match'" + pattern + "': " + result);
            }
        }

        scanner.close();
    }
}
