// Base class
class Animal {
    void makeSound() {
        System.out.println("Some generic animal sound");
    }
}

// Derived class
class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks");
    }
}

class Cat extends Animal {
    @Override
    void makeSound() {
        System.out.println("Cat meows");
    }
}

public class BIO {
    public static void main(String[] args) {
        Animal myAnimal = new Animal();
        Animal myDog = new Dog();  // Polymorphism
        Animal myCat = new Cat();  // Polymorphism

        myAnimal.makeSound();
        myDog.makeSound();
        myCat.makeSound();
    }
}
