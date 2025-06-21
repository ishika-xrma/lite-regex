package lite_regex;

public class RegexException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int position;
    private final String pattern;
    private final String details;

    public RegexException(String message, int position, String pattern, String details) {
        super(message);
        this.position = position;
        this.pattern = pattern;
        this.details = details;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage()).append("\n");
        sb.append("At position ").append(position).append(" in pattern:\n");
        sb.append(pattern).append("\n");
        sb.append(getPositionMarker()).append("\n");
        if (details != null) {
            sb.append("Details: ").append(details);
        }
        return sb.toString();
    }

    private String getPositionMarker() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < position; i++) {
            sb.append(" ");
        }
        sb.append("^");
        return sb.toString();
    }
}