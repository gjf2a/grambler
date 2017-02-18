package edu.hendrix.grambler;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

public class JIGParserTest {

	@Test
	public void test() throws FileNotFoundException {
		JIGParser p = new JIGParser();
		Grammar g = p.makeGrammarFrom(new File("src/edu/hendrix/grambler/grammars/CFG.java"));
		assertEquals("g: g sp line | line;\nline: nonterm sp ':' sp rhs sp ';' sp;\nrhs: rhs sp '|' sp elements | elements;\nsp: \"\\s*\";\nspacing: \"\\s+\";\nelements: elements spacing element | element;\nelement: term | nonterm | regex;\nterm: \"'.*?[^\\\\]'\";\nnonterm: \"\\w+\";\nregex: \"\\\".*?[^\\\\]\\\"\";\n", g.toString());
	}

}
