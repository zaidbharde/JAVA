public class MatrixExponentiationFibonacci {
    public static long[][] multiply(long[][] a, long[][] b) {
        long m00 = a[0][0]*b[0][0] + a[0][1]*b[1][0];
        long m01 = a[0][0]*b[0][1] + a[0][1]*b[1][1];
        long m10 = a[1][0]*b[0][0] + a[1][1]*b[1][0];
        long m11 = a[1][0]*b[0][1] + a[1][1]*b[1][1];
        return new long[][]{{m00, m01}, {m10, m11}};
    }

    public static long[][] matrixPower(long[][] base, int exp) {
        long[][] result = {{1, 0}, {0, 1}};
        while (exp > 0) {
            if ((exp & 1) == 1) result = multiply(result, base);
            base = multiply(base, base);
            exp >>= 1;
        }
        return result;
    }

    public static long fibonacci(int n) {
        if (n == 0) return 0;
        long[][] base = {{1, 1}, {1, 0}};
        long[][] result = matrixPower(base, n - 1);
        return result[0][0];
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.print(fibonacci(i) + " ");
        }
        System.out.println();
        System.out.println("Fib(50) = " + fibonacci(50));
    }
}
