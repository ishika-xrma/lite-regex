package lite_regex;
import java.util.List;
public class RegexEngine {
    private final NFA nfa;
    
    public RegexEngine(String pattern) {
        lexer lexer = new lexer(pattern);
        List<RegexToken> tokens = lexer.tokenize();
        
        parser parser = new parser(tokens);
        RegexNode ast = parser.parse();
        
        NFABuilder builder = new NFABuilder();
        this.nfa = builder.build(ast);
    }
    
    public boolean matches(String text) {
        NFAMatcher matcher = new NFAMatcher(nfa);
        return matcher.matches(text);
    }
    
    public static void main(String[] args) {
        // Simple test
        RegexEngine engine = new RegexEngine("a(b|c)*");
        
        System.out.println("a matches: " + engine.matches("a"));
        System.out.println("ab matches: " + engine.matches("ab"));
        System.out.println("abc matches: " + engine.matches("abc"));
        System.out.println("abcbc matches: " + engine.matches("abcbc"));
        System.out.println("d doesn't match: " + !engine.matches("d"));
    }
}
