package nano.web.controller;

import lombok.Data;

/**
 * HTTP 200 response body
 *
 * @param <T> body type
 */
@Data
public class Result<T> {

    private String error;
    private T data;

    public static <U> Result<U> of(U data) {
        var result = new Result<U>();
        result.setData(data);
        return result;
    }

    public static Result<?> error(String error) {
        var result = new Result<>();
        result.setError(error);
        return result;
    }
}
