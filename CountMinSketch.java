import java.util.*;

public class CountMinSketch {
    private final int[][] table;
    private final int width, depth;
    private final int[] seeds;

    public CountMinSketch(int width, int depth) {
        this.width = width;
        this.depth = depth;
        table = new int[depth][width];
        seeds = new int[depth];
        Random rand = new Random(42);
        for (int i = 0; i < depth; i++) seeds[i] = rand.nextInt();
    }

    private int hash(String item, int seed) {
        return Math.abs((item.hashCode() ^ seed) % width);
    }

    public void add(String item) {
        for (int i = 0; i < depth; i++) {
            table[i][hash(item, seeds[i])]++;
        }
    }

    public int estimate(String item) {
        int minCount = Integer.MAX_VALUE;
        for (int i = 0; i < depth; i++) {
            minCount = Math.min(minCount, table[i][hash(item, seeds[i])]);
        }
        return minCount;
    }

    public static void main(String[] args) {
        CountMinSketch cms = new CountMinSketch(100, 5);
        String[] items = {"apple", "banana", "apple", "apple", "cherry", "banana"};
        for (String item : items) cms.add(item);

        System.out.println("apple count estimate: " + cms.estimate("apple"));
        System.out.println("banana count estimate: " + cms.estimate("banana"));
        System.out.println("mango count estimate: " + cms.estimate("mango"));
    }
}
