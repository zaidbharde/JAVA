import java.util.*;

public class EcosystemSim {

    enum Species { EMPTY, GRASS, RABBIT, FOX }

    static final String[] SPRITES = {"  ", "░░", "🐇", "🦊"};
    static final int W = 40, H = 20;
    static final Random RNG = new Random(42);

    record Cell(Species species, int energy, int age) {}

    static Cell[][] grid = new Cell[H][W];
    static int generation = 0;

    static void init() {
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                grid[y][x] = new Cell(Species.EMPTY, 0, 0);

        for (int i = 0; i < W * H * 0.3; i++) {
            int x = RNG.nextInt(W), y = RNG.nextInt(H);
            grid[y][x] = new Cell(Species.GRASS, 5, 0);
        }
        for (int i = 0; i < 40; i++) {
            int x = RNG.nextInt(W), y = RNG.nextInt(H);
            grid[y][x] = new Cell(Species.RABBIT, 20, 0);
        }
        for (int i = 0; i < 10; i++) {
            int x = RNG.nextInt(W), y = RNG.nextInt(H);
            grid[y][x] = new Cell(Species.FOX, 30, 0);
        }
    }

    static int[] randomNeighbor(int x, int y) {
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};
        int[] d = dirs[RNG.nextInt(4)];
        int nx = (x + d[0] + W) % W;
        int ny = (y + d[1] + H) % H;
        return new int[]{nx, ny};
    }

    static void step() {
        Cell[][] next = new Cell[H][W];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                next[y][x] = new Cell(Species.EMPTY, 0, 0);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                Cell cell = grid[y][x];

                switch (cell.species) {
                    case GRASS -> {
                        next[y][x] = new Cell(Species.GRASS, cell.energy, cell.age + 1);
                        if (RNG.nextDouble() < 0.05) {
                            int[] n = randomNeighbor(x, y);
                            if (grid[n[1]][n[0]].species == Species.EMPTY)
                                next[n[1]][n[0]] = new Cell(Species.GRASS, 3, 0);
                        }
                    }
                    case RABBIT -> {
                        int energy = cell.energy - 1;
                        if (energy <= 0) continue;

                        int[] n = randomNeighbor(x, y);
                        int nx = n[0], ny = n[1];

                        if (grid[ny][nx].species == Species.GRASS) {
                            energy += 10;
                            next[ny][nx] = new Cell(Species.RABBIT, Math.min(energy, 40), cell.age + 1);
                        } else if (grid[ny][nx].species == Species.EMPTY) {
                            next[ny][nx] = new Cell(Species.RABBIT, energy, cell.age + 1);
                        } else {
                            next[y][x] = new Cell(Species.RABBIT, energy, cell.age + 1);
                        }

                        if (energy > 25 && RNG.nextDouble() < 0.1) {
                            int[] bn = randomNeighbor(x, y);
                            if (next[bn[1]][bn[0]].species == Species.EMPTY)
                                next[bn[1]][bn[0]] = new Cell(Species.RABBIT, 15, 0);
                        }
                    }
                    case FOX -> {
                        int energy = cell.energy - 2;
                        if (energy <= 0) continue;

                        int[] n = randomNeighbor(x, y);
                        int nx = n[0], ny = n[1];

                        if (grid[ny][nx].species == Species.RABBIT) {
                            energy += 20;
                            next[ny][nx] = new Cell(Species.FOX, Math.min(energy, 60), cell.age + 1);
                        } else if (grid[ny][nx].species == Species.EMPTY ||
                                   grid[ny][nx].species == Species.GRASS) {
                            next[ny][nx] = new Cell(Species.FOX, energy, cell.age + 1);
                        } else {
                            next[y][x] = new Cell(Species.FOX, energy, cell.age + 1);
                        }

                        if (energy > 40 && RNG.nextDouble() < 0.05) {
                            int[] bn = randomNeighbor(x, y);
                            if (next[bn[1]][bn[0]].species == Species.EMPTY)
                                next[bn[1]][bn[0]] = new Cell(Species.FOX, 20, 0);
                        }
                    }
                    default -> {}
                }
            }
        }
        grid = next;
        generation++;
    }

    static Map<Species, Integer> census() {
        Map<Species, Integer> counts = new EnumMap<>(Species.class);
        for (Species s : Species.values()) counts.put(s, 0);
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                counts.merge(grid[y][x].species, 1, Integer::sum);
        return counts;
    }

    static void render() {
        System.out.print("\033[H\033[2J");
        System.out.println("  ┌" + "──".repeat(W) + "┐");
        for (int y = 0; y < H; y++) {
            System.out.print("  │");
            for (int x = 0; x < W; x++)
                System.out.print(SPRITES[grid[y][x].species.ordinal()]);
            System.out.println("│");
        }
        System.out.println("  └" + "──".repeat(W) + "┘");

        Map<Species, Integer> c = census();
        System.out.printf("  Gen: %d | 🌿 Grass: %d | 🐇 Rabbits: %d | 🦊 Foxes: %d%n",
            generation, c.get(Species.GRASS), c.get(Species.RABBIT), c.get(Species.FOX));

        int maxBar = 30;
        int maxPop = Math.max(1, Collections.max(c.values()));
        for (var entry : List.of(
            Map.entry("🌿 Grass  ", c.get(Species.GRASS)),
            Map.entry("🐇 Rabbit ", c.get(Species.RABBIT)),
            Map.entry("🦊 Fox    ", c.get(Species.FOX))
        )) {
            int bar = entry.getValue() * maxBar / maxPop;
            System.out.println("  " + entry.getKey() + "│" + "█".repeat(bar) + " " + entry.getValue());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        init();

        boolean animate = args.length > 0 && args[0].equals("--animate");
        int generations = animate ? 300 : 200;

        List<int[]> history = new ArrayList<>();

        for (int i = 0; i < generations; i++) {
            step();
            Map<Species, Integer> c = census();
            history.add(new int[]{c.get(Species.GRASS), c.get(Species.RABBIT), c.get(Species.FOX)});

            if (animate) {
                render();
                Thread.sleep(100);
            }

            if (c.get(Species.RABBIT) == 0 && c.get(Species.FOX) == 0) {
                System.out.println("  Ecosystem collapsed at generation " + generation);
                break;
            }
        }

        if (!animate) {
            render();
        }

        System.out.println("\n  Population History (sampled):");
        System.out.printf("  %-5s %8s %8s %8s%n", "Gen", "Grass", "Rabbits", "Foxes");
        System.out.println("  " + "─".repeat(35));
        for (int i = 0; i < history.size(); i += Math.max(1, history.size() / 15)) {
            int[] h = history.get(i);
            System.out.printf("  %-5d %8d %8d %8d%n", i + 1, h[0], h[1], h[2]);
        }
    }
}
