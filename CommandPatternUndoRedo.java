import java.util.*;

interface Command {
    void execute();
    void undo();
}

public class CommandPatternUndoRedo {
    static StringBuilder document = new StringBuilder();
    static Deque<Command> undoStack = new ArrayDeque<>();
    static Deque<Command> redoStack = new ArrayDeque<>();

    static class AppendCommand implements Command {
        String text;
        AppendCommand(String text) { this.text = text; }
        public void execute() { document.append(text); }
        public void undo() { document.setLength(document.length() - text.length()); }
    }

    static void runCommand(Command cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
    }

    static void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    static void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }

    public static void main(String[] args) {
        runCommand(new AppendCommand("Hello"));
        runCommand(new AppendCommand(" World"));
        System.out.println(document);
        undo();
        System.out.println(document);
        redo();
        System.out.println(document);
    }
}
