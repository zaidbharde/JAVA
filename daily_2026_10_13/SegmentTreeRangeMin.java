import java.util.Arrays;

/** Range minimum queries with logarithmic updates and queries. */
public final class SegmentTreeRangeMin {
    private final int size;
    private final int[] tree;

    public SegmentTreeRangeMin(int[] values) {
        if (values.length == 0) throw new IllegalArgumentException("empty input");
        size = 1;
        while (size < values.length) size <<= 1;
        tree = new int[size << 1];
        Arrays.fill(tree, Integer.MAX_VALUE);
        System.arraycopy(values, 0, tree, size, values.length);
        for (int node = size - 1; node > 0; node--) {
            tree[node] = Math.min(tree[node << 1], tree[(node << 1) | 1]);
        }
    }

    public void update(int index, int value) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
        int node = size + index;
        tree[node] = value;
        for (node >>= 1; node > 0; node >>= 1) {
            tree[node] = Math.min(tree[node << 1], tree[(node << 1) | 1]);
        }
    }

    public int query(int left, int right) {
        if (left < 0 || right < left || right > size) throw new IndexOutOfBoundsException();
        int result = Integer.MAX_VALUE;
        for (left += size, right += size; left < right; left >>= 1, right >>= 1) {
            if ((left & 1) == 1) result = Math.min(result, tree[left++]);
            if ((right & 1) == 1) result = Math.min(result, tree[--right]);
        }
        return result;
    }

    public static void main(String[] args) {
        SegmentTreeRangeMin values = new SegmentTreeRangeMin(new int[] {7, 2, 9, 4});
        System.out.println(values.query(1, 4));
    }
}
