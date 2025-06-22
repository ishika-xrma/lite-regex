package lite_regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DFABuilder {
    public DFA build(NFA nfa) {
        Map<DFAState, Map<Character, DFAState>> transitionTable = new HashMap<>();
        Set<DFAState> acceptingStates = new HashSet<>();
        
        // Compute initial state (epsilon closure of NFA start state)
        Set<State> initialEpsilonClosure = computeEpsilonClosure(nfa.getStartState());
        DFAState startState = new DFAState(initialEpsilonClosure);
        
        Queue<DFAState> unprocessedStates = new LinkedList<>();
        unprocessedStates.add(startState);
        
        List<DFAState> allStates = new ArrayList<>();
        allStates.add(startState);
        
        // Check if initial state is accepting
        if (containsAcceptingState(initialEpsilonClosure)) {
            acceptingStates.add(startState);
        }
        
        while (!unprocessedStates.isEmpty()) {
            DFAState current = unprocessedStates.poll();
            
            // Get all unique input characters from NFA transitions
            Set<Character> alphabet = getAlphabet(current.getNfaStates());
            
            Map<Character, DFAState> transitions = new HashMap<>();
            
            for (char c : alphabet) {
                // Compute move and epsilon closure
                Set<State> moveResult = move(current.getNfaStates(), c);
                Set<State> newStateNfa = computeEpsilonClosure(moveResult);
                
                if (newStateNfa.isEmpty()) continue;
                
                DFAState newState = findOrCreateState(newStateNfa, allStates);
                
                if (newState == null) {
                    newState = new DFAState(newStateNfa);
                    allStates.add(newState);
                    unprocessedStates.add(newState);
                    
                    // Check if new state is accepting
                    if (containsAcceptingState(newStateNfa)) {
                        acceptingStates.add(newState);
                    }
                }
                
                transitions.put(c, newState);
            }
            
            transitionTable.put(current, transitions);
        }
        
        return new DFA(transitionTable, acceptingStates, startState);
    }
    
    private Set<Character> getAlphabet(Set<State> states) {
        Set<Character> alphabet = new HashSet<>();
        for (State state : states) {
            alphabet.addAll(state.getTransitions().keySet());
        }
        return alphabet;
    }
    
    private Set<State> move(Set<State> states, char c) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            Set<State> transitions = state.getNextStates(c);
            if (transitions != null) {
                result.addAll(transitions);
            }
        }
        return result;
    }
    
    private Set<State> computeEpsilonClosure(State state) {
        return computeEpsilonClosure(Set.of(state));
    }
    
    private Set<State> computeEpsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Queue<State> queue = new LinkedList<>(states);
        
        while (!queue.isEmpty()) {
            State current = queue.poll();
            for (State epsilonState : current.getEpsilonTransitions()) {
                if (closure.add(epsilonState)) {
                    queue.add(epsilonState);
                }
            }
        }
        
        return closure;
    }
    
    private boolean containsAcceptingState(Set<State> states) {
        for (State state : states) {
            if (state.isAccepting()) {
                return true;
            }
        }
        return false;
    }
    
    private DFAState findOrCreateState(Set<State> nfaStates, List<DFAState> existingStates) {
        for (DFAState state : existingStates) {
            if (state.getNfaStates().equals(nfaStates)) {
                return state;
            }
        }
        return null;
    }
}