import java.util.List;

public final class CsvRowValidatorRegressionTest {
    public static void main(String[] args) {
        CsvRowValidator.Result valid = CsvRowValidator.validate("\"a,b\",\"say \"\"hi\"\"\",7", 3, 2);
        check(valid.valid(), "well-formed quoted fields should pass: " + valid.issues());
        check(valid.values().equals(List.of("a,b", "say \"hi\"", "7")), "quoted values should decode");

        expectInvalid("\"unfinished", 2, -1);
        expectInvalid("ab\"cd,x", 2, -1);
        expectInvalid("\"ok\"tail,x", 2, -1);
        expectInvalid("a,b", 2, 2);
        System.out.println("CsvRowValidator regressions passed");
    }

    private static void expectInvalid(String row, int columns, int integerColumn) {
        try {
            CsvRowValidator.validate(row, columns, integerColumn);
            throw new AssertionError("invalid row was accepted: " + row);
        } catch (IllegalArgumentException expected) {
            // Invalid CSV syntax or column configuration is rejected.
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
