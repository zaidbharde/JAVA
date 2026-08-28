public class MiniSnippet018 {
    public static void main(String[] args) {
        boolean prime = 18 > 1;
        for (int d = 2; d * d <= 18; d++) if (18 % d == 0) prime = false;
        System.out.println(prime);
    }
}
