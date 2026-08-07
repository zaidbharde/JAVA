import java.util.*;

public class SuffixArray {
    public static Integer[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] suffixIndices = new Integer[n];
        for (int i = 0; i < n; i++) suffixIndices[i] = i;

        Arrays.sort(suffixIndices, (a, b) -> s.substring(a).compareTo(s.substring(b)));
        return suffixIndices;
    }

    public static void main(String[] args) {
        String s = "banana";
        Integer[] sa = buildSuffixArray(s);
        for (int idx : sa) {
            System.out.println(idx + ": " + s.substring(idx));
        }
    }
}
