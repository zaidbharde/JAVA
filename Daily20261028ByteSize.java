/** Formats a byte count using binary units (KiB, MiB, GiB, TiB). */
public final class Daily20261028ByteSize {
    private static final String[] UNITS = {"B", "KiB", "MiB", "GiB", "TiB"};
    private Daily20261028ByteSize() { }
    public static String format(long bytes) {
        if (bytes < 0) throw new IllegalArgumentException("bytes must be non-negative");
        double value = bytes; int unit = 0;
        while (value >= 1024 && unit < UNITS.length - 1) { value /= 1024; unit++; }
        if (unit == 0) return bytes + " B";
        return String.format(java.util.Locale.ROOT, "%.2f %s", value, UNITS[unit]);
    }
    public static void main(String[] args) { System.out.println(format(1536)); }
}
