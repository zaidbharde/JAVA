import java.io.*;

public class BmpStegHide {
    public static void main(String[] args) throws Exception {
        RandomAccessFile raf = new RandomAccessFile("img.bmp","rw");
        raf.seek(54); // pixel data

        String msg = "AMMAAR";
        byte[] b = msg.getBytes();
        raf.write(b.length); // length stored in LSB stream

        for (byte x : b) {
            for (int i=0;i<8;i++) {
                int pixel = raf.read();
                int bit = (x >> i) & 1;
                pixel = (pixel & 0xFE) | bit;
                raf.seek(raf.getFilePointer()-1);
                raf.write(pixel);
            }
        }
        raf.close();
    }
}
