import java.io.IOException;

public class AnalyzerApp {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new TaskManager();
        DirectoryAnalyzer directoryAnalyzer = new DirectoryAnalyzer(taskManager.getTask());
        System.out.println(String.join("\n", directoryAnalyzer.scanPath()));
    }
}