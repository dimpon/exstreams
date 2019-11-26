package io.unchecked;

import static io.unchecked.API.lift;
import static io.unchecked.API.sneak;
import static io.unchecked.API.sneakFunction;
import static io.unchecked.API.wrap;
import static io.unchecked.API.wrapFunction;
import static io.unchecked.API.wrapConsumer;
import static io.unchecked.TestsUtils.*;
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



    private static final ConsumerChecked<String, TestCheckedException> consumer = s -> consumeAndThrow(s);



    @Test
    @DisplayName("Testing Consumer. Sneak Checked Exception.")
    @Timeout(1)
    void testConsumerSneak() {

        //sneak
        sneak(consumer).accept("one");
        sneak(ConsumerTest::consumeAndThrow).accept("one");
        sneak((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s)).accept("one");

        isTestCheckedException(() -> sneak(consumer).accept("exception"));
        isTestCheckedException(() -> sneak(ConsumerTest::consumeAndThrow).accept("exception"));
        isTestCheckedException(() -> sneak((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s)).accept("exception"));
    }




    @Test
    @DisplayName("Testing Consumer. Lift Checked Exception on next level.")
    @Timeout(1)
    void testConsumerLift() {
        //lift
        try {
            lift(consumer).accept("one");
            lift(ConsumerTest::consumeAndThrow).accept("one");
            lift((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s)).accept("one");
        } catch (TestCheckedException e) {
            Assertions.fail("Expecting no exceptions");
        }

        {
            TestCheckedException exception = assertThrows(TestCheckedException.class, () -> lift(consumer).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            TestCheckedException exception = assertThrows(TestCheckedException.class, () -> lift(ConsumerTest::consumeAndThrow).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            TestCheckedException exception = assertThrows(TestCheckedException.class,
                    () -> lift((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s)).accept("exception"));
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
        wrap((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s), toRuntime).accept("one");

        {
            TestRuntimeException exception = assertThrows(TestRuntimeException.class, () -> wrap(consumer, toRuntime).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            TestRuntimeException exception = assertThrows(TestRuntimeException.class,
                    () -> wrap(ConsumerTest::consumeAndThrow, toRuntime).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            TestRuntimeException exception = assertThrows(TestRuntimeException.class,
                    () -> wrap((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s), toRuntime).accept("exception"));
            Assertions.assertNotNull(exception);
        }

        Function<TestCheckedException, TestWrappingCheckedException> f = (e) -> new TestWrappingCheckedException(e);

        try {
            wrap(ConsumerTest::consumeAndThrowAB, f);
        } catch (TestWrappingCheckedException e) {
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
            wrap((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s), toAnotherChecked).accept("one");
        } catch (TestWrappingCheckedException e) {
            Assertions.fail("Expecting no exceptions");
        }

        {
            TestWrappingCheckedException exception = assertThrows(TestWrappingCheckedException.class, () -> wrap(consumer, toAnotherChecked).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            TestWrappingCheckedException exception = assertThrows(TestWrappingCheckedException.class,
                    () -> wrap(ConsumerTest::consumeAndThrow, toAnotherChecked).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            TestWrappingCheckedException exception = assertThrows(TestWrappingCheckedException.class,
                    () -> wrap((ConsumerChecked<String, TestCheckedException>) (s) -> consumeAndThrow(s), toAnotherChecked).accept("exception"));
            Assertions.assertNotNull(exception);
        }
    }

    //////////////////

    @Test
    @Disabled
    void testCommonStuff() {

        ConsumerChecked<String, TestCheckedException> cons = Mockito.mock(ConsumerChecked.class);

        try {
            Stream.of("a", "b", "c").forEach(lift(cons));

            Mockito.verify(cons, Mockito.times(1)).accept("a");
            Mockito.verify(cons, Mockito.times(1)).accept("b");
            Mockito.verify(cons, Mockito.times(1)).accept("c");
        } catch (TestCheckedException e) {
            Assertions.fail("Expecting no exceptions");
        }

        ConsumerChecked<String, TestCheckedException.A> ch1 = s -> {
            if (s.equalsIgnoreCase("a"))
                throw new TestCheckedException.A();
        };

        Function<Exception, TestWrappingCheckedException> transform = TestWrappingCheckedException.A::new;

        try {
            wrap(ch1, transform).accept("b");
        } catch (TestWrappingCheckedException e) {
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
        } catch (TestCheckedException e) {
            e.printStackTrace();
        }

        try {
            lift(ConsumerTest::consumeAndThrow);
        } catch (TestCheckedException e) {
            e.printStackTrace();
        }

        try {
            lift(ConsumerTest::predicateAndThrow);
        } catch (TestCheckedException e) {
            e.printStackTrace();
        }

        try {
            lift(ConsumerTest::consumeAndThrowAB);
        } catch (TestCheckedException e) {
            e.printStackTrace();
        }

        wrap(ConsumerTest::functionAndThrow, toRuntime);
        wrap(ConsumerTest::consumeAndThrow, toRuntime);
        wrap(ConsumerTest::predicateAndThrow, toRuntime);
        wrap(ConsumerTest::consumeAndThrowAB, toRuntime);







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
                .map(wrapFunction((URL::new), toRuntime))
                .parallel()
                .forEach(wrapConsumer((url -> {

                    URLConnection yc = url.openConnection();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                            System.out.println(inputLine);
                    }

                }), toRuntime));




    }




    @Deprecated
    private static String functionAndThrow(String s) throws TestCheckedException {
        if (s.equalsIgnoreCase("exception"))
            throw new TestCheckedException();
        return s;
    }

    @Deprecated
    private static void consumeAndThrow(String s) throws TestCheckedException {
        if (s.equalsIgnoreCase("exception"))
            throw new TestCheckedException();
    }

    @Deprecated
    private static boolean predicateAndThrow(String s) throws TestCheckedException {
        if (s.equalsIgnoreCase("exception"))
            throw new TestCheckedException();
        return true;
    }

    @Deprecated
    private static Boolean functionToBooleanAndThrow(String s) throws TestCheckedException {
        if (s.equalsIgnoreCase("exception"))
            throw new TestCheckedException();
        return true;
    }

    @Deprecated
    private static void consumeAndThrowAB(String s) throws TestCheckedException.A, TestCheckedException.B {

        if (s.equalsIgnoreCase("exceptionA"))
            throw new TestCheckedException.A();

        if (s.equalsIgnoreCase("exceptionB"))
            throw new TestCheckedException.B();
    }



}
