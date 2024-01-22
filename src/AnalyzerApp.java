import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class AnalyzerApp {
    public static void main(String[] args) throws IOException {
        String path;
        Boolean recursive;
        Integer maxDepth = 1;
        Integer threadsNumber = 1;
        String includeExt;
        String excludeExt;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please, enter the absolute path...");
        path = scanner.nextLine();
        while (!validatePath(path)) {
            System.out.println("The selected directory does not exist, try again...");
            path = scanner.nextLine();
        }

        System.out.println("Please, enter recursive mode (true/false)...");
        while (scanner.hasNextLine() && !scanner.hasNextBoolean()) {
            System.out.println("Type only 'true' or 'false'");
            scanner.nextLine();
        }
        recursive = scanner.nextBoolean();

        if (recursive) {
            System.out.println("Please, enter recursive traversal depth...");
            while (scanner.hasNextLine() && !scanner.hasNextInt()) {
                System.out.println("Type number of depth...");
                scanner.nextLine();
            }
            maxDepth = scanner.nextInt();
            maxDepth = (maxDepth > 0) ? maxDepth : 1;
        }

        System.out.println("Please, enter number of threads...");
        while (scanner.hasNextLine() && !scanner.hasNextInt()) {
            System.out.println("Type number of threads...");
            scanner.nextLine();
        }
        threadsNumber = scanner.nextInt();
        threadsNumber = (threadsNumber > 0 && threadsNumber < 10) ? threadsNumber : 1;

        System.out.println("Please, enter extensions of analyzing files...");
        scanner.nextLine();
        includeExt = scanner.nextLine();

        System.out.println("Please, enter extensions do not include...");
        excludeExt = scanner.nextLine();
        //excludeExt = scanner.nextLine().split("\\s+|,\\s*|\\.\\s*");

        DirectoryAnalyzer directoryAnalyzer = new DirectoryAnalyzer(path, recursive, maxDepth, threadsNumber, includeExt, excludeExt);
        directoryAnalyzer.scanPath();
    }


    private static Boolean validatePath(String directory) {
        try {
            Path path = Paths.get(directory);
            return Files.exists(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }
}
