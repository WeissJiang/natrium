package nano.support;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * HTTP response body wrapper
 *
 * @param <T> payload type
 */
public class Result<T> {

    private static final Result<?> EMPTY = new Result<>(null, null);

    private final @Nullable String error;
    private final @Nullable T payload;

    public Result(@Nls @Nullable String error, @Nullable T payload) {
        this.error = error;
        this.payload = payload;
    }

    public @Nullable String getError() {
        return error;
    }

    public @Nullable T getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?> result = (Result<?>) o;
        return Objects.equals(error, result.error) &&
               Objects.equals(payload, result.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, payload);
    }

    @Override
    public @NotNull String toString() {
        return new StringJoiner(", ", Result.class.getSimpleName() + "[", "]")
                .add("error='" + error + "'")
                .add("payload=" + payload)
                .toString();
    }

    public static @NotNull <U> Result<U> of(U payload) {
        return new Result<>(null, payload);
    }

    public static @NotNull Result<?> error(@Nls String error) {
        return new Result<>(error, null);
    }

    @SuppressWarnings("unchecked")
    public static <U> @NotNull Result<U> empty() {
        return (Result<U>) EMPTY;
    }
}
