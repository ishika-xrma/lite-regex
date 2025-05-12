package lite_regex;

import java.util.*;

public class DFA {
    private final Map<Integer, Map<Character, Integer>> transitions;
    private final Set<Integer> acceptingStates;
    private final int startState;
    
    public DFA(Map<Integer, Map<Character, Integer>> transitions, 
               Set<Integer> acceptingStates, 
               int startState) {
        this.transitions = transitions;
        this.acceptingStates = acceptingStates;
        this.startState = startState;
    }
    
    public boolean matches(String text) {
        int currentState = startState;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Map<Character, Integer> stateTransitions = transitions.get(currentState);
            
            if (stateTransitions == null || !stateTransitions.containsKey(c)) {
                return false;
            }
            
            currentState = stateTransitions.get(c);
        }
        
        return acceptingStates.contains(currentState);
    }
}
