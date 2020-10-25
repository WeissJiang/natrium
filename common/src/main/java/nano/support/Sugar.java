package nano.support;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * 一点语法糖
 *
 * @author cbdyzj
 * @see <a href="https://github.com/cbdyzj/sugar">sugar</a>
 * @since 2020.8.4
 */
public abstract class Sugar {

    public static <T> @Nullable T findFirst(@Nullable Collection<T> list, @NotNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return null;
        }
        var ot = list.stream().filter(predicate).findFirst();
        return ot.orElse(null);
    }

    public static <T, R> @NotNull R reduce(@Nullable Collection<T> list, @NotNull R identity, @NotNull BiFunction<R, ? super T, R> accumulator) {
        if (isEmpty(list)) {
            return identity;
        }
        return list.stream().reduce(identity, accumulator, (a, c) -> null);
    }

    public static <T> @NotNull List<T> filter(@Nullable Collection<T> list, @NotNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> boolean every(@Nullable Collection<T> list, @NotNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().allMatch(predicate);
    }

    public static <T> void forEach(@Nullable Collection<T> list, @NotNull Consumer<? super T> action) {
        if (isEmpty(list)) {
            return;
        }
        list.forEach(action);
    }

    public static <T, R> @NotNull List<R> map(@Nullable Collection<T> list, @NotNull Function<? super T, ? extends R> mapper) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T, K> @NotNull Map<K, T> toMap(@Nullable Collection<T> list, @NotNull Function<? super T, ? extends K> keyExtractor) {
        return toMap(list, keyExtractor, Function.identity());
    }

    public static <T, K, V> @NotNull Map<K, V> toMap(@Nullable Collection<T> list,
                                                     @NotNull Function<? super T, ? extends K> keyExtractor,
                                                     @NotNull Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, valueExtractor));
    }

    public static <T> boolean includes(@Nullable Collection<T> list, @NotNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    public static <T> @Nullable T getFirst(@Nullable Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        var first = (T) null;
        for (T it : collection) {
            first = it;
            break;
        }
        return first;
    }

    public static <T> @Nullable T getLast(@Nullable Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        // if is List
        if (collection instanceof List) {
            var list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        // other collection
        var last = (T) null;
        for (T it : collection) {
            last = it;
        }
        return last;
    }

    /**
     * A one-line string template engine
     *
     * @param template "hello {name}"
     * @param scope    Map.of("name", "world")
     * @return "hello world"
     */
    public static @NotNull String render(@NotNull String template, @NotNull Map<String, ?> scope) {
        return Pattern.compile("(\\{(\\w+)})").matcher(template).replaceAll(mr -> requireNonNull(scope.get(mr.group(2)), mr.group(1) + " not found in scope").toString());
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
