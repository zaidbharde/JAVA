```java
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class BmpHeaderParser {

    private static final int BMP_MAGIC_OFFSET = 0;
    private static final int WIDTH_OFFSET     = 18;
    private static final int HEIGHT_OFFSET    = 22;
    private static final int BITSPERPIXEL_OFFSET = 28;
    private static final byte BMP_MAGIC_1 = 'B';
    private static final byte BMP_MAGIC_2 = 'M';
    private static final short DEFAULT_BITS = 0;
    private static final String USAGE = "Usage: java BmpHeaderParser <path-to-bmp> [offset]";

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length > 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Error: Cannot read file: " + args[0]);
            System.exit(1);
        }

        boolean offsetMode = args.length > 1;
        int offset = offsetMode ? Integer.parseInt(args[1]) : 0;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileSize = raf.length();
            if (fileSize <= BITSPERPIXEL_OFFSET + 2 && !offsetMode) {
                System.out.println("Error: File too small for BMP header.");
                System.exit(1);
            }

            raf.seek(offset);

            if (!verifyBmpMagic(raf)) {
                System.out.println("Warning: Not a valid BMP file (magic bytes mismatch).");
            }

            raf.seek(WIDTH_OFFSET + offset);
            int width = reverseBytesInt(raf.readInt());

            raf.seek(HEIGHT_OFFSET + offset);
            int height = reverseBytesInt(raf.readInt());

            raf.seek(BITSPERPIXEL_OFFSET + offset);
            short bitsPerPixel = reverseBytesShort(raf.readShort());

            int rowsPerPlane = reverseBytesShort(raf.readShort());

            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║          BMP Header Information      ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.println("Width:              " + width);
            System.out.println("Height:             " + height);
            System.out.println("Bits Per Pixel:     " + bitsPerPixel);
            System.out.println("Planes:             " + rowsPerPlane);
            System.out.println("Offset Used:        " + (offsetMode ? offset : "Default"));
            System.out.println("Total Size Bytes:   " + fileSize);
            System.out.println("├──────────────────────────────────────┤");

            String bitColorDepth = getBitColorDepth(bitsPerPixel);
            System.out.printf("Color Depth:        %s (%d-bit)%n", bitColorDepth, bitsPerPixel);
            System.out.println("├──────────────────────────────────────┤");

            int estimatedPixelCount = Math.abs(width) * Math.abs(height);
            System.out.printf("Estimated Pixels:   %,d%n", estimatedPixelCount);

            int minEstimateBits = estimatedPixelCount * bitsPerPixel;
            System.out.printf("Minimum Image Data: %,d bytes%n", (minEstimateBits + 7) / 8);
            System.out.println("╚══════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static boolean verifyBmpMagic(RandomAccessFile raf) throws IOException {
        long pos = raf.getFilePointer();
        raf.seek(pos);
        byte magic1 = raf.readByte();
        raf.seek(pos + 1);
        byte magic2 = raf.readByte();
        return (magic1 == BMP_MAGIC_1) && (magic2 == BMP_MAGIC_2);
    }

    private static int reverseBytesInt(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).flip().getInt();
    }

    private static short reverseBytesShort(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value).flip().getShort();
    }

    private static String getBitColorDepth(int bits) {
        switch (bits) {
            case 1: return "Monochrome (Black/White)";
            case 4: return "16 Colors (NIBBLE)";
            case 8: return "256 Colors (PALETTE)";
            case 16: return "High Color (5-6-5)";
            case 24: return "True Color (RGB)";
            case 32: return "Full RGBA (with Alpha)";
            default: return "Unknown (" + bits + ")";
        }
    }
}
```
