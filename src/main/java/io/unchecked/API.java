package io.unchecked;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class API {

	@SuppressWarnings("unchecked")
	static <E extends Throwable, R> R sneakyThrow(Throwable t) throws E {
		throw (E) t;
	}

	/**
	 * Consumer
	 */
	public static <T, E extends Exception> Consumer<T> liftConsumer(ConsumerChecked<T, E> f) throws E {
		return lift(f);
	}

	public static <T, E extends Exception> Consumer<T> lift(ConsumerChecked<T, E> f) throws E {
		return sneak(f);
	}

	public static <T, E extends Exception> Consumer<T> sneakConsumer(ConsumerChecked<T, E> f) {
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

	public static <T, ES extends Exception, ET extends Exception> Consumer<T> wrapConsumer(ConsumerChecked<T, ES> f,
			Function<? super ES, ? extends ET> t) throws ET {
		return wrap(f, t);
	}

	@SuppressWarnings("unchecked")
	public static <T, ES extends Exception, ET extends Exception> Consumer<T> wrap(ConsumerChecked<T, ES> f,
			Function<? super ES, ? extends ET> t) throws ET {
		return sneakConsumer(s -> {
			try {
				f.accept(s);
			} catch (Exception e) {
				throw t.apply((ES) e);
			}
		});
	}

	/**
	 * Predicate
	 */
	public static <T, E extends Exception> Predicate<T> liftPredicate(PredicateChecked<T, E> f) throws E {
		return lift(f);
	}

	public static <T, E extends Exception> Predicate<T> lift(PredicateChecked<T, E> f) throws E {
		return sneak(f);
	}

	public static <T, E extends Exception> Predicate<T> sneakPredicate(PredicateChecked<T, E> f) {
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




	public static <T, ES extends Exception, ET extends Exception> Predicate<T> wrapPredicate(PredicateChecked<T, ES> f,
			Function<? super ES, ? extends ET> t) throws ET {
		return wrap(f, t);
	}

	@SuppressWarnings("unchecked")
	public static <T, ES extends Exception, ET extends Exception> Predicate<T> wrap(PredicateChecked<T, ES> f,
			Function<? super ES, ? extends ET> t) throws ET {
		return sneakPredicate(s -> {
			try {
				return f.test(s);
			} catch (Exception e) {
				throw t.apply((ES) e);
			}
		});
	}

	/**
	 * Function
	 */
	public static <T, R, E extends Exception> Function<T, R> liftFunction(FunctionChecked<T, R, E> f) throws E {
		return lift(f);
	}

	public static <T, R, E extends Exception> Function<T, R> lift(FunctionChecked<T, R, E> f) throws E {
		return sneak(f);
	}

	public static <T, R, E extends Exception> Function<T, R> sneakFunction(FunctionChecked<T, R, E> f) {
		return sneak(f);
	}

	public static <T, R, E extends Exception> Function<T, R> sneak(FunctionChecked<T, R, E> f) {
		return s -> {
			try {
				return f.apply(s);
			} catch (Exception ex) {
				return sneakyThrow(ex);
			}
		};
	}

	public static <T, R, E1 extends Exception, E2 extends Exception> Function<T, R> wrapFunction(FunctionChecked<T, R, E1> f,
			Function<? super E1, ? extends E2> t) throws E2 {
		return wrap(f, t);
	}

	@SuppressWarnings("unchecked")
	public static <T, R, E1 extends Exception, E2 extends Exception> Function<T, R> wrap(FunctionChecked<T, R, E1> f,
			Function<? super E1, ? extends E2> t) throws E2 {
		return sneakFunction((T s) -> {
			try {
				return f.apply(s);
			} catch (Exception e) {
				throw t.apply((E1) e);
			}
		});
	}
}
