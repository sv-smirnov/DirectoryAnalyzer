public class FileStatistic {
    volatile String extension = "";
    volatile Integer count = 0;
    volatile Long size = 0L;
    volatile Long lines = 0L;
    volatile Long nonEmptyLines = 0L;
    volatile Long comments = 0L;

    public FileStatistic(String extension) {
        this.extension = extension;
    }

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

    public FileStatistic increaseCount() {
        this.count = this.count + 1;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public FileStatistic increaseSize(Long size) {
        this.size = this.size + size;
        return this;
    }

    public Long getLines() {
        return lines;
    }

    public FileStatistic increaseLines(Long lines) {
        this.lines = this.lines + lines;
        return this;
    }

    public Long getNonEmptyLines() {
        return nonEmptyLines;
    }

    public FileStatistic increaseNonEmptyLines(Long nonEmptyLines) {
        this.nonEmptyLines = this.nonEmptyLines + nonEmptyLines;
        return this;
    }

    public Long getComments() {
        return comments;
    }

    public FileStatistic increaseComments(Long comments) {
        this.comments = this.comments + comments;
        return this;
    }
}