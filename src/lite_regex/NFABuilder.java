package lite_regex;
public class NFABuilder {
    public NFA build(RegexNode node) {
        if (node instanceof CharacterNode) {
            CharacterNode charNode = (CharacterNode) node;
            State start = new State();
            State accept = new State();
            start.addTransition(charNode.getCharacter(), accept);
            return new NFA(start, accept);
        } else if (node instanceof AnyCharNode) {
            // For simplicity, we'll handle a subset of ASCII characters
            State start = new State();
            State accept = new State();
            for (char c = 0; c < 128; c++) {
                start.addTransition(c, accept);
            }
            return new NFA(start, accept);
        } else if (node instanceof ConcatenationNode) {
            ConcatenationNode catNode = (ConcatenationNode) node;
            NFA leftNFA = build(catNode.getLeft());
            NFA rightNFA = build(catNode.getRight());
            
            // Connect left accept state to right start state
            leftNFA.getAcceptState().addEpsilonTransition(rightNFA.getStartState());
            leftNFA.getAcceptState().setAccepting(false);
            
            return new NFA(leftNFA.getStartState(), rightNFA.getAcceptState());
        } else if (node instanceof AlternationNode) {
            AlternationNode altNode = (AlternationNode) node;
            NFA leftNFA = build(altNode.getLeft());
            NFA rightNFA = build(altNode.getRight());
            
            State start = new State();
            State accept = new State();
            
            // Connect start to both branches with epsilon transitions
            start.addEpsilonTransition(leftNFA.getStartState());
            start.addEpsilonTransition(rightNFA.getStartState());
            
            // Connect both branch ends to accept state
            leftNFA.getAcceptState().addEpsilonTransition(accept);
            rightNFA.getAcceptState().addEpsilonTransition(accept);
            
            leftNFA.getAcceptState().setAccepting(false);
            rightNFA.getAcceptState().setAccepting(false);
            
            return new NFA(start, accept);
        } else if (node instanceof RepetitionNode) {
            RepetitionNode repNode = (RepetitionNode) node;
            NFA childNFA = build(repNode.getChild());
            
            State start = new State();
            State accept = new State();
            
            start.addEpsilonTransition(childNFA.getStartState());
            childNFA.getAcceptState().addEpsilonTransition(accept);
            
            // Handle different repetition operators
            switch (repNode.getOperator()) {
                case '*': // zero or more
                    // Allow skipping the pattern
                    start.addEpsilonTransition(accept);
                    // Allow looping back
                    childNFA.getAcceptState().addEpsilonTransition(childNFA.getStartState());
                    break;
                case '+': // one or more
                    // Allow looping back, but not skipping
                    childNFA.getAcceptState().addEpsilonTransition(childNFA.getStartState());
                    break;
                case '?': // zero or one
                    // Allow skipping the pattern
                    start.addEpsilonTransition(accept);
                    break;
            }
            
            childNFA.getAcceptState().setAccepting(false);
            return new NFA(start, accept);
        }
        
        throw new IllegalArgumentException("Unsupported regex node type: " + node.getClass().getName());
    }
}
