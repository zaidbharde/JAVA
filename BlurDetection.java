import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class BlurDetection {

    private static final double DEFAULT_THRESHOLD = 100.0;

    private static double computeVarianceOfLaplacian(BufferedImage img) {
        int width  = img.getWidth();
        int height = img.getHeight();

        double sum = 0.0;
        double sumSq = 0.0;
        int count = 0;

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                double center = gray(img.getRGB(x, y));
                double up     = gray(img.getRGB(x, y - 1));
                double down   = gray(img.getRGB(x, y + 1));
                double left   = gray(img.getRGB(x - 1, y));
                double right  = gray(img.getRGB(x + 1, y));

                double lap = up + down + left + right - 4 * center;

                sum += lap;
                sumSq += lap * lap;
                count++;
            }
        }

        double mean = sum / count;
        return (sumSq / count) - (mean * mean);
    }

    private static double gray(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return 0.299 * r + 0.587 * g + 0.114 * b;
    }

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: java BlurDetection <image-path> [threshold]");
            return;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File not found: " + args[0]);
            return;
        }

        double threshold = args.length > 1
                ? Double.parseDouble(args[1])
                : DEFAULT_THRESHOLD;

        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            System.out.println("Unsupported or corrupted image file.");
            return;
        }

        double variance = computeVarianceOfLaplacian(image);

        System.out.println("📷 Blur Score (Variance of Laplacian): " + variance);
        System.out.println("Threshold: " + threshold);

        if (variance < threshold) {
            System.out.println("❌ Image is likely BLURRY");
        } else {
            System.out.println("✅ Image is SHARP");
        }
    }
}
