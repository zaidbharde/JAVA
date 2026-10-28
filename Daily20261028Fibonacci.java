/** Computes non-negative Fibonacci numbers with fast doubling. */
public final class Daily20261028Fibonacci {
    private Daily20261028Fibonacci() { }
    public static long value(int n) {
        if (n < 0 || n > 92) throw new IllegalArgumentException("n must be from 0 to 92");
        long a = 0, b = 1;
        if (n == 0) return a;
        int highest = 31 - Integer.numberOfLeadingZeros(n);
        for (int bit = highest; bit >= 0; bit--) {
            long c = a * (2 * b - a), d = a * a + b * b;
            if (((n >> bit) & 1) == 0) { a = c; b = d; }
            else { a = d; b = c + d; }
        }
        return a;
    }
    public static void main(String[] args) { System.out.println(value(50)); }
}
