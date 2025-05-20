package lite_regex;

public class RepetitionNode extends RegexNode {
    private final RegexNode child;
    private final char operator;

    public RepetitionNode(RegexNode child, char operator) {
        this.child = child;
        this.operator = operator;
    }

    public RegexNode getChild() { return child; }
    public char getOperator() { return operator; }
}
