import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    String path;
    Boolean recursive;
    Integer maxDepth;
    Integer threadsNumber;
    String includeExt;
    String excludeExt;
    List<Path> files;
    public ConcurrentHashMap<String, FileStatistic> fileStatistics = new ConcurrentHashMap<>();
    private volatile FileStatistic fileStatistic;

    public DirectoryAnalyzer(String path, Boolean recursive, Integer maxDepth, Integer threadsNumber, String includeExt, String excludeExt) {
        this.path = path;
        this.recursive = recursive;
        this.maxDepth = maxDepth;
        this.threadsNumber = threadsNumber;
        this.includeExt = includeExt;
        this.excludeExt = excludeExt;
    }

    public synchronized void scanPath() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);
        Stream<Path> pathStream = Files.walk(Paths.get(path), maxDepth);
        files = pathStream.filter(file -> !Files.isDirectory(file)
                        && includeExt.contains(file.getFileName().toString().substring(file.getFileName().toString().indexOf(".") + 1))
                        && !excludeExt.contains(file.getFileName().toString().substring(file.getFileName().toString().indexOf(".") + 1)))
                .collect(Collectors.toList());
        System.out.println("В указанной директории обнаружено " + files.size() + " файлов указанного типа");
        for (int i = 0; i < files.size(); i++) {
            Path file = files.get(i);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Поток: " + Thread.currentThread().getName() + ". Обрабатывает файл: " + file.getFileName());
                        String extension = file.getFileName().toString().substring(file.getFileName().toString().indexOf(".") + 1);
                        synchronized (fileStatistics) {
                            if (!fileStatistics.containsKey(extension)) {
                                fileStatistic = new FileStatistic();
                                fileStatistic.extension = extension;
                                fileStatistics.put(extension, fileStatistic);
                            } else {
                                fileStatistic = fileStatistics.get(extension);
                            }
                            fileStatistic.count = fileStatistic.count + 1;
                            fileStatistic.size = fileStatistic.size + Files.size(file);
                            if (Files.isReadable(file)) {
                                Stream<String> lines = Files.lines(file, Charset.defaultCharset());
                                lines.forEach(line -> {
                                    fileStatistic.lines = fileStatistic.lines + 1;
                                    if (!Objects.equals(line, "")) {
                                        fileStatistic.nonEmptyLines = fileStatistic.nonEmptyLines + 1;
                                    }
                                    if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                                        fileStatistic.comments = fileStatistic.comments + 1;
                                    }
                                });
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UncheckedIOException e) {
                        System.out.println("В директории имеются файлы с неизвестной кодировкой!");
                    }
                }
            });
        }
        ;
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (HashMap.Entry<String, FileStatistic> entry : fileStatistics.entrySet()) {
                System.out.println(entry.getValue().toString());
            }
        }
    }
}
