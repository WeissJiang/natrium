package nano.support.io;

import lombok.Cleanup;
import lombok.experimental.Delegate;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

    @Delegate
    private final Resource delegate;

    public SimpleResource(Resource delegate) {
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
        @Cleanup var inputStream = this.getInputStream();
        return inputStream.readAllBytes();
    }

    public String getAsString() throws IOException {
        return this.getAsString(utf8);
    }

    public String getAsString(Charset charset) throws IOException {
        var bytes = this.getAllBytes();
        return new String(bytes, charset);
    }
}
