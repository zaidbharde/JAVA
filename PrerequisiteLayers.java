import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Produces topological layers for tasks whose prerequisites are already known. */
public final class PrerequisiteLayers {
    private PrerequisiteLayers() {
    }

    public static List<List<String>> build(Map<String, Set<String>> prerequisites) {
        Map<String, Set<String>> remaining = new HashMap<>();
        prerequisites.forEach((task, deps) -> remaining.put(task, new HashSet<>(deps)));
        prerequisites.values().forEach(deps -> deps.forEach(dep -> remaining.putIfAbsent(dep, new HashSet<>())));
        List<List<String>> layers = new ArrayList<>();
        while (!remaining.isEmpty()) {
            List<String> ready = remaining.entrySet().stream()
                    .filter(entry -> entry.getValue().isEmpty())
                    .map(Map.Entry::getKey).sorted().toList();
            if (ready.isEmpty()) throw new IllegalArgumentException("dependency cycle detected");
            layers.add(ready);
            ready.forEach(remaining::remove);
            remaining.values().forEach(deps -> deps.removeAll(ready));
        }
        return layers;
    }
}
