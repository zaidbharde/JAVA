import java.util.Arrays;

public class UnionFindComponents {
    private final int[] parent;
    private final int[] rank;
    private int components;

    public UnionFindComponents(int size) {
        if (size < 0) throw new IllegalArgumentException("size must be non-negative");
        parent = new int[size];
        rank = new int[size];
        components = size;
        for (int index = 0; index < size; index++) parent[index] = index;
    }

    public int find(int value) {
        if (value < 0 || value >= parent.length) throw new IndexOutOfBoundsException(value);
        if (parent[value] != value) parent[value] = find(parent[value]);
        return parent[value];
    }

    public boolean union(int first, int second) {
        int left = find(first), right = find(second);
        if (left == right) return false;
        if (rank[left] < rank[right]) { int swap = left; left = right; right = swap; }
        parent[right] = left;
        if (rank[left] == rank[right]) rank[left]++;
        components--;
        return true;
    }

    public int componentCount() { return components; }

    public static void main(String[] args) {
        UnionFindComponents sets = new UnionFindComponents(6);
        sets.union(0, 1); sets.union(1, 2); sets.union(4, 5);
        System.out.println(sets.componentCount() + " components " + Arrays.toString(sets.parent));
    }
}
