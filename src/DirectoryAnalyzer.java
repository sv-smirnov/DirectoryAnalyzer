import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryAnalyzer {
    private Task task;
    private List<Path> files;
    private ConcurrentHashMap<String, FileStatistic> fileStatistics = new ConcurrentHashMap<>();

    public DirectoryAnalyzer(Task task) {
        this.task = task;
    }

    public ArrayList<String> scanPath() throws IOException {
        ArrayList<String> result = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(task.threads);
        Stream<Path> pathStream = Files.walk(Paths.get(task.path), task.depth);
        files = pathStream.filter(file -> !Files.isDirectory(file)
                        && task.includeExt.contains(file.getFileName().toString().substring(file.getFileName().toString().indexOf(".") + 1))
                        && !task.excludeExt.contains(file.getFileName().toString().substring(file.getFileName().toString().indexOf(".") + 1)))
                .collect(Collectors.toList());
        System.out.println("В указанной директории обнаружено " + files.size() + " файлов указанного типа");
        for (int i = 0; i < files.size(); i++) {
            Path file = files.get(i);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Поток: " + Thread.currentThread().getName() + ". Обрабатывает файл: " + file.getFileName());
                    String fileName = file.getFileName().toString();
                    String extension = !fileName.isEmpty() ? fileName.substring(fileName.lastIndexOf(".") + 1) : " ";
                    FileStatistic fileStatistic = new FileStatistic(extension);
                    fileStatistic.count = 1;
                    try {
                        fileStatistic.size = Files.size(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        if (Files.isReadable(file)) {
                            Stream<String> lines = Files.lines(file, Charset.defaultCharset());
                            lines.forEach(line -> {
                                fileStatistic.increaseLines(1L);
                                if (!Objects.equals(line, "")) {
                                    fileStatistic.increaseNonEmptyLines(1L);
                                }
                                if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                                    fileStatistic.increaseComments(1L);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UncheckedIOException e) {
                        System.out.println("В директории имеются файлы с неизвестной кодировкой!");
                    }
                    if (!fileStatistics.containsKey(extension)) {
                        fileStatistics.putIfAbsent(extension, new FileStatistic(extension));
                    }
                    fileStatistics.get(extension)
                            .increaseCount()
                            .increaseSize(fileStatistic.getSize())
                            .increaseLines(fileStatistic.getLines())
                            .increaseNonEmptyLines(fileStatistic.getNonEmptyLines())
                            .increaseComments(fileStatistic.getComments());
                }
            });
        }
        ;
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                System.out.println("Directory analyze completed!");
            } else {
                List<Runnable> interruptedThreads = executorService.shutdownNow();
                System.out.printf("At least " + interruptedThreads.size() + " files not scanned!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (HashMap.Entry<String, FileStatistic> entry : fileStatistics.entrySet()) {
                result.add(entry.getValue().toString());
            }
        }
        return result;
    }
}
