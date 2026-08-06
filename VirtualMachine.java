import java.util.*;

public class VirtualMachine {

    private static final int MEM_SIZE = 256;
    private static final int STACK_SIZE = 64;

    private final int[] memory = new int[MEM_SIZE];
    private final int[] stack  = new int[STACK_SIZE];
    private int ip = 0;
    private int sp = -1;
    private boolean running = true;
    private boolean debug = false;
    private int cycleCount = 0;
    private final StringBuilder output = new StringBuilder();

    enum Op {
        NOP(0), PUSH(1), POP(2), DUP(3), SWAP(4), OVER(5),
        ADD(10), SUB(11), MUL(12), DIV(13), MOD(14), NEG(15),
        AND(20), OR(21), XOR(22), NOT(23),
        EQ(30), LT(31), GT(32),
        JMP(40), JZ(41), JNZ(42), CALL(43), RET(44),
        LOAD(50), STORE(51),
        PRINT(60), PRINTC(61), READ(62),
        HALT(99);

        final int code;
        Op(int code) { this.code = code; }
        static Op fromCode(int code) {
            for (Op op : values()) if (op.code == code) return op;
            return NOP;
        }
    }

    private void push(int val) {
        if (sp >= STACK_SIZE - 1) throw new RuntimeException("Stack overflow");
        stack[++sp] = val;
    }

    private int pop() {
        if (sp < 0) throw new RuntimeException("Stack underflow");
        return stack[sp--];
    }

    private int peek() {
        if (sp < 0) throw new RuntimeException("Stack empty");
        return stack[sp];
    }

    public void load(int[] program, int offset) {
        System.arraycopy(program, 0, memory, offset, program.length);
    }

    public void run() {
        running = true;
        while (running && ip < MEM_SIZE) {
            step();
            cycleCount++;
            if (cycleCount > 100000) {
                System.out.println("  Cycle limit reached!");
                break;
            }
        }
    }

    private void step() {
        Op op = Op.fromCode(memory[ip]);
        if (debug) printState(op);

        switch (op) {
            case NOP   -> ip++;
            case PUSH  -> { push(memory[++ip]); ip++; }
            case POP   -> { pop(); ip++; }
            case DUP   -> { push(peek()); ip++; }
            case SWAP  -> { int a = pop(), b = pop(); push(a); push(b); ip++; }
            case OVER  -> { int a = pop(), b = peek(); push(a); push(b); ip++; }
            case ADD   -> { int b = pop(), a = pop(); push(a + b); ip++; }
            case SUB   -> { int b = pop(), a = pop(); push(a - b); ip++; }
            case MUL   -> { int b = pop(), a = pop(); push(a * b); ip++; }
            case DIV   -> { int b = pop(), a = pop(); push(b == 0 ? 0 : a / b); ip++; }
            case MOD   -> { int b = pop(), a = pop(); push(b == 0 ? 0 : a % b); ip++; }
            case NEG   -> { push(-pop()); ip++; }
            case AND   -> { push(pop() & pop()); ip++; }
            case OR    -> { push(pop() | pop()); ip++; }
            case XOR   -> { push(pop() ^ pop()); ip++; }
            case NOT   -> { push(~pop()); ip++; }
            case EQ    -> { push(pop() == pop() ? 1 : 0); ip++; }
            case LT    -> { int b = pop(), a = pop(); push(a < b ? 1 : 0); ip++; }
            case GT    -> { int b = pop(), a = pop(); push(a > b ? 1 : 0); ip++; }
            case JMP   -> ip = memory[++ip];
            case JZ    -> { int addr = memory[++ip]; ip = pop() == 0 ? addr : ip + 1; }
            case JNZ   -> { int addr = memory[++ip]; ip = pop() != 0 ? addr : ip + 1; }
            case CALL  -> { int addr = memory[++ip]; push(ip + 1); ip = addr; }
            case RET   -> ip = pop();
            case LOAD  -> { push(memory[memory[++ip]]); ip++; }
            case STORE -> { memory[memory[++ip]] = pop(); ip++; }
            case PRINT -> { output.append(pop()); output.append(' '); ip++; }
            case PRINTC-> { output.append((char) pop()); ip++; }
            case HALT  -> { running = false; }
            default    -> { System.out.println("  Unknown opcode: " + memory[ip]); ip++; }
        }
    }

    private void printState(Op op) {
        System.out.printf("  [IP=%3d] %-6s | Stack:", ip, op);
        for (int i = 0; i <= sp; i++) System.out.printf(" %d", stack[i]);
        System.out.println();
    }

    static int[] assemble(String source) {
        Map<String, Integer> labels = new HashMap<>();
        List<String> tokens = new ArrayList<>();

        for (String line : source.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith(";")) continue;
            if (line.endsWith(":")) {
                labels.put(line.substring(0, line.length()-1), tokens.size());
                continue;
            }
            for (String tok : line.split("\\s+")) {
                if (!tok.isEmpty() && !tok.startsWith(";")) tokens.add(tok);
            }
        }

        int[] program = new int[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            String tok = tokens.get(i);
            try {
                program[i] = Integer.parseInt(tok);
            } catch (NumberFormatException e) {
                if (labels.containsKey(tok)) {
                    program[i] = labels.get(tok);
                } else {
                    try {
                        program[i] = Op.valueOf(tok.toUpperCase()).code;
                    } catch (IllegalArgumentException ex) {
                        System.out.println("  Unknown token: " + tok);
                        program[i] = 0;
                    }
                }
            }
        }
        return program;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  💻 Stack-Based Virtual Machine");
        System.out.println("=".repeat(50));

        System.out.println("\n  --- Program 1: Fibonacci ---");
        String fibAsm = """
            PUSH 0
            PUSH 1
            loop:
            DUP
            PRINT
            SWAP
            OVER
            ADD
            DUP
            PUSH 1000
            LT
            JNZ loop
            POP
            POP
            HALT
        """;

        VirtualMachine vm1 = new VirtualMachine();
        vm1.load(VirtualMachine.assemble(fibAsm), 0);
        vm1.run();
        System.out.println("  Output: " + vm1.output);
        System.out.println("  Cycles: " + vm1.cycleCount);

        System.out.println("\n  --- Program 2: Factorial (5!) ---");
        int[] factorial = {
            1, 5,      // PUSH 5
            1, 1,      // PUSH 1    (accumulator)
            3,         // SWAP      [1, 5]
            // loop:
            3,         // DUP       [1, 5, 5]
            1, 1,      // PUSH 1
            31,        // LT        [1, 5, (5<1)]
            42, 22,    // JNZ end
            3,         // DUP       [1, 5, 5]
            4,         // SWAP      [1, 5, 5] → rearrange
            // multiply accumulator
            12,        // MUL
            4,         // SWAP
            1, 1,      // PUSH 1
            11,        // SUB
            40, 5,     // JMP loop
            // end:
            2,         // POP
            60,        // PRINT
            99         // HALT
        };

        VirtualMachine vm2 = new VirtualMachine();
        vm2.load(factorial, 0);
        vm2.run();
        System.out.println("  Output: " + vm2.output);

        System.out.println("\n  --- Program 3: Hello World ---");
        int[] hello = {
            1, 72, 61,    // H
            1, 101, 61,   // e
            1, 108, 61,   // l
            1, 108, 61,   // l
            1, 111, 61,   // o
            1, 32, 61,    // (space)
            1, 87, 61,    // W
            1, 111, 61,   // o
            1, 114, 61,   // r
            1, 108, 61,   // l
            1, 100, 61,   // d
            1, 33, 61,    // !
            99             // HALT
        };

        VirtualMachine vm3 = new VirtualMachine();
        vm3.load(hello, 0);
        vm3.run();
        System.out.println("  Output: " + vm3.output);

        System.out.println("\n  --- Program 4: Sum 1 to 10 ---");
        int[] sumProg = {
            1, 0,      // PUSH 0   (sum)
            1, 1,      // PUSH 1   (counter)
            // loop (ip=4):
            3,         // DUP
            1, 10,     // PUSH 10
            32,        // GT
            42, 18,    // JNZ done
            3,         // DUP
            4,         // SWAP
            // stack: [counter, sum, counter]
            // wait, let's fix: [sum, counter]
            // DUP → [sum, counter, counter]
            // SWAP after would mess up
            // Better approach:
            3,         // DUP       [sum, i, i]
            4, 4,      // SWAP×2    rearrange to add
            10,        // ADD       [sum+i, i]
            4,         // SWAP      [i, sum+i]
            1, 1,      // PUSH 1
            10,        // ADD       [i+1, sum+i]
            4,         // SWAP      [sum, i+1]
            40, 4,     // JMP loop
            // done (ip=18):
            2,         // POP counter
            60,        // PRINT sum
            99         // HALT
        };

        VirtualMachine vm4 = new VirtualMachine();
        vm4.load(sumProg, 0);
        vm4.run();
        System.out.println("  Output: " + vm4.output);
        System.out.println("  Cycles: " + vm4.cycleCount);
    }
}
