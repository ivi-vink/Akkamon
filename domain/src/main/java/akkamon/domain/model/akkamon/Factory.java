package akkamon.domain.model.akkamon;

public interface Factory<T, E> {
    T fromName(E name);
}
