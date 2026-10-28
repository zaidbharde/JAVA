/** Computes sum, minimum, maximum, and mean in one pass. */
public final class Daily20261028ArrayStats {
    public final long sum;
    public final int min, max;
    public final double mean;
    private Daily20261028ArrayStats(long sum, int min, int max, double mean) {
        this.sum = sum; this.min = min; this.max = max; this.mean = mean;
    }
    public static Daily20261028ArrayStats of(int[] values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException("non-empty array required");
        long sum = 0; int min = values[0], max = values[0];
        for (int v : values) { sum += v; min = Math.min(min, v); max = Math.max(max, v); }
        return new Daily20261028ArrayStats(sum, min, max, sum / (double) values.length);
    }
    public static void main(String[] args) { Daily20261028ArrayStats s = of(new int[]{2, 4, 6}); System.out.println(s.sum + ", mean=" + s.mean); }
}
