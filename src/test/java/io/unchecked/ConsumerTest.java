package io.unchecked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.unchecked.API.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ConsumerTest {

    private static final Function<VeryCheckedException, VeryRuntimeException> toRuntime = VeryRuntimeException::new;
    private static final Function<VeryCheckedException, AppCheckedException> toAnotherChecked = AppCheckedException::new;
    private static final Function<Exception, AppCheckedException> anyToAnotherChecked = AppCheckedException::new;

    private static final ConsumerChecked<String, VeryCheckedException> consume = s -> consumeAndThrow(s);

    @Test
    @DisplayName("Testing Consumer. Sneak Checked Exception.")
    @Timeout(1)
    void testConsumerSneak() {

        //sneak
        sneak(consume).accept("one");
        sneak(ConsumerTest::consumeAndThrow).accept("one");
        sneak((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("one");

        isVeryCheckedException(() -> sneak(consume).accept("exception"));
        isVeryCheckedException(() -> sneak(ConsumerTest::consumeAndThrow).accept("exception"));
        isVeryCheckedException(() -> sneak((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("exception"));

    }

    @Test
    @DisplayName("Testing Consumer. Lift Checked Exception on next level.")
    @Timeout(1)
    void testConsumerLift() {
        //lift
        try {
            lift(consume).accept("one");
            lift(ConsumerTest::consumeAndThrow).accept("one");
            lift((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s)).accept("one");
        } catch (VeryCheckedException e) {
            Assertions.fail("Expecting no exceptions");
        }

        {
            VeryCheckedException exception = assertThrows(VeryCheckedException.class, () -> lift(consume).accept("exception"));
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
        wrap(consume, toRuntime).accept("one");
        wrap(ConsumerTest::consumeAndThrow, toRuntime).accept("one");
        wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toRuntime).accept("one");

        {
            VeryRuntimeException exception = assertThrows(VeryRuntimeException.class, () -> wrap(consume, toRuntime).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            VeryRuntimeException exception = assertThrows(VeryRuntimeException.class, () -> wrap(ConsumerTest::consumeAndThrow, toRuntime).accept("exception"));
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
            wrap(consume, toAnotherChecked).accept("one");
            wrap(ConsumerTest::consumeAndThrow, toAnotherChecked).accept("one");
            wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toAnotherChecked).accept("one");
        } catch (AppCheckedException e) {
            Assertions.fail("Expecting no exceptions");
        }

        {
            AppCheckedException exception = assertThrows(AppCheckedException.class, () -> wrap(consume, toAnotherChecked).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            AppCheckedException exception = assertThrows(AppCheckedException.class, () -> wrap(ConsumerTest::consumeAndThrow, toAnotherChecked).accept("exception"));
            Assertions.assertNotNull(exception);
        }
        {
            AppCheckedException exception = assertThrows(AppCheckedException.class,
                    () -> wrap((ConsumerChecked<String, VeryCheckedException>) (s) -> consumeAndThrow(s), toAnotherChecked).accept("exception"));
            Assertions.assertNotNull(exception);
        }
    }

   /* @Test
    void testPredicate() {

        final PredicateChecked<String, VeryCheckedException> p = s -> predicateAndThrow(s);

        Stream.of("3", "5").filter(sneak(p)).findFirst();
        Stream.of("3", "5").filter(sneak(ConsumerTest::predicateAndThrow)).findFirst();
        ;
        Stream.of("3", "5").filter(sneak((PredicateChecked<String, VeryCheckedException>) s -> predicateAndThrow(s))).findFirst();


        try {

            Predicate<String> lift = lift(ConsumerTest::predicateAndThrow2);

            Stream.of("3", "7").filter(lift(ConsumerTest::predicateAndThrow2)).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Stream.of("3", "7").filter(wrap(ConsumerTest::predicateAndThrow, toAnotherChecked)).findFirst();
        } catch (AppCheckedException e) {
            e.printStackTrace();
        }

        try {
            wrap(ConsumerTest::predicateAndThrow2, anyToAnotherChecked);
        } catch (AppCheckedException e) {
            e.printStackTrace();
        }

        //Predicate<String> wrap = wrap(ConsumerTest::predicateAndThrow, anyToAnotherChecked);

    }*/

    private static void consumeAndThrow(String s) throws VeryCheckedException {
        if (s.equalsIgnoreCase("exception"))
            throw new VeryCheckedException();
    }

    private static boolean predicateAndThrow(String s) throws VeryCheckedException {

        if (s.equalsIgnoreCase("exception"))
            throw new VeryCheckedException();

        return true;
    }

    private static void consumeAndThrowAB(String s) throws ACheckedException, BCheckedException {

        if (s.equalsIgnoreCase("exceptionA"))
            throw new ACheckedException();

        if (s.equalsIgnoreCase("exceptionB"))
            throw new BCheckedException();
    }

    private static void consumeAndThrowA(String s) throws ACheckedException {

        if (s.equalsIgnoreCase("exceptionA"))
            throw new ACheckedException();

    }

    private static void isVeryCheckedException(Runnable action) {
        VeryCheckedException exception = assertThrows(VeryCheckedException.class, () -> {
            action.run();
        });
        Assertions.assertTrue(exception instanceof VeryCheckedException);
    }

}
