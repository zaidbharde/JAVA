class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word):
        node = self.root
        for ch in word:
            node = node.children.setdefault(ch, TrieNode())
        node.is_end = True

    def _collect(self, node, prefix, results):
        if node.is_end:
            results.append(prefix)
        for ch, child in node.children.items():
            self._collect(child, prefix + ch, results)

    def autocomplete(self, prefix):
        node = self.root
        for ch in prefix:
            if ch not in node.children:
                return []
            node = node.children[ch]
        results = []
        self._collect(node, prefix, results)
        return results


if __name__ == "__main__":
    trie = Trie()
    words = ["car", "care", "careful", "cat", "cater", "dog"]
    for w in words:
        trie.insert(w)
    print(trie.autocomplete("car"))   # ['car', 'care', 'careful']
    print(trie.autocomplete("cat"))   # ['cat', 'cater']
