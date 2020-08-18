package nano.support;

/**
 * Onion is just like an Onion
 *
 * @param <T> Context
 * @author cbdyzj
 * @since 2018.3.23
 */
public final class Onion<T> {

    private Middleware<T> middleware = (ctx, nxt) -> nxt.next();

    @SafeVarargs
    public final void use(Middleware<T>... middlewares) {
        for (Middleware<T> m : middlewares) {
            this.middleware = compose(this.middleware, m);
        }
    }

    public void handle(T context) throws Exception {

        this.middleware.via(context, () -> {
        });
    }

    public interface Middleware<T> {

        void via(T context, Next next) throws Exception;
    }

    public interface Next {

        void next() throws Exception;
    }

    public static <U> Middleware<U> compose(Middleware<U> before, Middleware<U> after) {
        return (ctx, nxt) -> before.via(ctx, () -> after.via(ctx, nxt));
    }

}
