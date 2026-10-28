import java.util.LinkedHashSet;
import java.util.Set;
/** Removes duplicates while retaining the original order. */
public final class Daily20261028StableUnique {
    private Daily20261028StableUnique() { }
    public static int[] unique(int[] values) {
        if (values == null) throw new IllegalArgumentException("array is null");
        Set<Integer> seen = new LinkedHashSet<Integer>();
        for (int value : values) seen.add(value);
        int[] result = new int[seen.size()];
        int i = 0;
        for (Integer value : seen) result[i++] = value;
        return result;
    }
    public static void main(String[] args) {
        for (int n : unique(new int[]{4, 2, 4, 1, 2})) System.out.print(n + " ");
    }
}
