package io.unchecked;

public class AppCheckedException extends Exception {
    public AppCheckedException(Throwable cause) {
        super(cause);
    }


    static class A extends AppCheckedException{
        public A(Throwable cause) {
            super(cause);
        }
    }
    static class B extends AppCheckedException{
        public B(Throwable cause) {
            super(cause);
        }
    }
    static class C extends AppCheckedException{
        public C(Throwable cause) {
            super(cause);
        }
    }

}
