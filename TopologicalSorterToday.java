import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Returns a stable topological ordering or rejects cyclic dependencies. */
public final class TopologicalSorterToday {
    public static List<String> sort(Map<String, List<String>> dependencies) {
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> outgoing = new HashMap<>();
        for (String node : dependencies.keySet()) {
            indegree.putIfAbsent(node, 0);
            outgoing.putIfAbsent(node, new ArrayList<>());
            for (String dependency : dependencies.getOrDefault(node, List.of())) {
                indegree.putIfAbsent(dependency, 0);
                outgoing.computeIfAbsent(dependency, ignored -> new ArrayList<>()).add(node);
                indegree.put(node, indegree.get(node) + 1);
            }
        }
        ArrayDeque<String> ready = new ArrayDeque<>();
        indegree.forEach((node, degree) -> { if (degree == 0) ready.add(node); });
        List<String> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            String node = ready.removeFirst();
            order.add(node);
            for (String dependent : outgoing.getOrDefault(node, Collections.emptyList())) {
                int remaining = indegree.merge(dependent, -1, Integer::sum);
                if (remaining == 0) ready.addLast(dependent);
            }
        }
        if (order.size() != indegree.size()) throw new IllegalArgumentException("dependency cycle");
        return order;
    }
}
