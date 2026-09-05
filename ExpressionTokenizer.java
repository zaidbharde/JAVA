import java.util.ArrayList;
import java.util.List;

/** Converts a compact arithmetic expression into tokens without evaluating it. */
public final class ExpressionTokenizer {
    public enum Kind { NUMBER, IDENTIFIER, OPERATOR, LEFT_PAREN, RIGHT_PAREN }
    public record Token(Kind kind, String text, int position) {}

    public static List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        while (index < expression.length()) {
            char current = expression.charAt(index);
            if (Character.isWhitespace(current)) {
                index++;
            } else if (Character.isDigit(current) || current == '.') {
                int start = index++;
                while (index < expression.length()
                        && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
                    index++;
                }
                tokens.add(new Token(Kind.NUMBER, expression.substring(start, index), start));
            } else if (Character.isLetter(current) || current == '_') {
                int start = index++;
                while (index < expression.length()
                        && (Character.isLetterOrDigit(expression.charAt(index)) || expression.charAt(index) == '_')) {
                    index++;
                }
                tokens.add(new Token(Kind.IDENTIFIER, expression.substring(start, index), start));
            } else if ("+-*/%^".indexOf(current) >= 0) {
                tokens.add(new Token(Kind.OPERATOR, String.valueOf(current), index++));
            } else if (current == '(') {
                tokens.add(new Token(Kind.LEFT_PAREN, "(", index++));
            } else if (current == ')') {
                tokens.add(new Token(Kind.RIGHT_PAREN, ")", index++));
            } else {
                throw new IllegalArgumentException("Unexpected character at " + index + ": " + current);
            }
        }
        return tokens;
    }

    public static void main(String[] args) {
        tokenize("total * (tax + 2.5)").forEach(System.out::println);
    }
}
