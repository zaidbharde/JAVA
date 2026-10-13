import java.util.ArrayList;
import java.util.List;

/** Union-find with path compression, union by size, and component reporting. */
public final class UnionFindComponents {
    private final int[] parent;
    private final int[] size;

    public UnionFindComponents(int elements) {
        if (elements < 0) throw new IllegalArgumentException("negative element count");
        parent = new int[elements];
        size = new int[elements];
        for (int i = 0; i < elements; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int element) {
        check(element);
        int root = element;
        while (root != parent[root]) root = parent[root];
        while (element != root) {
            int next = parent[element];
            parent[element] = root;
            element = next;
        }
        return root;
    }

    public boolean union(int first, int second) {
        int left = find(first), right = find(second);
        if (left == right) return false;
        if (size[left] < size[right]) { int swap = left; left = right; right = swap; }
        parent[right] = left;
        size[left] += size[right];
        return true;
    }

    public List<Integer> componentSizes() {
        List<Integer> result = new ArrayList<>();
        for (int element = 0; element < parent.length; element++) {
            if (find(element) == element) result.add(size[element]);
        }
        return result;
    }

    private void check(int element) {
        if (element < 0 || element >= parent.length) throw new IndexOutOfBoundsException(element);
    }

    public static void main(String[] args) {
        UnionFindComponents graph = new UnionFindComponents(5);
        graph.union(0, 1); graph.union(3, 4);
        System.out.println(graph.componentSizes());
    }
}
