package moonlightflower.com.superioribus2.shared;

public interface ListenerAction<T> {
    void fire(T el);
}