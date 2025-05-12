package lite_regex;
import java.util.*;
public class EnhancedRegexEngine {
    private final NFA nfa;
    private DFA dfa; // optional, for performance
    private final boolean useDFA;
    
    public EnhancedRegexEngine(String pattern) {
        this(pattern, true); // Use DFA by default for better performance
    }
    
    public EnhancedRegexEngine(String pattern, boolean useDFA) {
        lexer lexer = new lexer(pattern);
        List<RegexToken> tokens = lexer.tokenize();
        
        parser parser = new parser(tokens);
        RegexNode ast = parser.parse();
        
        NFABuilder builder = new NFABuilder();
        this.nfa = builder.build(ast);
        this.useDFA = useDFA;
        
        if (useDFA) {
            NFAtoDFAConverter converter = new NFAtoDFAConverter();
            this.dfa = converter.convert(nfa);
        }
    }
    
    public boolean matches(String text) {
        if (useDFA) {
            return dfa.matches(text);
        } else {
            NFAMatcher matcher = new NFAMatcher(nfa);
            return matcher.matches(text);
        }
    }
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter regex pattern: ");
        String pattern = scanner.nextLine();

        EnhancedRegexEngine engine = new EnhancedRegexEngine(pattern);

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
