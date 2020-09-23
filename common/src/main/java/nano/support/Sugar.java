package nano.support;

import lombok.NonNull;
import org.springframework.validation.ObjectError;

import java.util.*;
import java.util.function.*;
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

    public static <T> T findFirst(Collection<T> list, @NonNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return null;
        }
        Optional<T> ot = list.stream().filter(predicate).findFirst();
        return ot.orElse(null);
    }

    public static <T, R> R reduce(Collection<T> list, @NonNull R identity, @NonNull BiFunction<R, ? super T, R> accumulator) {
        if (isEmpty(list)) {
            return identity;
        }
        return list.stream().reduce(identity, accumulator, (a, c) -> null);
    }

    public static <T> List<T> filter(Collection<T> list, @NonNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> boolean every(Collection<T> list, @NonNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().allMatch(predicate);
    }

    public static <T> void forEach(Collection<T> list, @NonNull Consumer<? super T> action) {
        if (isEmpty(list)) {
            return;
        }
        list.forEach(action);
    }

    public static <T, R> List<R> map(Collection<T> list, @NonNull Function<? super T, ? extends R> mapper) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T, K> Map<K, T> toMap(Collection<T> list, @NonNull Function<? super T, ? extends K> keyExtractor) {
        return toMap(list, keyExtractor, Function.identity());
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> list,
                                            @NonNull Function<? super T, ? extends K> keyExtractor,
                                            @NonNull Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, valueExtractor));
    }

    public static <T> boolean includes(Collection<T> list, @NonNull Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    public static <T> T getFirst(Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        var first = collection.stream().findFirst();
        return first.orElse(null);
    }

    public static String render(@NonNull String template, Map<String, ?> scope) {
        return Pattern.compile("(\\{(\\w+)})").matcher(template)
                .replaceAll(mr -> requireNonNull(scope.get(mr.group(2)), mr.group(1) + " not found in scope").toString());
    }

    public static boolean is(Supplier<?> a, Supplier<?> b) {
        return Objects.equals(a.get(), b.get());
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
