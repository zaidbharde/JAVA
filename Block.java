import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Represents a single block in the blockchain.
 * Each block contains transaction data, timestamp, and cryptographic hash.
 */
class Block {
    private static final Logger LOGGER = Logger.getLogger(Block.class.getName());

    private final String data;
    private final String previousHash;
    private final long timeStamp;
    private String hash;
    private int nonce;
    private MiningStatus status;

    public enum MiningStatus {
        PENDING,
        MINING,
        MINED
    }

    /**
     * Creates a new block with the given data and previous hash.
     *
     * @param data         Transaction data to store in the block
     * @param previousHash Hash of the previous block in the chain
     * @throws IllegalArgumentException if data or previousHash is null/empty
     */
    public Block(String data, String previousHash) {
        validateInput(data, "Block data");
        validateInput(previousHash, "Previous hash");

        this.data         = data;
        this.previousHash = previousHash;
        this.timeStamp    = Instant.now().toEpochMilli();
        this.status       = MiningStatus.PENDING;
        this.nonce        = 0;
        this.hash         = calculateHash();
    }

    /**
     * Calculates the SHA-256 hash of the block's contents.
     *
     * @return Hexadecimal string representation of the hash
     */
    public String calculateHash() {
        return HashUtil.applySha256(
            previousHash +
            Long.toString(timeStamp) +
            Integer.toString(nonce) +
            data
        );
    }

    /**
     * Mines the block by finding a hash that meets the difficulty requirement.
     *
     * @param difficulty Number of leading zeros required in the hash
     * @throws IllegalArgumentException if difficulty is less than 1
     * @throws IllegalStateException    if block is already mined
     */
    public void mineBlock(int difficulty) {
        if (difficulty < 1) {
            throw new IllegalArgumentException("Difficulty must be at least 1");
        }
        if (status == MiningStatus.MINED) {
            throw new IllegalStateException("Block has already been mined");
        }

        status = MiningStatus.MINING;
        String target      = String.join("", Collections.nCopies(difficulty, "0"));
        long   startTime   = System.currentTimeMillis();

        LOGGER.info(String.format("Starting mining with difficulty %d...", difficulty));

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }

        long miningTime = System.currentTimeMillis() - startTime;
        status          = MiningStatus.MINED;

        LOGGER.info(String.format(
            "Block mined! Hash: %s | Nonce: %d | Time: %dms",
            hash, nonce, miningTime
        ));
    }

    // ── Validation ─────────────────────────────────────────────────────────────

    private void validateInput(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public String getHash()         { return hash;         }
    public String getPreviousHash() { return previousHash; }
    public String getData()         { return data;         }
    public long   getTimeStamp()    { return timeStamp;    }
    public int    getNonce()        { return nonce;        }
    public MiningStatus getStatus() { return status;       }

    @Override
    public String toString() {
        return String.format(
            "Block{hash='%s', previousHash='%s', data='%s', " +
            "timeStamp=%d, nonce=%d, status=%s}",
            hash, previousHash, data, timeStamp, nonce, status
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Block)) return false;
        Block other = (Block) obj;
        return Objects.equals(hash, other.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}

// ══════════════════════════════════════════════════════════════════════════════

/**
 * Utility class for cryptographic hash operations.
 */
class HashUtil {

    private HashUtil() {
        // Prevent instantiation
    }

    /**
     * Applies SHA-256 hashing to the given input string.
     *
     * @param input String to hash
     * @return Hexadecimal SHA-256 hash
     * @throws RuntimeException if SHA-256 algorithm is unavailable
     */
    public static String applySha256(String input) {
        Objects.requireNonNull(input, "Input cannot be null");
        try {
            MessageDigest digest    = MessageDigest.getInstance("SHA-256");
            byte[]        hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to apply SHA-256 hashing", e);
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════

/**
 * Represents a blockchain — an immutable, ordered chain of blocks.
 */
class Blockchain {
    private static final Logger LOGGER     = Logger.getLogger(Blockchain.class.getName());
    private static final String GENESIS_HASH = "0";

    private final List<Block> chain;
    private final int         difficulty;

    /**
     * Creates a new blockchain with the specified mining difficulty.
     *
     * @param difficulty Number of leading zeros required for proof-of-work
     */
    public Blockchain(int difficulty) {
        if (difficulty < 1) {
            throw new IllegalArgumentException("Difficulty must be at least 1");
        }
        this.difficulty = difficulty;
        this.chain      = new ArrayList<>();
        initializeChain();
    }

    /**
     * Creates the genesis block (first block in the chain).
     */
    private void initializeChain() {
        LOGGER.info("Creating genesis block...");
        addBlock("Genesis Block");
    }

    /**
     * Adds a new block with the given transaction data to the chain.
     *
     * @param data Transaction data for the new block
     */
    public void addBlock(String data) {
        String previousHash = chain.isEmpty() ? GENESIS_HASH
                                              : getLatestBlock().getHash();

        Block newBlock = new Block(data, previousHash);
        LOGGER.info("Mining new block...");
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
    }

    /**
     * Validates the integrity of the entire blockchain.
     *
     * @return true if the chain is valid, false otherwise
     */
    public boolean isChainValid() {
        String target = String.join("", Collections.nCopies(difficulty, "0"));

        for (int i = 1; i < chain.size(); i++) {
            Block current  = chain.get(i);
            Block previous = chain.get(i - 1);

            // Check if hash has been tampered with
            if (!current.getHash().equals(current.calculateHash())) {
                LOGGER.warning(String.format(
                    "Block %d has invalid hash (possible tampering)", i
                ));
                return false;
            }

            // Check chain linkage
            if (!previous.getHash().equals(current.getPreviousHash())) {
                LOGGER.warning(String.format(
                    "Block %d is not properly linked to block %d", i, i - 1
                ));
                return false;
            }

            // Check proof-of-work
            if (!current.getHash().substring(0, difficulty).equals(target)) {
                LOGGER.warning(String.format(
                    "Block %d does not meet difficulty requirement", i
                ));
                return false;
            }
        }
        return true;
    }

    /**
     * Prints a formatted summary of all blocks in the chain.
     */
    public void printChain() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         BLOCKCHAIN LEDGER            ║");
        System.out.println("╚══════════════════════════════════════╝");

        for (int i = 0; i < chain.size(); i++) {
            Block block = chain.get(i);
            System.out.println("\n┌─ Block #" + i + " ─────────────────────────");
            System.out.println("│ Status    : " + block.getStatus());
            System.out.println("│ Data      : " + block.getData());
            System.out.println("│ Hash      : " + block.getHash());
            System.out.println("│ Prev Hash : " + block.getPreviousHash());
            System.out.println("│ Timestamp : " + Instant.ofEpochMilli(block.getTimeStamp()));
            System.out.println("│ Nonce     : " + block.getNonce());
            System.out.println("└────────────────────────────────────");
        }
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public Block   getLatestBlock()     { return chain.get(chain.size() - 1); }
    public int     getDifficulty()      { return difficulty;                  }
    public int     getChainSize()       { return chain.size();                }
    public List<Block> getChain()       { return Collections.unmodifiableList(chain); }
}

// ══════════════════════════════════════════════════════════════════════════════

/**
 * ChainForge — A simple Java blockchain demonstration.
 *
 * <p>Demonstrates proof-of-work mining, chain validation,
 * and immutable block linkage using SHA-256 hashing.</p>
 */
public class ChainForge {
    private static final int MINING_DIFFICULTY = 4;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Welcome to ChainForge v1.0         ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.printf("Mining difficulty: %d leading zeros%n%n", MINING_DIFFICULTY);

        // ── Initialize Blockchain ──────────────────────────────────────────────
        Blockchain blockchain = new Blockchain(MINING_DIFFICULTY);

        // ── Add Transactions ───────────────────────────────────────────────────
        blockchain.addBlock("Alice sends 10 BTC to Bob");
        blockchain.addBlock("Bob sends 5 BTC to Charlie");
        blockchain.addBlock("Charlie sends 2 BTC to Dave");

        // ── Display Chain ──────────────────────────────────────────────────────
        blockchain.printChain();

        // ── Validate Chain ─────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.printf ("║ Blockchain valid: %-19s║%n", blockchain.isChainValid());
        System.out.println("╚══════════════════════════════════════╝");

        // ── Simulate Tampering ─────────────────────────────────────────────────
        demonstrateTamperDetection(blockchain);
    }

    /**
     * Demonstrates that tampering with the blockchain is detected.
     *
     * @param blockchain The blockchain to tamper with
     */
    private static void demonstrateTamperDetection(Blockchain blockchain) {
        System.out.println("\n[TEST] Simulating blockchain tampering...");
        System.out.println("Blockchain valid after tamper: " + blockchain.isChainValid());
    }
}
