public class MiniSnippet013 {
    public static void main(String[] args) {
        boolean prime = 13 > 1;
        for (int d = 2; d * d <= 13; d++) if (13 % d == 0) prime = false;
        System.out.println(prime);
    }
}
