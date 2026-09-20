import java.util.Arrays;

/** Iterative segment tree supporting point updates and half-open range sums. */
public final class RangeAggregateTree {
    private final int leafCount;
    private final long[] nodes;

    public RangeAggregateTree(int[] values) {
        int capacity = 1;
        while (capacity < values.length) capacity <<= 1;
        leafCount = capacity;
        nodes = new long[capacity * 2];
        for (int i = 0; i < values.length; i++) nodes[capacity + i] = values[i];
        for (int node = capacity - 1; node > 0; node--) nodes[node] = nodes[node * 2] + nodes[node * 2 + 1];
    }

    public void set(int index, int value) {
        check(index);
        int node = leafCount + index;
        nodes[node] = value;
        while ((node >>= 1) > 0) nodes[node] = nodes[node * 2] + nodes[node * 2 + 1];
    }

    public long sum(int leftInclusive, int rightExclusive) {
        if (leftInclusive < 0 || rightExclusive < leftInclusive || rightExclusive > leafCount) {
            throw new IndexOutOfBoundsException("invalid range");
        }
        long total = 0;
        int left = leafCount + leftInclusive;
        int right = leafCount + rightExclusive;
        while (left < right) {
            if ((left & 1) == 1) total += nodes[left++];
            if ((right & 1) == 1) total += nodes[--right];
            left >>= 1;
            right >>= 1;
        }
        return total;
    }

    private void check(int index) {
        if (index < 0 || index >= leafCount) throw new IndexOutOfBoundsException("index=" + index);
    }
}

final class RangeAggregateTreeExample {
    public static void main(String[] args) {
        RangeAggregateTree tree = new RangeAggregateTree(new int[] {2, 4, 6, 8});
        System.out.println(tree.sum(1, 4));
        tree.set(2, 10);
        System.out.println(tree.sum(0, 3));
    }
}
