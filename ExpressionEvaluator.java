public class ExpressionEvaluator {
    private final String expression;
    private int position;

    private ExpressionEvaluator(String expression) {
        this.expression = expression.replaceAll("\\s+", "");
    }

    public static int evaluate(String expression) {
        ExpressionEvaluator parser = new ExpressionEvaluator(expression);
        int value = parser.parseExpression();
        if (parser.position != parser.expression.length()) {
            throw new IllegalArgumentException("Unexpected input at position " + parser.position);
        }
        return value;
    }

    private int parseExpression() {
        int value = parseTerm();
        while (position < expression.length() && (peek('+') || peek('-'))) {
            char operator = expression.charAt(position++);
            int right = parseTerm();
            value = operator == '+' ? value + right : value - right;
        }
        return value;
    }

    private int parseTerm() {
        int value = parseFactor();
        while (position < expression.length() && (peek('*') || peek('/'))) {
            char operator = expression.charAt(position++);
            int right = parseFactor();
            if (operator == '/' && right == 0) throw new ArithmeticException("division by zero");
            value = operator == '*' ? value * right : value / right;
        }
        return value;
    }

    private int parseFactor() {
        if (peek('+') || peek('-')) {
            boolean negative = expression.charAt(position++) == '-';
            int value = parseFactor();
            return negative ? -value : value;
        }
        if (peek('(')) {
            position++;
            int value = parseExpression();
            if (!peek(')')) throw new IllegalArgumentException("missing closing parenthesis");
            position++;
            return value;
        }
        int start = position;
        while (position < expression.length() && Character.isDigit(expression.charAt(position))) position++;
        if (start == position) throw new IllegalArgumentException("number expected at position " + position);
        return Integer.parseInt(expression.substring(start, position));
    }

    private boolean peek(char expected) {
        return position < expression.length() && expression.charAt(position) == expected;
    }

    public static void main(String[] args) {
        System.out.println(evaluate("-2 + 3 * (4 + 5)"));
    }
}
