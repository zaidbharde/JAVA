import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PasswordManager {

    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, Entry> vault = new LinkedHashMap<>();
    private final SecretKey key;

    record Entry(String site, String username, byte[] nonce,
                 byte[] encryptedPassword, long created) {}

    public PasswordManager(String masterPassword) throws Exception {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, 65536, 256);
        byte[] derived = factory.generateSecret(spec).getEncoded();
        spec.clearPassword();
        key = new SecretKeySpec(derived, "AES");
    }

    private byte[] encrypt(String plaintext, byte[] nonce) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    private String decrypt(byte[] ciphertext, byte[] nonce) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }

    public void store(String site, String username, String password) throws Exception {
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        RANDOM.nextBytes(nonce);
        vault.put(site, new Entry(site, username, nonce, encrypt(password, nonce), System.currentTimeMillis()));
    }

    public String retrieve(String site) throws Exception {
        Entry entry = vault.get(site);
        if (entry == null) return null;
        return decrypt(entry.encryptedPassword, entry.nonce);
    }

    public void delete(String site) {
        vault.remove(site);
    }

    public static String generate(int length, boolean symbols) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (symbols) chars += "!@#$%^&*()-_=+[]{}|;:,.<>?";
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }

    public static int strength(String password) {
        int score = 0;
        if (password.length() >= 8)  score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*"))   score++;
        if (password.matches(".*[^a-zA-Z0-9].*")) score++;
        return score;
    }

    public static String strengthLabel(int score) {
        if (score <= 2) return "❌ Weak";
        if (score <= 4) return "⚠️  Medium";
        if (score <= 5) return "✅ Strong";
        return "💪 Very Strong";
    }

    public void listSites() {
        System.out.println("\n  Stored passwords:");
        System.out.printf("  %-15s %-15s %-20s%n", "Site", "Username", "Stored");
        System.out.println("  " + "─".repeat(50));
        vault.forEach((site, e) ->
            System.out.printf("  %-15s %-15s %-20s%n",
                e.site, e.username, new Date(e.created)));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("  Password Manager");
        System.out.println("=".repeat(50));

        PasswordManager pm = new PasswordManager(generate(32, true));

        pm.store("github.com",   "alice",   "MyGitP@ss123");
        pm.store("google.com",   "alice42", "G00gl3!Secure");
        pm.store("twitter.com",  "bob",     generate(20, true));
        pm.store("amazon.com",   "charlie", generate(16, false));

        pm.listSites();

        System.out.println("\n  Retrieved passwords:");
        for (String site : List.of("github.com", "google.com", "twitter.com", "amazon.com")) {
            String pwd = pm.retrieve(site);
            int s = strength(pwd);
            System.out.printf("  %-15s : %-25s %s (score: %d)%n",
                site, pwd, strengthLabel(s), s);
        }

        System.out.println("\n  Generated passwords:");
        for (int len : new int[]{8, 12, 16, 20}) {
            String pwd = generate(len, true);
            System.out.printf("  len=%-2d : %-25s %s%n", len, pwd, strengthLabel(strength(pwd)));
        }

        System.out.println("\n  Strength checker:");
        for (String pwd : new String[]{"abc", "password", "P@ssw0rd", "Xy$9kL!mN3pQ", "a1B2c3D4e5F6g7H8!"}) {
            System.out.printf("  %-25s → %s%n", pwd, strengthLabel(strength(pwd)));
        }
    }
}
