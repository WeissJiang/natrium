package nano.support;

import lombok.NonNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
