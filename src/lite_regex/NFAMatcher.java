package lite_regex;
import java.util.*;

public class NFAMatcher {
    private final NFA nfa;
    
    public NFAMatcher(NFA nfa) {
        this.nfa = nfa;
    }
    
    public boolean matches(String text) {
        Set<State> currentStates = computeEpsilonClosure(Collections.singleton(nfa.getStartState()));
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            currentStates = step(currentStates, c);
            
            if (currentStates.isEmpty()) {
                return false; // No valid transitions
            }
        }
        
        // Check if any current state is an accepting state
        return currentStates.stream().anyMatch(State::isAccepting);
    }
    
    private Set<State> step(Set<State> states, char symbol) {
        Set<State> nextStates = new HashSet<>();
        
        for (State state : states) {
            Set<State> transitions = state.getNextStates(symbol);
            nextStates.addAll(transitions);
        }
        
        return computeEpsilonClosure(nextStates);
    }
    
    
    private Set<State> computeEpsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);
        
        while (!stack.isEmpty()) {
            State state = stack.pop();
            
            for (State next : state.getEpsilonTransitions()) {
                if (closure.add(next)) {
                    stack.push(next);
                }
            }
        }
        
        return closure;
    }
}
