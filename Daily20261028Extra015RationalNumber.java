import java.math.BigInteger;
import java.util.Objects;

/** Immutable, normalized exact rational number. */
public final class Daily20261028Extra015RationalNumber implements Comparable<Daily20261028Extra015RationalNumber> {
    private final BigInteger numerator;
    private final BigInteger denominator;

    public Daily20261028Extra015RationalNumber(BigInteger numerator, BigInteger denominator) {
        Objects.requireNonNull(numerator, "numerator");
        Objects.requireNonNull(denominator, "denominator");
        if (denominator.signum() == 0) throw new IllegalArgumentException("denominator must be nonzero");
        BigInteger gcd = numerator.gcd(denominator);
        BigInteger n = numerator.divide(gcd), d = denominator.divide(gcd);
        if (d.signum() < 0) { n = n.negate(); d = d.negate(); }
        this.numerator = n;
        this.denominator = d;
    }

    public Daily20261028Extra015RationalNumber(long numerator, long denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public BigInteger numerator() { return numerator; }
    public BigInteger denominator() { return denominator; }

    @Override public int compareTo(Daily20261028Extra015RationalNumber other) {
        Objects.requireNonNull(other, "other");
        return numerator.multiply(other.denominator).compareTo(other.numerator.multiply(denominator));
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Daily20261028Extra015RationalNumber)) return false;
        Daily20261028Extra015RationalNumber other = (Daily20261028Extra015RationalNumber) obj;
        return numerator.equals(other.numerator) && denominator.equals(other.denominator);
    }

    @Override public int hashCode() { return Objects.hash(numerator, denominator); }
    @Override public String toString() { return numerator + "/" + denominator; }

    public static void main(String[] args) {
        var half = new Daily20261028Extra015RationalNumber(2, 4);
        var alsoHalf = new Daily20261028Extra015RationalNumber(-3, -6);
        assert half.equals(alsoHalf) && half.toString().equals("1/2");
        assert new Daily20261028Extra015RationalNumber(1, -3).compareTo(new Daily20261028Extra015RationalNumber(-1, 4)) < 0;
        assert new Daily20261028Extra015RationalNumber(0, Long.MIN_VALUE).toString().equals("0/1");
        try { new Daily20261028Extra015RationalNumber(1, 0); throw new AssertionError("zero denominator accepted"); }
        catch (IllegalArgumentException expected) { }
        System.out.println("RationalNumber self-check passed");
    }
}
