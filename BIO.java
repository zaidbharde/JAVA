import java.util.ArrayList;
import java.util.List;

/**
 * Base type for every animal in the zoo. Holds shared state (name)
 * and behavior (sleep), and forces subclasses to define their own sound.
 */
abstract class Animal {

    private final String name;

    protected Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** Each concrete animal must define how it sounds. */
    protected abstract void makeSound();

    public void sleep() {
        System.out.println(name + " is sleeping... Zzz");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name='" + name + "'}";
    }
}

/** Marks an animal as domesticated / capable of playing with a human. */
interface Pet {
    void play();
}

class Dog extends Animal implements Pet {

    public Dog(String name) {
        super(name);
    }

    @Override
    protected void makeSound() {
        System.out.println(getName() + " barks: Woof! Woof!");
    }

    @Override
    public void play() {
        System.out.println(getName() + " is fetching a ball.");
    }
}

class Cat extends Animal implements Pet {

    public Cat(String name) {
        super(name);
    }

    @Override
    protected void makeSound() {
        System.out.println(getName() + " meows: Meow~");
    }

    @Override
    public void play() {
        System.out.println(getName() + " is chasing a laser pointer.");
    }
}

class Bird extends Animal implements Pet {

    public Bird(String name) {
        super(name);
    }

    @Override
    protected void makeSound() {
        System.out.println(getName() + " chirps: Tweet tweet!");
    }

    @Override
    public void play() {
        System.out.println(getName() + " is fluttering around its perch.");
    }
}

class Lion extends Animal {

    public Lion(String name) {
        super(name);
    }

    @Override
    protected void makeSound() {
        System.out.println(getName() + " roars: ROAR!");
    }
}

public class BIO {

    public static void main(String[] args) {
        List<Animal> animals = buildZoo();

        for (Animal animal : animals) {
            interactWith(animal);
        }
    }

    private static List<Animal> buildZoo() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog("Buddy"));
        animals.add(new Cat("Luna"));
        animals.add(new Bird("Kiwi"));
        animals.add(new Lion("Simba"));
        return animals;
    }

    private static void interactWith(Animal animal) {
        System.out.println("--- Interaction with " + animal.getClass().getSimpleName() + " ---");
        animal.makeSound();
        animal.sleep();

        if (animal instanceof Pet pet) {
            pet.play();
        } else {
            System.out.println(animal.getName() + " is a wild animal and doesn't play.");
        }

        System.out.println();
    }
}
