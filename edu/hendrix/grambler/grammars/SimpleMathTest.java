package edu.hendrix.grambler.grammars;

import static org.junit.Assert.*;
import org.junit.*;

import edu.hendrix.grambler.Util;

public class SimpleMathTest {
	@Test
	public void test125() throws edu.hendrix.grambler.MalformedGrammarException {
		edu.hendrix.grambler.Grammar g = new SimpleMath();
		edu.hendrix.grambler.Tree t = g.parse2("222+333*44444");
		assertEquals(true, t.isError());
	}

	@Test
	public void test356() throws edu.hendrix.grambler.MalformedGrammarException {
		edu.hendrix.grambler.Grammar g = new SimpleMath();
		edu.hendrix.grambler.Tree t = g.parse2("222+333+44444");
		assertEquals(false, t.isError());
	}

	@Test
	public void test584() throws edu.hendrix.grambler.MalformedGrammarException {
		edu.hendrix.grambler.Grammar g = new SimpleMath();
		edu.hendrix.grambler.Tree t = g.parse2("222+333+44444");
		assertEquals("sum\n\tsum\n\t\tsum\n\t\t\tnumber: \"222\"\n\t\tsp: \"\"\n\t\top: \"+\"\n\t\tsp: \"\"\n\t\tnumber: \"333\"\n\tsp: \"\"\n\top: \"+\"\n\tsp: \"\"\n\tnumber: \"44444\"\n", t.toTextTree());
	}

	@Test
	public void test979() throws edu.hendrix.grambler.MalformedGrammarException {
		edu.hendrix.grambler.Grammar g = new SimpleMath();
		edu.hendrix.grambler.Tree t = g.parse2("222+333-44444");
		assertEquals("sum\n\tsum\n\t\tsum\n\t\t\tnumber: \"222\"\n\t\tsp: \"\"\n\t\top: \"+\"\n\t\tsp: \"\"\n\t\tnumber: \"333\"\n\tsp: \"\"\n\top: \"-\"\n\tsp: \"\"\n\tnumber: \"44444\"\n", t.toTextTree());
	}

}

