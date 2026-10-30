import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public final class PasswordManagerRegressionTest {
    public static void main(String[] args) throws Exception {
        PasswordManager manager = new PasswordManager("test-master-password");
        manager.store("one.example", "user", "same-password");
        manager.store("two.example", "user", "same-password");
        check("same-password".equals(manager.retrieve("one.example")), "first password round trip");
        check("same-password".equals(manager.retrieve("two.example")), "second password round trip");

        Field vaultField = PasswordManager.class.getDeclaredField("vault");
        vaultField.setAccessible(true);
        Map<?, ?> vault = (Map<?, ?>) vaultField.get(manager);
        Object first = vault.get("one.example");
        Object second = vault.get("two.example");
        byte[] firstNonce = component(first, "nonce");
        byte[] secondNonce = component(second, "nonce");
        byte[] firstCiphertext = component(first, "encryptedPassword");
        byte[] secondCiphertext = component(second, "encryptedPassword");
        check(!Arrays.equals(firstNonce, secondNonce), "each stored entry needs a fresh nonce");
        check(!Arrays.equals(firstCiphertext, secondCiphertext), "equal plaintexts must not have identical ciphertexts");
        System.out.println("PasswordManager encryption regressions passed");
    }

    private static byte[] component(Object entry, String name) throws Exception {
        Method accessor = entry.getClass().getDeclaredMethod(name);
        accessor.setAccessible(true);
        return (byte[]) accessor.invoke(entry);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
