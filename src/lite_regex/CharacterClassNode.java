package lite_regex;
import java.util.*;

public class CharacterClassNode extends RegexNode {
    private final Set<Character> characters;
    private final boolean negated;
    
    public CharacterClassNode(Set<Character> characters, boolean negated) {
        this.characters = characters;
        this.negated = negated;
    }
    
    public Set<Character> getCharacters() {
        return characters;
    }
    
    public boolean isNegated() {
        return negated;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CharClass[");
        if (negated) {
            sb.append("^");
        }
        for (char c : characters) {
            sb.append(c);
        }
        sb.append("]");
        return sb.toString();
    }
}