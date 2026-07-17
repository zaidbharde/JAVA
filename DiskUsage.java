import java.io.File;

public class DiskUsage {
    public static void main(String[] args) {
        for (File root : File.listRoots()) {
            long total = root.getTotalSpace();
            long free = root.getFreeSpace();
            System.out.printf("%s : %.2f%% used (%.2f GB free of %.2f GB)%n",
                    root.getAbsolutePath(),
                    100.0 * (total - free) / total,
                    free / 1e9,
                    total / 1e9);
        }
    }
}
