/** Temperature conversions between Celsius, Fahrenheit, and Kelvin. */
public final class Daily20261028Temperature {
    private Daily20261028Temperature() { }
    public static double toFahrenheit(double celsius) { return celsius * 9.0 / 5.0 + 32; }
    public static double toCelsius(double fahrenheit) { return (fahrenheit - 32) * 5.0 / 9.0; }
    public static double toKelvin(double celsius) {
        if (celsius < -273.15) throw new IllegalArgumentException("below absolute zero");
        return celsius + 273.15;
    }
    public static void main(String[] args) {
        System.out.printf("20 C = %.1f F, %.2f K%n", toFahrenheit(20), toKelvin(20));
    }
}
