/** Standalone regression tests; run with assertions enabled. */
public final class BipartiteMatcherTest {
    public static void main(String[] args) {
        testEmptyAndUnbalanced();
        testIsolatedAndReassignment();
        testInvalidInputs();
        testExhaustiveSmallGraphs();
        System.out.println("BipartiteMatcherTest: all tests passed");
    }

    private static void testEmptyAndUnbalanced() {
        check(new BipartiteMatcher(0, 0).maximumMatching() == 0, "empty graph");
        check(new BipartiteMatcher(0, 4).maximumMatching() == 0, "empty left partition");
        BipartiteMatcher graph = new BipartiteMatcher(1, 3);
        graph.addEdge(0, 2);
        check(graph.maximumMatching() == 1, "unbalanced partitions");
        check(graph.maximumMatching() == 1, "repeat computation");
        check(new BipartiteMatcher(3, 0).maximumMatching() == 0, "empty right partition");
    }

    private static void testIsolatedAndReassignment() {
        BipartiteMatcher isolated = new BipartiteMatcher(3, 3);
        isolated.addEdge(0, 0);
        check(isolated.maximumMatching() == 1, "isolated vertices");

        // Greedy first choice 0->0 must be undone so that 1 can use its only neighbor.
        BipartiteMatcher reassignment = new BipartiteMatcher(2, 2);
        reassignment.addEdge(0, 0);
        reassignment.addEdge(0, 1);
        reassignment.addEdge(1, 0);
        check(reassignment.maximumMatching() == 2, "augmenting-path reassignment");
        reassignment.addEdge(0, 1); // duplicate edges do not change the answer
        check(reassignment.maximumMatching() == 2, "duplicate edge");
    }

    private static void testInvalidInputs() {
        expect(IllegalArgumentException.class, () -> new BipartiteMatcher(-1, 2));
        expect(IllegalArgumentException.class, () -> new BipartiteMatcher(2, -1));
        BipartiteMatcher graph = new BipartiteMatcher(2, 2);
        expect(IndexOutOfBoundsException.class, () -> graph.addEdge(-1, 0));
        expect(IndexOutOfBoundsException.class, () -> graph.addEdge(2, 0));
        expect(IndexOutOfBoundsException.class, () -> graph.addEdge(0, -1));
        expect(IndexOutOfBoundsException.class, () -> graph.addEdge(0, 2));
    }

    /** Enumerate every simple bipartite graph through 3x3 and compare with brute force. */
    private static void testExhaustiveSmallGraphs() {
        for (int leftSize = 0; leftSize <= 3; leftSize++) {
            for (int rightSize = 0; rightSize <= 3; rightSize++) {
                int edgeCount = leftSize * rightSize;
                for (int mask = 0; mask < (1 << edgeCount); mask++) {
                    BipartiteMatcher graph = new BipartiteMatcher(leftSize, rightSize);
                    boolean[][] edges = new boolean[leftSize][rightSize];
                    for (int bit = 0; bit < edgeCount; bit++) {
                        if ((mask & (1 << bit)) != 0) {
                            int left = bit / rightSize;
                            int right = bit % rightSize;
                            graph.addEdge(left, right);
                            edges[left][right] = true;
                        }
                    }
                    check(graph.maximumMatching() == bruteForce(edges, 0, 0),
                            "exhaustive graph " + leftSize + "x" + rightSize + " mask " + mask);
                }
            }
        }
    }

    private static int bruteForce(boolean[][] edges, int left, int usedRights) {
        if (left == edges.length) return 0;
        int best = bruteForce(edges, left + 1, usedRights);
        for (int right = 0; right < (edges.length == 0 ? 0 : edges[left].length); right++) {
            if (edges[left][right] && (usedRights & (1 << right)) == 0) {
                best = Math.max(best, 1 + bruteForce(edges, left + 1, usedRights | (1 << right)));
            }
        }
        return best;
    }

    private static void expect(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Expected " + type.getSimpleName() + ", got " + error, error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
