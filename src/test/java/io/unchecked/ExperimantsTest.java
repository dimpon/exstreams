package io.unchecked;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Stream;

import static io.unchecked.API.sneakFunction;
import static io.unchecked.API.wrapFunction;
import static io.unchecked.TestsUtils.anyToAnotherChecked;
import static io.unchecked.TestsUtils.toRuntime;

public class ExperimantsTest {


    @Test
    void testNewInstance() {

        Stream.of(String.class, Object.class)
                .map(sneakFunction(this::instance))
                .forEach(System.out::println);

        try {
            Stream.of(API.class, Object.class)
                    .map(wrapFunction(Class::newInstance, anyToAnotherChecked))
                    .forEach(System.out::println);
        } catch (TestWrappingCheckedException e) {
            e.printStackTrace();
        }

        FunctionChecked3<Class, Object, IllegalAccessException, InstantiationException> t3 = this::instance;

        sneakFunction(t3);

        wrapFunction(t3, toRuntime);


        try {
            liftFunction2(t3);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    public static <T, R, E extends Exception, E1 extends Exception> Function<T, R> liftFunction2(FunctionChecked3<T, R, E, E1> f) throws E, E1 {
        return sneakFunction(f);
    }


    private <T> T instance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

}
