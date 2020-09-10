package nano.support;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SimpleResourceLoader {

    private static final Charset utf8 = StandardCharsets.UTF_8;

    private final ResourceLoader delegate;

    public SimpleResourceLoader(ResourceLoader delegate) {
        this.delegate = delegate;
    }

    public Resource getResource(String location) {
        return this.delegate.getResource(location);
    }

    @SneakyThrows
    public InputStream getResourceAsInputStream(String location) {
        return this.getResource(location).getInputStream();
    }

    public Reader getResourceAsReader(String location, Charset charset) {
        var inputStream = this.getResourceAsInputStream(location);
        return new InputStreamReader(inputStream, charset);
    }

    public Reader getResourceAsReader(String location) {
        var inputStream = this.getResourceAsInputStream(location);
        return new InputStreamReader(inputStream, utf8);
    }

    @SneakyThrows
    public byte[] getResourceBytes(String location) {
        @Cleanup var inputStream = this.getResourceAsInputStream(location);
        return inputStream.readAllBytes();
    }

    public String getResourceAsString(String location) {
        return this.getResourceAsString(location, utf8);
    }

    public String getResourceAsString(String location, Charset charset) {
        var bytes = this.getResourceBytes(location);
        return new String(bytes, charset);
    }


}

