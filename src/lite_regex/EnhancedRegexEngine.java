package lite_regex;
import java.util.List;
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
        // Simple test
        RegexEngine engine = new RegexEngine("a(b|c)*");
        
        System.out.println("a matches: " + engine.matches("a"));
        System.out.println("ab matches: " + engine.matches("ab"));
        System.out.println("abc matches: " + engine.matches("abc"));
        System.out.println("abcbc matches: " + engine.matches("abcbc"));
        System.out.println("d doesn't match: " + !engine.matches("d"));
    }
}
