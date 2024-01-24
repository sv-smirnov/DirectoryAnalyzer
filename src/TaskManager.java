import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class TaskManager {
    public Task getTask() {
        Scanner scanner = new Scanner(System.in);
        Task task = new Task();

        System.out.println("Please, enter the absolute path...");
        task.path = scanner.nextLine();
        while (!validatePath(task.path)) {
            System.out.println("The selected directory does not exist, try again...");
            task.path = scanner.nextLine();
        }

        System.out.println("Please, enter recursive mode (true/false)...");
        while (scanner.hasNextLine() && !scanner.hasNextBoolean()) {
            System.out.println("Type only 'true' or 'false'");
            scanner.nextLine();
        }
        task.recursive = scanner.nextBoolean();

        if (task.recursive) {
            System.out.println("Please, enter recursive traversal depth...");
            while (scanner.hasNextLine() && !scanner.hasNextInt()) {
                System.out.println("Type number of depth...");
                scanner.nextLine();
            }
            task.depth = scanner.nextInt();
            task.depth = (task.depth > 0) ? task.depth : 1;
        }

        System.out.println("Please, enter number of threads...");
        while (scanner.hasNextLine() && !scanner.hasNextInt()) {
            System.out.println("Type number of threads...");
            scanner.nextLine();
        }
        task.threads = scanner.nextInt();
        task.threads = (task.threads > 0 && task.threads < 10) ? task.threads : 1;

        System.out.println("Please, enter extensions of analyzing files...");
        scanner.nextLine();
        task.includeExt = scanner.nextLine();

        System.out.println("Please, enter extensions do not include...");
        task.excludeExt = scanner.nextLine();
        return task;
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