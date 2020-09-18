package nano.web.controller;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * HTTP 200 response body
 *
 * @param <T> payload type
 */
public class Result<T> {

    private static final Result<?> EMPTY = new Result<>(null, null);

    @Nullable
    private final String error;

    @Nullable
    private final T payload;

    public Result(@Nullable String error, @Nullable T payload) {
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
    public String toString() {
        return "Result{" +
               "error='" + error + '\'' +
               ", payload=" + payload +
               '}';
    }

    public static <U> Result<U> of(U payload) {
        return new Result<>(null, payload);
    }

    public static Result<?> error(String error) {
        return new Result<>(error, null);
    }

    @SuppressWarnings("unchecked")
    public static <U> Result<U> empty() {
        return (Result<U>) EMPTY;
    }
}
