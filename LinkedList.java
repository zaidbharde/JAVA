/**
 * Singly Linked List with common operations.
 */
public class LinkedList {

    // ── Node ──────────────────────────────────────────────────────────
    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // ── Fields ────────────────────────────────────────────────────────
    private Node head;
    private int size;

    // ── Operations ────────────────────────────────────────────────────

    /** Append a value to the end of the list. */
    public void add(int value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /** Remove the first occurrence of value. Returns true if removed. */
    public boolean remove(int value) {
        if (head == null) return false;

        if (head.data == value) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data == value) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /** Reverse the list in place. */
    public void reverse() {
        Node prev = null;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }

    public int size()     { return size; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(" → ");
            current = current.next;
        }
        return sb.append("]").toString();
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        LinkedList list = new LinkedList();

        for (int v : new int[]{10, 20, 30, 40, 50}) list.add(v);
        System.out.println("Built   : " + list);

        list.remove(30);
        System.out.println("Removed : " + list);

        list.reverse();
        System.out.println("Reversed: " + list);

        System.out.println("Size    : " + list.size());
    }
}
