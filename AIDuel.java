import java.util.Random;

public class AIDuel {

    private static final Random random = new Random();
    private static final int MAX_HP = 100;

    static class Fighter {
        private String name;
        private int hp;
        private int wins = 0;

        public Fighter(String name) {
            this.name = name;
            this.hp = MAX_HP;
        }

        public String getName() {
            return name;
        }

        public int getHP() {
            return hp;
        }

        public boolean isAlive() {
            return hp > 0;
        }

        public int attack() {
            return random.nextInt(16) + 10; // 10-25
        }

        public int defend() {
            return random.nextInt(8) + 3; // 3-10
        }

        public void takeDamage(int damage) {
            hp = Math.max(0, hp - damage);
        }

        public void addWin() {
            wins++;
        }

        public int getWins() {
            return wins;
        }
    }

    public static void main(String[] args) {

        Fighter botX = new Fighter("⚙️ BotX");
        Fighter botZ = new Fighter("🤖 BotZ");

        int turn = 1;

        System.out.println("========== AI DUEL ==========");

        while (botX.isAlive() && botZ.isAlive()) {

            System.out.println("\n========= TURN " + turn + " =========");

            fight(botX, botZ);
            if (!botZ.isAlive())
                break;

            fight(botZ, botX);

            printHealth(botX);
            printHealth(botZ);

            turn++;
        }

        Fighter winner = botX.isAlive() ? botX : botZ;
        winner.addWin();

        System.out.println("\n===============================");
        System.out.println("🏆 WINNER: " + winner.getName());
        System.out.println("===============================");
    }

    static void fight(Fighter attacker, Fighter defender) {

        // 10% miss chance
        if (random.nextInt(100) < 10) {
            System.out.println(attacker.getName() + " MISSED the attack!");
            return;
        }

        int attack = attacker.attack();
        int defense = defender.defend();

        boolean critical = random.nextInt(100) < 15;

        if (critical) {
            attack *= 2;
        }

        int damage = Math.max(0, attack - defense);

        defender.takeDamage(damage);

        System.out.println(attacker.getName() + " attacks (" + attack + ")"
                + (critical ? " 💥 CRITICAL!" : "")
                + " | " + defender.getName() + " defends (" + defense + ")"
                + " | Damage: " + damage
                + " | HP Left: " + defender.getHP());
    }

    static void printHealth(Fighter fighter) {

        int bars = fighter.getHP() / 5;

        StringBuilder healthBar = new StringBuilder();

        for (int i = 0; i < bars; i++)
            healthBar.append("█");

        for (int i = bars; i < 20; i++)
            healthBar.append("░");

        System.out.printf("%s [%s] %d/%d HP%n",
                fighter.getName(),
                healthBar,
                fighter.getHP(),
                MAX_HP);
    }
}
