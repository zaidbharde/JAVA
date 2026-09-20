import java.util.ArrayList;
import java.util.List;

/** Greedy text wrapper that preserves words and reports line widths. */
public final class TextWrapPlanner {
    private TextWrapPlanner() {}

    public static List<String> wrap(String text, int width) {
        if (width < 1) throw new IllegalArgumentException("width must be positive");
        String[] words = text.trim().isEmpty() ? new String[0] : text.trim().split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (word.length() > width) {
                if (line.length() > 0) { lines.add(line.toString()); line.setLength(0); }
                for (int start = 0; start < word.length(); start += width) {
                    lines.add(word.substring(start, Math.min(start + width, word.length())));
                }
                continue;
            }
            if (line.length() == 0) line.append(word);
            else if (line.length() + 1 + word.length() <= width) line.append(' ').append(word);
            else { lines.add(line.toString()); line.setLength(0); line.append(word); }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    public static String alignCenter(String line, int width) {
        if (line.length() > width) throw new IllegalArgumentException("line exceeds width");
        int padding = width - line.length();
        return " ".repeat(padding / 2) + line + " ".repeat(padding - padding / 2);
    }
}

final class TextWrapPlannerExample {
    public static void main(String[] args) {
        for (String line : TextWrapPlanner.wrap("A compact report needs readable lines", 14)) {
            System.out.println(TextWrapPlanner.alignCenter(line, 14));
        }
    }
}
