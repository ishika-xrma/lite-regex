package lite_regex;

import java.util.ArrayList;
import java.util.List;

public class lexer {
    private final String pattern;
    private int position;
    
    public lexer(String pattern) {
        this.pattern = pattern;
        this.position = 0;
    }
    
    public List<RegexToken> tokenize() {
        List<RegexToken> tokens = new ArrayList<>();
        
        while (position < pattern.length()) {
            char c = pattern.charAt(position++);
            
            switch (c) {
                case '.':
                    tokens.add(new RegexToken(RegexToken.TokenType.DOT, c));
                    break;
                case '*':
                    tokens.add(new RegexToken(RegexToken.TokenType.STAR, c));
                    break;
                case '+':
                    tokens.add(new RegexToken(RegexToken.TokenType.PLUS, c));
                    break;
                case '?':
                    tokens.add(new RegexToken(RegexToken.TokenType.QUESTION, c));
                    break;
                case '(':
                    tokens.add(new RegexToken(RegexToken.TokenType.LPAREN, c));
                    break;
                case ')':
                    tokens.add(new RegexToken(RegexToken.TokenType.RPAREN, c));
                    break;
                case '|':
                    tokens.add(new RegexToken(RegexToken.TokenType.ALTERNATION, c));
                    break;
                case '[':
                    tokens.add(new RegexToken(RegexToken.TokenType.LBRACKET, c));
                    break;
                case ']':
                    tokens.add(new RegexToken(RegexToken.TokenType.RBRACKET, c));
                    break;
                case '^':
                    tokens.add(new RegexToken(RegexToken.TokenType.CARET, c));
                    break;
                case '$':
                    tokens.add(new RegexToken(RegexToken.TokenType.DOLLAR, c));
                    break;
                case '\\':
                    if (position < pattern.length()) {
                        // Handle escape sequences
                        c = pattern.charAt(position++);
                        tokens.add(new RegexToken(RegexToken.TokenType.ESCAPE, c));
                    } else {
                        throw new IllegalArgumentException("Invalid escape sequence at end of pattern");
                    }
                    break;
                default:
                    tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, c));
            }
        }
        
        return tokens;
    }
}
