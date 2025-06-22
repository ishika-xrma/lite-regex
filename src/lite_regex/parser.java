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
            
            // Handle *, +, ? quantifiers
            if (token.getType() == RegexToken.TokenType.STAR ||
                token.getType() == RegexToken.TokenType.PLUS ||
                token.getType() == RegexToken.TokenType.QUESTION) {
                position++;
                return new RepetitionNode(base, token.getValue());
            }
            // Handle {n,m} quantifiers
            else if (token.getType() == RegexToken.TokenType.LBRACE) {
                return parseQuantifier(base);
            }
        }
        return base;
    }

    private RegexNode parseQuantifier(RegexNode child) {
        position++; // skip {
        
        int min = parseNumber();
        Integer max = null;
        
        if (position < tokens.size() && tokens.get(position).getType() == RegexToken.TokenType.COMMA) {
            position++; // skip ,
            if (position < tokens.size() && tokens.get(position).getType() != RegexToken.TokenType.RBRACE) {
                max = parseNumber();
            }
        }
        
        if (position >= tokens.size() || tokens.get(position).getType() != RegexToken.TokenType.RBRACE) {
            throwParseError("Unclosed quantifier", "Expected '}'");
        }
        position++; // skip }
        
        if (max != null && max < min) {
            throwParseError("Invalid quantifier range", "Max must be >= min");
        }
        
        return new QuantifierNode(child, min, max);
    }

    private int parseNumber() {
        if (position >= tokens.size()) {
            throwParseError("Expected number in quantifier", "Found end of pattern");
        }

        StringBuilder num = new StringBuilder();
        while (position < tokens.size() &&
               tokens.get(position).getType() == RegexToken.TokenType.NUMBER) {
            num.append(tokens.get(position).getValue());
            position++;
        }

        if (num.length() == 0) {
            throwParseError("Expected number in quantifier", "No digits found");
        }

        try {
            return Integer.parseInt(num.toString());
        } catch (NumberFormatException e) {
            throwParseError("Invalid number in quantifier", "Number too large");
            return 0; // Unreachable
        }
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
                // Check if this dot was escaped
                if (position > 1 && tokens.get(position-2).getType() == RegexToken.TokenType.ESCAPE) {
                    return new CharacterNode('.');
                }
                return new AnyCharNode();
            case LPAREN:
                RegexNode expr = parseExpression();
                if (position >= tokens.size() || tokens.get(position).getType() != RegexToken.TokenType.RPAREN) {
                    throwParseError("Missing closing parenthesis", 
                                  "No matching ')' for opening '(' at position " + (position-1));
                }
                position++;
                return expr;
            case LBRACKET:
                return parseCharacterClass();
            case ESCAPE:
                // Handle the escaped character (next token)
                if (position >= tokens.size()) {
                    throwParseError("Invalid escape sequence", 
                                  "Escape character '\\' at end of pattern");
                }
                RegexToken nextToken = tokens.get(position);
                position++;
                return new CharacterNode(nextToken.getValue());
            default:
                throwParseError("Unexpected token: " + token.getType(), 
                              "Token '" + token.getValue() + "' is not valid in this position");
        }
        // This line should never be reached because the default case throws an exception
        throw new AssertionError("Unreachable code");
    }
    private RegexNode parseCharacterClass() {
        boolean negated = false;
        Set<Character> characters = new HashSet<>();
        int classStartPos = position - 1; // Position of the '['

        // Check for negation
        if (position < tokens.size() && tokens.get(position).getType() == RegexToken.TokenType.CARET) {
            negated = true;
            position++;
        }

        while (position < tokens.size() && tokens.get(position).getType() != RegexToken.TokenType.RBRACKET) {
            RegexToken current = tokens.get(position);
            
            // Handle character ranges (e.g., a-z)
            if (position + 2 < tokens.size() && 
                tokens.get(position + 1).getType() == RegexToken.TokenType.DASH) {
                char start = current.getValue();
                char end = tokens.get(position + 2).getValue();
                
                if (start > end) {
                    throwParseError("Invalid character range", 
                                  "Range '" + start + "-" + end + "' is invalid (start > end)");
                }
                
                for (char c = start; c <= end; c++) {
                    characters.add(c);
                }
                position += 3;
            } else {
                // Single character
                characters.add(current.getValue());
                position++;
            }
        }
        
        if (position >= tokens.size() || tokens.get(position).getType() != RegexToken.TokenType.RBRACKET) {
            throwParseError("Unclosed character class", 
                          "Character class starting at position " + classStartPos + " is not closed");
        }
        position++;
        
        return new CharacterClassNode(characters, negated);
    }
}