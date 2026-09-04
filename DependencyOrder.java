import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Produces a deterministic topological order and rejects dependency cycles. */
public final class DependencyOrder {
    public static List<String> sort(Map<String, List<String>> dependencies) {
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> outgoing = new HashMap<>();
        for (String item : dependencies.keySet()) { indegree.putIfAbsent(item, 0); outgoing.putIfAbsent(item, new ArrayList<>()); }
        for (var entry : dependencies.entrySet()) {
            for (String prerequisite : entry.getValue()) {
                indegree.putIfAbsent(prerequisite, 0);
                outgoing.computeIfAbsent(prerequisite, ignored -> new ArrayList<>()).add(entry.getKey());
                indegree.merge(entry.getKey(), 1, Integer::sum);
            }
        }
        ArrayDeque<String> ready = new ArrayDeque<>();
        indegree.entrySet().stream().filter(e -> e.getValue() == 0).map(Map.Entry::getKey).sorted().forEach(ready::add);
        List<String> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            String item = ready.removeFirst(); order.add(item);
            for (String dependent : outgoing.getOrDefault(item, List.of()).stream().sorted().toList()) {
                if (indegree.merge(dependent, -1, Integer::sum) == 0) ready.addLast(dependent);
            }
        }
        if (order.size() != indegree.size()) throw new IllegalArgumentException("dependency cycle detected");
        return List.copyOf(order);
    }

    public static Set<String> prerequisitesOf(String item, Map<String, List<String>> dependencies) {
        Set<String> result = new HashSet<>();
        collect(item, dependencies, result);
        result.remove(item);
        return Set.copyOf(result);
    }
    private static void collect(String item, Map<String, List<String>> graph, Set<String> seen) {
        if (!seen.add(item)) return;
        for (String prerequisite : graph.getOrDefault(item, List.of())) collect(prerequisite, graph, seen);
    }
}
