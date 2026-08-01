import java.util.*;

public class MiniRegex {

    // ─────────────────────────────────────────
    // Token types
    // ─────────────────────────────────────────

    enum TokenType {
        LITERAL,        // a b c …
        DOT,            // .
        CHAR_CLASS,     // [abc] [a-z] [^abc]
        ANCHOR_START,   // ^
        ANCHOR_END,     // $
        GROUP_OPEN,     // (
        GROUP_CLOSE,    // )
        ALTERNATION,    // |
        QUANTIFIER      // * + ? {n} {n,} {n,m}
    }

    // ─────────────────────────────────────────
    // Token
    // ─────────────────────────────────────────

    static class Token {

        final TokenType type;

        // LITERAL
        final char literal;

        // CHAR_CLASS
        final boolean   negated;
        final char[]    classChars;   // literal chars in class
        final char[][]  classRanges;  // [from, to] pairs

        // QUANTIFIER
        final int  minRep;
        final int  maxRep;   // -1 = unlimited

        // ESCAPE category (for \d \w \s etc.)
        final char escapeCode;  // 'd','w','s','D','W','S', or '\0'

        // ── Constructors ─────────────────────

        /** Literal character */
        Token(char literal) {
            this.type       = TokenType.LITERAL;
            this.literal    = literal;
            this.negated    = false;
            this.classChars = null;
            this.classRanges= null;
            this.minRep     = 1;
            this.maxRep     = 1;
            this.escapeCode = '\0';
        }

        /** Escape sequence \d \w \s etc. */
        Token(char escapeCode, boolean ignored) {
            this.type       = TokenType.LITERAL;
            this.literal    = '\0';
            this.negated    = false;
            this.classChars = null;
            this.classRanges= null;
            this.minRep     = 1;
            this.maxRep     = 1;
            this.escapeCode = escapeCode;
        }

        /** Dot / anchor / group / alternation */
        Token(TokenType type) {
            this.type       = type;
            this.literal    = '\0';
            this.negated    = false;
            this.classChars = null;
            this.classRanges= null;
            this.minRep     = 1;
            this.maxRep     = 1;
            this.escapeCode = '\0';
        }

        /** Quantifier */
        Token(int minRep, int maxRep) {
            this.type       = TokenType.QUANTIFIER;
            this.literal    = '\0';
            this.negated    = false;
            this.classChars = null;
            this.classRanges= null;
            this.minRep     = minRep;
            this.maxRep     = maxRep;
            this.escapeCode = '\0';
        }

        /** Character class */
        Token(boolean negated, char[] classChars, char[][] classRanges) {
            this.type        = TokenType.CHAR_CLASS;
            this.literal     = '\0';
            this.negated     = negated;
            this.classChars  = classChars;
            this.classRanges = classRanges;
            this.minRep      = 1;
            this.maxRep      = 1;
            this.escapeCode  = '\0';
        }

        @Override
        public String toString() {
            return switch (type) {
                case LITERAL     -> escapeCode != '\0'
                                    ? "\\" + escapeCode
                                    : String.valueOf(literal);
                case DOT         -> ".";
                case ANCHOR_START-> "^";
                case ANCHOR_END  -> "$";
                case GROUP_OPEN  -> "(";
                case GROUP_CLOSE -> ")";
                case ALTERNATION -> "|";
                case QUANTIFIER  -> "{" + minRep + "," + (maxRep < 0 ? "" : maxRep) + "}";
                case CHAR_CLASS  -> "[class]";
            };
        }
    }

    // ─────────────────────────────────────────
    // Match result
    // ─────────────────────────────────────────

    public record MatchResult(boolean matched, int start, int end, String group) {
        public String value() { return matched ? group : ""; }
    }

    // ─────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────

    private final String  rawPattern;
    private final Token[] tokens;

    // ─────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────

    public MiniRegex(String pattern) {
        this.rawPattern = pattern;
        this.tokens     = tokenize(pattern);
    }

    // ─────────────────────────────────────────
    // Tokenizer
    // ─────────────────────────────────────────

    private Token[] tokenize(String pattern) {
        List<Token> list = new ArrayList<>();
        int i = 0;

        while (i < pattern.length()) {
            char c = pattern.charAt(i);

            switch (c) {

                case '^' -> { list.add(new Token(TokenType.ANCHOR_START)); i++; }
                case '$' -> { list.add(new Token(TokenType.ANCHOR_END));   i++; }
                case '.' -> { list.add(new Token(TokenType.DOT));          i++; }
                case '(' -> { list.add(new Token(TokenType.GROUP_OPEN));   i++; }
                case ')' -> { list.add(new Token(TokenType.GROUP_CLOSE));  i++; }
                case '|' -> { list.add(new Token(TokenType.ALTERNATION));  i++; }

                // ── Escape sequences ─────────
                case '\\' -> {
                    if (i + 1 < pattern.length()) {
                        char next = pattern.charAt(i + 1);
                        list.add(new Token(next, true)); // escape token
                        i += 2;
                    } else {
                        list.add(new Token(c));
                        i++;
                    }
                }

                // ── Character classes ────────
                case '[' -> {
                    int close = pattern.indexOf(']', i + 1);
                    if (close == -1) {
                        list.add(new Token(c));
                        i++;
                        break;
                    }
                    String inner   = pattern.substring(i + 1, close);
                    boolean negate = inner.startsWith("^");
                    if (negate) inner = inner.substring(1);

                    List<Character> chars  = new ArrayList<>();
                    List<char[]>    ranges = new ArrayList<>();

                    int j = 0;
                    while (j < inner.length()) {
                        if (j + 2 < inner.length() && inner.charAt(j + 1) == '-') {
                            ranges.add(new char[]{ inner.charAt(j), inner.charAt(j + 2) });
                            j += 3;
                        } else {
                            chars.add(inner.charAt(j));
                            j++;
                        }
                    }

                    char[]   ca = new char[chars.size()];
                    for (int k = 0; k < ca.length; k++) ca[k] = chars.get(k);
                    char[][] ra = ranges.toArray(new char[0][]);

                    list.add(new Token(negate, ca, ra));
                    i = close + 1;
                }

                // ── Quantifiers ──────────────
                case '*' -> { list.add(new Token(0, -1)); i++; }
                case '+' -> { list.add(new Token(1, -1)); i++; }
                case '?' -> { list.add(new Token(0,  1)); i++; }

                case '{' -> {
                    int close = pattern.indexOf('}', i + 1);
                    if (close == -1) { list.add(new Token(c)); i++; break; }

                    String inner = pattern.substring(i + 1, close);
                    String[] parts = inner.split(",", -1);

                    try {
                        int min, max;
                        if (parts.length == 1) {
                            min = max = Integer.parseInt(parts[0].trim());
                        } else {
                            min = Integer.parseInt(parts[0].trim());
                            max = parts[1].trim().isEmpty()
                                  ? -1
                                  : Integer.parseInt(parts[1].trim());
                        }
                        list.add(new Token(min, max));
                        i = close + 1;
                    } catch (NumberFormatException e) {
                        list.add(new Token(c));
                        i++;
                    }
                }

                default -> { list.add(new Token(c)); i++; }
            }
        }

        return list.toArray(new Token[0]);
    }

    // ─────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────

    /** Returns true if the pattern matches anywhere in the text. */
    public boolean matches(String text) {
        return findFirst(text).matched();
    }

    /** Returns the first match, or an unmatched result. */
    public MatchResult findFirst(String text) {
        boolean anchored = tokens.length > 0
                           && tokens[0].type == TokenType.ANCHOR_START;
        int startIdx = anchored ? 1 : 0;
        Token[] effective = anchored
                            ? Arrays.copyOfRange(tokens, 1, tokens.length)
                            : tokens;

        int start = anchored ? 0 : -1;
        int end   = -1;

        if (anchored) {
            int[] res = matchTokens(effective, 0, text, 0);
            if (res != null) return new MatchResult(true, 0, res[0], text.substring(0, res[0]));
            return new MatchResult(false, -1, -1, "");
        }

        for (int i = 0; i <= text.length(); i++) {
            int[] res = matchTokens(effective, 0, text, i);
            if (res != null) {
                return new MatchResult(true, i, res[0], text.substring(i, res[0]));
            }
        }
        return new MatchResult(false, -1, -1, "");
    }

    /** Returns all non-overlapping matches. */
    public List<MatchResult> findAll(String text) {
        List<MatchResult> results = new ArrayList<>();
        boolean anchored = tokens.length > 0
                           && tokens[0].type == TokenType.ANCHOR_START;
        Token[] effective = anchored
                            ? Arrays.copyOfRange(tokens, 1, tokens.length)
                            : tokens;

        int i = 0;
        while (i <= text.length()) {
            int[] res = matchTokens(effective, 0, text, i);
            if (res != null) {
                int matchEnd = res[0];
                results.add(new MatchResult(true, i, matchEnd, text.substring(i, matchEnd)));
                i = matchEnd == i ? i + 1 : matchEnd; // avoid infinite loop on zero-width
            } else {
                if (anchored) break;
                i++;
            }
        }
        return results;
    }

    /** Replaces the first match with the replacement string. */
    public String replaceFirst(String text, String replacement) {
        MatchResult m = findFirst(text);
        if (!m.matched()) return text;
        return text.substring(0, m.start()) + replacement + text.substring(m.end());
    }

    /** Replaces all non-overlapping matches with the replacement string. */
    public String replaceAll(String text, String replacement) {
        List<MatchResult> all = findAll(text);
        if (all.isEmpty()) return text;

        StringBuilder sb  = new StringBuilder();
        int           pos = 0;
        for (MatchResult m : all) {
            sb.append(text, pos, m.start()).append(replacement);
            pos = m.end();
        }
        sb.append(text.substring(pos));
        return sb.toString();
    }

    @Override
    public String toString() { return "/" + rawPattern + "/"; }

    // ─────────────────────────────────────────
    // Core matcher
    // ─────────────────────────────────────────

    /**
     * Matches tokens[ti..] against text starting at position pos.
     * Returns new text position on success, or null on failure.
     */
    private int[] matchTokens(Token[] toks, int ti, String text, int pos) {

        // ── Skip leading anchors ─────────────
        while (ti < toks.length && toks[ti].type == TokenType.ANCHOR_START) {
            if (pos != 0) return null;
            ti++;
        }

        if (ti >= toks.length) return new int[]{ pos };

        Token tok = toks[ti];

        // ── End anchor ───────────────────────
        if (tok.type == TokenType.ANCHOR_END) {
            return pos == text.length() ? new int[]{ pos } : null;
        }

        // ── Alternation ──────────────────────
        // Split top-level alternation into branches
        int altIdx = findTopLevelAlternation(toks, ti);
        if (altIdx != -1) {
            Token[] left  = Arrays.copyOfRange(toks, ti,       altIdx);
            Token[] right = Arrays.copyOfRange(toks, altIdx + 1, toks.length);

            int[] r = matchTokens(left, 0, text, pos);
            if (r != null) {
                // continue with nothing after left (left consumed the rest)
                return r;
            }
            return matchTokens(right, 0, text, pos);
        }

        // ── Group ────────────────────────────
        if (tok.type == TokenType.GROUP_OPEN) {
            int closeIdx = findMatchingClose(toks, ti);
            if (closeIdx == -1) return null;

            Token[] inner = Arrays.copyOfRange(toks, ti + 1, closeIdx);
            Token[] after = Arrays.copyOfRange(toks, closeIdx + 1, toks.length);

            // Check for quantifier after group
            int[] quantifier = { 1, 1 };
            int   afterStart = 0;

            if (after.length > 0 && after[0].type == TokenType.QUANTIFIER) {
                quantifier[0] = after[0].minRep;
                quantifier[1] = after[0].maxRep;
                afterStart    = 1;
            }

            Token[] realAfter = Arrays.copyOfRange(after, afterStart, after.length);
            return matchQuantified(inner, true, quantifier[0], quantifier[1],
                                   text, pos, realAfter);
        }

        // ── Look ahead for quantifier ────────
        boolean hasQuantifier = ti + 1 < toks.length
                                && toks[ti + 1].type == TokenType.QUANTIFIER;
        int min = 1, max = 1;
        Token[] after = Arrays.copyOfRange(toks, ti + 1, toks.length);

        if (hasQuantifier) {
            min   = toks[ti + 1].minRep;
            max   = toks[ti + 1].maxRep;
            after = Arrays.copyOfRange(toks, ti + 2, toks.length);
        }

        return matchQuantified(new Token[]{ tok }, false, min, max, text, pos, after);
    }

    // ─────────────────────────────────────────
    // Quantified matching (greedy)
    // ─────────────────────────────────────────

    private int[] matchQuantified(Token[] unit, boolean isGroup,
                                   int min, int max,
                                   String text, int pos,
                                   Token[] after) {

        List<Integer> positions = new ArrayList<>();
        positions.add(pos);

        int current = pos;
        int count   = 0;

        // Greedily consume as many as possible
        while (max < 0 || count < max) {
            int next = isGroup
                       ? advanceGroup(unit, text, current)
                       : advanceSingle(unit[0], text, current);
            if (next == -1) break;
            positions.add(next);
            current = next;
            count++;
        }

        if (count < min) return null;

        // Try from most consumed → least (greedy backtrack)
        for (int k = positions.size() - 1; k >= min; k--) {
            int[] res = matchTokens(after, 0, text, positions.get(k));
            if (res != null) return res;
        }
        return null;
    }

    /** Advance past a single token. Returns new position or -1. */
    private int advanceSingle(Token tok, String text, int pos) {
        if (pos >= text.length()) return -1;
        return matchSingleChar(tok, text.charAt(pos)) ? pos + 1 : -1;
    }

    /** Advance past an inner group expression. Returns new position or -1. */
    private int advanceGroup(Token[] group, String text, int pos) {
        int[] res = matchTokens(group, 0, text, pos);
        return res != null ? res[0] : -1;
    }

    // ─────────────────────────────────────────
    // Single-character matching
    // ─────────────────────────────────────────

    private boolean matchSingleChar(Token tok, char c) {
        return switch (tok.type) {
            case DOT        -> true;
            case LITERAL    -> matchLiteral(tok, c);
            case CHAR_CLASS -> matchClass(tok, c);
            default         -> false;
        };
    }

    private boolean matchLiteral(Token tok, char c) {
        // Escape sequences
        if (tok.escapeCode != '\0') {
            return switch (tok.escapeCode) {
                case 'd' -> Character.isDigit(c);
                case 'D' -> !Character.isDigit(c);
                case 'w' -> Character.isLetterOrDigit(c) || c == '_';
                case 'W' -> !(Character.isLetterOrDigit(c) || c == '_');
                case 's' -> Character.isWhitespace(c);
                case 'S' -> !Character.isWhitespace(c);
                default  -> tok.escapeCode == c;
            };
        }
        return tok.literal == c;
    }

    private boolean matchClass(Token tok, char c) {
        boolean matched = false;

        for (char ch : tok.classChars) {
            if (ch == c) { matched = true; break; }
        }

        if (!matched) {
            for (char[] range : tok.classRanges) {
                if (c >= range[0] && c <= range[1]) { matched = true; break; }
            }
        }

        return tok.negated != matched;
    }

    // ─────────────────────────────────────────
    // Structural helpers
    // ─────────────────────────────────────────

    /** Finds top-level '|' index, or -1. */
    private int findTopLevelAlternation(Token[] toks, int start) {
        int depth = 0;
        for (int i = start; i < toks.length; i++) {
            if (toks[i].type == TokenType.GROUP_OPEN)  depth++;
            if (toks[i].type == TokenType.GROUP_CLOSE) depth--;
            if (toks[i].type == TokenType.ALTERNATION && depth == 0) return i;
        }
        return -1;
    }

    /** Finds the matching GROUP_CLOSE for the GROUP_OPEN at index open. */
    private int findMatchingClose(Token[] toks, int open) {
        int depth = 0;
        for (int i = open; i < toks.length; i++) {
            if (toks[i].type == TokenType.GROUP_OPEN)  depth++;
            if (toks[i].type == TokenType.GROUP_CLOSE) depth--;
            if (depth == 0) return i;
        }
        return -1;
    }

    // ─────────────────────────────────────────
    // Main – tests
    // ─────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              Mini Regex Engine – Test Suite              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        record Test(String label, String pattern, String text, boolean expected) {}

        List<Test> tests = List.of(

            // ── Literals & anchors ────────────────────────────────────────────
            new Test("Literal match",       "hello",    "say hello world", true),
            new Test("Anchor start – pass", "^hello",   "hello world",     true),
            new Test("Anchor start – fail", "^hello",   "say hello",       false),
            new Test("Anchor end – pass",   "world$",   "hello world",     true),
            new Test("Anchor end – fail",   "world$",   "world hello",     false),

            // ── Dot ───────────────────────────────────────────────────────────
            new Test("Dot wildcard",        "h.llo",    "hello",           true),
            new Test("Dot wildcard 2",      "h.llo",    "hxllo",           true),

            // ── Quantifiers ───────────────────────────────────────────────────
            new Test("Star – zero",         "ab*c",     "ac",              true),
            new Test("Star – many",         "ab*c",     "abbbbc",          true),
            new Test("Plus – zero fail",    "ab+c",     "ac",              false),
            new Test("Plus – one pass",     "ab+c",     "abc",             true),
            new Test("Question – zero",     "ab?c",     "ac",              true),
            new Test("Question – one",      "ab?c",     "abc",             true),
            new Test("Question – two fail", "ab?c",     "abbc",            false),
            new Test("Exact {n}",           "a{3}",     "aaa",             true),
            new Test("Exact {n} fail",      "a{3}",     "aa",              false),
            new Test("Range {n,m}",         "a{2,4}",   "aaa",             true),
            new Test("Range {n,m} fail",    "a{2,4}",   "a",               false),
            new Test("At-least {n,}",       "a{2,}",    "aaaaa",           true),

            // ── Dot-star ──────────────────────────────────────────────────────
            new Test("Dot-star",            ".*",       "anything",        true),
            new Test("Dot-star middle",     "a.*b",     "axxxb",           true),

            // ── Character classes ─────────────────────────────────────────────
            new Test("Class match",         "[abc]",    "b",               true),
            new Test("Class no match",      "[abc]",    "d",               false),
            new Test("Class range",         "[a-z]+",   "hello",           true),
            new Test("Negated class",       "[^abc]",   "d",               true),
            new Test("Negated class fail",  "[^abc]",   "a",               false),

            // ── Escape sequences ──────────────────────────────────────────────
            new Test("\\d digit",           "\\d+",     "12345",           true),
            new Test("\\d non-digit fail",  "\\d+",     "abc",             false),
            new Test("\\w word char",       "\\w+",     "hello_1",         true),
            new Test("\\s whitespace",      "\\s",      " ",               true),
            new Test("\\D non-digit",       "\\D+",     "abc",             true),

            // ── Alternation ───────────────────────────────────────────────────
            new Test("Alternation left",    "cat|dog",  "cat",             true),
            new Test("Alternation right",   "cat|dog",  "dog",             true),
            new Test("Alternation fail",    "cat|dog",  "fish",            false),

            // ── Groups ────────────────────────────────────────────────────────
            new Test("Group basic",         "(ab)+",    "ababab",          true),
            new Test("Group fail",          "(ab)+",    "aab",             false),
            new Test("Group alternation",   "(cat|dog)s","cats",           true)
        );

        System.out.printf("%n  %-24s %-14s %-22s %-8s %-8s%n",
                          "Label", "Pattern", "Text", "Expected", "Got");
        System.out.println("  " + "─".repeat(82));

        int passed = 0;
        List<String> failures = new ArrayList<>();

        for (Test t : tests) {
            boolean result = new MiniRegex(t.pattern).matches(t.text);
            boolean ok     = result == t.expected;
            if (ok) passed++;
            else failures.add(t.label);

            System.out.printf("  %-24s %-14s %-22s %-8s %-8s %s%n",
                    t.label,
                    t.pattern,
                    "\"" + t.text + "\"",
                    t.expected,
                    result,
                    ok ? "✅" : "❌");
        }

        System.out.printf("%n  Passed: %d/%d%n", passed, tests.size());

        if (!failures.isEmpty()) {
            System.out.println("  Failed: " + failures);
        }

        // ── findAll ───────────────────────────────────────────────────────────
        System.out.println("\n" + "─".repeat(60));
        System.out.println("  findAll / replace demos");
        System.out.println("─".repeat(60));

        demo("a.b",    "aab axb ayb acb qqq aob");
        demo("\\d+",   "abc 123 def 456 ghi 7");
        demo("[A-Z]",  "Hello World From Java");
        demo("cat|dog","I have a cat and a dog and another cat");

        // ── replaceAll ────────────────────────────────────────────────────────
        System.out.println();
        MiniRegex rNum  = new MiniRegex("\\d+");
        String    input = "Price: 100, Qty: 25, Total: 2500";
        System.out.println("  Input   : " + input);
        System.out.println("  Pattern : \\d+");
        System.out.println("  Replace : ***");
        System.out.println("  Result  : " + rNum.replaceAll(input, "***"));
    }

    private static void demo(String pattern, String text) {
        MiniRegex r   = new MiniRegex(pattern);
        var       all = r.findAll(text);
        System.out.printf("  %-12s in %-44s → %s%n",
                          "/" + pattern + "/",
                          "\"" + text + "\"",
                          all.stream().map(MatchResult::value).toList());
    }
}
