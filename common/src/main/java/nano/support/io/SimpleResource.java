package nano.support.io;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A simple resource
 *
 * @author cbdyzj
 * @since 2020.9.12
 */
public class SimpleResource implements Resource {

    private static final Charset utf8 = StandardCharsets.UTF_8;

    private final Resource delegate;

    public SimpleResource(@NotNull Resource delegate) {
        this.delegate = delegate;
    }

    public Reader getAsReader(Charset charset) throws IOException {
        var inputStream = this.getInputStream();
        return new InputStreamReader(inputStream, charset);
    }

    public Reader getAsReader() throws IOException {
        return this.getAsReader(utf8);
    }

    public byte[] getAllBytes() throws IOException {
        var inputStream = this.getInputStream();
        try (inputStream) {
            return inputStream.readAllBytes();
        }
    }

    public String getAsString() throws IOException {
        return this.getAsString(utf8);
    }

    public String getAsString(Charset charset) throws IOException {
        var bytes = this.getAllBytes();
        return new String(bytes, charset);
    }

    @Override
    public boolean exists() {
        return this.delegate.exists();
    }

    @Override
    public @NotNull URL getURL() throws IOException {
        return this.delegate.getURL();
    }

    @Override
    public @NotNull URI getURI() throws IOException {
        return this.delegate.getURI();
    }

    @Override
    public @NotNull File getFile() throws IOException {
        return this.delegate.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return this.delegate.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return this.delegate.lastModified();
    }

    @Override
    public @NotNull Resource createRelative(@NotNull String relativePath) throws IOException {
        return this.delegate.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return this.delegate.getFilename();
    }

    @Override
    public @NotNull String getDescription() {
        return this.delegate.getDescription();
    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return this.delegate.getInputStream();
    }
}
