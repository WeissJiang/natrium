package nano.support;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件切割者
 * <p>
 * 把一个大文件切割成一个个小文件
 * 把若干个小文件合并成一个大文件
 */
public class FileCutter {

    private static final int BUFFER_SIZE = 8192;
    private static final String TEMP_FILE_PREFIX = "file-cutter";

    private final Options options = new Options() {{
        this.setFilename(getId());
        this.setPartSuffix(".part");
        // 50MB
        this.setUnitSize(50_000_000);
    }};

    public FileCutter() {
    }

    public FileCutter(@NotNull Options options) {
        setIfNotNull(options::getFilename, this.options::setFilename);
        setIfNotNull(options::getUnitSize, this.options::setUnitSize);
        setIfNotNull(options::getPartSuffix, this.options::setPartSuffix);
    }

    /**
     * split files
     */
    public Map<String, InputStreamSource> split(@NotNull InputStreamSource source) throws IOException {
        var is = source.getInputStream();
        try (is) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            var split = new HashMap<String, InputStreamSource>();
            for (int i = 1; ; i++) {
                var partFilename = this.options.getFilename() + this.options.getPartSuffix() + i;
                var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, partFilename);
                var os = Files.newOutputStream(tempFile);
                try (os) {
                    long transferred = 0;
                    while ((read = is.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                        os.write(buffer, 0, read);
                        transferred += read;
                        if (transferred + BUFFER_SIZE > this.options.getUnitSize()) {
                            break;
                        }
                    }
                }
                tempFile.toFile().deleteOnExit();
                split.put(partFilename, new FileSystemResource(tempFile));
                if (read <= 0) {
                    break;
                }
            }
            return split;
        }
    }

    /**
     * merge files
     */
    public Pair<String, InputStreamSource> merge(@NotNull Map<String, InputStreamSource> partSourceMap) throws IOException {
        var filename = this.options.getFilename();
        var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, filename);
        tempFile.toFile().deleteOnExit();
        var os = Files.newOutputStream(tempFile);
        try (os) {
            var partFilenameList = new ArrayList<>(partSourceMap.keySet());
            partFilenameList.sort(String::compareTo);
            for (String partFilename : partFilenameList) {
                var is = partSourceMap.get(partFilename).getInputStream();
                try (is) {
                    is.transferTo(os);
                }
            }
            return Pair.of(filename, new FileSystemResource(tempFile));
        }

    }

    /**
     * build zip file
     */
    public InputStreamSource zip(Map<String, InputStreamSource> sourceMap) throws IOException {
        var filename = this.options.getFilename();
        var zipFilename = filename + ".zip";
        var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, zipFilename);
        tempFile.toFile().deleteOnExit();
        var zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempFile));
        try (zipOutputStream) {
            for (var source : sourceMap.entrySet()) {
                zipOutputStream.putNextEntry(new ZipEntry(source.getKey()));
                var is = source.getValue().getInputStream();
                try (is) {
                    is.transferTo(zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
            return new FileSystemResource(tempFile);
        }
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
    public static class Options {

        private String filename;
        private String partSuffix;
        private Integer unitSize;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getPartSuffix() {
            return partSuffix;
        }

        public void setPartSuffix(String partSuffix) {
            this.partSuffix = partSuffix;
        }

        public Integer getUnitSize() {
            return unitSize;
        }

        public void setUnitSize(Integer unitSize) {
            this.unitSize = unitSize;
        }
    }
}
