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
        boolean inCharacterClass = false;
        
        while (position < pattern.length()) {
            char c = pattern.charAt(position);
            int currentPos = position;
            
            try {
                if (inCharacterClass && c == ']') {
                    tokens.add(new RegexToken(RegexToken.TokenType.RBRACKET, c, currentPos));
                    position++;
                    inCharacterClass = false;
                    continue;
                }
                
                switch (c) {
                    case '\\':
                        handleEscapeSequence(tokens, currentPos);
                        break;
                    case '[':
                        tokens.add(new RegexToken(RegexToken.TokenType.LBRACKET, c, currentPos));
                        position++;
                        inCharacterClass = true;
                        break;
                    case '-':
                        if (inCharacterClass) {
                            // Only treat as range operator if between two characters
                            if (position > 0 && position + 1 < pattern.length() && 
                                pattern.charAt(position-1) != '[' && 
                                pattern.charAt(position+1) != ']') {
                                tokens.add(new RegexToken(RegexToken.TokenType.DASH, c, currentPos));
                            } else {
                                tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, c, currentPos));
                            }
                        } else {
                            tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, c, currentPos));
                        }
                        position++;
                        break;
                    case '.':
                        // Check if this dot was escaped
                        if (position > 0 && pattern.charAt(position-1) == '\\') {
                            tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, c, currentPos));
                        } else {
                            tokens.add(new RegexToken(RegexToken.TokenType.DOT, c, currentPos));
                        }
                        position++;
                        break;
                    case '*':
                        tokens.add(new RegexToken(RegexToken.TokenType.STAR, c, currentPos));
                        position++;
                        break;
                    case '+':
                        tokens.add(new RegexToken(RegexToken.TokenType.PLUS, c, currentPos));
                        position++;
                        break;
                    case '?':
                        tokens.add(new RegexToken(RegexToken.TokenType.QUESTION, c, currentPos));
                        position++;
                        break;
                    case '(':
                        tokens.add(new RegexToken(RegexToken.TokenType.LPAREN, c, currentPos));
                        position++;
                        break;
                    case ')':
                        tokens.add(new RegexToken(RegexToken.TokenType.RPAREN, c, currentPos));
                        position++;
                        break;
                    case '|':
                        tokens.add(new RegexToken(RegexToken.TokenType.ALTERNATION, c, currentPos));
                        position++;
                        break;
                    case '^':
                        tokens.add(new RegexToken(RegexToken.TokenType.CARET, c, currentPos));
                        position++;
                        break;
                    case '$':
                        tokens.add(new RegexToken(RegexToken.TokenType.DOLLAR, c, currentPos));
                        position++;
                        break;
                    default:
                        tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, c, currentPos));
                        position++;
                }
            } catch (RegexException e) {
                throw e;
            } catch (Exception e) {
                throw new RegexException("Unexpected error while parsing regex", 
                                      currentPos, pattern, 
                                      "Character '" + c + "' caused an error: " + e.getMessage());
            }
        }
        
        return tokens;
    }

    private void handleEscapeSequence(List<RegexToken> tokens, int pos) {
        if (position + 1 >= pattern.length()) {
            throw new RegexException("Invalid escape sequence at end of pattern", 
                                  pos, pattern, 
                                  "Escape character '\\' must be followed by another character");
        }
        position++; // skip the backslash
        char escapedChar = pattern.charAt(position);
        position++;
        
        // Special case for escaped dot
        if (escapedChar == '.') {
            tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, '.', pos));
        }
        else {
            switch (escapedChar) {
                case 'w':
                    tokens.add(new RegexToken(RegexToken.TokenType.WORD, 'w', pos));
                    break;
                case 'd':
                    tokens.add(new RegexToken(RegexToken.TokenType.DIGIT, 'd', pos));
                    break;
                default:
                    // Treat other escaped chars as literals
                    tokens.add(new RegexToken(RegexToken.TokenType.CHARACTER, escapedChar, pos));
            }
        }
    }
}