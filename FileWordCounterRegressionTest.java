import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public final class FileWordCounterRegressionTest {
    public static void main(String[] args) throws Exception {
        Path input = Files.createTempFile("word-counter", ".txt");
        Locale original = Locale.getDefault();
        try {
            Files.writeString(input, "TITLE title");
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Map<String, Long> counts = FileWordCounter.countWords(input.toString());
            if (!counts.equals(Map.of("title", 2L))) {
                throw new AssertionError("case normalization should be locale independent: " + counts);
            }
        } finally {
            Locale.setDefault(original);
            Files.deleteIfExists(input);
        }
        System.out.println("FileWordCounter locale regression passed");
    }
}
