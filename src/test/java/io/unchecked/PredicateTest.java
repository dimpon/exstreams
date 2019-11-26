package io.unchecked;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static io.unchecked.API.sneak;
import static io.unchecked.TestsUtils.isTestCheckedException;
import static io.unchecked.TestsUtils.returnOrThrow;

public class PredicateTest {

    private static final PredicateChecked<String, TestCheckedException> predicator = s -> trueOrThrow(s);

    @Test
    @DisplayName("Testing Predicate. Sneak Predicate Exception.")
    @Timeout(1)
    void testPredicateSneak() {

        //sneak
        boolean one1 = sneak(predicator).test("one");
        boolean one2 = sneak(PredicateTest::trueOrThrow).test("one");
        boolean one3 = sneak((PredicateChecked<String, TestCheckedException>) (s) -> trueOrThrow(s)).test("one");

        isTestCheckedException(() -> sneak(predicator).test("exception"));
        isTestCheckedException(() -> sneak(PredicateTest::trueOrThrow).test("exception"));
        isTestCheckedException(() -> sneak((PredicateChecked<String, TestCheckedException>) (s) -> trueOrThrow(s)).test("exception"));
    }

    private static boolean trueOrThrow(String in) throws TestCheckedException {
        return returnOrThrow(in, true);
    }
}
