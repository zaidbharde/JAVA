/** Inclusive integer range with size and membership helpers. */
public final class Daily20261028IntRange {
    private final int start, end;
    public Daily20261028IntRange(int start, int end) {
        if (end < start) throw new IllegalArgumentException("end before start");
        this.start = start; this.end = end;
    }
    public boolean contains(int value) { return value >= start && value <= end; }
    public long size() { return (long) end - start + 1; }
    public int clamp(int value) { return Math.max(start, Math.min(end, value)); }
    public static void main(String[] args) {
        Daily20261028IntRange r = new Daily20261028IntRange(3, 7);
        System.out.println(r.contains(5) + ", size=" + r.size());
    }
}
