import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Maximum-cardinality matching for a graph with explicit left/right partitions. */
public final class BipartiteMatcher {
    private final int leftSize;
    private final int rightSize;
    private final List<Integer>[] neighbors;

    @SuppressWarnings("unchecked")
    public BipartiteMatcher(int leftSize, int rightSize) {
        if (leftSize < 0 || rightSize < 0) {
            throw new IllegalArgumentException("Partition sizes must be non-negative");
        }
        this.leftSize = leftSize;
        this.rightSize = rightSize;
        neighbors = (List<Integer>[]) new List<?>[leftSize];
        for (int i = 0; i < leftSize; i++) {
            neighbors[i] = new ArrayList<>();
        }
    }

    /** Adds an edge from a left-partition vertex to a right-partition vertex. */
    public void addEdge(int left, int right) {
        if (left < 0 || left >= leftSize || right < 0 || right >= rightSize) {
            throw new IndexOutOfBoundsException("Edge endpoint outside its partition");
        }
        neighbors[left].add(right);
    }

    /** Returns the maximum matching size; each call recomputes from scratch. */
    public int maximumMatching() {
        int[] leftMatch = new int[leftSize];
        int[] rightMatch = new int[rightSize];
        int[] distance = new int[leftSize];
        Arrays.fill(leftMatch, -1);
        Arrays.fill(rightMatch, -1);
        int matching = 0;
        while (buildLayers(leftMatch, rightMatch, distance)) {
            for (int left = 0; left < leftSize; left++) {
                if (leftMatch[left] == -1 && augment(left, leftMatch, rightMatch, distance)) {
                    matching++;
                }
            }
        }
        return matching;
    }

    private boolean buildLayers(int[] leftMatch, int[] rightMatch, int[] distance) {
        int[] queue = new int[leftSize];
        int head = 0;
        int tail = 0;
        Arrays.fill(distance, -1);
        for (int left = 0; left < leftSize; left++) {
            if (leftMatch[left] == -1) {
                distance[left] = 0;
                queue[tail++] = left;
            }
        }
        boolean reachesFreeRight = false;
        while (head < tail) {
            int left = queue[head++];
            for (int right : neighbors[left]) {
                int pairedLeft = rightMatch[right];
                if (pairedLeft == -1) {
                    reachesFreeRight = true;
                } else if (distance[pairedLeft] == -1) {
                    distance[pairedLeft] = distance[left] + 1;
                    queue[tail++] = pairedLeft;
                }
            }
        }
        return reachesFreeRight;
    }

    private boolean augment(int left, int[] leftMatch, int[] rightMatch, int[] distance) {
        for (int right : neighbors[left]) {
            int pairedLeft = rightMatch[right];
            if (pairedLeft == -1 || (distance[pairedLeft] == distance[left] + 1
                    && augment(pairedLeft, leftMatch, rightMatch, distance))) {
                leftMatch[left] = right;
                rightMatch[right] = left;
                return true;
            }
        }
        distance[left] = -1;
        return false;
    }
}
