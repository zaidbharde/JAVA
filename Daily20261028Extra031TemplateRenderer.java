import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Renders ${name} placeholders, HTML-escaping values and rejecting missing keys. */
public final class Daily20261028Extra031TemplateRenderer {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Za-z_][A-Za-z0-9_.-]*)}");

    private Daily20261028Extra031TemplateRenderer() { }

    public static String render(String template, Map<String, ?> values) {
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(values, "values");
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder(template.length());
        int position = 0;
        while (matcher.find()) {
            String stray = template.substring(position, matcher.start());
            if (stray.contains("${")) throw new IllegalArgumentException("malformed placeholder");
            out.append(stray);
            String key = matcher.group(1);
            if (!values.containsKey(key)) throw new IllegalArgumentException("missing template key: " + key);
            Object value = Objects.requireNonNull(values.get(key), "null value for key: " + key);
            out.append(escapeHtml(value.toString()));
            position = matcher.end();
        }
        String tail = template.substring(position);
        if (tail.contains("${")) throw new IllegalArgumentException("malformed placeholder");
        return out.append(tail).toString();
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static void main(String[] args) {
        String rendered = render("<p>Hello ${user}: ${count}</p>",
                Map.of("user", "A&B <C>", "count", 3));
        if (!rendered.equals("<p>Hello A&amp;B &lt;C&gt;: 3</p>"))
            throw new AssertionError(rendered);
        try {
            render("${absent}", Map.of());
            throw new AssertionError("missing key was accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println(rendered);
    }
}
