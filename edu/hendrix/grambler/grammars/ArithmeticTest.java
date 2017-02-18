package edu.hendrix.grambler.grammars;

import static org.junit.Assert.*;
import org.junit.*;

import edu.hendrix.grambler.Util;

public class ArithmeticTest {
	@Test
	public void test125() {
		edu.hendrix.grambler.Grammar g = new Arithmetic();
		edu.hendrix.grambler.Tree t = g.parse2("2**3 * 4");
		assertEquals(false, t.isError());
	}

	@Test
	public void test304() {
		edu.hendrix.grambler.Grammar g = new Arithmetic();
		edu.hendrix.grambler.Tree t = g.parse2("2**3 * 4");
		assertEquals("expr\n\tsum\n\t\tproduct\n\t\t\tproduct\n\t\t\t\tpower\n\t\t\t\t\tpower\n\t\t\t\t\t\tnumber: \"2\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\texpOp: \"**\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\tnumber: \"3\"\n\t\t\tsp: \" \"\n\t\t\tmulOp: \"*\"\n\t\t\tsp: \" \"\n\t\t\tpower\n\t\t\t\tnumber: \"4\"\n\tsp: \"\"\n", t.toTextTree());
	}

	@Test
	public void test775() {
		edu.hendrix.grambler.Grammar g = new Arithmetic();
		edu.hendrix.grambler.Tree t = g.parse2("12 + 7 * 2");
		assertEquals(false, t.isError());
	}

	@Test
	public void test956() {
		edu.hendrix.grambler.Grammar g = new Arithmetic();
		edu.hendrix.grambler.Tree t = g.parse2("12 + 7 * 2");
		assertEquals("expr\n\tsum\n\t\tsum\n\t\t\tproduct\n\t\t\t\tpower\n\t\t\t\t\tnumber: \"12\"\n\t\tsp: \" \"\n\t\taddOp: \"+\"\n\t\tsp: \" \"\n\t\tproduct\n\t\t\tproduct\n\t\t\t\tpower\n\t\t\t\t\tnumber: \"7\"\n\t\t\tsp: \" \"\n\t\t\tmulOp: \"*\"\n\t\t\tsp: \" \"\n\t\t\tpower\n\t\t\t\tnumber: \"2\"\n\tsp: \"\"\n", t.toTextTree());
	}

}

