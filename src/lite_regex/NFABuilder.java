package lite_regex;

public class NFABuilder {
    public NFA build(RegexNode node) {
        try {
            if (node instanceof CharacterNode) {
                return buildCharacterNode((CharacterNode) node);
            } else if (node instanceof AnyCharNode) {
                return buildAnyCharNode();
            } else if (node instanceof WordCharNode) {
                return buildWordCharNode();
            } else if (node instanceof DigitNode) {
                return buildDigitNode();
            } else if (node instanceof ConcatenationNode) {
                return buildConcatenationNode((ConcatenationNode) node);
            } else if (node instanceof AlternationNode) {
                return buildAlternationNode((AlternationNode) node);
            } else if (node instanceof RepetitionNode) {
                return buildRepetitionNode((RepetitionNode) node);
            } else if (node instanceof CharacterClassNode) {
                return buildCharacterClassNode((CharacterClassNode) node);
            } else {
                throw new IllegalArgumentException("Unsupported regex node type: " + 
                    (node != null ? node.getClass().getName() : "null"));
            }
        } catch (Exception e) {
            throw new RegexException("Failed to build NFA", 
                                   0, "", 
                                   "Error processing node: " + e.getMessage());
        }
    }

    private NFA buildCharacterNode(CharacterNode node) {
        State start = new State();
        State accept = new State();
        start.addTransition(node.getCharacter(), accept);
        return new NFA(start, accept);
    }

    private NFA buildAnyCharNode() {
        State start = new State();
        State accept = new State();
        for (char c = 0; c < 128; c++) {
            start.addTransition(c, accept);
        }
        return new NFA(start, accept);
    }

    private NFA buildWordCharNode() {
        State start = new State();
        State accept = new State();
        // \w matches [a-zA-Z0-9_]
        for (char c = 'a'; c <= 'z'; c++) start.addTransition(c, accept);
        for (char c = 'A'; c <= 'Z'; c++) start.addTransition(c, accept);
        for (char c = '0'; c <= '9'; c++) start.addTransition(c, accept);
        start.addTransition('_', accept);
        return new NFA(start, accept);
    }

    private NFA buildDigitNode() {
        State start = new State();
        State accept = new State();
        for (char c = '0'; c <= '9'; c++) {
            start.addTransition(c, accept);
        }
        return new NFA(start, accept);
    }

    private NFA buildConcatenationNode(ConcatenationNode node) {
        NFA leftNFA = build(node.getLeft());
        NFA rightNFA = build(node.getRight());
        leftNFA.getAcceptState().addEpsilonTransition(rightNFA.getStartState());
        leftNFA.getAcceptState().setAccepting(false);
        return new NFA(leftNFA.getStartState(), rightNFA.getAcceptState());
    }

    private NFA buildAlternationNode(AlternationNode node) {
        NFA leftNFA = build(node.getLeft());
        NFA rightNFA = build(node.getRight());
        State start = new State();
        State accept = new State();
        start.addEpsilonTransition(leftNFA.getStartState());
        start.addEpsilonTransition(rightNFA.getStartState());
        leftNFA.getAcceptState().addEpsilonTransition(accept);
        rightNFA.getAcceptState().addEpsilonTransition(accept);
        leftNFA.getAcceptState().setAccepting(false);
        rightNFA.getAcceptState().setAccepting(false);
        return new NFA(start, accept);
    }

    private NFA buildRepetitionNode(RepetitionNode node) {
        NFA childNFA = build(node.getChild());
        State start = new State();
        State accept = new State();
        start.addEpsilonTransition(childNFA.getStartState());
        childNFA.getAcceptState().addEpsilonTransition(accept);
        childNFA.getAcceptState().setAccepting(false);

        switch (node.getOperator()) {
            case '*':
                start.addEpsilonTransition(accept);
                childNFA.getAcceptState().addEpsilonTransition(childNFA.getStartState());
                break;
            case '+':
                childNFA.getAcceptState().addEpsilonTransition(childNFA.getStartState());
                break;
            case '?':
                start.addEpsilonTransition(accept);
                break;
        }
        
        return new NFA(start, accept);
    }

    private NFA buildCharacterClassNode(CharacterClassNode node) {
        State start = new State();
        State accept = new State();
        if (node.isNegated()) {
            for (char c = 0; c < 128; c++) {
                if (!node.getCharacters().contains(c)) {
                    start.addTransition(c, accept);
                }
            }
        } else {
            for (char c : node.getCharacters()) {
                start.addTransition(c, accept);
            }
        }
        return new NFA(start, accept);
    }
}