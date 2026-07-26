import java.util.*;
import java.util.regex.*;

public class MarkdownParser {

    public static String parse(String markdown) {
        String[] lines   = markdown.split("\n");
        StringBuilder html = new StringBuilder();
        boolean inList   = false;
        boolean inCode   = false;

        for (String line : lines) {
            if (line.startsWith("```")) {
                if (inCode) { html.append("</code></pre>\n"); inCode = false; }
                else        { html.append("<pre><code>\n");   inCode = true; }
                continue;
            }
            if (inCode) { html.append(escapeHtml(line)).append("\n"); continue; }

            if (line.startsWith("- ") || line.startsWith("* ")) {
                if (!inList) { html.append("<ul>\n"); inList = true; }
                html.append("  <li>").append(parseInline(line.substring(2))).append("</li>\n");
                continue;
            } else if (inList) {
                html.append("</ul>\n");
                inList = false;
            }

            if (line.startsWith("######"))     html.append("<h6>").append(parseInline(line.substring(7).trim())).append("</h6>\n");
            else if (line.startsWith("#####"))  html.append("<h5>").append(parseInline(line.substring(6).trim())).append("</h5>\n");
            else if (line.startsWith("####"))   html.append("<h4>").append(parseInline(line.substring(5).trim())).append("</h4>\n");
            else if (line.startsWith("###"))    html.append("<h3>").append(parseInline(line.substring(4).trim())).append("</h3>\n");
            else if (line.startsWith("##"))     html.append("<h2>").append(parseInline(line.substring(3).trim())).append("</h2>\n");
            else if (line.startsWith("#"))      html.append("<h1>").append(parseInline(line.substring(2).trim())).append("</h1>\n");
            else if (line.matches("-{3,}|\\*{3,}|_{3,}")) html.append("<hr>\n");
            else if (line.startsWith("> "))     html.append("<blockquote>").append(parseInline(line.substring(2))).append("</blockquote>\n");
            else if (line.trim().isEmpty())     html.append("\n");
            else                                html.append("<p>").append(parseInline(line)).append("</p>\n");
        }

        if (inList) html.append("</ul>\n");
        if (inCode) html.append("</code></pre>\n");

        return html.toString();
    }

    private static String parseInline(String text) {
        text = Pattern.compile("\\*\\*(.+?)\\*\\*").matcher(text).replaceAll("<strong>$1</strong>");
        text = Pattern.compile("__(.+?)__").matcher(text).replaceAll("<strong>$1</strong>");
        text = Pattern.compile("\\*(.+?)\\*").matcher(text).replaceAll("<em>$1</em>");
        text = Pattern.compile("_(.+?)_").matcher(text).replaceAll("<em>$1</em>");
        text = Pattern.compile("~~(.+?)~~").matcher(text).replaceAll("<del>$1</del>");
        text = Pattern.compile("`(.+?)`").matcher(text).replaceAll("<code>$1</code>");
        text = Pattern.compile("\\[(.+?)\\]\\((.+?)\\)").matcher(text).replaceAll("<a href=\"$2\">$1</a>");
        text = Pattern.compile("!\\[(.+?)\\]\\((.+?)\\)").matcher(text).replaceAll("<img src=\"$2\" alt=\"$1\">");
        return text;
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        String md = """
            # Hello World
            ## Subtitle here

            This is **bold** and *italic* and ~~strikethrough~~.

            Here is `inline code` and a [link](https://google.com).

            > This is a blockquote

            - Item one
            - Item **two**
            - Item *three*

            ---

            ```
            def hello():
                print("code block")
            ```

            Final paragraph with **mixed** _formatting_ here.
            """;

        System.out.println("=".repeat(50));
        System.out.println("  Markdown Parser");
        System.out.println("=".repeat(50));

        String html = parse(md);
        System.out.println(html);
    }
}
