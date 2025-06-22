package lite_regex;

import java.util.List;
import java.util.Scanner;

public class RegexEngine {
    private final NFA nfa;
    private final String pattern;
    private final int minLength;
    private final Integer maxLength;
    private final NFAMatcher matcher;

    public RegexEngine(String pattern) {
        this.pattern = pattern;
        try {
            // Lexical analysis
            lexer lexer = new lexer(pattern);
            List<RegexToken> tokens = lexer.tokenize();

            // Parsing
            parser parser = new parser(tokens, pattern);
            RegexNode ast = parser.parse();

            // NFA construction
            NFABuilder nfaBuilder = new NFABuilder();
            this.nfa = nfaBuilder.build(ast);
            this.matcher = new NFAMatcher(nfa);
            
            // Store length constraints for quick access
            this.minLength = nfa.getMinLength();
            this.maxLength = nfa.getMaxLength();
            
        } catch (RegexException e) {
            throw e;
        } catch (Exception e) {
            throw new RegexException("Failed to compile regex pattern",
                    0, pattern,
                    "Unexpected error: " + e.getMessage());
        }
    }
    
    public boolean matches(String text) {
        // Quick length check before NFA matching
        if (text.length() < minLength) {
            return false;
        }
        if (maxLength != null && text.length() > maxLength) {
            return false;
        }
        return matcher.matches(text);
    }

    public String getPattern() {
        return pattern;
    }

    public int getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter regex pattern: ");
        String pattern = scanner.nextLine();

        try {
            RegexEngine engine = new RegexEngine(pattern);
            System.out.println("Pattern compiled successfully!");
            
            // Print pattern info
            System.out.println("\n=== Pattern Information ===");
            System.out.println("Minimum length: " + engine.getMinLength());
            if (engine.getMaxLength() != null) {
                System.out.println("Maximum length: " + engine.getMaxLength());
            } else {
                System.out.println("Maximum length: unlimited");
            }
            
            // Print explanation
            System.out.println("\n=== Pattern Explanation ===");
            System.out.println(PatternExplainer.explain(pattern));
            System.out.println("===========================\n");

            // Interactive testing loop
            while (true) {
                System.out.print("Enter text to test (or type 'exit' to quit): ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                boolean result = engine.matches(input);
                System.out.println("'" + input + "' " + (result ? "matches" : "does not match") +
                        " pattern '" + pattern + "'");
                
                // Show length info if mismatch
                if (!result) {
                    if (input.length() < engine.getMinLength()) {
                        System.out.println("  (Input too short - minimum length is " + 
                                         engine.getMinLength() + ")");
                    } else if (engine.getMaxLength() != null && 
                              input.length() > engine.getMaxLength()) {
                        System.out.println("  (Input too long - maximum length is " + 
                                         engine.getMaxLength() + ")");
                    }
                }
            }
        } catch (RegexException e) {
            System.err.println(e.getMessage());
        } finally {
            scanner.close();
        }
    }
}