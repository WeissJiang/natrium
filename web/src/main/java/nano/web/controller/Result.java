package nano.web.controller;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * HTTP 200 response body
 *
 * @param <T> payload type
 */
@Data
public class Result<T> {

    @Nullable
    private String error;

    @Nullable
    private T payload;

    public static <U> Result<U> of(U payload) {
        var result = new Result<U>();
        result.setPayload(payload);
        return result;
    }

    public static Result<?> error(String error) {
        var result = new Result<>();
        result.setError(error);
        return result;
    }

    public static <U> Result<U> empty() {
        return new Result<>();
    }
}
