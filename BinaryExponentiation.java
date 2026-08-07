public class BinaryExponentiation {
    public static long power(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            exp >>= 1;
            base = (base * base) % mod;
        }
        return result;
    }

    public static void main(String[] args) {
        long base = 2, exp = 1000000, mod = 1000000007;
        System.out.println(base + "^" + exp + " mod " + mod + " = " + power(base, exp, mod));
    }
}
