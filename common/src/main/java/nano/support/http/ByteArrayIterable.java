package nano.support.http;

import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class ByteArrayIterable {

    public static <T> @NotNull Iterable<byte @NotNull []> map(Iterable<T> iterable, Function<T, Iterable<byte @NotNull []>> mapper) {
        return () -> {
            var iterator = iterable.iterator();
            return new Iterator<>() {

                Iterator<byte[]> delegate = Collections.emptyIterator();

                @Override
                public boolean hasNext() {
                    while (!this.delegate.hasNext() && iterator.hasNext()) {
                        this.delegate = mapper.apply(iterator.next()).iterator();
                    }
                    return this.delegate.hasNext();
                }

                @Override
                public byte[] next() {
                    return this.delegate.next();
                }
            };
        };
    }

    @SafeVarargs
    public static @NotNull Iterable<byte @NotNull []> compose(Iterable<byte[]> @NotNull ... iterable) {
        if (iterable.length == 0) {
            return Collections::emptyIterator;
        }
        return () -> {
            var iterator = Arrays.stream(iterable).iterator();
            return new Iterator<>() {

                Iterator<byte[]> delegate = Collections.emptyIterator();

                @Override
                public boolean hasNext() {
                    while (!this.delegate.hasNext() && iterator.hasNext()) {
                        this.delegate = iterator.next().iterator();
                    }
                    return this.delegate.hasNext();
                }

                @Override
                public byte[] next() {
                    return this.delegate.next();
                }
            };
        };
    }

    public static @NotNull Iterable<byte @NotNull []> from(@NotNull Supplier<@NotNull InputStream> supplier) {
        return () -> {
            var inputStream = supplier.get();
            return new Iterator<>() {

                private final byte[] buffer = new byte[8192];
                private boolean drained = false;

                @Override
                public boolean hasNext() {
                    return !this.drained;
                }

                @Override
                public byte[] next() {
                    this.ensureNotDrained();
                    int read = this.readBytes();
                    if (read > 0) {
                        var actualBytes = new byte[read];
                        System.arraycopy(this.buffer, 0, actualBytes, 0, read);
                        return actualBytes;
                    } else {
                        this.drained = true;
                        this.closeStream();
                        return new byte[0];
                    }
                }

                private int readBytes() {
                    try {
                        return inputStream.read(this.buffer);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }

                private void closeStream() {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }

                private void ensureNotDrained() {
                    if (this.drained) {
                        throw new NoSuchElementException("Stream is drained");
                    }
                }
            };
        };
    }
}
