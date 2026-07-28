import java.util.*;

public class TopologicalSort {
    public static List<Integer> sort(int nodes, List<int[]> edges) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[nodes];
        for (int i = 0; i < nodes; i++) graph.add(new ArrayList<>());

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            inDegree[edge[1]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < nodes; i++)
            if (inDegree[i] == 0) queue.add(i);

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            result.add(curr);
            for (int neighbor : graph.get(curr)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) queue.add(neighbor);
            }
        }

        if (result.size() != nodes) throw new RuntimeException("Cycle detected!");
        return result;
    }

    public static void main(String[] args) {
        List<int[]> edges = new ArrayList<>();
        edges.add(new int[]{0, 1});
        edges.add(new int[]{0, 2});
        edges.add(new int[]{1, 3});
        edges.add(new int[]{2, 3});
        System.out.println("Order: " + sort(4, edges));
    }
}
