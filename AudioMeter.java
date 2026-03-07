// AudioMeter.java
import javax.sound.sampled.*;

public class AudioMeter {
    public static void main(String[] args) throws Exception {
        AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(fmt);
        line.start();
        byte[] buf = new byte[4096];
        while (true) {
            int r = line.read(buf, 0, buf.length);
            double sum = 0;
            for (int i=0; i<r; i+=2) {
                int val = (buf[i+1]<<8) | (buf[i]&0xFF);
                sum += val*val;
            }
            double rms = Math.sqrt(sum / (r/2));
            System.out.printf("RMS: %.2f%n", rms);
            Thread.sleep(200);
        }
    }
}
