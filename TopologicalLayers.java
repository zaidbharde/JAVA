import java.util.*;

/** Groups a directed acyclic graph into dependency-ready layers. */
public final class TopologicalLayers {
    private TopologicalLayers() { }

    public static <T> List<List<T>> build(Map<T, ? extends Collection<T>> graph) {
        Map<T, Integer> indegree = new LinkedHashMap<>();
        for (Map.Entry<T, ? extends Collection<T>> entry : graph.entrySet()) {
            indegree.putIfAbsent(entry.getKey(), 0);
            for (T child : entry.getValue()) {
                indegree.merge(child, 1, Integer::sum);
            }
        }
        List<T> ready = new ArrayList<>();
        indegree.forEach((node, degree) -> { if (degree == 0) ready.add(node); });
        Collections.sort(ready, Comparator.comparing(String::valueOf));
        List<List<T>> layers = new ArrayList<>();
        int visited = 0;
        while (!ready.isEmpty()) {
            List<T> layer = new ArrayList<>(ready);
            layers.add(Collections.unmodifiableList(layer));
            ready = new ArrayList<>();
            for (T node : layer) {
                visited++;
                for (T child : graph.getOrDefault(node, List.of())) {
                    if (indegree.merge(child, -1, Integer::sum) == 0) ready.add(child);
                }
            }
            ready.sort(Comparator.comparing(String::valueOf));
        }
        if (visited != indegree.size()) throw new IllegalArgumentException("graph contains a cycle");
        return Collections.unmodifiableList(layers);
    }
}
