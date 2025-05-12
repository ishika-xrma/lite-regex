package lite_regex;
import java.util.*;
class CharacterClassNode extends RegexNode {
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
}
