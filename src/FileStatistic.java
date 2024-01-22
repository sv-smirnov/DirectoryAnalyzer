public class FileStatistic {
    volatile String extension = "";
    volatile Integer count = 0;
    volatile Long size = 0L;
    volatile Long lines = 0L;
    volatile Long nonEmptyLines = 0L;
    volatile Long comments = 0L;

    @Override
    public String toString() {
        return "FileStatistic{" +
                "extension='" + extension + '\'' +
                ", count=" + count +
                ", size=" + size +
                ", lines=" + lines +
                ", nonEmptyLines=" + nonEmptyLines +
                ", comments=" + comments +
                '}';
    }
}
