package nano.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arguments {

    private final Object[] args;

    public Arguments(Object @NotNull ... args) {
        this.args = args;
    }

    public <T> @Nullable T get(int index, @NotNull Class<T> clazz) {
        this.checkIndex(index);
        return clazz.cast(this.args[index]);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this.args.length) {
            throw new IllegalArgumentException("The index exceeds the length of args");
        }
    }
}
