package lite_regex;

public class RegexToken {
	public enum TokenType {
        CHARACTER,      // Regular character
        DOT,            // . (any character)
        STAR,           // * (zero or more)
        PLUS,           // + (one or more)
        QUESTION,       // ? (zero or one)
        LPAREN,         // (
        RPAREN,         // )
        ALTERNATION,    // |
        LBRACKET,       // [
        RBRACKET,       // ]
        CARET,          // ^ (start of line or negation)
        DOLLAR,         // $ (end of line)
        ESCAPE          // \ (escape character)
    }
    
    private final TokenType type;
    private final char value;
    
    public RegexToken(TokenType type, char value) {
        this.type = type;
        this.value = value;
    }
    
    // Getters
    public TokenType getType() { return type; }
    public char getValue() { return value; }
    
    @Override
    public String toString() {
        return "Token{type=" + type + ", value='" + value + "'}";
    }

}
