import java.util.*;

interface Observer {
    void update(float temperature);
}

class WeatherStation {
    private List<Observer> observers = new ArrayList<>();
    private float temperature;

    public void addObserver(Observer o) { observers.add(o); }

    public void setTemperature(float temp) {
        this.temperature = temp;
        notifyObservers();
    }

    private void notifyObservers() {
        for (Observer o : observers) o.update(temperature);
    }
}

class PhoneDisplay implements Observer {
    public void update(float temperature) {
        System.out.println("Phone Display: Temperature updated to " + temperature + "°C");
    }
}

class TVDisplay implements Observer {
    public void update(float temperature) {
        System.out.println("TV Display: Temperature updated to " + temperature + "°C");
    }
}

public class ObserverPattern {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();
        station.addObserver(new PhoneDisplay());
        station.addObserver(new TVDisplay());

        station.setTemperature(28.5f);
        station.setTemperature(31.0f);
    }
}
