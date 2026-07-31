import java.util.*;

public class JosephusProblem {
    public static int findSurvivor(int n, int k) {
        List<Integer> people = new ArrayList<>();
        for (int i = 1; i <= n; i++) people.add(i);

        int index = 0;
        while (people.size() > 1) {
            index = (index + k - 1) % people.size();
            System.out.println("Eliminated: " + people.remove(index));
        }
        return people.get(0);
    }

    public static void main(String[] args) {
        int n = 7, k = 3;
        System.out.println("Survivor: " + findSurvivor(n, k));
    }
}
