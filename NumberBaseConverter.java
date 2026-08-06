/**
 * Number Base Converter — convert integers between decimal, binary,
 * octal, and hexadecimal with a step-by-step breakdown.
 */
public class NumberBaseConverter {

    // ── Conversions ───────────────────────────────────────────────────
    public static String toBase(int decimal, int base) {
        if (base < 2 || base > 16)
            throw new IllegalArgumentException("Base must be between 2 and 16.");
        if (decimal == 0) return "0";

        StringBuilder result = new StringBuilder();
        boolean negative = decimal < 0;
        long n = Math.abs((long) decimal);

        while (n > 0) {
            int remainder = (int)(n % base);
            result.append(remainder < 10
                ? (char)('0' + remainder)
                : (char)('A' + remainder - 10));
            n /= base;
        }

        if (negative) result.append('-');
        return result.reverse().toString();
    }

    public static int fromBase(String value, int base) {
        return Integer.parseInt(value, base);
    }

    // ── Step breakdown ────────────────────────────────────────────────
    public static void showSteps(int decimal, int base) {
        System.out.println("\n  Converting " + decimal + " to base " + base + ":");
        System.out.println("  " + "─".repeat(36));
        System.out.printf("  %-10s %-10s %-10s%n", "Number", "÷ Base", "Remainder");
        System.out.println("  " + "─".repeat(36));

        long n = Math.abs((long) decimal);
        while (n > 0) {
            long quotient  = n / base;
            long remainder = n % base;
            System.out.printf("  %-10d %-10d %-10d%n", n, quotient, remainder);
            n = quotient;
        }
        System.out.println("  " + "─".repeat(36));
        System.out.println("  Result (read remainders bottom-up): "
            + toBase(decimal, base));
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        int[] numbers = {255, 1024, 42, 0, -15};

        System.out.println("=".repeat(52));
        System.out.println("  Number Base Converter");
        System.out.println("=".repeat(52));
        System.out.printf("  %-8s %-12s %-10s %-8s %-8s%n",
            "Decimal", "Binary", "Octal", "Hex", "Base-5");
        System.out.println("  " + "─".repeat(48));

        for (int num : numbers) {
            System.out.printf("  %-8d %-12s %-10s %-8s %-8s%n",
                num,
                toBase(num, 2),
                toBase(num, 8),
                toBase(num, 16),
                toBase(num, 5));
        }

        // Step-by-step for one number
        showSteps(255, 2);
    }
}
