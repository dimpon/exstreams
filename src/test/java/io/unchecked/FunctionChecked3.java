package io.unchecked;

import static io.unchecked.API.sneakyThrow;

@FunctionalInterface
public interface FunctionChecked3<T, R, E extends Exception, E1 extends Exception> extends FunctionChecked<T, R, E> {

    default R apply(T t) throws E {
        try {
            return apply3(t);
        } catch (Exception ex) {
            return sneakyThrow(ex);
        }
    }

    R apply3(T t) throws E, E1;

}
