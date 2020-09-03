package nano.support;

import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;

import java.io.Closeable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件切割者
 * <p>
 * - 把一个大文件切割成一个个小文件
 * - 把若干个小文件合并成一个大文件
 */
public class FileCutter implements Closeable {

    private static final int BUFFER_SIZE = 8192;
    private static final String TEMP_FILE_PREFIX = "file-cutter";

    private final Map<String, Path> tempFiles = new HashMap<>();

    private final Options options = new Options() {{
        this.setFilename(getId());
        this.setPartSuffix(".part");
        // 50MB
        this.setUnitSize(50_000_000);
    }};

    public FileCutter() {
    }

    public FileCutter(@NonNull Options options) {
        setIfNotNull(options::getFilename, this.options::setFilename);
        setIfNotNull(options::getUnitSize, this.options::setUnitSize);
        setIfNotNull(options::getPartSuffix, this.options::setPartSuffix);
    }

    /**
     * split files
     */
    @SneakyThrows
    public Map<String, InputStreamSource> split(@NonNull InputStreamSource source) {
        @Cleanup var is = source.getInputStream();
        for (int i = 1; ; i++) {
            var partFilename = this.options.getFilename() + this.options.getPartSuffix() + i;
            var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, partFilename);
            @Cleanup var os = Files.newOutputStream(tempFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            long transferred = 0;
            int read;
            while ((read = is.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                os.write(buffer, 0, read);
                transferred += read;
                if (transferred + BUFFER_SIZE > this.options.getUnitSize()) {
                    break;
                }
            }
            this.tempFiles.put(partFilename, tempFile);
            if (read <= 0) {
                break;
            }
        }
        var split = new HashMap<String, InputStreamSource>();
        this.tempFiles.forEach((filename, path) -> split.put(filename, new FileSystemResource(path)));
        return split;
    }

    /**
     * merge files
     */
    @SneakyThrows
    public Pair<String, InputStreamSource> merge(@NonNull Map<String, InputStreamSource> partSourceMap) {
        var filename = this.options.getFilename();
        var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, filename);
        @Cleanup var os = Files.newOutputStream(tempFile);
        var partFilenameList = new ArrayList<>(partSourceMap.keySet());
        partFilenameList.sort(String::compareTo);
        for (String partFilename : partFilenameList) {
            @Cleanup var is = partSourceMap.get(partFilename).getInputStream();
            is.transferTo(os);
        }
        this.tempFiles.put(filename, tempFile);
        return new Pair<>(filename, new FileSystemResource(tempFile));
    }

    /**
     * build zip file
     */
    @SneakyThrows
    public InputStreamSource zip(Map<String, InputStreamSource> sourceMap) {
        var filename = this.options.getFilename();
        var zipFilename = filename + ".zip";
        var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, zipFilename);
        @Cleanup var zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempFile));
        for (var source : sourceMap.entrySet()) {
            zipOutputStream.putNextEntry(new ZipEntry(source.getKey()));
            @Cleanup var is = source.getValue().getInputStream();
            is.transferTo(zipOutputStream);
            zipOutputStream.closeEntry();
        }
        this.tempFiles.put(zipFilename, tempFile);
        return new FileSystemResource(tempFile);
    }

    /**
     * clean temp files if exists
     */
    @SneakyThrows
    @Override
    public void close() {
        if (this.tempFiles.isEmpty()) {
            return;
        }
        for (var tempFile : this.tempFiles.values()) {
            Files.deleteIfExists(tempFile);
        }
        this.tempFiles.clear();
    }

    /**
     * get UUID
     */
    private static String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * set if not null
     */
    private static <O> void setIfNotNull(Supplier<O> getter, Consumer<O> setter) {
        O o = getter.get();
        if (o != null) {
            setter.accept(o);
        }
    }


    /**
     * FileCutter options
     */
    @Data
    public static class Options {

        private String filename;
        private String partSuffix;
        private Integer unitSize;
    }
}
