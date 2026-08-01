import java.util.Random;
import java.util.Scanner;

public class AIDuel {

    private static final Random random = new Random();
    private static final int MAX_HP = 100;
    private static final int TOTAL_ROUNDS = 3;

    // ─────────────────────────────────────────
    // Enums
    // ─────────────────────────────────────────

    enum FighterClass {
        WARRIOR ("⚔️  Warrior", 120, 0.10, 0.20, 0.08),  // High HP, high crit
        MAGE    ("🔮 Mage",     80,  0.25, 0.30, 0.05),  // Low HP, very high crit/miss
        ROGUE   ("🗡️  Rogue",   90,  0.05, 0.25, 0.15);  // Balanced, high dodge

        final String label;
        final int    baseHP;
        final double missChance;
        final double critChance;
        final double dodgeChance;

        FighterClass(String label, int baseHP,
                     double missChance, double critChance, double dodgeChance) {
            this.label       = label;
            this.baseHP      = baseHP;
            this.missChance  = missChance;
            this.critChance  = critChance;
            this.dodgeChance = dodgeChance;
        }
    }

    enum StatusEffect {
        NONE, POISONED, STUNNED, BURNED
    }

    // ─────────────────────────────────────────
    // Fighter
    // ─────────────────────────────────────────

    static class Fighter {

        private final String       name;
        private final FighterClass fighterClass;
        private int                hp;
        private int                maxHP;
        private int                wins;
        private StatusEffect       status;
        private int                statusDuration;

        public Fighter(String name, FighterClass fighterClass) {
            this.name         = name;
            this.fighterClass = fighterClass;
            this.maxHP        = fighterClass.baseHP;
            this.hp           = maxHP;
            this.wins         = 0;
            this.status       = StatusEffect.NONE;
            this.statusDuration = 0;
        }

        // ── Getters ──────────────────────────

        public String       getName()        { return name; }
        public FighterClass getFighterClass(){ return fighterClass; }
        public int          getHP()          { return hp; }
        public int          getMaxHP()       { return maxHP; }
        public int          getWins()        { return wins; }
        public StatusEffect getStatus()      { return status; }
        public boolean      isAlive()        { return hp > 0; }

        // ── Combat ───────────────────────────

        /**
         * Base attack varies by class.
         * Warrior: 12-27 | Mage: 15-35 | Rogue: 10-22
         */
        public int attack() {
            return switch (fighterClass) {
                case WARRIOR -> random.nextInt(16) + 12;
                case MAGE    -> random.nextInt(21) + 15;
                case ROGUE   -> random.nextInt(13) + 10;
            };
        }

        /**
         * Base defense varies by class.
         * Warrior: 5-14 | Mage: 2-7 | Rogue: 3-10
         */
        public int defend() {
            return switch (fighterClass) {
                case WARRIOR -> random.nextInt(10) + 5;
                case MAGE    -> random.nextInt(6)  + 2;
                case ROGUE   -> random.nextInt(8)  + 3;
            };
        }

        public void takeDamage(int damage) {
            hp = Math.max(0, hp - damage);
        }

        public void heal(int amount) {
            hp = Math.min(maxHP, hp + amount);
        }

        public void addWin() { wins++; }

        // ── Status Effects ───────────────────

        public void applyStatus(StatusEffect effect, int duration) {
            if (status == StatusEffect.NONE) {
                this.status         = effect;
                this.statusDuration = duration;
            }
        }

        /**
         * Processes status effect at the start of a turn.
         * @return descriptive message, or null if no effect
         */
        public String processStatus() {
            if (status == StatusEffect.NONE) return null;

            String message = null;

            switch (status) {
                case POISONED -> {
                    int poisonDmg = (int)(maxHP * 0.05);
                    takeDamage(poisonDmg);
                    message = String.format("☠️  %s takes %d poison damage! (HP: %d)",
                                            name, poisonDmg, hp);
                }
                case BURNED -> {
                    int burnDmg = (int)(maxHP * 0.07);
                    takeDamage(burnDmg);
                    message = String.format("🔥 %s takes %d burn damage! (HP: %d)",
                                            name, burnDmg, hp);
                }
                case STUNNED -> {
                    message = String.format("⚡ %s is stunned and cannot act!", name);
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

        // ── Special Ability ──────────────────

        /**
         * Each class has a unique special ability (20% activation chance).
         * Warrior: Shield Bash – stuns enemy for 1 turn
         * Mage:    Arcane Burst – applies burn for 2 turns
         * Rogue:   Venom Strike – applies poison for 3 turns
         */
        public SpecialResult useSpecial(Fighter target) {
            if (random.nextDouble() >= 0.20) return null;

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

        // ── Reset ────────────────────────────

        public void resetForNewRound() {
            hp             = maxHP;
            status         = StatusEffect.NONE;
            statusDuration = 0;
        }
    }

    // ─────────────────────────────────────────
    // SpecialResult record
    // ─────────────────────────────────────────

    record SpecialResult(String message, int extraDamage) {}

    // ─────────────────────────────────────────
    // Main
    // ─────────────────────────────────────────

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        printBanner();

        Fighter botX = createFighter("⚙️  BotX", scanner);
        Fighter botZ = createFighter("🤖 BotZ", scanner);

        System.out.println("\n" + "═".repeat(50));
        System.out.printf("  %s [%s]  VS  %s [%s]%n",
                botX.getName(), botX.getFighterClass().label,
                botZ.getName(), botZ.getFighterClass().label);
        System.out.println("═".repeat(50));
        System.out.println("       First to " + TOTAL_ROUNDS + " wins takes the trophy!");
        System.out.println("═".repeat(50));

        pause(scanner);

        // ── Round Loop ────────────────────────

        int roundNumber = 1;

        while (botX.getWins() < TOTAL_ROUNDS && botZ.getWins() < TOTAL_ROUNDS) {

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

        // ── Match Winner ──────────────────────

        Fighter champion = botX.getWins() >= TOTAL_ROUNDS ? botX : botZ;

        System.out.println("\n" + "═".repeat(50));
        System.out.println("  🏆 MATCH WINNER: " + champion.getName()
                + " [" + champion.getFighterClass().label + "]");
        System.out.printf("  Final Score: %s %d – %d %s%n",
                botX.getName(), botX.getWins(),
                botZ.getWins(), botZ.getName());
        System.out.println("═".repeat(50));

        scanner.close();
    }

    // ─────────────────────────────────────────
    // Round Logic
    // ─────────────────────────────────────────

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

    // ─────────────────────────────────────────
    // Turn Logic
    // ─────────────────────────────────────────

    static void processTurn(Fighter attacker, Fighter defender) {

        // 1. Process attacker's status effect
        String statusMsg = attacker.processStatus();
        if (statusMsg != null) System.out.println("  " + statusMsg);

        if (!attacker.isAlive()) return;

        // 2. Skip turn if stunned
        if (attacker.isStunned()) return;

        // 3. Dodge check
        if (random.nextDouble() < defender.getFighterClass().dodgeChance) {
            System.out.printf("  %s dodged %s's attack!%n",
                              defender.getName(), attacker.getName());
            return;
        }

        // 4. Miss check
        if (random.nextDouble() < attacker.getFighterClass().missChance) {
            System.out.printf("  %s MISSED the attack!%n", attacker.getName());
            return;
        }

        // 5. Special ability attempt
        SpecialResult special = attacker.useSpecial(defender);
        if (special != null) {
            System.out.println("  " + special.message());
        }

        // 6. Regular attack
        int rawAttack = attacker.attack();
        int defense   = defender.defend();
        boolean crit  = random.nextDouble() < attacker.getFighterClass().critChance;

        if (crit) rawAttack = (int)(rawAttack * 1.75);

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

    // ─────────────────────────────────────────
    // Fighter Creation
    // ─────────────────────────────────────────

    static Fighter createFighter(String name, Scanner scanner) {

        System.out.println("\nChoose class for " + name + ":");
        FighterClass[] classes = FighterClass.values();

        for (int i = 0; i < classes.length; i++) {
            FighterClass fc = classes[i];
            System.out.printf("  [%d] %-15s HP: %-4d Miss: %.0f%%  Crit: %.0f%%  Dodge: %.0f%%%n",
                    i + 1,
                    fc.label,
                    fc.baseHP,
                    fc.missChance  * 100,
                    fc.critChance  * 100,
                    fc.dodgeChance * 100);
        }

        int choice = 0;
        while (choice < 1 || choice > classes.length) {
            System.out.print("Enter choice (1-" + classes.length + "): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next();
            }
        }

        return new Fighter(name, classes[choice - 1]);
    }

    // ─────────────────────────────────────────
    // Display Helpers
    // ─────────────────────────────────────────

    static void printHealth(Fighter fighter) {

        int barLength = 20;
        int filled    = (int)((double) fighter.getHP() / fighter.getMaxHP() * barLength);

        String color = filled > 12 ? "🟩" : filled > 6 ? "🟨" : "🟥";

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled;     i++) bar.append("█");
        for (int i = filled; i < barLength; i++) bar.append("░");

        String statusTag = switch (fighter.getStatus()) {
            case POISONED -> " ☠️ ";
            case STUNNED  -> " ⚡";
            case BURNED   -> " 🔥";
            default       -> "";
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
        if (scanner.hasNextLine()) scanner.nextLine();
    }
}
