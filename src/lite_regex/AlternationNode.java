package lite_regex;

public class AlternationNode extends RegexNode {
    private final RegexNode left;
    private final RegexNode right;

    public AlternationNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }

    public RegexNode getLeft() { return left; }
    public RegexNode getRight() { return right; }
    @Override
    public String toString() {
        return "Alt(" + left + " | " + right + ")";
    }
}
