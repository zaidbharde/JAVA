import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class Encryption {

    static String caesarEncrypt(String text, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                sb.append((char)((c - base + shift) % 26 + base));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    static String caesarDecrypt(String text, int shift) {
        return caesarEncrypt(text, 26 - (shift % 26));
    }

    static String vigenereEncrypt(String text, String key) {
        StringBuilder sb = new StringBuilder();
        key = key.toUpperCase();
        int ki = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = key.charAt(ki % key.length()) - 'A';
                sb.append((char)((c - base + shift) % 26 + base));
                ki++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    static String vigenereDecrypt(String text, String key) {
        StringBuilder invertedKey = new StringBuilder();
        for (char c : key.toUpperCase().toCharArray())
            invertedKey.append((char)((26 - (c - 'A')) % 26 + 'A'));
        return vigenereEncrypt(text, invertedKey.toString());
    }

    static String xorEncrypt(String text, String key) {
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes  = key.getBytes(StandardCharsets.UTF_8);
        byte[] result    = new byte[textBytes.length];
        for (int i = 0; i < textBytes.length; i++)
            result[i] = (byte)(textBytes[i] ^ keyBytes[i % keyBytes.length]);
        return Base64.getEncoder().encodeToString(result);
    }

    static String xorDecrypt(String encoded, String key) {
        byte[] data     = Base64.getDecoder().decode(encoded);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result   = new byte[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = (byte)(data[i] ^ keyBytes[i % keyBytes.length]);
        return new String(result, StandardCharsets.UTF_8);
    }

    static String generateKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }

    public static void main(String[] args) {
        String message = "Attack at dawn";

        System.out.println("=".repeat(50));
        System.out.println("  Encryption Algorithms");
        System.out.println("=".repeat(50));

        System.out.println("\n  Original: " + message);

        System.out.println("\n  --- Caesar (shift=3) ---");
        String c1 = caesarEncrypt(message, 3);
        System.out.println("  Encrypted: " + c1);
        System.out.println("  Decrypted: " + caesarDecrypt(c1, 3));

        System.out.println("\n  --- Vigenere (key=SECRET) ---");
        String v1 = vigenereEncrypt(message, "SECRET");
        System.out.println("  Encrypted: " + v1);
        System.out.println("  Decrypted: " + vigenereDecrypt(v1, "SECRET"));

        String key = generateKey(16);
        System.out.println("\n  --- XOR (key=" + key + ") ---");
        String x1 = xorEncrypt(message, key);
        System.out.println("  Encrypted: " + x1);
        System.out.println("  Decrypted: " + xorDecrypt(x1, key));
    }
}
