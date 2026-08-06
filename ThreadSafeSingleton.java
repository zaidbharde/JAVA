public class ThreadSafeSingleton {
    private static volatile ThreadSafeSingleton instance;
    private int usageCount = 0;

    private ThreadSafeSingleton() {
        System.out.println("Instance created");
    }

    public static ThreadSafeSingleton getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }

    public void use() {
        usageCount++;
        System.out.println("Used " + usageCount + " times");
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> ThreadSafeSingleton.getInstance().use();
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        t1.join(); t2.join();
    }
}
