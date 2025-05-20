package lite_regex;

import java.util.List;

public class parser {
    private final List<RegexToken> tokens;
    private int position;

    public parser(List<RegexToken> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public RegexNode parse() {
        RegexNode expr = parseExpression();

        if (position < tokens.size()) {
            throw new IllegalArgumentException("Unexpected token at position " + position);
        }

        return expr;
    }

    private RegexNode parseExpression() {
        RegexNode term = parseTerm();

        while (position < tokens.size() && tokens.get(position).getType() == RegexToken.TokenType.ALTERNATION) {
            position++; // Consume '|'
            RegexNode right = parseTerm();
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
            throw new IllegalArgumentException("Unexpected end of regex pattern");
        }

        RegexToken token = tokens.get(position++);

        switch (token.getType()) {
            case CHARACTER:
                return new CharacterNode(token.getValue());
            case DOT:
                return new AnyCharNode();
            case LPAREN:
                RegexNode expr = parseExpression();
                if (position >= tokens.size() || tokens.get(position).getType() != RegexToken.TokenType.RPAREN) {
                    throw new IllegalArgumentException("Missing closing parenthesis");
                }
                position++;
                return expr;
            case ESCAPE:
                return new CharacterNode(token.getValue());
            default:
                throw new IllegalArgumentException("Unexpected token: " + token);
        }
    }
}
