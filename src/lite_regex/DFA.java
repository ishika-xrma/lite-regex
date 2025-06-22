package lite_regex;

import java.util.Map;
import java.util.Set;

public class DFA {
    private final DFAState startState;
    private final Set<DFAState> acceptingStates;
    private final Map<DFAState, Map<Character, DFAState>> transitionTable;
    private final int minLength;
    private final Integer maxLength;

    public DFA(DFAState startState, 
              Set<DFAState> acceptingStates,
              Map<DFAState, Map<Character, DFAState>> transitionTable,
              int minLength, Integer maxLength) {
        this.startState = startState;
        this.acceptingStates = acceptingStates;
        this.transitionTable = transitionTable;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public boolean matches(String input) {
        DFAState current = startState;

        for (char c : input.toCharArray()) {
            Map<Character, DFAState> transitions = transitionTable.get(current);
            if (transitions == null || !transitions.containsKey(c)) {
                return false;  // ❌ dead end
            }
            current = transitions.get(c);
        }

        return acceptingStates.contains(current);  // ✅ Only final state must be accepting
    }


    public int getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }
}