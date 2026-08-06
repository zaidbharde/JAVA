import java.util.*;

public class HuffmanCoding {
    static class Node {
        char ch; int freq;
        Node left, right;
        Node(char ch, int freq) { this.ch = ch; this.freq = freq; }
    }

    public static void main(String[] args) {
        String text = "huffman coding example";
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) freqMap.merge(c, 1, Integer::sum);

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.freq - b.freq);
        for (var entry : freqMap.entrySet())
            pq.add(new Node(entry.getKey(), entry.getValue()));

        while (pq.size() > 1) {
            Node left = pq.poll(), right = pq.poll();
            Node parent = new Node('\0', left.freq + right.freq);
            parent.left = left; parent.right = right;
            pq.add(parent);
        }

        Map<Character, String> codes = new HashMap<>();
        buildCodes(pq.poll(), "", codes);
        codes.forEach((k, v) -> System.out.println(k + " : " + v));
    }

    static void buildCodes(Node node, String code, Map<Character, String> codes) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            codes.put(node.ch, code);
            return;
        }
        buildCodes(node.left, code + "0", codes);
        buildCodes(node.right, code + "1", codes);
    }
}
