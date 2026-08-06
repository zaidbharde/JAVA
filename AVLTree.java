/**
 * AVL Tree — self-balancing BST with rotations.
 * Guarantees O(log n) insert, delete, and search.
 */
public class AVLTree {

    // ── Node ──────────────────────────────────────────────────────────
    private static class Node {
        int  key, height;
        Node left, right;

        Node(int key) {
            this.key    = key;
            this.height = 1;
        }
    }

    // ── Fields ────────────────────────────────────────────────────────
    private Node root;

    // ── Height helpers ────────────────────────────────────────────────
    private int height(Node n)        { return n == null ? 0 : n.height; }
    private int balanceFactor(Node n) { return n == null ? 0 : height(n.left) - height(n.right); }

    private void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    // ── Rotations ─────────────────────────────────────────────────────
    private Node rotateRight(Node y) {
        Node x  = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left  = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y  = x.right;
        Node T2 = y.left;
        y.left  = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // ── Balance ───────────────────────────────────────────────────────
    private Node balance(Node n) {
        updateHeight(n);
        int bf = balanceFactor(n);

        // Left heavy
        if (bf > 1) {
            if (balanceFactor(n.left) < 0)
                n.left = rotateLeft(n.left);   // Left-Right case
            return rotateRight(n);
        }
        // Right heavy
        if (bf < -1) {
            if (balanceFactor(n.right) > 0)
                n.right = rotateRight(n.right); // Right-Left case
            return rotateLeft(n);
        }
        return n;
    }

    // ── Insert ────────────────────────────────────────────────────────
    public void insert(int key)          { root = insert(root, key); }

    private Node insert(Node n, int key) {
        if (n == null) return new Node(key);
        if (key < n.key)      n.left  = insert(n.left,  key);
        else if (key > n.key) n.right = insert(n.right, key);
        else return n;   // duplicate
        return balance(n);
    }

    // ── Delete ────────────────────────────────────────────────────────
    public void delete(int key)          { root = delete(root, key); }

    private Node delete(Node n, int key) {
        if (n == null) return null;

        if      (key < n.key) n.left  = delete(n.left,  key);
        else if (key > n.key) n.right = delete(n.right, key);
        else {
            if (n.left == null)  return n.right;
            if (n.right == null) return n.left;
            Node min = minNode(n.right);
            n.key    = min.key;
            n.right  = delete(n.right, min.key);
        }
        return balance(n);
    }

    private Node minNode(Node n) {
        while (n.left != null) n = n.left;
        return n;
    }

    // ── Search ────────────────────────────────────────────────────────
    public boolean search(int key) { return search(root, key); }

    private boolean search(Node n, int key) {
        if (n == null)       return false;
        if (key == n.key)    return true;
        return key < n.key ? search(n.left, key) : search(n.right, key);
    }

    // ── Traversal ─────────────────────────────────────────────────────
    public void inOrder() { inOrder(root); System.out.println(); }

    private void inOrder(Node n) {
        if (n == null) return;
        inOrder(n.left);
        System.out.printf("%d(h=%d) ", n.key, n.height);
        inOrder(n.right);
    }

    public int height() { return height(root); }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();

        System.out.println("=".repeat(44));
        System.out.println("  AVL Tree Demo");
        System.out.println("=".repeat(44));

        int[] keys = {10, 20, 30, 40, 50, 25, 15, 5, 35, 45};
        for (int k : keys) {
            tree.insert(k);
            System.out.printf("  Insert %-3d → height=%d%n", k, tree.height());
        }

        System.out.print("\n  In-order: ");
        tree.inOrder();

        System.out.println("\n  Search 25: " + tree.search(25));
        System.out.println("  Search 99: " + tree.search(99));

        tree.delete(30);
        System.out.print("  After delete(30): ");
        tree.inOrder();
        System.out.println("  Height: " + tree.height());
    }
}
