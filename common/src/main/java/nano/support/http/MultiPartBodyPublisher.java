package nano.support.http;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.net.http.HttpRequest;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MultiPartBodyPublisher {

    private final Map<String, String> stringParts = new HashMap<>();
    private final Map<String, FilePart> fileParts = new HashMap<>();
    private final String boundary = UUID.randomUUID().toString();

    public void addPart(@NotNull String name, @NotNull String value) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(value, "value must be not null");
        this.stringParts.put(name, value);
    }

    public void addPart(@NotNull String name, @NotNull MultiPartBodyPublisher.FilePart filePart) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(filePart, "filePart must be not null");
        this.fileParts.put(name, filePart);
    }

    public HttpRequest.BodyPublisher build() {
        if (this.stringParts.isEmpty() && this.fileParts.isEmpty()) {
            throw new IllegalStateException("Must have at least one part to build multipart message.");
        }
        return HttpRequest.BodyPublishers.ofByteArrays(this::getByteIterator);
    }

    public String getBoundary() {
        return this.boundary;
    }

    private @NotNull Iterator<byte[]> getByteIterator() {
        return ByteArrayIterators.compose(
                this.getStringPartByteArrayIterator(),
                this.getFilePartByteArrayIterator(),
                this.getFinalBoundaryByteArrayIterator()
        );
    }

    private Iterator<byte[]> getStringPartByteArrayIterator() {
        return ByteArrayIterators.map(this.stringParts.entrySet().iterator(), entry -> {
            var name = entry.getKey();
            var value = entry.getValue();
            var part = "--" + this.boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
                    "Content-Type: text/plain; charset=UTF-8\r\n\r\n" + value + "\r\n";
            return ByteArrayIterators.singleton(part.getBytes(UTF_8));
        });
    }

    private Iterator<byte[]> getFilePartByteArrayIterator() {
        return ByteArrayIterators.map(this.fileParts.entrySet().iterator(), entry -> {
            var name = entry.getKey();
            var filePart = entry.getValue();
            var filename = filePart.getFilename();
            var contentType = filePart.getContentType();
            var partHeader = "--" + this.boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n" +
                    "Content-Type: " + contentType + "\r\n\r\n";
            return ByteArrayIterators.compose(
                    ByteArrayIterators.singleton(partHeader.getBytes(UTF_8)),
                    ByteArrayIterators.from(filePart.getInputStream()),
                    ByteArrayIterators.singleton("\r\n".getBytes(UTF_8))
            );
        });
    }

    private Iterator<byte[]> getFinalBoundaryByteArrayIterator() {
        return ByteArrayIterators.singleton(("--" + this.boundary + "--").getBytes(UTF_8));
    }

    @FunctionalInterface
    public interface FilePart {

        InputStream getInputStream();

        default String getFilename() {
            return "";
        }

        default String getContentType() {
            return "application/octet-stream";
        }

        static @NotNull FilePart from(@NotNull Resource resource) {
            Objects.requireNonNull(resource, "supplier must be not null");
            return new FilePart() {

                @Override
                public InputStream getInputStream() {
                    try {
                        return resource.getInputStream();
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }

                @Override
                public String getFilename() {
                    return Objects.requireNonNullElse(resource.getFilename(), "");
                }

                @Override
                public String getContentType() {
                    var contentType = URLConnection.guessContentTypeFromName(this.getFilename());
                    return Objects.requireNonNullElse(contentType, "application/octet-stream");
                }
            };
        }
    }
}