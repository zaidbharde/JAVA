import java.util.*;

public class Dijkstra {

    private record Edge(int target, double weight) {}

    private record Result(double[] distances, int[] previous) {}

    private final int vertices;
    private final List<List<Edge>> adj;

    public Dijkstra(int vertices) {
        this.vertices = vertices;
        this.adj      = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    public void addEdge(int from, int to, double weight) {
        adj.get(from).add(new Edge(to, weight));
        adj.get(to).add(new Edge(from, weight));
    }

    public Result shortestPath(int source) {
        double[] dist = new double[vertices];
        int[]    prev = new int[vertices];
        boolean[] visited = new boolean[vertices];
        Arrays.fill(dist, Double.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(
            Comparator.comparingDouble(a -> dist[a[0]]));
        pq.offer(new int[]{source});

        while (!pq.isEmpty()) {
            int u = pq.poll()[0];
            if (visited[u]) continue;
            visited[u] = true;

            for (Edge e : adj.get(u)) {
                double newDist = dist[u] + e.weight;
                if (newDist < dist[e.target]) {
                    dist[e.target] = newDist;
                    prev[e.target] = u;
                    pq.offer(new int[]{e.target});
                }
            }
        }
        return new Result(dist, prev);
    }

    public List<Integer> getPath(int[] prev, int target) {
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = prev[at])
            path.add(0, at);
        return path;
    }

    public void printGraph() {
        System.out.println("\n  Adjacency List:");
        for (int i = 0; i < vertices; i++) {
            System.out.printf("  %d →", i);
            for (Edge e : adj.get(i))
                System.out.printf(" %d(%.1f)", e.target, e.weight);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(48));
        System.out.println("  Dijkstra's Shortest Path");
        System.out.println("=".repeat(48));

        Dijkstra g = new Dijkstra(7);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 6);
        g.addEdge(1, 3, 5);
        g.addEdge(2, 3, 8);
        g.addEdge(3, 4, 10);
        g.addEdge(3, 5, 15);
        g.addEdge(4, 6, 2);
        g.addEdge(5, 6, 6);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 5, 9);

        g.printGraph();

        for (int src : new int[]{0, 3}) {
            Result r = g.shortestPath(src);
            System.out.printf("\n  Shortest paths from %d:%n", src);
            System.out.printf("  %-6s %-10s %-s%n", "Node", "Distance", "Path");
            System.out.println("  " + "─".repeat(40));

            for (int i = 0; i < 7; i++) {
                if (r.distances[i] == Double.MAX_VALUE) {
                    System.out.printf("  %-6d %-10s %-s%n", i, "INF", "unreachable");
                } else {
                    List<Integer> path = g.getPath(r.previous, i);
                    System.out.printf("  %-6d %-10.1f %-s%n", i, r.distances[i], path);
                }
            }
        }
    }
}
