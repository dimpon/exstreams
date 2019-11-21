package io.unchecked;

@FunctionalInterface
public interface ConsumerChecked<T, E extends Exception> {
    void accept(T t) throws E;
}
