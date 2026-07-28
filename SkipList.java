import java.util.Random;

public class SkipList {
    private static final int MAX_LEVEL = 6;
    private final Node head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
    private int level = 0;
    private final Random rand = new Random();

    static class Node {
        int value;
        Node[] forward;
        Node(int value, int level) {
            this.value = value;
            forward = new Node[level + 1];
        }
    }

    private int randomLevel() {
        int lvl = 0;
        while (rand.nextDouble() < 0.5 && lvl < MAX_LEVEL) lvl++;
        return lvl;
    }

    public void insert(int value) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node curr = head;
        for (int i = level; i >= 0; i--) {
            while (curr.forward[i] != null && curr.forward[i].value < value)
                curr = curr.forward[i];
            update[i] = curr;
        }
        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level + 1; i <= newLevel; i++) update[i] = head;
            level = newLevel;
        }
        Node newNode = new Node(value, newLevel);
        for (int i = 0; i <= newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    public boolean search(int value) {
        Node curr = head;
        for (int i = level; i >= 0; i--) {
            while (curr.forward[i] != null && curr.forward[i].value < value)
                curr = curr.forward[i];
        }
        curr = curr.forward[0];
        return curr != null && curr.value == value;
    }

    public static void main(String[] args) {
        SkipList sl = new SkipList();
        int[] vals = {3, 6, 7, 9, 12, 19, 17};
        for (int v : vals) sl.insert(v);
        System.out.println("Search 9: " + sl.search(9));
        System.out.println("Search 15: " + sl.search(15));
    }
}
