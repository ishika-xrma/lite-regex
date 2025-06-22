package lite_regex;
import java.util.*;

//5. PATTERN EXPLANATION GENERATOR
class PatternExplainer {
 public static String explain(String pattern) {
     StringBuilder explanation = new StringBuilder();
     explanation.append("Pattern Explanation for: ").append(pattern).append("\n\n");
     
     lexer lexer = new lexer(pattern);
     List<RegexToken> tokens = lexer.tokenize();
     
     for (int i = 0; i < tokens.size(); i++) {
         RegexToken token = tokens.get(i);
         explanation.append(explainToken(token, i, tokens));
     }
     
     return explanation.toString();
 }
 
 private static String explainToken(RegexToken token, int index, List<RegexToken> tokens) {
     switch (token.getType()) {
         case CHARACTER:
             return String.format("• Matches literal character '%c'\n", token.getValue());
         case DOT:
             return "• Matches any single character (except newline)\n";
         case STAR:
             return "• Matches zero or more of the preceding element\n";
         case PLUS:
             return "• Matches one or more of the preceding element\n";
         case QUESTION:
             return "• Matches zero or one of the preceding element (optional)\n";
         case LPAREN:
             return "• Starts a capturing group\n";
         case RPAREN:
             return "• Ends a capturing group\n";
         case LBRACKET:
             return "• Starts a character class (matches any one character from the set)\n";
         case RBRACKET:
             return "• Ends a character class\n";
         case ALTERNATION:
             return "• OR operator - matches either the pattern before or after\n";
         case CARET:
             return "• Matches start of string (or line in multiline mode)\n";
         case DOLLAR:
             return "• Matches end of string (or line in multiline mode)\n";
         case WORD:
             return "• Matches word characters (letters, digits, underscore)\n";
         case DIGIT:
             return "• Matches any digit (0-9)\n";
         default:
             return String.format("• Token: %s\n", token.getType());
     }
 }
}