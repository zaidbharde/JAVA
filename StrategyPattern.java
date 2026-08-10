interface PaymentStrategy {
    void pay(int amount);
}

class UpiPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid Rs." + amount + " via UPI");
    }
}

class CardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid Rs." + amount + " via Card");
    }
}

class ShoppingCart {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void checkout(int amount) {
        strategy.pay(amount);
    }
}

public class StrategyPattern {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.setStrategy(new UpiPayment());
        cart.checkout(500);

        cart.setStrategy(new CardPayment());
        cart.checkout(1200);
    }
}
