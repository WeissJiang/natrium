package nano.support;

public class NeverException extends RuntimeException {

    public NeverException(Throwable cause) {
        super(cause);
    }
}
