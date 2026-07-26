import java.util.*;

public class RegexEngine {

    private sealed interface Node permits
        Literal, Dot, CharClass, Sequence, Alternation,
        Quantifier, Group, Anchor {}

    private record Literal(char ch) implements Node {}
    private record Dot() implements Node {}
    private record CharClass(Set<Character> chars, boolean negated) implements Node {}
    private record Sequence(List<Node> nodes) implements Node {}
    private record Alternation(Node left, Node right) implements Node {}
    private record Quantifier(Node node, int min, int max, boolean greedy) implements Node {}
    private record Group(Node node, int index) implements Node {}
    private record Anchor(char type) implements Node {}

    private static class Parser {
        private final String pattern;
        private int pos;
        private int groupCount = 0;

        Parser(String pattern) { this.pattern = pattern; }

        Node parse() {
            Node node = parseAlternation();
            if (pos != pattern.length())
                throw new RuntimeException("Unexpected char at " + pos);
            return node;
        }

        private Node parseAlternation() {
            Node left = parseSequence();
            if (pos < pattern.length() && pattern.charAt(pos) == '|') {
                pos++;
                return new Alternation(left, parseAlternation());
            }
            return left;
        }

        private Node parseSequence() {
            List<Node> nodes = new ArrayList<>();
            while (pos < pattern.length() &&
                   pattern.charAt(pos) != ')' &&
                   pattern.charAt(pos) != '|') {
                nodes.add(parseQuantifier());
            }
            return nodes.size() == 1 ? nodes.get(0) : new Sequence(nodes);
        }

        private Node parseQuantifier() {
            Node node = parseAtom();
            if (pos >= pattern.length()) return node;

            int min = -1, max = -1;
            switch (pattern.charAt(pos)) {
                case '*' -> { min = 0; max = Integer.MAX_VALUE; pos++; }
                case '+' -> { min = 1; max = Integer.MAX_VALUE; pos++; }
                case '?' -> { min = 0; max = 1; pos++; }
                case '{' -> {
                    pos++;
                    min = readInt();
                    max = min;
                    if (pos < pattern.length() && pattern.charAt(pos) == ',') {
                        pos++;
                        max = pos < pattern.length() && pattern.charAt(pos) == '}'
                            ? Integer.MAX_VALUE : readInt();
                    }
                    expect('}');
                }
            }

            if (min == -1) return node;

            boolean greedy = true;
            if (pos < pattern.length() && pattern.charAt(pos) == '?') {
                greedy = false; pos++;
            }
            return new Quantifier(node, min, max, greedy);
        }

        private Node parseAtom() {
            char ch = pattern.charAt(pos);
            return switch (ch) {
                case '(' -> { pos++; int idx = ++groupCount;
                    Node n = parseAlternation(); expect(')');
                    yield new Group(n, idx); }
                case '[' -> parseCharClass();
                case '.' -> { pos++; yield new Dot(); }
                case '^' -> { pos++; yield new Anchor('^'); }
                case '$' -> { pos++; yield new Anchor('$'); }
                case '\\' -> { pos++;
                    if (pos >= pattern.length()) throw new RuntimeException("Trailing backslash");
                    yield new Literal(pattern.charAt(pos++)); }
                default -> { pos++; yield new Literal(ch); }
            };
        }

        private Node parseCharClass() {
            pos++;
            boolean negated = false;
            if (pos < pattern.length() && pattern.charAt(pos) == '^') {
                negated = true; pos++;
            }
            Set<Character> chars = new LinkedHashSet<>();
            while (pos < pattern.length() && pattern.charAt(pos) != ']') {
                char c = pattern.charAt(pos++);
                if (pos + 1 < pattern.length() && pattern.charAt(pos) == '-') {
                    pos++;
                    char end = pattern.charAt(pos++);
                    for (char i = c; i <= end; i++) chars.add(i);
                } else {
                    chars.add(c);
                }
            }
            expect(']');
            return new CharClass(chars, negated);
        }

        private int readInt() {
            int start = pos;
            while (pos < pattern.length() && Character.isDigit(pattern.charAt(pos))) pos++;
            return Integer.parseInt(pattern.substring(start, pos));
        }

        private void expect(char ch) {
            if (pos >= pattern.length() || pattern.charAt(pos) != ch)
                throw new RuntimeException("Expected '" + ch + "' at " + pos);
            pos++;
        }
    }

    private record MatchState(int position, Map<Integer, String> groups) {
        MatchState advance(int n) { return new MatchState(position + n, new HashMap<>(groups)); }
        MatchState withGroup(int idx, String val) {
            var g = new HashMap<>(groups); g.put(idx, val); return new MatchState(position, g);
        }
    }

    private final Node root;
    private final String pattern;

    public RegexEngine(String pattern) {
        this.pattern = pattern;
        this.root    = new Parser(pattern).parse();
    }

    private List<MatchState> match(Node node, String input, MatchState state) {
        return switch (node) {
            case Literal l -> {
                if (state.position < input.length() && input.charAt(state.position) == l.ch)
                    yield List.of(state.advance(1));
                yield List.of();
            }
            case Dot d -> {
                if (state.position < input.length() && input.charAt(state.position) != '\n')
                    yield List.of(state.advance(1));
                yield List.of();
            }
            case CharClass cc -> {
                if (state.position < input.length()) {
                    boolean contains = cc.chars.contains(input.charAt(state.position));
                    if (contains != cc.negated) yield List.of(state.advance(1));
                }
                yield List.of();
            }
            case Anchor a -> {
                if (a.type == '^' && state.position == 0) yield List.of(state);
                else if (a.type == '$' && state.position == input.length()) yield List.of(state);
                else yield List.of();
            }
            case Sequence seq -> {
                List<MatchState> current = List.of(state);
                for (Node n : seq.nodes) {
                    List<MatchState> next = new ArrayList<>();
                    for (MatchState s : current) next.addAll(match(n, input, s));
                    current = next;
                    if (current.isEmpty()) break;
                }
                yield current;
            }
            case Alternation alt -> {
                List<MatchState> results = new ArrayList<>(match(alt.left, input, state));
                results.addAll(match(alt.right, input, state));
                yield results;
            }
            case Group g -> {
                int start = state.position;
                List<MatchState> results = match(g.node, input, state);
                yield results.stream().map(s ->
                    s.withGroup(g.index, input.substring(start, s.position))
                ).toList();
            }
            case Quantifier q -> {
                yield matchQuantifier(q, input, state, 0);
            }
        };
    }

    private List<MatchState> matchQuantifier(Quantifier q, String input, MatchState state, int count) {
        List<MatchState> results = new ArrayList<>();

        if (count >= q.min && !q.greedy)
            results.add(state);

        if (count < q.max) {
            for (MatchState s : match(q.node, input, state)) {
                if (s.position > state.position)
                    results.addAll(matchQuantifier(q, input, s, count + 1));
            }
        }

        if (count >= q.min && q.greedy)
            results.add(state);

        return results;
    }

    public boolean matches(String input) {
        return !match(root, input, new MatchState(0, new HashMap<>())).stream()
            .filter(s -> s.position == input.length())
            .toList().isEmpty();
    }

    public Optional<Map<Integer, String>> find(String input) {
        for (int i = 0; i <= input.length(); i++) {
            var results = match(root, input, new MatchState(i, new HashMap<>()));
            for (var s : results) {
                if (s.position > i || i == input.length()) {
                    var groups = new HashMap<>(s.groups);
                    groups.put(0, input.substring(i, s.position));
                    return 
