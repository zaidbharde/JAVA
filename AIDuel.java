import java.util.Random;
import java.util.Scanner;

public class AIDuel {

    private static final Random random = new Random();

    // --- Tunable constants (previously magic numbers) ---
    private static final int WINS_NEEDED = 3;
    private static final double SPECIAL_TRIGGER_CHANCE = 0.20;
    private static final double CRIT_MULTIPLIER = 1.75;
    private static final double POISON_DAMAGE_PCT = 0.05;
    private static final double BURN_DAMAGE_PCT = 0.07;
    private static final int HEALTH_BAR_LENGTH = 20;

    enum FighterClass {
        WARRIOR("⚔️  Warrior", 120, 0.10, 0.20, 0.08),
        MAGE("🔮 Mage", 80, 0.25, 0.30, 0.05),
        ROGUE("🗡️  Rogue", 90, 0.05, 0.25, 0.15);

        final String label;
        final int baseHP;
        final double missChance;
        final double critChance;
        final double dodgeChance;

        FighterClass(String label, int baseHP,
                     double missChance, double critChance, double dodgeChance) {
            this.label = label;
            this.baseHP = baseHP;
            this.missChance = missChance;
            this.critChance = critChance;
            this.dodgeChance = dodgeChance;
        }
    }

    enum StatusEffect {
        NONE, POISONED, STUNNED, BURNED
    }

    static class Fighter {

        private final String name;
        private final FighterClass fighterClass;
        private int hp;
        private final int maxHP;
        private int wins;
        private StatusEffect status;
        private int statusDuration;

        public Fighter(String name, FighterClass fighterClass) {
            this.name = name;
            this.fighterClass = fighterClass;
            this.maxHP = fighterClass.baseHP;
            this.hp = maxHP;
            this.wins = 0;
            this.status = StatusEffect.NONE;
            this.statusDuration = 0;
        }

        public String getName() {
            return name;
        }

        public FighterClass getFighterClass() {
            return fighterClass;
        }

        public int getHP() {
            return hp;
        }

        public int getMaxHP() {
            return maxHP;
        }

        public int getWins() {
            return wins;
        }

        public StatusEffect getStatus() {
            return status;
        }

        public boolean isAlive() {
            return hp > 0;
        }

        public int attack() {
            return switch (fighterClass) {
                case WARRIOR -> random.nextInt(16) + 12;
                case MAGE -> random.nextInt(21) + 15;
                case ROGUE -> random.nextInt(13) + 10;
            };
        }

        public int defend() {
            return switch (fighterClass) {
                case WARRIOR -> random.nextInt(10) + 5;
                case MAGE -> random.nextInt(6) + 2;
                case ROGUE -> random.nextInt(8) + 3;
            };
        }

        public void takeDamage(int damage) {
            hp = Math.max(0, hp - damage);
        }

        public void heal(int amount) {
            hp = Math.min(maxHP, hp + amount);
        }

        public void addWin() {
            wins++;
        }

        public void applyStatus(StatusEffect effect, int duration) {
            if (status == StatusEffect.NONE) {
                this.status = effect;
                this.statusDuration = duration;
            }
        }

        public String processStatus() {
            if (status == StatusEffect.NONE) return null;

            String message = null;

            switch (status) {
                case POISONED -> {
                    int poisonDmg = (int) (maxHP * POISON_DAMAGE_PCT);
                    takeDamage(poisonDmg);
                    message = String.format("☠️  %s takes %d poison damage! (HP: %d)",
                            name, poisonDmg, hp);
                }
                case BURNED -> {
                    int burnDmg = (int) (maxHP * BURN_DAMAGE_PCT);
                    takeDamage(burnDmg);
                    message = String.format("🔥 %s takes %d burn damage! (HP: %d)",
                            name, burnDmg, hp);
                }
                case STUNNED -> {
                    message = String.format("⚡ %s is stunned and cannot act!", name);
                }
                default -> {
                }
            }

            statusDuration--;
            if (statusDuration <= 0) {
                status = StatusEffect.NONE;
            }

            return message;
        }

        public boolean isStunned() {
            return status == StatusEffect.STUNNED;
        }

        public SpecialResult useSpecial(Fighter target) {
            if (random.nextDouble() >= SPECIAL_TRIGGER_CHANCE) return null;

            return switch (fighterClass) {
                case WARRIOR -> {
                    target.applyStatus(StatusEffect.STUNNED, 1);
                    yield new SpecialResult(
                            "🛡️  Shield Bash! " + target.getName() + " is STUNNED!", 0);
                }
                case MAGE -> {
                    target.applyStatus(StatusEffect.BURNED, 2);
                    yield new SpecialResult(
                            "🔥 Arcane Burst! " + target.getName() + " is BURNED for 2 turns!", 0);
                }
                case ROGUE -> {
                    target.applyStatus(StatusEffect.POISONED, 3);
                    yield new SpecialResult(
                            "☠️  Venom Strike! " + target.getName() + " is POISONED for 3 turns!", 0);
                }
            };
        }

        public void resetForNewRound() {
            hp = maxHP;
            status = StatusEffect.NONE;
            statusDuration = 0;
        }

        public void resetForNewMatch() {
            resetForNewRound();
            wins = 0;
        }
    }

    record SpecialResult(String message, int extraDamage) {
    }

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            printBanner();

            boolean playAgain = true;
            while (playAgain) {
                runMatch(scanner);
                playAgain = askPlayAgain(scanner);
            }

            System.out.println("\nThanks for playing! 👋");
        }
    }

    static void runMatch(Scanner scanner) {

        Fighter botX = createFighter("⚙️  BotX", scanner);
        Fighter botZ = createFighter("🤖 BotZ", scanner);

        System.out.println("\n" + "═".repeat(50));
        System.out.printf("  %s [%s]  VS  %s [%s]%n",
                botX.getName(), botX.getFighterClass().label,
                botZ.getName(), botZ.getFighterClass().label);
        System.out.println("═".repeat(50));
        System.out.println("       First to " + WINS_NEEDED + " wins takes the trophy!");
        System.out.println("═".repeat(50));

        pause(scanner);

        int roundNumber = 1;

        while (botX.getWins() < WINS_NEEDED && botZ.getWins() < WINS_NEEDED) {

            System.out.println("\n" + "═".repeat(50));
            System.out.printf("  🥊 ROUND %d  |  Score: %s %d – %d %s%n",
                    roundNumber,
                    botX.getName(), botX.getWins(),
                    botZ.getWins(), botZ.getName());
            System.out.println("═".repeat(50));

            Fighter roundWinner = playRound(botX, botZ);
            roundWinner.addWin();

            System.out.println("\n🏅 Round winner: " + roundWinner.getName()
                    + "  |  Score: " + botX.getName() + " " + botX.getWins()
                    + " – " + botZ.getWins() + " " + botZ.getName());

            botX.resetForNewRound();
            botZ.resetForNewRound();

            roundNumber++;
            pause(scanner);
        }

        Fighter champion = botX.getWins() >= WINS_NEEDED ? botX : botZ;

        System.out.println("\n" + "═".repeat(50));
        System.out.println("  🏆 MATCH WINNER: " + champion.getName()
                + " [" + champion.getFighterClass().label + "]");
        System.out.printf("  Final Score: %s %d – %d %s%n",
                botX.getName(), botX.getWins(),
                botZ.getWins(), botZ.getName());
        System.out.println("═".repeat(50));
    }

    static Fighter playRound(Fighter botX, Fighter botZ) {

        int turn = 1;

        while (botX.isAlive() && botZ.isAlive()) {

            System.out.println("\n  ── Turn " + turn + " ──────────────────────────");

            processTurn(botX, botZ);
            if (!botZ.isAlive()) break;

            processTurn(botZ, botX);

            System.out.println();
            printHealth(botX);
            printHealth(botZ);

            turn++;
        }

        return botX.isAlive() ? botX : botZ;
    }

    static void processTurn(Fighter attacker, Fighter defender) {

        String statusMsg = attacker.processStatus();
        if (statusMsg != null) System.out.println("  " + statusMsg);

        if (!attacker.isAlive()) return;

        if (attacker.isStunned()) return;

        if (random.nextDouble() < defender.getFighterClass().dodgeChance) {
            System.out.printf("  %s dodged %s's attack!%n",
                    defender.getName(), attacker.getName());
            return;
        }

        if (random.nextDouble() < attacker.getFighterClass().missChance) {
            System.out.printf("  %s MISSED the attack!%n", attacker.getName());
            return;
        }

        SpecialResult special = attacker.useSpecial(defender);
        if (special != null) {
            System.out.println("  " + special.message());
        }

        int rawAttack = attacker.attack();
        int defense = defender.defend();
        boolean crit = random.nextDouble() < attacker.getFighterClass().critChance;

        if (crit) rawAttack = (int) (rawAttack * CRIT_MULTIPLIER);

        int damage = Math.max(1, rawAttack - defense);
        defender.takeDamage(damage);

        System.out.printf("  %s attacks (%d)%s | %s defends (%d) | Dmg: %d | HP: %d/%d%n",
                attacker.getName(),
                rawAttack,
                crit ? " 💥 CRIT!" : "",
                defender.getName(),
                defense,
                damage,
                defender.getHP(),
                defender.getMaxHP());
    }

    static Fighter createFighter(String name, Scanner scanner) {

        System.out.println("\nChoose class for " + name + ":");
        FighterClass[] classes = FighterClass.values();

        for (int i = 0; i < classes.length; i++) {
            FighterClass fc = classes[i];
            System.out.printf("  [%d] %-15s HP: %-4d Miss: %.0f%%  Crit: %.0f%%  Dodge: %.0f%%%n",
                    i + 1,
                    fc.label,
                    fc.baseHP,
                    fc.missChance * 100,
                    fc.critChance * 100,
                    fc.dodgeChance * 100);
        }

        int choice = readIntInRange(scanner, "Enter choice (1-" + classes.length + "): ", 1, classes.length);

        return new Fighter(name, classes[choice - 1]);
    }

    /**
     * Reads an integer within [min, max], reprompting on invalid input.
     * Also consumes the trailing newline so subsequent nextLine() calls behave predictably.
     */
    static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        int value = -1;
        while (value < min || value > max) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value < min || value > max) {
                    System.out.println("  Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("  That's not a number, try again.");
                scanner.next();
            }
        }
        scanner.nextLine(); // consume leftover newline after nextInt()
        return value;
    }

    static boolean askPlayAgain(Scanner scanner) {
        System.out.print("\nPlay another match? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.startsWith("y");
    }

    static void printHealth(Fighter fighter) {

        int filled = (int) ((double) fighter.getHP() / fighter.getMaxHP() * HEALTH_BAR_LENGTH);

        String color = filled > 12 ? "🟩" : filled > 6 ? "🟨" : "🟥";

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append("█");
        for (int i = filled; i < HEALTH_BAR_LENGTH; i++) bar.append("░");

        String statusTag = switch (fighter.getStatus()) {
            case POISONED -> " ☠️ ";
            case STUNNED -> " ⚡";
            case BURNED -> " 🔥";
            default -> "";
        };

        System.out.printf("  %s %s [%s] %d/%d HP%s%n",
                color,
                fighter.getName(),
                bar,
                fighter.getHP(),
                fighter.getMaxHP(),
                statusTag);
    }

    static void printBanner() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║           ⚔️   A I   D U E L   ⚔️               ║");
        System.out.println("║        Choose your fighter and battle!           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    static void pause(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
