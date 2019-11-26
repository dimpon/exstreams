package io.unchecked;

public class TestCheckedException extends Exception {

	static class A extends TestCheckedException {}
	static class B extends TestCheckedException {}
	static class C extends TestCheckedException {}

}
