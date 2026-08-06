import java.math.BigInteger;

public class KaratsubaMultiplication {
    public static BigInteger multiply(BigInteger x, BigInteger y) {
        int n = Math.max(x.bitLength(), y.bitLength());
        if (n <= 64) return x.multiply(y);

        int half = (n / 2) + (n % 2);

        BigInteger[] xParts = x.shiftRight(half).and(BigInteger.ONE.shiftLeft(half).subtract(BigInteger.ONE))
                .equals(x) ? null : null; // placeholder not used

        BigInteger mask = BigInteger.ONE.shiftLeft(half).subtract(BigInteger.ONE);
        BigInteger xLow = x.and(mask), xHigh = x.shiftRight(half);
        BigInteger yLow = y.and(mask), yHigh = y.shiftRight(half);

        BigInteger p1 = multiply(xHigh, yHigh);
        BigInteger p2 = multiply(xLow, yLow);
        BigInteger p3 = multiply(xHigh.add(xLow), yHigh.add(yLow));

        return p1.shiftLeft(2 * half)
                .add(p3.subtract(p1).subtract(p2).shiftLeft(half))
                .add(p2);
    }

    public static void main(String[] args) {
        BigInteger a = new BigInteger("123456789123456789");
        BigInteger b = new BigInteger("987654321987654321");
        System.out.println("Karatsuba: " + multiply(a, b));
        System.out.println("Direct:    " + a.multiply(b));
    }
}
