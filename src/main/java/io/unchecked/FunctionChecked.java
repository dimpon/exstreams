package io.unchecked;
@FunctionalInterface
public interface FunctionChecked<T, R, E extends Exception> {
	R apply(T t) throws E;
}
