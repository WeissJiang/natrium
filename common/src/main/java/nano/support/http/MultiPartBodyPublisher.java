package nano.support.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Flow;

public class MultiPartBodyPublisher {

    private final static Charset utf8 = StandardCharsets.UTF_8;

    private final HashMap<String, String> stringParts = new HashMap<>();
    private final HashMap<String, FilePartSpec> fileParts = new HashMap<>();
    private final String boundary = UUID.randomUUID().toString();

    public void addPart(@NotNull String name, @NotNull String value) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(value, "value must be not null");
        this.stringParts.put(name, value);
    }

    public void addPart(@NotNull String name, @NotNull FilePartSpec filePart) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(filePart, "filePart must be not null");
        this.fileParts.put(name, filePart);
    }

    public HttpRequest.BodyPublisher build() {
        if (this.stringParts.isEmpty() && this.fileParts.isEmpty()) {
            throw new IllegalStateException("Must have at least one part to build multipart message.");
        }
        return HttpRequest.BodyPublishers.ofByteArrays(this::getPartByteIterator);
    }

    public String getBoundary() {
        return this.boundary;
    }

    private @NotNull Iterator<byte[]> getPartByteIterator() {
        var boundary = this.boundary;

        var stringPartIterator = this.stringParts.entrySet().iterator();
        var filePartIterator = this.fileParts.entrySet().iterator();
        var finalBoundaryIterator = List.of(("--" + boundary + "--").getBytes(utf8)).iterator();
        return new Iterator<>() {

            private Iterator<byte[]> byteArrayIterator = Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                return finalBoundaryIterator.hasNext();
            }

            @Override
            public byte[] next() {
                if (stringPartIterator.hasNext()) {
                    var next = stringPartIterator.next();
                    var name = next.getKey();
                    var value = next.getValue();
                    String part = "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
                            "Content-Type: text/plain; charset=UTF-8\r\n\r\n" +
                            value + "\r\n";
                    return part.getBytes(utf8);
                }
                if (this.byteArrayIterator.hasNext()) {
                    return this.byteArrayIterator.next();
                }
                if (filePartIterator.hasNext()) {
                    var next = filePartIterator.next();
                    var name = next.getKey();
                    var filePart = next.getValue();
                    var partHeader = "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filePart.getFilename() + "\"\r\n" +
                            "Content-Type: " + filePart.getContentType() + "\r\n\r\n";
                    this.byteArrayIterator = new Iterator<>() {
                        private final Iterator<byte[]> fileByteArrayIterator = new ByteArrayIterator(filePart.getInputStream());
                        private boolean drained = false;

                        @Override
                        public boolean hasNext() {
                            return this.fileByteArrayIterator.hasNext() || !this.drained;
                        }

                        @Override
                        public byte[] next() {
                            if (this.drained) {
                                throw new NoSuchElementException("stream is drained");
                            }
                            if (this.fileByteArrayIterator.hasNext()) {
                                return this.fileByteArrayIterator.next();
                            } else {
                                this.drained = true;
                                return "\r\n".getBytes(utf8);
                            }
                        }
                    };
                    return partHeader.getBytes(utf8);
                }
                return finalBoundaryIterator.next();
            }
        };
    }

    public interface FilePartSpec {

        String getFilename();

        InputStream getInputStream();

        default String getContentType() {
            return "application/octet-stream";
        }

        static FilePartSpec from(@NotNull Resource resource) {
            Objects.requireNonNull(resource, "supplier must be not null");
            return new FilePartSpec() {
                @Override
                public InputStream getInputStream() {
                    try {
                        return resource.getInputStream();
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }

                @Override
                public @Nullable String getFilename() {
                    return resource.getFilename();
                }
            };
        }
    }

    public static void main(String[] args) {
        MultiPartBodyPublisher publisher = new MultiPartBodyPublisher();
        publisher.addPart("hello", "jojo");
        publisher.addPart("hekd", "sdasd");
        HttpRequest.BodyPublisher build = publisher.build();
        build.subscribe(new Flow.Subscriber<ByteBuffer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(1);
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBuffer item) {
                System.out.println(new String(item.array()));
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}