import java.util.*;

public class LruCacheDemo {
    public static void main(String[] args) {
        LinkedHashMap<String, Integer> cache = new LinkedHashMap<>(3, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, Integer> entry) {
                return size() > 3;
            }
        };
        cache.put("red", 7);
        cache.put("blue", 4);
        cache.put("gold", 9);
        cache.get("red");
        cache.put("green", 2);
        System.out.println(cache);
    }
}
