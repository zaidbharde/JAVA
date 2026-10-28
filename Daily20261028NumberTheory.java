/** Euclidean GCD and overflow-checked LCM helpers. */
public final class Daily20261028NumberTheory {
    private Daily20261028NumberTheory() { }
    public static long gcd(long a, long b) {
        a = Math.abs(a); b = Math.abs(b);
        while (b != 0) { long t = a % b; a = b; b = t; }
        return a;
    }
    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        return Math.multiplyExact(Math.abs(a / gcd(a, b)), Math.abs(b));
    }
    public static void main(String[] args) { System.out.println("gcd=" + gcd(84, 30) + ", lcm=" + lcm(12, 18)); }
}
