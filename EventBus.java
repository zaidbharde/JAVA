import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class EventBus {

    private final Map<String, List<Subscriber<?>>> subscribers = new ConcurrentHashMap<>();
    private final List<String> history = Collections.synchronizedList(new ArrayList<>());

    record Subscriber<T>(Consumer<T> handler, Class<T> type, boolean once, int priority) {}

    @SuppressWarnings("unchecked")
    public <T> Runnable on(String event, Class<T> type, Consumer<T> handler, int priority) {
        subscribers.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>());
        Subscriber<T> sub = new Subscriber<>(handler, type, false, priority);
        subscribers.get(event).add(sub);
        subscribers.get(event).sort((a, b) -> b.priority() - a.priority());
        return () -> subscribers.get(event).remove(sub);
    }

    public <T> void once(String event, Class<T> type, Consumer<T> handler) {
        subscribers.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>());
        subscribers.get(event).add(new Subscriber<>(handler, type, true, 0));
    }

    @SuppressWarnings("unchecked")
    public <T> void emit(String event, T data) {
        history.add(event + ": " + data);
        List<Subscriber<?>> subs = subscribers.get(event);
        if (subs == null) return;

        List<Subscriber<?>> toRemove = new ArrayList<>();
        for (Subscriber<?> sub : subs) {
            ((Consumer<T>) sub.handler()).accept(data);
            if (sub.once()) toRemove.add(sub);
        }
        subs.removeAll(toRemove);
    }

    public int listenerCount(String event) {
        return subscribers.getOrDefault(event, List.of()).size();
    }

    public List<String> getHistory() { return new ArrayList<>(history); }

    public static void main(String[] args) {
        EventBus bus = new EventBus();

        System.out.println("=".repeat(44));
        System.out.println("  Event Bus");
        System.out.println("=".repeat(44));

        Runnable unsub = bus.on("login", String.class, user ->
            System.out.println("  [AUTH] " + user + " logged in"), 10);

        bus.on("login", String.class, user ->
            System.out.println("  [LOG]  login event: " + user), 5);

        bus.once("signup", String.class, user ->
            System.out.println("  [ONCE] Welcome " + user + "!"));

        bus.on("order", Map.class, data ->
            System.out.println("  [ORDER] " + data), 0);

        System.out.println("\n  Emitting events...\n");

        bus.emit("login", "Alice");
        bus.emit("login", "Bob");
        bus.emit("signup", "Charlie");
        bus.emit("signup", "Diana");
        bus.emit("order", Map.of("id", "ORD-1", "total", 99.99));

        unsub.run();
        System.out.println("\n  After unsubscribe:");
        bus.emit("login", "Eve");

        System.out.println("\n  Listener counts:");
        System.out.println("    login  : " + bus.listenerCount("login"));
        System.out.println("    signup : " + bus.listenerCount("signup"));

        System.out.println("\n  Event history:");
        bus.getHistory().forEach(e -> System.out.println("    " + e));
    }
}
