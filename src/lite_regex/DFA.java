package lite_regex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DFA {
    private final Map<DFAState, Map<Character, DFAState>> transitionTable;
    private final Set<DFAState> acceptingStates;
    private final DFAState startState;

    public DFA(Map<DFAState, Map<Character, DFAState>> transitionTable, 
               Set<DFAState> acceptingStates, 
               DFAState startState) {
        this.transitionTable = transitionTable;
        this.acceptingStates = acceptingStates;
        this.startState = startState;
    }

    public boolean matches(String input) {
        DFAState current = startState;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            Map<Character, DFAState> transitions = transitionTable.get(current);
            if (transitions == null || !transitions.containsKey(c)) {
                return false;
            }
            current = transitions.get(c);
        }
        return acceptingStates.contains(current);
    }
}