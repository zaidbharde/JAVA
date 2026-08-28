public class MiniSnippet028 {
    public static void main(String[] args) {
        boolean prime = 28 > 1;
        for (int d = 2; d * d <= 28; d++) if (28 % d == 0) prime = false;
        System.out.println(prime);
    }
}
