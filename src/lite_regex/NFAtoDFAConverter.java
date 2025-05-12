package lite_regex;

import java.util.*;

public class NFAtoDFAConverter {
    public DFA convert(NFA nfa) {
        // Map NFA state sets to DFA state IDs
        Map<Set<State>, Integer> dfaStates = new HashMap<>();
        // DFA transitions
        Map<Integer, Map<Character, Integer>> transitions = new HashMap<>();
        // DFA accepting states
        Set<Integer> acceptingStates = new HashSet<>();
        
        // Start with epsilon closure of NFA start state
        Set<State> startStateSet = computeEpsilonClosure(Collections.singleton(nfa.getStartState()));
        int nextStateId = 0;
        
        // Assign ID to start state
        dfaStates.put(startStateSet, nextStateId++);
        
        // List of state sets to process
        Queue<Set<State>> worklist = new LinkedList<>();
        worklist.add(startStateSet);
        
        // Check if start state is accepting
        if (containsAccepting(startStateSet)) {
            acceptingStates.add(dfaStates.get(startStateSet));
        }
        
        // Process all reachable state sets
        while (!worklist.isEmpty()) {
            Set<State> currentStateSet = worklist.poll();
            int currentStateId = dfaStates.get(currentStateSet);
            Map<Character, Integer> stateTransitions = new HashMap<>();
            
            // Consider all possible input symbols (for simplicity, ASCII range)
            for (char c = 0; c < 128; c++) {
                Set<State> nextStateSet = step(currentStateSet, c);
                
                if (nextStateSet.isEmpty()) {
                    continue;
                }
                
                // Create a new DFA state if needed
                if (!dfaStates.containsKey(nextStateSet)) {
                    dfaStates.put(nextStateSet, nextStateId++);
                    worklist.add(nextStateSet);
                    
                    if (containsAccepting(nextStateSet)) {
                        acceptingStates.add(dfaStates.get(nextStateSet));
                    }
                }
                
                stateTransitions.put(c, dfaStates.get(nextStateSet));
            }
            
            transitions.put(currentStateId, stateTransitions);
        }
        
        return new DFA(transitions, acceptingStates, dfaStates.get(startStateSet));
    }
    
    private Set<State> step(Set<State> states, char symbol) {
        Set<State> nextStates = new HashSet<>();
        
        for (State state : states) {
            nextStates.addAll(state.getNextStates(symbol));
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
    
    private boolean containsAccepting(Set<State> states) {
        return states.stream().anyMatch(State::isAccepting);
    }
}
