import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class MarkdownParser {

    private interface BlockRule {
        boolean matches(String line);
        String apply(String line, ParserContext context);
    }

    private static class ParserContext {
        boolean inList = false;
        boolean inCode = false;
    }

    private static final List<BlockRule> RULES = List.of(
        (l) -> l.startsWith("```"), (l, ctx) -> {
            if (ctx.inCode) { ctx.inCode = false; return "</code></pre>"; }
            ctx.inCode = true; return "<pre><code>";
        },
        (l) -> l.startsWith("#"), (l, ctx) -> {
            int level = 0;
            while (level < l.length() && l.charAt(level) == '#') level++;
            String content = l.substring(level).trim();
            return String.format("<h%d>%s</h%d>", level, parseInline(content), level);
        },
        (l) -> l.startsWith("- ") || l.startsWith("* "), (l, ctx) -> {
            StringBuilder sb = new StringBuilder();
            if (!ctx.inList) { sb.append("<ul>\n"); ctx.inList = true; }
            sb.append("  <li>").append(parseInline(l.substring(2))).append("</li>");
            return sb.toString();
        },
        (l) -> l.startsWith("> "), (l, ctx) -> "<blockquote>" + parseInline(l.substring(2)) + "</blockquote>",
        (l) -> l.matches("-{3,}|\\*{3,}|_{3,}"), (l, ctx) -> "<hr>",
        (l) -> l.trim().isEmpty(), (l, ctx) -> ""
    );

    private static final Map<Pattern, String> INLINE_RULES = new LinkedHashMap<>() {{
        put(Pattern.compile("!\\[(.+?)\\]\\((.+?)\\)"), "<img src=\"$2\" alt=\"$1\">");
        put(Pattern.compile("\\[(.+?)\\]\\((.+?)\\)"), "<a href=\"$2\">$1</a>");
        put(Pattern.compile("(\\*\\*|__)(.+?)\\1"), "<strong>$2</strong>");
        put(Pattern.compile("(\\*|_)(.+?)\\1"), "<em>$2</em>");
        put(Pattern.compile("~~(.+?)~~"), "<del>$1</del>");
        put(Pattern.compile("`(.+?)`"), "<code>$1</code>");
    }};

    public static String parse(String markdown) {
        if (markdown == null) return "";
        
        ParserContext context = new ParserContext();
        StringBuilder html = new StringBuilder();
        String[] lines = markdown.split("\\R");

        for (String line : lines) {
            if (context.inCode && !line.startsWith("```")) {
                html.append(escapeHtml(line)).append("\n");
                continue;
            }

            if (context.inList && !line.startsWith("- ") && !line.startsWith("* ")) {
                html.append("</ul>\n");
                context.inList = false;
            }

            Optional<BlockRule> rule = RULES.stream().filter(r -> r.matches(line)).findFirst();
            
            if (rule.isPresent()) {
                String result = rule.get().apply(line, context);
                if (!result.isEmpty()) html.append(result).append("\n");
            } else {
                html.append("<p>").append(parseInline(line)).append("</p>\n");
            }
        }

        if (context.inList) html.append("</ul>\n");
        if (context.inCode) html.append("</code></pre>\n");

        return html.toString().trim();
    }

    private static String parseInline(String text) {
        for (Map.Entry<Pattern, String> entry : INLINE_RULES.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text;
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    public static void main(String[] args) {
        String md = """
            # Modern Parser
            This is a **robust** implementation.

            - Clean code
            - Pattern matching
            - Logic separation

            > Architecture matters.

            ```java
            public class Main {}
            ```
            """;

        System.out.println(parse(md));
    }
}
