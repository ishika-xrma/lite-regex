package lite_regex;

public class CharacterNode extends RegexNode {
    private final char character;

    public CharacterNode(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
    @Override
    public String toString() {
        return "Literal('" + character + "')";
    }
}
