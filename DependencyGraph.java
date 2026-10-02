import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Directed dependency graph with cycle detection and stable topological order. */
public final class DependencyGraph {
    private final Map<String, Set<String>> prerequisites = new HashMap<>();

    public void addTask(String task) {
        prerequisites.computeIfAbsent(task, ignored -> new HashSet<>());
    }

    public void dependsOn(String task, String prerequisite) {
        addTask(task);
        addTask(prerequisite);
        prerequisites.get(task).add(prerequisite);
    }

    public List<String> order() {
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> dependents = new HashMap<>();
        for (String task : prerequisites.keySet()) {
            indegree.put(task, prerequisites.get(task).size());
            dependents.put(task, new ArrayList<>());
        }
        for (Map.Entry<String, Set<String>> entry : prerequisites.entrySet()) {
            for (String prerequisite : entry.getValue()) {
                dependents.get(prerequisite).add(entry.getKey());
            }
        }
        ArrayDeque<String> ready = new ArrayDeque<>();
        indegree.forEach((task, degree) -> { if (degree == 0) ready.add(task); });
        List<String> result = new ArrayList<>();
        while (!ready.isEmpty()) {
            String task = ready.removeFirst();
            result.add(task);
            for (String dependent : dependents.get(task)) {
                int degree = indegree.merge(dependent, -1, Integer::sum);
                if (degree == 0) ready.addLast(dependent);
            }
        }
        if (result.size() != prerequisites.size()) {
            throw new IllegalStateException("dependency cycle detected");
        }
        Collections.sort(result);
        return result;
    }
}
