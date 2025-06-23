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
            } else if (node instanceof QuantifierNode) {
                return buildQuantifierNode((QuantifierNode) node);
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

    private NFA buildQuantifierNode(QuantifierNode node) {
        if (node.getMax() != null && node.getMin() == node.getMax()) {
            return buildExactCount(node);
        }
        return buildRangeQuantifier(node);
    }

    private NFA buildExactCount(QuantifierNode node) {
        State start = new State();
        State current = start;

        for (int i = 0; i < node.getMin(); i++) {
            NFA next = build(node.getChild());

            // Remove old accepting state
            next.getAcceptState().setAccepting(false);

            current.addEpsilonTransition(next.getStartState());
            current = next.getAcceptState();
        }

        State accept = new State();
        accept.setAccepting(true);  // <== ONLY ONE accepting state
        current.addEpsilonTransition(accept);

        return new NFA(start, accept, node.getMin(), node.getMin());
    }



    private NFA buildRangeQuantifier(QuantifierNode node) {
        State start = new State();
        State current = start;

        // Handle minimum repetitions (always required)
        for (int i = 0; i < node.getMin(); i++) {
            NFA next = build(node.getChild());
            next.getAcceptState().setAccepting(false);
            current.addEpsilonTransition(next.getStartState());
            current = next.getAcceptState();
        }

        State accept = new State();
        accept.setAccepting(true);

        if (node.getMax() != null) {
            // Finite case {n,m}
            State optionalStart = current;
            for (int i = node.getMin(); i < node.getMax(); i++) {
                NFA optional = build(node.getChild());
                optional.getAcceptState().setAccepting(false);
                optionalStart.addEpsilonTransition(optional.getStartState());
                optionalStart.addEpsilonTransition(accept); // Early exit path
                optionalStart = optional.getAcceptState();
            }
            optionalStart.addEpsilonTransition(accept);
        } else {
            // Unlimited case {n,}
            NFA loop = build(node.getChild());
            loop.getAcceptState().setAccepting(false);
            
            // Connect current state to both:
            // 1. The accept state (to allow matching just the minimum)
            current.addEpsilonTransition(accept);
            
            // 2. The loop start (to allow additional matches)
            current.addEpsilonTransition(loop.getStartState());
            
            // Connect the loop end back to itself to allow repeating
            loop.getAcceptState().addEpsilonTransition(loop.getStartState());
            
            // Connect the loop end to the accept state
            loop.getAcceptState().addEpsilonTransition(accept);
        }

        return new NFA(start, accept, node.getMin(), node.getMax());
    }

    private NFA buildCharacterNode(CharacterNode node) {
        State start = new State();
        State accept = new State();
        start.addTransition(node.getCharacter(), accept);
        return new NFA(start, accept, 1, 1);
    }

    private NFA buildAnyCharNode() {
        State start = new State();
        State accept = new State();
        for (char c = 0; c < 128; c++) {
            start.addTransition(c, accept);
        }
        return new NFA(start, accept, 1, 1);
    }

    private NFA buildWordCharNode() {
        State start = new State();
        State accept = new State();
        // \w matches [a-zA-Z0-9_]
        for (char c = 'a'; c <= 'z'; c++) start.addTransition(c, accept);
        for (char c = 'A'; c <= 'Z'; c++) start.addTransition(c, accept);
        for (char c = '0'; c <= '9'; c++) start.addTransition(c, accept);
        start.addTransition('_', accept);
        return new NFA(start, accept, 1, 1);
    }

    private NFA buildDigitNode() {
        State start = new State();
        State accept = new State();
        for (char c = '0'; c <= '9'; c++) {
            start.addTransition(c, accept);
        }
        return new NFA(start, accept, 1, 1);
    }

    private NFA buildConcatenationNode(ConcatenationNode node) {
        NFA leftNFA = build(node.getLeft());
        NFA rightNFA = build(node.getRight());
        leftNFA.getAcceptState().addEpsilonTransition(rightNFA.getStartState());
        leftNFA.getAcceptState().setAccepting(false);
        
        // Calculate combined length constraints
        int min = leftNFA.getMinLength() + rightNFA.getMinLength();
        Integer max = null;
        if (leftNFA.getMaxLength() != null && rightNFA.getMaxLength() != null) {
            max = leftNFA.getMaxLength() + rightNFA.getMaxLength();
        }
        return new NFA(leftNFA.getStartState(), rightNFA.getAcceptState(), min, max);
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
        
        // Calculate length constraints for alternation
        int min = Math.min(leftNFA.getMinLength(), rightNFA.getMinLength());
        Integer max = null;
        if (leftNFA.getMaxLength() != null && rightNFA.getMaxLength() != null) {
            max = Math.max(leftNFA.getMaxLength(), rightNFA.getMaxLength());
        }
        return new NFA(start, accept, min, max);
    }

    private NFA buildRepetitionNode(RepetitionNode node) {
        NFA childNFA = build(node.getChild());
        State start = new State();
        State accept = new State();
        start.addEpsilonTransition(childNFA.getStartState());
        childNFA.getAcceptState().addEpsilonTransition(accept);
        childNFA.getAcceptState().setAccepting(false);

        int min = 0;
        Integer max = null;
        
        switch (node.getOperator()) {
            case '*':
                start.addEpsilonTransition(accept);
                childNFA.getAcceptState().addEpsilonTransition(childNFA.getStartState());
                min = 0;
                max = null;
                break;
            case '+':
                childNFA.getAcceptState().addEpsilonTransition(childNFA.getStartState());
                min = childNFA.getMinLength();
                max = null;
                break;
            case '?':
                start.addEpsilonTransition(accept);
                min = 0;
                max = childNFA.getMaxLength();
                break;
        }
        
        return new NFA(start, accept, min, max);
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
        return new NFA(start, accept, 1, 1);
    }
}