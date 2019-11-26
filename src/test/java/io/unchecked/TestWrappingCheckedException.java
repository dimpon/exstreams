package io.unchecked;

public class TestWrappingCheckedException extends Exception {
    public TestWrappingCheckedException(Throwable cause) {
        super(cause);
    }


    static class A extends TestWrappingCheckedException {
        public A(Throwable cause) {
            super(cause);
        }
    }
    static class B extends TestWrappingCheckedException {
        public B(Throwable cause) {
            super(cause);
        }
    }
    static class C extends TestWrappingCheckedException {
        public C(Throwable cause) {
            super(cause);
        }
    }

}
