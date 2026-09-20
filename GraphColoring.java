import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Greedy graph coloring with an explicit color assignment for each vertex. */
public final class GraphColoring {
    private GraphColoring() {}

    public static int[] color(int[][] graph) {
        int n = graph.length;
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        for (int vertex = 0; vertex < n; vertex++) {
            boolean[] blocked = new boolean[n];
            for (int neighbor : graph[vertex]) {
                if (neighbor < 0 || neighbor >= n) throw new IllegalArgumentException("bad edge");
                if (colors[neighbor] >= 0) blocked[colors[neighbor]] = true;
            }
            int chosen = 0;
            while (chosen < n && blocked[chosen]) chosen++;
            colors[vertex] = chosen;
        }
        return colors;
    }

    public static List<List<Integer>> colorClasses(int[][] graph) {
        int[] colors = color(graph);
        List<List<Integer>> classes = new ArrayList<>();
        for (int vertex = 0; vertex < colors.length; vertex++) {
            while (classes.size() <= colors[vertex]) classes.add(new ArrayList<>());
            classes.get(colors[vertex]).add(vertex);
        }
        return classes;
    }
}

final class GraphColoringExample {
    public static void main(String[] args) {
        int[][] graph = {{1, 2}, {0, 2}, {0, 1, 3}, {2}};
        System.out.println(GraphColoring.colorClasses(graph));
    }
}
