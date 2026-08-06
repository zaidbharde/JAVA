import java.util.ArrayList;
import java.util.List;

abstract class Animal {
    private final String name;

    protected Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract void makeSound();

    public void sleep() {
        System.out.println(name + " is sleeping... Zzz");
    }
}

interface Pet {
    void play();
}

class Dog extends Animal implements Pet {
    public Dog(String name) {
        super(name);
    }

    @Override
    void makeSound() {
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
    void makeSound() {
        System.out.println(getName() + " meows: Meow~");
    }

    @Override
    public void play() {
        System.out.println(getName() + " is chasing a laser pointer.");
    }
}

class Lion extends Animal {
    public Lion(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(getName() + " roars: ROAR!");
    }
}

public class BIO {
    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog("Buddy"));
        animals.add(new Cat("Luna"));
        animals.add(new Lion("Simba"));

        for (Animal animal : animals) {
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
}
