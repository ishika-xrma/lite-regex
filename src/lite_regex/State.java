package lite_regex;
import java.util.*;

public class State {
    private static int nextId = 0;
    
    private final int id;
    private final Map<Character, Set<State>> transitions;
    private final Set<State> epsilonTransitions;
    private boolean isAccepting;
    
    public State() {
        this.id = nextId++;
        this.transitions = new HashMap<>();
        this.epsilonTransitions = new HashSet<>();
        this.isAccepting = false;
    }
    
    public int getId() {
        return id;
    }
    
    public void addTransition(char symbol, State target) {
        transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(target);
    }
    
    public void addEpsilonTransition(State target) {
        epsilonTransitions.add(target);
    }
    
    public Set<State> getNextStates(char symbol) {
        return transitions.getOrDefault(symbol, Collections.emptySet());
    }
    
    public Map<Character, Set<State>> getTransitions() {
        return Collections.unmodifiableMap(transitions);
    }
    
    public Set<State> getEpsilonTransitions() {
        return epsilonTransitions;
    }
    
    public void setAccepting(boolean accepting) {
        this.isAccepting = accepting;
    }
    
    public boolean isAccepting() {
        return isAccepting;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

