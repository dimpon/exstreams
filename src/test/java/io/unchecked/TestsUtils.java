package io.unchecked;

import org.junit.jupiter.api.Assertions;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestsUtils {

    static final Function<Exception, TestRuntimeException> toRuntime = TestRuntimeException::new;
    static final Function<TestCheckedException, TestWrappingCheckedException> toAnotherChecked = TestWrappingCheckedException::new;

    static final Function<Exception, TestWrappingCheckedException> anyToAnotherChecked = TestWrappingCheckedException::new;

    static void voidOrThrow(String in) throws TestCheckedException {
        if (in.equalsIgnoreCase("exception"))
            throw new TestCheckedException();
    }

    static <T> T returnOrThrow(String in, T out) throws TestCheckedException {
        if (in.equalsIgnoreCase("exception"))
            throw new TestCheckedException();
        return out;
    }

    static void isTestCheckedException(Runnable action) {
        TestCheckedException exception = assertThrows(TestCheckedException.class, action::run);
        Assertions.assertNotNull(exception);
    }

}
