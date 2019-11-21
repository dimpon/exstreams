package io.unchecked;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class API {

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, R> R sneakyThrow(Throwable t) throws E {
        throw (E) t;
    }

    /**
     * Consumer
     */
    public static <T, E extends Exception> Consumer<T> lift(ConsumerChecked<T, E> f) throws E {
        return sneak(f);
    }

    public static <T, E extends Exception> Consumer<T> sneak(ConsumerChecked<T, E> f) {
        return s -> {
            try {
                f.accept(s);
            } catch (Exception ex) {
                sneakyThrow(ex);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T, ES extends Exception, ET extends Exception> Consumer<T> wrap(ConsumerChecked<T, ES> f, Function<ES, ET> convert) throws ET {
        return sneak(s -> {
            try {
                f.accept(s);
            } catch (Exception e) {
                throw convert.apply((ES) e);
            }
        });
    }


    /**
     * Predicate
     */
    public static <T, E extends Exception> Predicate<T> lift(PredicateChecked<T, E> f) throws E {
        return sneak(f);
    }

    public static <T, E extends Exception> Predicate<T> sneak(PredicateChecked<T, E> f) {
        return s -> {
            try {
                return f.test(s);
            } catch (Exception ex) {
                return sneakyThrow(ex);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T, ES extends Exception, ET extends Exception> Predicate<T> wrap(PredicateChecked<T, ES> f, Function<ES, ET> convert) throws ET {
        return sneak(s -> {
            try {
                return f.test(s);
            } catch (Exception e) {
                throw convert.apply((ES) e);
            }
        });
    }


}
