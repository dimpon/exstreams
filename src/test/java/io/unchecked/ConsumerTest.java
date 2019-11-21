package io.unchecked;

import static io.unchecked.API.lift;
import static io.unchecked.API.sneak;
import static io.unchecked.API.sneakFunction;
import static io.unchecked.API.wrap;
import static io.unchecked.API.wrapFunction;
import static io.unchecked.API.wrapConsumer;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;

public class ConsumerTest {

	private static final Function<Exception, VeryRuntimeException> toRuntime = VeryRuntimeException::new;
	private static final Function<VeryCheckedException, AppCheckedException> toAnotherChecked = AppCheckedException::new;

	private static final Function<Exception, AppCheckedException> anyToAnotherChecked = AppCheckedException::new;

	private static final ConsumerChecked<String, VeryCheckedException> consumer = s -> consumeAndThrow(s);

	private static final PredicateChecked<String, VeryCheckedException> predicator = s -> predicateAndThrow(s);

	@Test
	@DisplayName("Testing Consumer. Sneak Checked Exception.")
	@Timeout(1)
	void testConsumerSneak() {

		//sneak
		sneak(consumer).accept("one");
		sneak(ConsumerTest::consumeAndThrow).accept("one");
		sneak((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("one");

		isVeryCheckedException(() -> sneak(consumer).accept("exception"));
		isVeryCheckedException(() -> sneak(ConsumerTest::consumeAndThrow).accept("exception"));
		isVeryCheckedException(() -> sneak((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("exception"));
	}

	@Test
	@DisplayName("Testing Predicate. Sneak Predicate Exception.")
	@Timeout(1)
	void testPredicateSneak() {

		//sneak
		boolean one1 = sneak(predicator).test("one");
		boolean one2 = sneak(ConsumerTest::predicateAndThrow).test("one");
		boolean one3 = sneak((PredicateChecked<String, VeryCheckedException>) (s) -> predicateAndThrow(s)).test("one");

		isVeryCheckedException(() -> sneak(predicator).test("exception"));
		isVeryCheckedException(() -> sneak(ConsumerTest::predicateAndThrow).test("exception"));
		isVeryCheckedException(() -> sneak((PredicateChecked<String, VeryCheckedException>) (s) -> predicateAndThrow(s)).test("exception"));
	}


	@Test
	@DisplayName("Testing Consumer. Lift Checked Exception on next level.")
	@Timeout(1)
	void testConsumerLift() {
		//lift
		try {
			lift(consumer).accept("one");
			lift(ConsumerTest::consumeAndThrow).accept("one");
			lift((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("one");
		} catch (VeryCheckedException e) {
			Assertions.fail("Expecting no exceptions");
		}

		{
			VeryCheckedException exception = assertThrows(VeryCheckedException.class, () -> lift(consumer).accept("exception"));
			Assertions.assertNotNull(exception);
		}
		{
			VeryCheckedException exception = assertThrows(VeryCheckedException.class, () -> lift(ConsumerTest::consumeAndThrow).accept("exception"));
			Assertions.assertNotNull(exception);
		}
		{
			VeryCheckedException exception = assertThrows(VeryCheckedException.class,
					() -> lift((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("exception"));
			Assertions.assertNotNull(exception);
		}
	}

	@Test
	@DisplayName("Testing Consumer. Wrap to Runtime Exception.")
	@Timeout(1)
	void testConsumerWrapToRuntime() {

		//wrap to runtime
		wrap(consumer, toRuntime).accept("one");
		wrap(ConsumerTest::consumeAndThrow, toRuntime).accept("one");
		wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toRuntime).accept("one");

		{
			VeryRuntimeException exception = assertThrows(VeryRuntimeException.class, () -> wrap(consumer, toRuntime).accept("exception"));
			Assertions.assertNotNull(exception);
		}
		{
			VeryRuntimeException exception = assertThrows(VeryRuntimeException.class,
					() -> wrap(ConsumerTest::consumeAndThrow, toRuntime).accept("exception"));
			Assertions.assertNotNull(exception);
		}
		{
			VeryRuntimeException exception = assertThrows(VeryRuntimeException.class,
					() -> wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toRuntime).accept("exception"));
			Assertions.assertNotNull(exception);
		}

		Function<VeryCheckedException, AppCheckedException> f = (e) -> new AppCheckedException(e);

		try {
			wrap(ConsumerTest::consumeAndThrowAB, f);
		} catch (AppCheckedException e) {
			e.printStackTrace();
		}

	}

	@Test
	@DisplayName("Testing Consumer. Wrap to Checked Exception.")
	@Timeout(1)
	void testConsumerWrapToChecked() {
		//wrap to another checked
		try {
			wrap(consumer, toAnotherChecked).accept("one");
			wrap(ConsumerTest::consumeAndThrow, toAnotherChecked).accept("one");
			wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toAnotherChecked).accept("one");
		} catch (AppCheckedException e) {
			Assertions.fail("Expecting no exceptions");
		}

		{
			AppCheckedException exception = assertThrows(AppCheckedException.class, () -> wrap(consumer, toAnotherChecked).accept("exception"));
			Assertions.assertNotNull(exception);
		}
		{
			AppCheckedException exception = assertThrows(AppCheckedException.class,
					() -> wrap(ConsumerTest::consumeAndThrow, toAnotherChecked).accept("exception"));
			Assertions.assertNotNull(exception);
		}
		{
			AppCheckedException exception = assertThrows(AppCheckedException.class,
					() -> wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toAnotherChecked).accept("exception"));
			Assertions.assertNotNull(exception);
		}
	}

	//////////////////

	@Test
	@Disabled
	void testCommonStuff() {

		ConsumerChecked<String, VeryCheckedException> cons = Mockito.mock(ConsumerChecked.class);

		try {
			Stream.of("a", "b", "c").forEach(lift(cons));

			Mockito.verify(cons, Mockito.times(1)).accept("a");
			Mockito.verify(cons, Mockito.times(1)).accept("b");
			Mockito.verify(cons, Mockito.times(1)).accept("c");
		} catch (VeryCheckedException e) {
			Assertions.fail("Expecting no exceptions");
		}

		ConsumerChecked<String, VeryCheckedException.A> ch1 = s -> {
			if (s.equalsIgnoreCase("a"))
				throw new VeryCheckedException.A();
		};

		Function<Exception, AppCheckedException> transform = AppCheckedException.A::new;

		try {
			wrap(ch1, transform).accept("b");
		} catch (AppCheckedException e) {
			e.printStackTrace();
		}

		FunctionChecked<Class, Object, Exception> f = Class::newInstance;


			/*Set<Object> collect = Stream.of(String.class, Integer.class)
					.map(sneak(Class::newInstance))
					.collect(Collectors.toSet());

			System.out.println(collect.toString());
			 */

		sneak(ConsumerTest::functionAndThrow);
		sneak(ConsumerTest::consumeAndThrow);
		sneak(ConsumerTest::predicateAndThrow);
		sneak(ConsumerTest::consumeAndThrowAB);

		try {
			lift(ConsumerTest::functionAndThrow);
		} catch (VeryCheckedException e) {
			e.printStackTrace();
		}

		try {
			lift(ConsumerTest::consumeAndThrow);
		} catch (VeryCheckedException e) {
			e.printStackTrace();
		}

		try {
			lift(ConsumerTest::predicateAndThrow);
		} catch (VeryCheckedException e) {
			e.printStackTrace();
		}

		try {
			lift(ConsumerTest::consumeAndThrowAB);
		} catch (VeryCheckedException e) {
			e.printStackTrace();
		}

		wrap(ConsumerTest::functionAndThrow, toRuntime);
		wrap(ConsumerTest::consumeAndThrow, toRuntime);
		wrap(ConsumerTest::predicateAndThrow, toRuntime);
		wrap(ConsumerTest::consumeAndThrowAB, toRuntime);

		Stream.of(API.class, Object.class)
				.map(sneakFunction(this::instance))
				.forEach(System.out::println);

		try {
			Stream.of(API.class, Object.class)
					.map(wrapFunction(Class::newInstance,anyToAnotherChecked))
					.forEach(System.out::println);
		} catch (AppCheckedException e) {
			e.printStackTrace();
		}





			/*String apply = wrapFunction(
					(FunctionChecked<Class<String>, String, ReflectiveOperationException>) this::instance, anyToAnotherChecked)
					.apply(String.class);*/

		Stream.of("a", "b")
				.filter(sneak(ConsumerTest::predicateAndThrow))
				.map(sneak(ConsumerTest::functionToBooleanAndThrow))
				.map(Object::toString)
				.map(wrap(ConsumerTest::functionAndThrow, toRuntime))
				.forEach(sneak(ConsumerTest::consumeAndThrow));

		FunctionChecked<String, URL, MalformedURLException> stringURLExceptionFunctionChecked = URL::new;


			Stream.of("http://www.oracle.com/", "http://www.google.com/")
					.map(wrapFunction((URL::new),toRuntime))
					.parallel()
					.forEach(wrapConsumer((url -> {

						URLConnection yc = url.openConnection();
						try (BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));) {
							String inputLine;
							while ((inputLine = in.readLine()) != null)
								System.out.println(inputLine);
						}

					}),toRuntime));

		String fileName = "/user/file";



	}

	private <T> T instance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
		return clazz.newInstance();
	}

	private static String functionAndThrow(String s) throws VeryCheckedException {
		if (s.equalsIgnoreCase("exception"))
			throw new VeryCheckedException();
		return s;
	}

	private static void consumeAndThrow(String s) throws VeryCheckedException {
		if (s.equalsIgnoreCase("exception"))
			throw new VeryCheckedException();
	}

	private static boolean predicateAndThrow(String s) throws VeryCheckedException {
		if (s.equalsIgnoreCase("exception"))
			throw new VeryCheckedException();
		return true;
	}

	private static Boolean functionToBooleanAndThrow(String s) throws VeryCheckedException {
		if (s.equalsIgnoreCase("exception"))
			throw new VeryCheckedException();
		return true;
	}

	private static void consumeAndThrowAB(String s) throws VeryCheckedException.A, VeryCheckedException.B {

		if (s.equalsIgnoreCase("exceptionA"))
			throw new VeryCheckedException.A();

		if (s.equalsIgnoreCase("exceptionB"))
			throw new VeryCheckedException.B();
	}

	private static void isVeryCheckedException(Runnable action) {
		VeryCheckedException exception = assertThrows(VeryCheckedException.class, () -> {
			action.run();
		});
		Assertions.assertTrue(exception instanceof VeryCheckedException);
	}

}
