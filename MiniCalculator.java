import java.util.Scanner;
import java.util.Stack;

/**
 * Mini Calculator — evaluates full math expressions like "3 + 4 * (2 - 1)"
 * using the Shunting Yard algorithm (respects operator precedence).
 */
public class MiniCalculator {

    // ── Shunting Yard ─────────────────────────────────────────────────
    public static double evaluate(String expression) {
        Stack<Double>     values  = new Stack<>();
        Stack<Character>  ops     = new Stack<>();
        char[]            tokens  = expression.replaceAll("\\s+", "").toCharArray();

        for (int i = 0; i < tokens.length; i++) {
            char c = tokens[i];

            // Number (handle multi-digit and decimals)
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length &&
                       (Character.isDigit(tokens[i]) || tokens[i] == '.')) {
                    sb.append(tokens[i++]);
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));

            } else if (c == '(') {
                ops.push(c);

            } else if (c == ')') {
                while (ops.peek() != '(') values.push(applyOp(ops.pop(), values));
                ops.pop();   // discard '('

            } else if (isOperator(c)) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                    values.push(applyOp(ops.pop(), values));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) values.push(applyOp(ops.pop(), values));
        return values.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char op) {
        return (op == '*' || op == '/') ? 2 : (op == '+' || op == '-') ? 1 : 0;
    }

    private static double applyOp(char op, Stack<Double> values) {
        double b = values.pop(), a = values.pop();
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Division by zero.");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=".repeat(42));
        System.out.println("  Mini Calculator (type 'quit' to exit)");
        System.out.println("  Supports: + - * /  and parentheses");
        System.out.println("=".repeat(42));

        String[] demos = {"3 + 4", "10 - 3 * 2", "(10 - 3) * 2", "100 / 4 + 5 * 3"};
        System.out.println("\n  Quick demos:");
        for (String expr : demos) {
            System.out.printf("  %-25s = %.4f%n", expr, evaluate(expr));
        }

        System.out.println("\n  Interactive mode:");
        while (true) {
            System.out.print("  > ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quit")) break;
            try {
                System.out.printf("  = %.4f%n", evaluate(input));
            } catch (Exception e) {
                System.out.println("  Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
