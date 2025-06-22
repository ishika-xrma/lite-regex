package lite_regex;

import java.util.*;

public class DFABuilder {
    private final Map<Set<State>, DFAState> stateCache = new HashMap<>();

    public DFA build(NFA nfa) {
        stateCache.clear();

        Map<DFAState, Map<Character, DFAState>> transitionTable = new HashMap<>();
        Set<DFAState> acceptingStates = new HashSet<>();
        Queue<DFAState> unprocessedStates = new ArrayDeque<>();

        // ✅ FIX: use the new overload for single-state epsilon closure
        Set<State> initialClosure = epsilonClosure(nfa.getStartState());
        DFAState startState = getCachedState(initialClosure);
        unprocessedStates.add(startState);

        if (isAccepting(initialClosure)) {
            acceptingStates.add(startState);
        }

        while (!unprocessedStates.isEmpty()) {
            DFAState current = unprocessedStates.poll();
            Map<Character, DFAState> transitions = transitionTable.computeIfAbsent(current, k -> new HashMap<>());

            for (char c : getAlphabet(current.getNfaStates())) {
                Set<State> moveResult = move(current.getNfaStates(), c);
                if (moveResult.isEmpty()) continue;

                Set<State> closure = epsilonClosure(moveResult);
                DFAState target = getCachedState(closure);

                transitions.put(c, target);

                if (!transitionTable.containsKey(target)) {
                    unprocessedStates.add(target);
                    if (isAccepting(closure)) acceptingStates.add(target);
                }
            }
        }

        return new DFA(startState, acceptingStates, transitionTable,
                nfa.getMinLength(), nfa.getMaxLength());
    }

    private DFAState getCachedState(Set<State> nfaStates) {
        Set<State> key = Collections.unmodifiableSet(new HashSet<>(nfaStates));
        return stateCache.computeIfAbsent(key, DFAState::new);
    }

    private Set<Character> getAlphabet(Set<State> states) {
        Set<Character> alphabet = new HashSet<>();
        for (State state : states) {
            for (Character c : state.getTransitions().keySet()) {
                if (c != null) {
                    alphabet.add(c);
                }
            }
        }
        return alphabet;
    }

    private Set<State> move(Set<State> states, char c) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            result.addAll(state.getNextStates(c));
        }
        return result;
    }

    // ✅ Add this overload to fix your compilation issue
    private Set<State> epsilonClosure(State state) {
        return epsilonClosure(Collections.singleton(state));
    }

    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>();
        Deque<State> stack = new ArrayDeque<>(states);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            if (closure.add(state)) {
                stack.addAll(state.getEpsilonTransitions());
            }
        }

        return closure;
    }

    private boolean isAccepting(Set<State> states) {
        for (State state : states) {
            if (state.isAccepting()) return true;
        }
        return false;
    }
}
