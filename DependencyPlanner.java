import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Produces a dependency order using Kahn's algorithm and reports cycles clearly. */
public final class DependencyPlanner {
    public static List<String> order(Map<String, List<String>> dependencies) {
        Map<String, Integer> incoming = new HashMap<>();
        Map<String, List<String>> outgoing = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
            incoming.putIfAbsent(entry.getKey(), 0);
            for (String prerequisite : entry.getValue()) {
                incoming.putIfAbsent(prerequisite, 0);
                outgoing.computeIfAbsent(prerequisite, ignored -> new ArrayList<>()).add(entry.getKey());
                incoming.merge(entry.getKey(), 1, Integer::sum);
            }
        }
        ArrayDeque<String> ready = new ArrayDeque<>();
        incoming.forEach((name, count) -> { if (count == 0) ready.add(name); });
        List<String> result = new ArrayList<>();
        while (!ready.isEmpty()) {
            String current = ready.removeFirst();
            result.add(current);
            for (String dependent : outgoing.getOrDefault(current, List.of())) {
                int remaining = incoming.merge(dependent, -1, Integer::sum);
                if (remaining == 0) ready.addLast(dependent);
            }
        }
        if (result.size() != incoming.size()) {
            throw new IllegalArgumentException("dependency graph contains a cycle");
        }
        return result;
    }

    public static void main(String[] args) {
        Map<String, List<String>> graph = Map.of(
                "package", List.of("compile", "test"),
                "test", List.of("compile"),
                "compile", List.of("sources"));
        System.out.println(order(graph));
    }
}
