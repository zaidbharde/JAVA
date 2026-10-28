import java.util.ArrayDeque;
import java.util.Deque;
/** Checks whether (), [], and {} are correctly balanced in text. */
public final class Daily20261028BracketCheck {
    private Daily20261028BracketCheck() { }
    public static boolean isBalanced(String text) {
        if (text == null) return false;
        Deque<Character> stack = new ArrayDeque<Character>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(' || c == '[' || c == '{') stack.push(c);
            else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                char open = stack.pop();
                if ((c == ')' && open != '(') || (c == ']' && open != '[')
                        || (c == '}' && open != '{')) return false;
            }
        }
        return stack.isEmpty();
    }
    public static void main(String[] args) { System.out.println(isBalanced("a{b[c(d)]}")); }
}
