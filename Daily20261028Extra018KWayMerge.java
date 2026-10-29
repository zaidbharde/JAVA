import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** Efficiently merges multiple ascending integer lists using a min-heap. */
public final class Daily20261028Extra018KWayMerge {
    private Daily20261028Extra018KWayMerge() { }

    private static final class Entry {
        final int value, listIndex, elementIndex;
        Entry(int value, int listIndex, int elementIndex) {
            this.value = value;
            this.listIndex = listIndex;
            this.elementIndex = elementIndex;
        }
    }

    /** Returns a new sorted list; rejects null inputs and lists that are not ascending. */
    public static List<Integer> merge(List<? extends List<Integer>> lists) {
        if (lists == null) throw new IllegalArgumentException("lists must not be null");
        PriorityQueue<Entry> heap = new PriorityQueue<>(Comparator.comparingInt(e -> e.value));
        long total = 0;
        for (int i = 0; i < lists.size(); i++) {
            List<Integer> list = lists.get(i);
            if (list == null) throw new IllegalArgumentException("input list must not be null");
            total += list.size();
            for (int j = 0; j < list.size(); j++) {
                Integer value = list.get(j);
                if (value == null || (j > 0 && value < list.get(j - 1)))
                    throw new IllegalArgumentException("lists must contain ascending, non-null integers");
            }
            if (!list.isEmpty()) heap.add(new Entry(list.get(0), i, 0));
        }
        if (total > Integer.MAX_VALUE) throw new IllegalArgumentException("merged result is too large");
        List<Integer> result = new ArrayList<>((int) total);
        while (!heap.isEmpty()) {
            Entry current = heap.remove();
            result.add(current.value);
            int next = current.elementIndex + 1;
            List<Integer> source = lists.get(current.listIndex);
            if (next < source.size()) heap.add(new Entry(source.get(next), current.listIndex, next));
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> merged = merge(List.of(List.of(-2, 4, 10), List.of(-2, 3), List.of(), List.of(1, 8)));
        if (!merged.equals(List.of(-2, -2, 1, 3, 4, 8, 10))) throw new AssertionError(merged);
        if (!merge(List.of()).isEmpty()) throw new AssertionError("empty input");
        try {
            merge(List.of(List.of(2, 1)));
            throw new AssertionError("unsorted input accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("KWayMerge self-check passed");
    }
}
