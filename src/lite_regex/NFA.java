package lite_regex;
public class NFA {
    private final State startState;
    private final State acceptState;
    
    public NFA(State startState, State acceptState) {
        this.startState = startState;
        this.acceptState = acceptState;
        this.acceptState.setAccepting(true);
    }
    
    public State getStartState() {
        return startState;
    }
    
    public State getAcceptState() {
        return acceptState;
    }
}
