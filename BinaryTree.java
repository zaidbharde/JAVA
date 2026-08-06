/**
 * Binary Search Tree — insert, search, delete, and four traversals.
 */
public class BinaryTree {

    // ── Node ──────────────────────────────────────────────────────────
    private static class Node {
        int   value;
        Node  left, right;

        Node(int value) { this.value = value; }
    }

    // ── Fields ────────────────────────────────────────────────────────
    private Node root;

    // ── Insert ────────────────────────────────────────────────────────
    public void insert(int value) {
        root = insertRec(root, value);
    }

    private Node insertRec(Node node, int value) {
        if (node == null) return new Node(value);
        if (value < node.value)      node.left  = insertRec(node.left,  value);
        else if (value > node.value) node.right = insertRec(node.right, value);
        return node;   // duplicate: ignore
    }

    // ── Search ────────────────────────────────────────────────────────
    public boolean search(int value) {
        return searchRec(root, value);
    }

    private boolean searchRec(Node node, int value) {
        if (node == null)            return false;
        if (value == node.value)     return true;
        return value < node.value
            ? searchRec(node.left,  value)
            : searchRec(node.right, value);
    }

    // ── Delete ────────────────────────────────────────────────────────
    public void delete(int value) {
        root = deleteRec(root, value);
    }

    private Node deleteRec(Node node, int value) {
        if (node == null) return null;

        if (value < node.value) {
            node.left  = deleteRec(node.left,  value);
        } else if (value > node.value) {
            node.right = deleteRec(node.right, value);
        } else {
            // Node to delete found
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;

            // Two children: replace with in-order successor
            node.value = minValue(node.right);
            node.right = deleteRec(node.right, node.value);
        }
        return node;
    }

    private int minValue(Node node) {
        while (node.left != null) node = node.left;
        return node.value;
    }

    // ── Traversals ────────────────────────────────────────────────────
    public void inOrder()   { inOrderRec(root);   System.out.println(); }
    public void preOrder()  { preOrderRec(root);  System.out.println(); }
    public void postOrder() { postOrderRec(root); System.out.println(); }

    private void inOrderRec(Node n)   { if (n != null) { inOrderRec(n.left);   System.out.print(n.value + " "); inOrderRec(n.right);  } }
    private void preOrderRec(Node n)  { if (n != null) { System.out.print(n.value + " "); preOrderRec(n.left);  preOrderRec(n.right); } }
    private void postOrderRec(Node n) { if (n != null) { postOrderRec(n.left); postOrderRec(n.right); System.out.print(n.value + " "); } }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();

        System.out.println("=".repeat(40));
        System.out.println("  Binary Search Tree Demo");
        System.out.println("=".repeat(40));

        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80}) tree.insert(v);

        System.out.print("In-Order   : "); tree.inOrder();
        System.out.print("Pre-Order  : "); tree.preOrder();
        System.out.print("Post-Order : "); tree.postOrder();

        System.out.println("\nSearch 40  : " + tree.search(40));
        System.out.println("Search 99  : " + tree.search(99));

        tree.delete(30);
        System.out.print("\nAfter delete(30) In-Order: ");
        tree.inOrder();
    }
}
