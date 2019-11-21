package io.unchecked;

@FunctionalInterface
public interface PredicateChecked<T,E extends Exception> {
    boolean test(T t) throws E;
}
