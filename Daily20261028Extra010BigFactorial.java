import java.math.BigInteger;

/** Computes nonnegative integer factorials without primitive-integer overflow. */
public final class Daily20261028Extra010BigFactorial {
    private Daily20261028Extra010BigFactorial() { }

    /** Returns n!; negative inputs are rejected. */
    public static BigInteger factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be nonnegative");
        BigInteger result = BigInteger.ONE;
        for (int factor = 2; factor <= n; factor++) {
            result = result.multiply(BigInteger.valueOf(factor));
        }
        return result;
    }

    public static void main(String[] args) {
        if (!factorial(0).equals(BigInteger.ONE)
                || !factorial(1).equals(BigInteger.ONE)
                || !factorial(5).equals(BigInteger.valueOf(120))
                || !factorial(20).equals(new BigInteger("2432902008176640000"))
                || !factorial(100).toString().startsWith("933262154439")) {
            throw new AssertionError("Factorial self-check failed");
        }
        try {
            factorial(-1);
            throw new AssertionError("Negative input was accepted");
        } catch (IllegalArgumentException expected) {
            // Expected validation behavior.
        }
        System.out.println("BigFactorial self-check passed");
    }
}
