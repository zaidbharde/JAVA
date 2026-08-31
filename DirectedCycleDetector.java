import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectedCycleDetector {
    private enum Mark { NEW, ACTIVE, DONE }

    public static List<String> findCycle(Map<String, List<String>> graph) {
        Map<String, Mark> marks = new HashMap<>();
        Deque<String> path = new ArrayDeque<>();
        for (String node : graph.keySet()) {
            List<String> cycle = visit(node, graph, marks, path);
            if (!cycle.isEmpty()) return cycle;
        }
        return Collections.emptyList();
    }

    private static List<String> visit(String node, Map<String, List<String>> graph,
                                      Map<String, Mark> marks, Deque<String> path) {
        Mark mark = marks.getOrDefault(node, Mark.NEW);
        if (mark == Mark.DONE) return Collections.emptyList();
        if (mark == Mark.ACTIVE) {
            List<String> cycle = new ArrayList<>();
            for (String item : path) cycle.add(item);
            cycle.add(node);
            return cycle;
        }
        marks.put(node, Mark.ACTIVE);
        path.addLast(node);
        for (String next : graph.getOrDefault(node, List.of())) {
            List<String> cycle = visit(next, graph, marks, path);
            if (!cycle.isEmpty()) return cycle;
        }
        path.removeLast();
        marks.put(node, Mark.DONE);
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        Map<String, List<String>> graph = Map.of("compile", List.of("test"),
                "test", List.of("package"), "package", List.of("compile"));
        System.out.println(findCycle(graph));
    }
}
