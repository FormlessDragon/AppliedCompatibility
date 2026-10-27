package appeng.api.features;

@SuppressWarnings("unused")
public interface ILocatableRegistry {

    default ILocatable getLocatableBy(final long serial) {
        return null;
    }

    default void registerLocatable(final ILocatable locatable) {
    }
}
