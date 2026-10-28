/** Tests whether a non-negative integer reads the same in either direction. */
public final class Daily20261028IntegerPalindrome {
    private Daily20261028IntegerPalindrome() { }
    public static boolean test(int value) {
        if (value < 0 || value != 0 && value % 10 == 0) return false;
        int reversedHalf = 0;
        while (value > reversedHalf) {
            reversedHalf = reversedHalf * 10 + value % 10;
            value /= 10;
        }
        return value == reversedHalf || value == reversedHalf / 10;
    }
    public static void main(String[] args) { System.out.println(test(12321) + ", " + test(123)); }
}
