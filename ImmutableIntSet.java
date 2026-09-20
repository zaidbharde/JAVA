import java.util.Arrays;

/** Immutable sorted integer set with binary-search membership and set operations. */
public final class ImmutableIntSet {
    private final int[] values;

    public ImmutableIntSet(int... input) {
        values = input.clone();
        Arrays.sort(values);
        int unique = 0;
        for (int value : values) {
            if (unique == 0 || values[unique - 1] != value) values[unique++] = value;
        }
        if (unique != values.length) values = Arrays.copyOf(values, unique);
    }

    public boolean contains(int value) { return Arrays.binarySearch(values, value) >= 0; }
    public int size() { return values.length; }
    public int get(int index) { return values[index]; }

    public ImmutableIntSet union(ImmutableIntSet other) {
        int[] merged = new int[values.length + other.values.length];
        int i = 0, j = 0, k = 0;
        while (i < values.length || j < other.values.length) {
            int left = i < values.length ? values[i] : Integer.MAX_VALUE;
            int right = j < other.values.length ? other.values[j] : Integer.MAX_VALUE;
            int chosen = Math.min(left, right);
            merged[k++] = chosen;
            while (i < values.length && values[i] == chosen) i++;
            while (j < other.values.length && other.values[j] == chosen) j++;
        }
        return new ImmutableIntSet(Arrays.copyOf(merged, k));
    }

    @Override public String toString() { return Arrays.toString(values); }
}

final class ImmutableIntSetExample {
    public static void main(String[] args) {
        ImmutableIntSet first = new ImmutableIntSet(5, 1, 3, 3);
        ImmutableIntSet second = new ImmutableIntSet(2, 3, 8);
        System.out.println(first.union(second));
    }
}
