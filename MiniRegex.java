import java.util.*;

public class MiniRegex {

    enum TokenType {
        LITERAL,
        DOT,
        CHAR_CLASS,
        ANCHOR_START,
        ANCHOR_END,
        GROUP_OPEN,
        GROUP_CLOSE,
        ALTERNATION,
        QUANTIFIER
    }

    static class Token {

        final TokenType type;
        final char literal;
        final boolean negated;
        final char[] classChars;
        final char[][] classRanges;
        final int minRep;
        final int maxRep;
        final char escapeCode;

        Token(char literal) {
            this.type = TokenType.LITERAL;
            this.literal = literal;
            this.negated = false;
            this.classChars = null;
            this.classRanges = null;
            this.minRep = 1;
            this.maxRep = 1;
            this.escapeCode = '\0';
        }

        Token(char escapeCode, boolean ignored) {
            this.type = TokenType.LITERAL;
            this.literal = '\0';
            this.negated = false;
            this.classChars = null;
            this.classRanges = null;
            this.minRep = 1;
            this.maxRep = 1;
            this.escapeCode = escapeCode;
        }

        Token(TokenType type) {
            this.type = type;
            this.literal = '\0';
            this.negated = false;
            this.classChars = null;
            this.classRanges = null;
            this.minRep = 1;
            this.maxRep = 1;
            this.escapeCode = '\0';
        }

        Token(int minRep, int maxRep) {
            this.type = TokenType.QUANTIFIER;
            this.literal = '\0';
            this.negated = false;
            this.classChars = null;
            this.classRanges = null;
            this.minRep = minRep;
            this.maxRep = maxRep;
            this.escapeCode = '\0';
        }

        Token(boolean negated, char[] classChars, char[][] classRanges) {
            this.type = TokenType.CHAR_CLASS;
            this.literal = '\0';
            this.negated = negated;
            this.classChars = classChars;
            this.classRanges = classRanges;
            this.minRep = 1;
            this.maxRep = 1;
            this.escapeCode = '\0';
        }
    }

    public record MatchResult(boolean matched, int start, int end, String group) {
        public String value() {
            return matched ? group : "";
        }
    }

    private final String rawPattern;
    private final Token[] tokens;

    public MiniRegex(String pattern) {
        this.rawPattern = pattern;
        this.tokens = tokenize(pattern);
    }

    private Token[] tokenize(String pattern) {
        List<Token> list = new ArrayList<>();
        int i = 0;

        while (i < pattern.length()) {
            char c = pattern.charAt(i);

            switch (c) {
                case '^' -> {
                    list.add(new Token(TokenType.ANCHOR_START));
                    i++;
                }
                case '$' -> {
                    list.add(new Token(TokenType.ANCHOR_END));
                    i++;
                }
                case '.' -> {
                    list.add(new Token(TokenType.DOT));
                    i++;
                }
                case '(' -> {
                    list.add(new Token(TokenType.GROUP_OPEN));
                    i++;
                }
                case ')' -> {
                    list.add(new Token(TokenType.GROUP_CLOSE));
                    i++;
                }
                case '|' -> {
                    list.add(new Token(TokenType.ALTERNATION));
                    i++;
                }
                case '\\' -> {
                    if (i + 1 < pattern.length()) {
                        char next = pattern.charAt(i + 1);
                        list.add(new Token(next, true));
                        i += 2;
                    } else {
                        list.add(new Token(c));
                        i++;
                    }
                }
                case '[' -> {
                    int close = pattern.indexOf(']', i + 1);
                    if (close == -1) {
                        list.add(new Token(c));
                        i++;
                        break;
                    }
                    String inner = pattern.substring(i + 1, close);
                    boolean negate = inner.startsWith("^");
                    if (negate) inner = inner.substring(1);

                    List<Character> chars = new ArrayList<>();
                    List<char[]> ranges = new ArrayList<>();

                    int j = 0;
                    while (j < inner.length()) {
                        if (j + 2 < inner.length() && inner.charAt(j + 1) == '-') {
                            ranges.add(new char[]{inner.charAt(j), inner.charAt(j + 2)});
                            j += 3;
                        } else {
                            chars.add(inner.charAt(j));
                            j++;
                        }
                    }

                    char[] ca = new char[chars.size()];
                    for (int k = 0; k < ca.length; k++) ca[k] = chars.get(k);
                    char[][] ra = ranges.toArray(new char[0][]);

                    list.add(new Token(negate, ca, ra));
                    i = close + 1;
                }
                case '*' -> {
                    list.add(new Token(0, -1));
                    i++;
                }
                case '+' -> {
                    list.add(new Token(1, -1));
                    i++;
                }
                case '?' -> {
                    list.add(new Token(0, 1));
                    i++;
                }
                case '{' -> {
                    int close = pattern.indexOf('}', i + 1);
                    if (close == -1) {
                        list.add(new Token(c));
                        i++;
                        break;
                    }
                    String inner = pattern.substring(i + 1, close);
                    String[] parts = inner.split(",", -1);

                    try {
                        int min, max;
                        if (parts.length == 1) {
                            min = max = Integer.parseInt(parts[0].trim());
                        } else {
                            min = Integer.parseInt(parts[0].trim());
                            String maxStr = parts[1].trim();
                            max = maxStr.isEmpty() ? -1 : Integer.parseInt(maxStr);
                        }
                        list.add(new Token(min, max));
                        i = close + 1;
                    } catch (NumberFormatException e) {
                        list.add(new Token(c));
                        i++;
                    }
                }
                default -> {
                    list.add(new Token(c));
                    i++;
                }
            }
        }

        return list.toArray(new Token[0]);
    }

    public boolean matches(String text) {
        return findFirst(text).matched();
    }

    public MatchResult findFirst(String text) {
        boolean anchored = tokens.length > 0 && tokens[0].type == TokenType.ANCHOR_START;
        Token[] effective = anchored ? Arrays.copyOfRange(tokens, 1, tokens.length) : tokens;

        if (anchored) {
            int[] res = matchTokens(effective, 0, text, 0);
            if (res != null) {
                return new MatchResult(true, 0, res[0], text.substring(0, res[0]));
            }
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

    public List<MatchResult> findAll(String text) {
        List<MatchResult> results = new ArrayList<>();
        boolean anchored = tokens.length > 0 && tokens[0].type == TokenType.ANCHOR_START;
        Token[] effective = anchored ? Arrays.copyOfRange(tokens, 1, tokens.length) : tokens;

        int i = 0;
        while (i <= text.length()) {
            int[] res = matchTokens(effective, 0, text, i);
            if (res != null) {
                int matchEnd = res[0];
                results.add(new MatchResult(true, i, matchEnd, text.substring(i, matchEnd)));
                i = matchEnd == i ? i + 1 : matchEnd;
            } else {
                if (anchored) break;
                i++;
            }
        }
        return results;
    }

    public String replaceFirst(String text, String replacement) {
        MatchResult m = findFirst(text);
        if (!m.matched()) return text;
        return text.substring(0, m.start()) + replacement + text.substring(m.end());
    }

    public String replaceAll(String text, String replacement) {
        List<MatchResult> all = findAll(text);
        if (all.isEmpty()) return text;

        StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (MatchResult m : all) {
            sb.append(text, pos, m.start()).append(replacement);
            pos = m.end();
        }
        sb.append(text.substring(pos));
        return sb.toString();
    }

    @Override
    public String toString() {
        return "/" + rawPattern + "/";
    }

    private int[] matchTokens(Token[] toks, int ti, String text, int pos) {
        while (ti < toks.length && toks[ti].type == TokenType.ANCHOR_START) {
            if (pos != 0) return null;
            ti++;
        }

        if (ti >= toks.length) return new int[]{pos};

        Token tok = toks[ti];

        if (tok.type == TokenType.ANCHOR_END) {
            return pos == text.length() ? new int[]{pos} : null;
        }

        int altIdx = findTopLevelAlternation(toks, ti);
        if (altIdx != -1) {
            Token[] left = Arrays.copyOfRange(toks, ti, altIdx);
            Token[] right = Arrays.copyOfRange(toks, altIdx + 1, toks.length);

            int[] r = matchTokens(left, 0, text, pos);
            if (r != null) {
                return r;
            }
            return matchTokens(right, 0, text, pos);
        }

        if (tok.type == TokenType.GROUP_OPEN) {
            int closeIdx = findMatchingClose(toks, ti);
            if (closeIdx == -1) return null;

            Token[] inner = Arrays.copyOfRange(toks, ti + 1, closeIdx);
            Token[] after = Arrays.copyOfRange(toks, closeIdx + 1, toks.length);

            int minQ = 1;
            int maxQ = 1;
            int afterStart = 0;

            if (after.length > 0 && after[0].type == TokenType.QUANTIFIER) {
                minQ = after[0].minRep;
                maxQ = after[0].maxRep;
                afterStart = 1;
            }

            Token[] realAfter = Arrays.copyOfRange(after, afterStart, after.length);
            return matchQuantified(inner, true, minQ, maxQ, text, pos, realAfter);
        }

        boolean hasQuantifier = ti + 1 < toks.length && toks[ti + 1].type == TokenType.QUANTIFIER;
        int min = 1;
        int max = 1;
        Token[] afterToks = Arrays.copyOfRange(toks, ti + 1, toks.length);

        if (hasQuantifier) {
            min = toks[ti + 1].minRep;
            max = toks[ti + 1].maxRep;
            afterToks = Arrays.copyOfRange(toks, ti + 2, toks.length);
        }

        return matchQuantified(new Token[]{tok}, false, min, max, text, pos, afterToks);
    }

    private int[] matchQuantified(Token[] unit, boolean isGroup,
                                  int min, int max,
                                  String text, int pos,
                                  Token[] after) {

        List<Integer> positions = new ArrayList<>();
        positions.add(pos);

        int current = pos;
        int count = 0;

        while (max < 0 || count < max) {
            int next;
            if (isGroup) {
                next = advanceGroup(unit, text, current);
            } else {
                next = advanceSingle(unit[0], text, current);
            }
            if (next == -1) break;
            positions.add(next);
            current = next;
            count++;
        }

        if (count < min) return null;

        for (int k = positions.size() - 1; k >= min; k--) {
            int[] res = matchTokens(after, 0, text, positions.get(k));
            if (res != null) return res;
        }
        return null;
    }

    private int advanceSingle(Token tok, String text, int pos) {
        if (pos >= text.length()) return -1;
        return matchSingleChar(tok, text.charAt(pos)) ? pos + 1 : -1;
    }

    private int advanceGroup(Token[] group, String text, int pos) {
        int[] res = matchTokens(group, 0, text, pos);
        return res != null ? res[0] : -1;
    }

    private boolean matchSingleChar(Token tok, char c) {
        return switch (tok.type) {
            case DOT -> true;
            case LITERAL -> matchLiteral(tok, c);
            case CHAR_CLASS -> matchClass(tok, c);
            default -> false;
        };
    }

    private boolean matchLiteral(Token tok, char c) {
        if (tok.escapeCode != '\0') {
            return switch (tok.escapeCode) {
                case 'd' -> Character.isDigit(c);
                case 'D' -> !Character.isDigit(c);
                case 'w' -> Character.isLetterOrDigit(c) || c == '_';
                case 'W' -> !(Character.isLetterOrDigit(c) || c == '_');
                case 's' -> Character.isWhitespace(c);
                case 'S' -> !Character.isWhitespace(c);
                default -> tok.escapeCode == c;
            };
        }
        return tok.literal == c;
    }

    private boolean matchClass(Token tok, char c) {
        boolean matched = false;

        if (tok.classChars != null) {
            for (char ch : tok.classChars) {
                if (ch == c) {
                    matched = true;
                    break;
                }
            }
        }

        if (!matched && tok.classRanges != null) {
            for (char[] range : tok.classRanges) {
                if (c >= range[0] && c <= range[1]) {
                    matched = true;
                    break;
                }
            }
        }

        return tok.negated != matched;
    }

    private int findTopLevelAlternation(Token[] toks, int start) {
        int depth = 0;
        for (int i = start; i < toks.length; i++) {
            if (toks[i].type == TokenType.GROUP_OPEN) depth++;
            if (toks[i].type == TokenType.GROUP_CLOSE) depth--;
            if (toks[i].type == TokenType.ALTERNATION && depth == 0) return i;
        }
        return -1;
    }

    private int findMatchingClose(Token[] toks, int open) {
        int depth = 0;
        for (int i = open; i < toks.length; i++) {
            if (toks[i].type == TokenType.GROUP_OPEN) depth++;
            if (toks[i].type == TokenType.GROUP_CLOSE) depth--;
            if (depth == 0) return i;
        }
        return -1;
    }
}
