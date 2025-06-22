package lite_regex;

public class QuantifierNode extends RegexNode {
    private final RegexNode child;
    private final int min;
    private final Integer max; // null means unlimited
    
    public QuantifierNode(RegexNode child, int min, Integer max) {
        this.child = child;
        this.min = min;
        this.max = max;
        if (max != null && max < min) {
            throw new IllegalArgumentException("Max must be >= min");
        }
    }
    
    public boolean isExact() {
        return max != null && max == min;
    }
    
    public RegexNode getChild() { return child; }
    public int getMin() { return min; }
    public Integer getMax() { return max; }
    
    @Override
    public String toString() {
        return "Quantifier(" + child + ", " + min + (max != null ? "," + max : "") + ")";
    }
}