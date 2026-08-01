import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

class Block {

    public enum MiningStatus { PENDING, MINING, MINED }

    private final String previousHash;
    private final long timestamp;
    private final List<String> transactions;
    private final int difficulty;

    private String merkleRoot;
    private String hash;
    private long nonce;
    private MiningStatus status;

    public Block(List<String> transactions, String previousHash, int difficulty) {
        if (transactions == null || transactions.isEmpty())
            throw new IllegalArgumentException("Transactions required");
        if (previousHash == null)
            throw new IllegalArgumentException("Previous hash required");
        if (difficulty < 1)
            throw new IllegalArgumentException("Difficulty must be >= 1");

        this.transactions = List.copyOf(transactions);
        this.previousHash = previousHash;
        this.timestamp = Instant.now().toEpochMilli();
        this.difficulty = difficulty;
        this.merkleRoot = computeMerkleRoot(this.transactions);
        this.hash = calculateHash();
        this.status = MiningStatus.PENDING;
    }

    public String calculateHash() {
        return HashUtil.sha256(
                previousHash +
                timestamp +
                nonce +
                merkleRoot
        );
    }

    public void mine() {
        if (status == MiningStatus.MINED)
            throw new IllegalStateException("Already mined");

        status = MiningStatus.MINING;
        String target = "0".repeat(difficulty);

        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }

        status = MiningStatus.MINED;
    }

    public void mineAsync(int threads) {
        if (status == MiningStatus.MINED)
            throw new IllegalStateException("Already mined");

        status = MiningStatus.MINING;
        String target = "0".repeat(difficulty);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicBoolean found = new AtomicBoolean(false);

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            pool.submit(() -> {
                long localNonce = threadId;
                while (!found.get()) {
                    String testHash = HashUtil.sha256(
                            previousHash +
                            timestamp +
                            localNonce +
                            merkleRoot
                    );
                    if (testHash.startsWith(target)) {
                        if (found.compareAndSet(false, true)) {
                            nonce = localNonce;
                            hash = testHash;
                        }
                        break;
                    }
                    localNonce += threads;
                }
            });
        }

        pool.shutdown();
        try { pool.awaitTermination(10, TimeUnit.MINUTES); }
        catch (InterruptedException ignored) {}

        status = MiningStatus.MINED;
    }

    private String computeMerkleRoot(List<String> txs) {
        List<String> layer = txs.stream()
                .map(HashUtil::sha256)
                .toList();

        while (layer.size() > 1) {
            List<String> next = new ArrayList<>();
            for (int i = 0; i < layer.size(); i += 2) {
                String left = layer.get(i);
                String right = (i + 1 < layer.size()) ? layer.get(i + 1) : left;
                next.add(HashUtil.sha256(left + right));
            }
            layer = next;
        }
        return layer.get(0);
    }

    public boolean validate() {
        if (!hash.equals(calculateHash())) return false;
        if (!hash.startsWith("0".repeat(difficulty))) return false;
        return true;
    }

    public String getHash() { return hash; }
    public String getPreviousHash() { return previousHash; }
    public List<String> getTransactions() { return transactions; }
    public long getTimestamp() { return timestamp; }
    public long getNonce() { return nonce; }
    public MiningStatus getStatus() { return status; }
}

class Blockchain {

    private final List<Block> chain = new ArrayList<>();
    private final int difficulty;

    public Blockchain(int difficulty) {
        if (difficulty < 1)
            throw new IllegalArgumentException("Difficulty must be >= 1");
        this.difficulty = difficulty;
        createGenesis();
    }

    private void createGenesis() {
        addBlock(List.of("Genesis Block"));
    }

    public void addBlock(List<String> transactions) {
        String prevHash = chain.isEmpty() ? "0" : getLatestBlock().getHash();
        Block block = new Block(transactions, prevHash, difficulty);
        block.mine();
        chain.add(block);
    }

    public boolean isValid() {
        for (int i = 0; i < chain.size(); i++) {
            Block current = chain.get(i);

            if (!current.validate()) return false;

            if (i > 0) {
                Block previous = chain.get(i - 1);
                if (!current.getPreviousHash().equals(previous.getHash()))
                    return false;
            }
        }
        return true;
    }

    public void print() {
        System.out.println("\n========== BLOCKCHAIN ==========");
        for (int i = 0; i < chain.size(); i++) {
            Block b = chain.get(i);
            System.out.println("Block #" + i);
            System.out.println(" Hash: " + b.getHash());
            System.out.println(" Prev: " + b.getPreviousHash());
            System.out.println(" Nonce: " + b.getNonce());
            System.out.println(" Time: " + Instant.ofEpochMilli(b.getTimestamp()));
            System.out.println(" Tx: " + b.getTransactions());
            System.out.println("--------------------------------");
        }
    }

    public Block getLatestBlock() { return chain.get(chain.size() - 1); }
    public List<Block> getChain() { return Collections.unmodifiableList(chain); }
}

class HashUtil {
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

public class ChainForge {

    public static void main(String[] args) {

        int difficulty = 4;
        Blockchain chain = new Blockchain(difficulty);

        chain.addBlock(List.of(
                "Alice -> Bob : 10 BTC",
                "Bob -> Charlie : 5 BTC"
        ));

        chain.addBlock(List.of(
                "Charlie -> Dave : 2 BTC",
                "Dave -> Eve : 1 BTC"
        ));

        chain.print();

        System.out.println("Chain valid: " + chain.isValid());
    }
}
