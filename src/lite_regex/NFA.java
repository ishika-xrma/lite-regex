package lite_regex;
public class NFA {
    private final State startState;
    private final State acceptState;
    private final int minLength;
    private final Integer maxLength;
    
    public NFA(State startState, State acceptState, int minLength, Integer maxLength) {
        this.startState = startState;
        this.acceptState = acceptState;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.acceptState.setAccepting(true);
    }
    public State getStartState() {
        return startState;
    }
    
    public State getAcceptState() {
        return acceptState;
    }
    
    public int getMinLength() {
        return minLength;
    }
    
    public Integer getMaxLength() {
        return maxLength;
    }
}
