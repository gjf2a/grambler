package edu.hendrix.grambler.grammars;

import static org.junit.Assert.*;
import org.junit.*;

public class BadNewlineTest {
	@Test
	public void test314() {
		edu.hendrix.grambler.Grammar g = new BadNewline();
		edu.hendrix.grambler.Tree t = g.parse2("thing\nthing\nthing");
		assertEquals(false, t.isError());
	}

}

