import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Rounds monetary amounts to a chosen number of fractional digits using an explicit policy. */
public final class Daily20261028Extra016DecimalRounding {
    private static final int MAX_SCALE = 10_000;

    private Daily20261028Extra016DecimalRounding() {}

    /** Returns a rounded copy; the input BigDecimal is never modified. */
    public static BigDecimal round(BigDecimal amount, int scale, RoundingMode policy) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(policy, "policy");
        if (scale < 0 || scale > MAX_SCALE) {
            throw new IllegalArgumentException("scale must be between 0 and " + MAX_SCALE);
        }
        return amount.setScale(scale, policy);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected validation failure");
        } catch (NullPointerException | IllegalArgumentException | ArithmeticException expected) {
            // Expected invalid argument.
        }
    }

    public static void main(String[] args) {
        check(round(new BigDecimal("12.345"), 2, RoundingMode.HALF_UP)
                .equals(new BigDecimal("12.35")), "half-up rounding");
        check(round(new BigDecimal("-12.345"), 2, RoundingMode.HALF_EVEN)
                .equals(new BigDecimal("-12.34")), "half-even negative tie");
        check(round(new BigDecimal("9.999"), 2, RoundingMode.DOWN)
                .equals(new BigDecimal("9.99")), "truncate toward zero");
        check(round(new BigDecimal("1.2"), 4, RoundingMode.UNNECESSARY)
                .equals(new BigDecimal("1.2000")), "scale without loss");
        expectFailure(() -> round(null, 2, RoundingMode.HALF_UP));
        expectFailure(() -> round(BigDecimal.ONE, -1, RoundingMode.HALF_UP));
        expectFailure(() -> round(BigDecimal.ONE, 2, null));
        expectFailure(() -> round(new BigDecimal("1.001"), 2, RoundingMode.UNNECESSARY));
        System.out.println("DecimalRounding self-check passed");
    }
}
