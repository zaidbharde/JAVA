public class FractalRenderer {

    static final String[] PALETTE = {" ", ".", ":", "-", "=", "+", "*", "#", "%", "@"};
    static final String[] COLORS = {
        "\033[34m", "\033[94m", "\033[36m", "\033[96m", "\033[32m",
        "\033[92m", "\033[33m", "\033[93m", "\033[91m", "\033[31m"
    };
    static final String RESET = "\033[0m";

    static int mandelbrot(double cx, double cy, int maxIter) {
        double zx = 0, zy = 0;
        int i = 0;
        while (zx * zx + zy * zy < 4 && i < maxIter) {
            double tmp = zx * zx - zy * zy + cx;
            zy = 2 * zx * zy + cy;
            zx = tmp;
            i++;
        }
        return i;
    }

    static int julia(double zx, double zy, double cx, double cy, int maxIter) {
        int i = 0;
        while (zx * zx + zy * zy < 4 && i < maxIter) {
            double tmp = zx * zx - zy * zy + cx;
            zy = 2 * zx * zy + cy;
            zx = tmp;
            i++;
        }
        return i;
    }

    static int burningShip(double cx, double cy, int maxIter) {
        double zx = 0, zy = 0;
        int i = 0;
        while (zx * zx + zy * zy < 4 && i < maxIter) {
            double tmp = zx * zx - zy * zy + cx;
            zy = Math.abs(2 * zx * zy) + cy;
            zx = tmp;
            i++;
        }
        return i;
    }

    static void renderMandelbrot(int width, int height, int maxIter,
                                  double cx, double cy, double zoom, boolean color) {
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder("  ");
            for (int x = 0; x < width; x++) {
                double real = cx + (x - width / 2.0) / (width / 4.0) / zoom;
                double imag = cy + (y - height / 2.0) / (height / 2.0) / zoom;
                int iter = mandelbrot(real, imag, maxIter);
                appendPixel(row, iter, maxIter, color);
            }
            System.out.println(row);
        }
    }

    static void renderJulia(int width, int height, int maxIter,
                             double jcx, double jcy, boolean color) {
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder("  ");
            for (int x = 0; x < width; x++) {
                double zx = (x - width / 2.0) / (width / 4.0);
                double zy = (y - height / 2.0) / (height / 2.0);
                int iter = julia(zx, zy, jcx, jcy, maxIter);
                appendPixel(row, iter, maxIter, color);
            }
            System.out.println(row);
        }
    }

    static void renderBurningShip(int width, int height, int maxIter, boolean color) {
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder("  ");
            for (int x = 0; x < width; x++) {
                double real = -2.0 + x * 3.5 / width;
                double imag = -2.0 + y * 3.5 / height;
                int iter = burningShip(real, imag, maxIter);
                appendPixel(row, iter, maxIter, color);
            }
            System.out.println(row);
        }
    }

    static void renderSierpinski(int size) {
        for (int y = size - 1; y >= 0; y--) {
            System.out.print("  " + " ".repeat(y));
            for (int x = 0; x <= size - y; x++)
                System.out.print((x & y) == 0 ? "▲ " : "  ");
            System.out.println();
        }
    }

    private static void appendPixel(StringBuilder row, int iter, int maxIter, boolean color) {
        if (iter == maxIter) {
            row.append(color ? "\033[30m██\033[0m" : "  ");
        } else {
            int idx = iter % PALETTE.length;
            if (color) {
                row.append(COLORS[idx]).append("██").append(RESET);
            } else {
                String ch = PALETTE[idx];
                row.append(ch).append(ch);
            }
        }
    }

    public static void main(String[] args) {
        boolean color = true;

        System.out.println("=".repeat(60));
        System.out.println("  🌀 Fractal Renderer");
        System.out.println("=".repeat(60));

        System.out.println("\n  Mandelbrot Set:");
        renderMandelbrot(60, 25, 80, -0.5, 0, 1, color);

        System.out.println("\n  Mandelbrot (Zoomed — Seahorse Valley):");
        renderMandelbrot(60, 20, 150, -0.745, 0.186, 30, color);

        System.out.println("\n  Julia Set (c = -0.7 + 0.27i):");
        renderJulia(60, 25, 80, -0.7, 0.27015, color);

        System.out.println("\n  Julia Set (c = -0.8 + 0.156i):");
        renderJulia(60, 20, 80, -0.8, 0.156, color);

        System.out.println("\n  Burning Ship Fractal:");
        renderBurningShip(60, 20, 80, color);

        System.out.println("\n  Sierpinski Triangle (order 32):");
        renderSierpinski(32);
    }
}
