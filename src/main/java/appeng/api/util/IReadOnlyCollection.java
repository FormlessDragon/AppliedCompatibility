package appeng.api.util;

/**
 * Old AE read-only iterable collection ABI used by mods compiled against appeng.api.
 *
 * @param <T> element type exposed by the old AE API call.
 */
public interface IReadOnlyCollection<T> extends Iterable<T> {

    /**
     * @return number of elements currently exposed by the read-only view.
     */
    int size();

    /**
     * @return true when the read-only view contains no elements.
     */
    boolean isEmpty();

    /**
     * @param node value to check.
     * @return true when the read-only view contains the value.
     */
    boolean contains(Object node);
}
