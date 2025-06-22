package lite_regex;

import java.util.List;
import java.util.Scanner;

public class RegexEngine {
    private final DFA dfa;
    private final String pattern;

    public RegexEngine(String pattern) {
        this.pattern = pattern;
        try {
            lexer lexer = new lexer(pattern);
            List<RegexToken> tokens = lexer.tokenize();

            parser parser = new parser(tokens, pattern);
            RegexNode ast = parser.parse();

            NFABuilder nfaBuilder = new NFABuilder();
            NFA nfa = nfaBuilder.build(ast);

            DFABuilder dfaBuilder = new DFABuilder();
            this.dfa = dfaBuilder.build(nfa);
        } catch (RegexException e) {
            throw e;
        } catch (Exception e) {
            throw new RegexException("Failed to compile regex pattern",
                    0, pattern,
                    "Unexpected error: " + e.getMessage());
        }
    }
    
    public boolean matches(String text) {
        return dfa.matches(text);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter regex pattern: ");
        String pattern = scanner.nextLine();

        try {
            RegexEngine engine = new RegexEngine(pattern);
            System.out.println("Pattern compiled successfully!");

            // Print explanation
            System.out.println("\n=== Pattern Explanation ===");
            System.out.println(PatternExplainer.explain(pattern));
            System.out.println("===========================\n");

            while (true) {
                System.out.print("Enter text to test (or type 'exit' to quit): ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                boolean result = engine.matches(input);
                System.out.println("'" + input + "' " + (result ? "matches" : "does not match") +
                        " pattern '" + pattern + "'");
            }
        } catch (RegexException e) {
            System.err.println(e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
