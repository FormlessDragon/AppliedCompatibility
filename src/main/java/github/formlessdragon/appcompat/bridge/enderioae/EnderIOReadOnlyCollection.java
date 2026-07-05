package github.formlessdragon.appcompat.bridge.enderioae;

import appeng.api.util.IReadOnlyCollection;

import java.util.Iterator;
import java.util.List;

public final class EnderIOReadOnlyCollection<T> implements IReadOnlyCollection<T> {

    private final List<T> values;

    public EnderIOReadOnlyCollection(final List<T> values) {
        this.values = values;
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public boolean contains(final Object node) {
        return this.values.contains(node);
    }

    @Override
    public Iterator<T> iterator() {
        return this.values.iterator();
    }
}
