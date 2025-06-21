package lite_regex;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class parser {
    private final List<RegexToken> tokens;
    private int position;
    private final String originalPattern;

    public parser(List<RegexToken> tokens, String originalPattern) {
        this.tokens = tokens;
        this.position = 0;
        this.originalPattern = originalPattern;
    }

    public RegexNode parse() {
        try {
            RegexNode expr = parseExpression();
            if (position < tokens.size()) {
                throwParseError("Unexpected token at end of pattern", 
                              "Expected end of pattern but found '" + tokens.get(position).getValue() + "'");
            }
            return expr;
        } catch (RegexException e) {
            throw e;
        } catch (Exception e) {
            throw new RegexException("Failed to parse regex pattern", 
                                  0, originalPattern, 
                                  "Unexpected error: " + e.getMessage());
        }
    }

    private void throwParseError(String message, String details) {
        int errorPos = position < tokens.size() ? tokens.get(position).getPosition() : originalPattern.length();
        throw new RegexException("Regex parse error: " + message, 
                              errorPos, 
                              originalPattern, 
                              details);
    }

    private RegexNode parseExpression() {
        RegexNode term = parseTerm();
        while (position < tokens.size() && tokens.get(position).getType() == RegexToken.TokenType.ALTERNATION) {
            int opPos = tokens.get(position).getPosition();
            position++;
            RegexNode right = parseTerm();
            if (right == null) {
                throwParseError("Missing right operand for alternation", 
                              "Expected pattern after '|' at position " + opPos);
            }
            term = new AlternationNode(term, right);
        }
        return term;
    }

    private RegexNode parseTerm() {
        RegexNode factor = null;
        while (position < tokens.size()) {
            RegexToken token = tokens.get(position);
            if (token.getType() == RegexToken.TokenType.ALTERNATION ||
                token.getType() == RegexToken.TokenType.RPAREN) {
                break;
            }

            RegexNode nextFactor = parseFactor();
            if (nextFactor == null) {
                throwParseError("Invalid factor in pattern", 
                              "Unexpected token '" + token.getValue() + "'");
            }

            if (factor == null) {
                factor = nextFactor;
            } else {
                factor = new ConcatenationNode(factor, nextFactor);
            }
        }
        return factor != null ? factor : new CharacterNode('\0');
    }

    private RegexNode parseFactor() {
        RegexNode base = parseBase();
        if (position < tokens.size()) {
            RegexToken token = tokens.get(position);
            if (token.getType() == RegexToken.TokenType.STAR ||
                token.getType() == RegexToken.TokenType.PLUS ||
                token.getType() == RegexToken.TokenType.QUESTION) {
                position++;
                return new RepetitionNode(base, token.getValue());
            }
        }
        return base;
    }

    private RegexNode parseBase() {
        if (position >= tokens.size()) {
            throwParseError("Unexpected end of pattern", 
                          "Expected a character, group, or character class");
        }

        RegexToken token = tokens.get(position);
        position++;

        switch (token.getType()) {
            case WORD:
                return new WordCharNode();
            case DIGIT:
                return new DigitNode();
            case CHARACTER:
                return new CharacterNode(token.getValue());
            case DOT:
                return new AnyCharNode();
            case LPAREN:
                RegexNode expr = parseExpression();
                if (position >= tokens.size() || tokens.get(position).getType() != RegexToken.TokenType.RPAREN) {
                    throwParseError("Missing closing parenthesis", 
                                  "No matching ')' for opening '(' at position " + token.getPosition());
                }
                position++;
                return expr;
            case LBRACKET:
                return parseCharacterClass();
            case ESCAPE:
                return new CharacterNode(token.getValue());
            default:
                throwParseError("Unexpected token: " + token.getType(), 
                              "Token '" + token.getValue() + "' is not valid in this position");
        }
        return null;
    }

    private RegexNode parseCharacterClass() {
        boolean negated = false;
        Set<Character> characters = new HashSet<>();
        int classStartPos = position - 1; // Position of the '['

        if (position < tokens.size() && tokens.get(position).getType() == RegexToken.TokenType.CARET) {
            negated = true;
            position++;
        }

        if (position >= tokens.size()) {
            throwParseError("Unclosed character class", 
                          "Character class starting at position " + classStartPos + " is not closed");
        }

        while (position < tokens.size() && tokens.get(position).getType() != RegexToken.TokenType.RBRACKET) {
            RegexToken current = tokens.get(position);
            
            if (position + 2 < tokens.size() && 
                tokens.get(position + 1).getType() == RegexToken.TokenType.DASH) {
                // Handle range (e.g., a-z)
                char start = current.getValue();
                char end = tokens.get(position + 2).getValue();
                position += 3;
                
                if (start > end) {
                    throwParseError("Invalid character range", 
                                  "Range '" + start + "-" + end + "' is invalid (start > end)");
                }
                
                for (char c = start; c <= end; c++) {
                    characters.add(c);
                }
            } else {
                characters.add(current.getValue());
                position++;
            }
        }
        
        if (position >= tokens.size() || tokens.get(position).getType() != RegexToken.TokenType.RBRACKET) {
            throwParseError("Unclosed character class", 
                          "Character class starting at position " + classStartPos + " is not closed");
        }
        position++;
        
        if (characters.isEmpty()) {
            throwParseError("Empty character class", 
                          "Character class must contain at least one character");
        }
        
        return new CharacterClassNode(characters, negated);
    }
}