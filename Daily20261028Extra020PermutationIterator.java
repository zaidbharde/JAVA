import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Iterates distinct lexicographic permutations of a sorted copy, using O(n) state. */
public final class Daily20261028Extra020PermutationIterator implements Iterator<int[]> {
    private final int[] current;
    private boolean available = true;

    /** Input may contain duplicates; each distinct permutation is yielded once. */
    public Daily20261028Extra020PermutationIterator(int[] values) {
        if (values == null) throw new IllegalArgumentException("values must not be null");
        current = values.clone();
        Arrays.sort(current);
    }

    @Override public boolean hasNext() { return available; }

    @Override public int[] next() {
        if (!available) throw new NoSuchElementException("no permutations remain");
        int[] result = current.clone();
        int pivot = current.length - 2;
        while (pivot >= 0 && current[pivot] >= current[pivot + 1]) pivot--;
        if (pivot < 0) available = false;
        else {
            int successor = current.length - 1;
            while (current[successor] <= current[pivot]) successor--;
            swap(pivot, successor);
            for (int left = pivot + 1, right = current.length - 1; left < right; left++, right--)
                swap(left, right);
        }
        return result;
    }

    private void swap(int a, int b) {
        int value = current[a]; current[a] = current[b]; current[b] = value;
    }

    public static void main(String[] args) {
        Daily20261028Extra020PermutationIterator it =
                new Daily20261028Extra020PermutationIterator(new int[]{2, 1, 1});
        StringBuilder seen = new StringBuilder();
        while (it.hasNext()) seen.append(Arrays.toString(it.next())).append(';');
        if (!seen.toString().equals("[1, 1, 2];[1, 2, 1];[2, 1, 1];"))
            throw new AssertionError(seen);
        Daily20261028Extra020PermutationIterator empty =
                new Daily20261028Extra020PermutationIterator(new int[0]);
        if (!empty.hasNext() || empty.next().length != 0 || empty.hasNext())
            throw new AssertionError("empty input handling");
        System.out.println("PermutationIterator self-check passed");
    }
}
