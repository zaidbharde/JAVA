import java.util.*;

public class JosephusProblem {

    public interface JosephusSolver {
        int solve(int n, int k);
        String getName();
    }

    public static class RecursiveSolver implements JosephusSolver {
        @Override
        public int solve(int n, int k) {
            if (n == 1) return 1;
            return (solve(n - 1, k) + k - 1) % n + 1;
        }

        @Override
        public String getName() { return "Recursive (O(N))"; }
    }

    public static class IterativeSolver implements JosephusSolver {
        @Override
        public int solve(int n, int k) {
            int survivor = 0;
            for (int i = 2; i <= n; i++) {
                survivor = (survivor + k) % i;
            }
            return survivor + 1;
        }

        @Override
        public String getName() { return "Iterative (O(N) space-efficient)"; }
    }

    public static class SimulationSolver implements JosephusSolver {
        @Override
        public int solve(int n, int k) {
            List<Integer> circle = new LinkedList<>();
            for (int i = 1; i <= n; i++) circle.add(i);

            Iterator<Integer> it = circle.iterator();
            while (circle.size() > 1) {
                int count = 0;
                while (count < k) {
                    if (!it.hasNext()) it = circle.iterator();
                    it.next();
                    count++;
                }
                it.remove();
            }
            return circle.get(0);
        }

        @Override
        public String getName() { return "LinkedList Simulation (O(N*K))"; }
    }

    public static void main(String[] args) {
        int n = 7;
        int k = 3;

        List<JosephusSolver> solvers = List.of(
            new RecursiveSolver(),
            new IterativeSolver(),
            new SimulationSolver()
        );

        System.out.printf("Josephus Problem: n=%d, k=%d%n", n, k);
        System.out.println("=".repeat(40));

        for (JosephusSolver solver : solvers) {
            long start = System.nanoTime();
            int result = solver.solve(n, k);
            long end = System.nanoTime();
            
            System.out.printf("%-30s -> Result: %d (Time: %dns)%n", 
                solver.getName(), result, (end - start));
        }
    }
}
