package edu.hendrix.grambler;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilTest {
	static String escaped1 = "\"\\s*\"";
	static String escaped2 = "hello";
	static String escapedBig = "lines: lines '\n' line | line;\nline: assign | orExpr;\nassign: symbol \"\\s*\" '=' \"\\s*\" orExpr;\norExpr: orExpr \"\\s+\" 'or' \"\\s+\" andExpr | andExpr;\nandExpr: andExpr \"\\s+\" 'and' \"\\s+\" notExpr | notExpr;\nnotExpr: 'not' \"\\s+\" parenExpr | parenExpr;\nparenExpr: '(' \"\\s*\" orExpr \"\\s*\" ')' | symbol\nsymbol: \"[A-Za-z]+\";";

	@Test
	public void putback1() {
		assertEquals("\n", Util.putBackEscapes("\\n"));
	}
	
	@Test
	public void test1() {
		assertEquals(escaped1, Util.dejavafy(Util.javafy(escaped1)));
	}
	
	@Test
	public void test2() {
		System.out.println(Util.javafy(escaped2));
		assertEquals(escaped2, Util.dejavafy(Util.javafy(escaped2)));
	}
	
	@Test
	public void testBig() {
		assertEquals(escapedBig, Util.dejavafy(Util.javafy(escapedBig)));
	}

}
