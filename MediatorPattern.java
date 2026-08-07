import java.util.*;

interface ChatMediator {
    void sendMessage(String message, User sender);
    void addUser(User user);
}

class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) { users.add(user); }

    public void sendMessage(String message, User sender) {
        for (User u : users) {
            if (u != sender) u.receive(message, sender.getName());
        }
    }
}

class User {
    private String name;
    private ChatMediator mediator;

    public User(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public String getName() { return name; }

    public void send(String message) {
        mediator.sendMessage(message, this);
    }

    public void receive(String message, String from) {
        System.out.println(name + " received from " + from + ": " + message);
    }
}

public class MediatorPattern {
    public static void main(String[] args) {
        ChatRoom room = new ChatRoom();
        User zaid = new User("Zaid", room);
        User ansari = new User("Ansari", room);
        User baig = new User("Baig", room);

        room.addUser(zaid); room.addUser(ansari); room.addUser(baig);
        zaid.send("Hey team, FORESIGHT deployment ready!");
    }
}
