package lite_regex;

import java.util.Set;

public class DFAState {
    private final Set<State> nfaStates;
    private final int id;
    private static int nextId = 0;

    public DFAState(Set<State> nfaStates) {
        this.nfaStates = nfaStates;
        this.id = nextId++;
    }

    public Set<State> getNfaStates() {
        return nfaStates;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DFAState)) return false;
        DFAState other = (DFAState) obj;
        return nfaStates.equals(other.nfaStates);
    }

    @Override
    public int hashCode() {
        return nfaStates.hashCode();
    }

    @Override
    public String toString() {
        return "DFAState-" + id + "(" + nfaStates + ")";
    }
}