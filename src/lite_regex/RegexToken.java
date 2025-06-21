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
        ESCAPE,         // \ (escape character)
        DASH,           // - (range in character class)
        WORD,          // \w
        DIGIT          // \d
    }
    
    private final TokenType type;
    private final char value;
    private final int position;
    
    public RegexToken(TokenType type, char value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }
    
    public TokenType getType() { return type; }
    public char getValue() { return value; }
    public int getPosition() { return position; }
    
    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%c', pos=%d}", type, value, position);
    }
}