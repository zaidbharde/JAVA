import java.util.*;

/** Produces stable topological layers for tasks with prerequisite edges. */
public final class Daily20261021DependencyLayers {
    public static <T> List<List<T>> plan(Map<T, ? extends Collection<T>> dependencies) {
        Map<T, Set<T>> remaining = new LinkedHashMap<>();
        for (T node : dependencies.keySet()) remaining.put(node, new LinkedHashSet<>(dependencies.get(node)));
        dependencies.values().forEach(values -> values.forEach(node -> remaining.putIfAbsent(node, new LinkedHashSet<>())));
        List<List<T>> layers = new ArrayList<>();
        while (!remaining.isEmpty()) {
            List<T> ready = remaining.entrySet().stream()
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey).sorted(Comparator.comparing(String::valueOf)).toList();
            if (ready.isEmpty()) throw new IllegalArgumentException("dependency cycle detected");
            layers.add(ready);
            for (T node : ready) remaining.remove(node);
            for (Set<T> prerequisites : remaining.values()) prerequisites.removeAll(ready);
        }
        return layers;
    }

    public static <T> List<T> flatten(Map<T, ? extends Collection<T>> dependencies) {
        return plan(dependencies).stream().flatMap(Collection::stream).toList();
    }
}
