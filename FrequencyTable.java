import java.util.*;

public class FrequencyTable {
    public static void main(String[] args) {
        String input = "green blue green amber blue green";
        Map<String, Integer> counts = new TreeMap<>();
        for (String word : input.split(" ")) {
            counts.merge(word, 1, Integer::sum);
        }
        counts.forEach((word, count) ->
            System.out.println(word + " -> " + count));
    }
}
