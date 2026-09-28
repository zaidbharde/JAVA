import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Produces dependency layers for tasks that can run in parallel. */
public final class BuildOrderPlanner {
    private BuildOrderPlanner() {}

    public static List<List<Integer>> layers(int taskCount, int[][] dependencies) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] incoming = new int[taskCount];
        for (int i = 0; i < taskCount; i++) graph.add(new ArrayList<>());
        for (int[] edge : dependencies) {
            graph.get(edge[0]).add(edge[1]);
            incoming[edge[1]]++;
        }
        ArrayDeque<Integer> ready = new ArrayDeque<>();
        for (int task = 0; task < taskCount; task++) if (incoming[task] == 0) ready.add(task);
        List<List<Integer>> answer = new ArrayList<>();
        int completed = 0;
        while (!ready.isEmpty()) {
            int layerSize = ready.size();
            List<Integer> layer = new ArrayList<>();
            for (int i = 0; i < layerSize; i++) {
                int task = ready.remove();
                layer.add(task);
                completed++;
                for (int next : graph.get(task)) if (--incoming[next] == 0) ready.add(next);
            }
            answer.add(layer);
        }
        if (completed != taskCount) throw new IllegalArgumentException("dependency cycle");
        return answer;
    }
}
