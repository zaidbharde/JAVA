import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Bank Account — deposit, withdraw, transfer, and full transaction history.
 */
public class BankAccount {

    // ── Transaction record ────────────────────────────────────────────
    private record Transaction(String type, double amount, double balance, LocalDateTime time) {
        private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public String toString() {
            return String.format("  %-12s %+10.2f   Balance: %10.2f   %s",
                type, amount, balance, time.format(FMT));
        }
    }

    // ── Fields ────────────────────────────────────────────────────────
    private final String         owner;
    private final String         accountNumber;
    private       double         balance;
    private final List<Transaction> history = new ArrayList<>();

    public BankAccount(String owner, String accountNumber, double initialDeposit) {
        if (initialDeposit < 0)
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        this.owner         = owner;
        this.accountNumber = accountNumber;
        this.balance       = initialDeposit;
        record("OPEN", initialDeposit);
    }

    // ── Operations ────────────────────────────────────────────────────
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive.");
        balance += amount;
        record("DEPOSIT", amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0)   throw new IllegalArgumentException("Withdrawal must be positive.");
        if (amount > balance) throw new IllegalStateException(
            "Insufficient funds. Balance: " + balance + ", Requested: " + amount);
        balance -= amount;
        record("WITHDRAW", -amount);
    }

    public void transfer(BankAccount target, double amount) {
        this.withdraw(amount);
        target.deposit(amount);
        System.out.printf("  Transferred %.2f from %s → %s%n",
            amount, this.owner, target.owner);
    }

    public double getBalance() { return balance; }

    private void record(String type, double amount) {
        history.add(new Transaction(type, amount, balance, LocalDateTime.now()));
    }

    // ── Statement ─────────────────────────────────────────────────────
    public void printStatement() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  Account Statement");
        System.out.printf("  Owner   : %s%n  Account : %s%n",
            owner, accountNumber);
        System.out.println("=".repeat(70));
        System.out.printf("  %-12s %-10s   %-14s   %s%n",
            "Type", "Amount", "Balance", "Timestamp");
        System.out.println("  " + "─".repeat(66));
        history.forEach(System.out::println);
        System.out.println("  " + "─".repeat(66));
        System.out.printf("  Current Balance: %.2f%n", balance);
        System.out.println("=".repeat(70));
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        BankAccount alice = new BankAccount("Alice", "ACC-001", 1000.00);
        BankAccount bob   = new BankAccount("Bob",   "ACC-002",  500.00);

        alice.deposit(500.00);
        alice.withdraw(200.00);
        alice.transfer(bob, 300.00);

        alice.printStatement();
        bob.printStatement();

        // Test insufficient funds
        try {
            alice.withdraw(99999.00);
        } catch (IllegalStateException e) {
            System.out.println("\n  Caught: " + e.getMessage());
        }
    }
}
