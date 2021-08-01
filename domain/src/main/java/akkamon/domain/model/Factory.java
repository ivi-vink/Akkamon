package akkamon.domain.model;

public interface Factory<T, E> {
    T fromName(E name);
}
