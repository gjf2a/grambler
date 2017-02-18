package edu.hendrix.grambler;

//Copyright 2012 by Gabriel J. Ferrer
//
//This program is part of the Grambler project.
//
//Grambler is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as 
//published by the Free Software Foundation, either version 3 of 
//the License, or (at your option) any later version.
//
//Grambler is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with Grambler.  If not, see <http://www.gnu.org/licenses/>..

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.*;

import edu.hendrix.grambler.grammars.*;

public class GramblerTest {
	
	public void simpleTest(Grammar g, String input) {
		Chart c = new Chart(input, g);
		assertTrue(c.reachesAcceptingState());
	}
	
	public void errorTest(Grammar g, String input, String msg) {
		Tree t = g.parse2(input);
		assertEquals(msg, t.getErrorMessage());
	}
	
	@Test
	public void hashTest() {
		Grammar g1 = makeGrammar1();
		Grammar g2 = makeGrammar1();
		Grammar g3 = makeGrammar2();
		assertEquals(g1.hashCode(), g2.hashCode());
		assertNotSame(g2.hashCode(), g3.hashCode());
	}

	@Test
	public void test1() {
		simpleTest(makeGrammar1(), "2+3");
	}

	@Test
	public void test2() {
		simpleTest(makeGrammar1(), "27+3");
	}
	
	@Test
	public void test3() {
		simpleTest(makeGrammar2(), "27+3");
	}
	
	@Test
	public void test4() {
		errorTest(makeGrammar2(), "27+", "Syntax error: Input exhausted");
	}
	
	@Test
	public void test5() {
		simpleTest(makeGrammar3(), "543+432+321");
	}
	
	@Test
	public void test6() {
		simpleTest(makeGrammar3(), "543 + 432 + 321");
	}
	
	@Test
	public void test6a() throws ParseException {
		Grammar g = makeGrammar3();
		g.parse("543 + 432 + 321");
	}
	
	@Test
	public void testZeroLengthEnding1() {
		simpleTest(new SimpleMathSpaceBug(), "543+432+321");
	}
	
	@Test
	public void test7() {
		errorTest(makeGrammar2(), "27-3", "Syntax error in line 1 (matched up to: [27])");
	}
	
	@Test(expected=ParseException.class)
	public void test7a() throws ParseException {
		Grammar g = makeGrammar2();
		g.parse("27-3");
	}
	
	@Test
	public void test8() {
		simpleTest(makeGrammar3(), "543 + 432\n+321");
	}
	
	@Test 
	public void test9() {
		errorTest(makeGrammar3(), "543 + 432\n*321", "Syntax error in line 2 (matched up to: [543 + 432])");
	}
	
	@Test
	public void test9a() {
		errorTest(makeGrammar3(), "543 + 432 * 321", "Syntax error in line 1 (matched up to: [543 + 432 ])");
	}
	
	@Test
	public void test10() {
		errorTest(Grammar.makeFrom("sum: number '+' number;\nnumber: '0' | '1';\n"), "0+1+0", "Syntax error in line 1 (matched up to: [0+1])");
	}
	
	@Test
	public void test11() {
		Grammar g = Grammar.makeFrom("command: event | action;\naction: \"fd|bk|lt|rt\";\nevent: \"cs|pd|pu|home|st|ht\";\n");
		simpleTest(g, "fd");
	}
	
	@Test
	public void test12() {
		Grammar g = Grammar.makeFrom("bug: code;\ncode: func | op;\nfunc: 'to' sp name sp params sp list;\nop: control sp list | ifElse | command;\ncommand: action sp num | event;\nlist: '[' group ']';\ngroup: group sp op | op;\ncontrol: 'repeat' sp num | 'if' sp cond;\nifElse: 'ifelse' sp cond sp list sp list;\ncond: '(' cond ')' | cond sp booleanOp sp cond | num sp booleanOp sp num;\naction: \"fd|bk|lt|rt\";\nevent: \"pd|pu|home|cs|st|ht\";\nnum: \"\\d+\" | param | num sp mathOp sp num | '(' num ')';\nbooleanOp: \"<|>|=|<=|>=\";\nmathOp: \"\\+|-|/|\\*\";\nparams: params sp param | param;\nparam: ':' name;\nname: \"[A-Za-z]\\w*\";\nsp: \"\\s+\";\n");
		simpleTest(g, "repeat 5 [rt 20 lt 15 fd 5]");
	}
	
	public void treeTest(Grammar g, String input, String targetTreeText) {
		Chart c = new Chart(input, g);
		assertEquals(targetTreeText, c.getParseTree().toTextTree());
	}
	
	@Test
	public void cleanUpTerminalTest1() {
		assertEquals("\n", Util.cleanUpTerminal("'\\n'"));
	}
	
	@Test
	public void displayTerminalTest1() {
		assertEquals("'\\n'", Util.displayTerminal("\n"));
	}
	
	@Test
	public void newLineTest1() {
		simpleTest(makeGrammar4(), "thing\nthing\n");
	}
	
	@Test
	public void newLineTest2() {
		simpleTest(makeGrammar5(), "thing\nthing\n");
	}
		
	@Test
	public void test32587() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("g: g sp line | line;\nline: nonterm sp ':' sp rhs sp ';' sp;\nrhs: rhs sp '|' sp elements | elements;\nsp: \"\\s*\";\nspacing: \"\\s+\";\nelements: elements spacing element | element;\nelement: term | nonterm | regex;\nterm: \"'.*?[^\\\\]'\";\nnonterm: \"\\w+\";\nregex: \"\\\".*?[^\\\\]\\\"\";\n");
		assertEquals(false, t.isError());
	}

	@Test
	public void test33049() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("top: package class | class;\npackage: optSpace 'package' space packageName ';' optSpace;\npackageName: packageName '.' name | name;\nname: \"\\w+\";\nclass: 'public class' space name space 'extends ' grammarClass ' {' optSpace constructor optSpace '}' space;\ngrammarClass: 'Grammar' | 'edu.hendrix.grambler.Grammar';\nconstructor: 'public' space name optSpace '() {' optSpace 'super();' optSpace productions optSpace '}';\nproductions: productions space production | production;\nproduction: 'addProduction(' string optSpace rhs ');';\nrhs: rhs alternative | alternative;\nalternative: ',' optSpace 'new String[]{' stringList '}' optSpace;\nstringList: stringList ',' optSpace string | string;\nstring: \"\\\".*?[^\\\\]\\\"\";\nspace: \"\\s+\";\noptSpace: \"\\s*\";");
		assertEquals(false, t.isError());
	}

	@Test
	public void test33981() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("top: package class | class;\npackage: optSpace 'package' space packageName ';' optSpace;\npackageName: packageName '.' name | name;\nname: \"\\w+\";\nclass: 'public class' space name space 'extends ' grammarClass ' {' optSpace constructor optSpace '}' space;\ngrammarClass: 'Grammar' | 'edu.hendrix.grambler.Grammar';\nconstructor: 'public' space name optSpace '() {' optSpace 'super();' optSpace productions optSpace '}';\nproductions: productions space production | production;\nproduction: 'addProduction(' string optSpace rhs ');';\nrhs: rhs alternative | alternative;\nalternative: ',' optSpace 'new String[]{' stringList '}' optSpace;\nstringList: stringList ',' optSpace string | string;\nstring: \"\\\".*?[^\\\\]\\\"\";\nspace: \"\\s+\";\noptSpace: \"\\s*\";");
		assertEquals("g\n\tg\n\t\tg\n\t\t\tg\n\t\t\t\tg\n\t\t\t\t\tg\n\t\t\t\t\t\tg\n\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"top\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"package\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"class\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"class\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"package\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'package'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"space\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"packageName\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"';'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"packageName\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"packageName\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'.'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"name\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"name\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"name\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tregex: \"\\\"\\\\w+\\\"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"class\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'public class'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"space\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"name\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"space\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'extends '\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"grammarClass\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"' {'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"constructor\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'}'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"space\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"grammarClass\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'Grammar'\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'edu.hendrix.grambler.Grammar'\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\tnonterm: \"constructor\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'public'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"space\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"name\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'() {'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'super();'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"productions\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'}'\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\tnonterm: \"productions\"\n\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"productions\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"space\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"production\"\n\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"production\"\n\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\tnonterm: \"production\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'addProduction('\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"string\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"rhs\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tterm: \"');'\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\tline\n\t\t\t\t\t\t\tnonterm: \"rhs\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"rhs\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"alternative\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"alternative\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\tline\n\t\t\t\t\t\tnonterm: \"alternative\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\trhs\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"','\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'new String[]{'\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"stringList\"\n\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tterm: \"'}'\"\n\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\tsp: \"\"\n\t\t\t\tline\n\t\t\t\t\tnonterm: \"stringList\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t:: \":\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\trhs\n\t\t\t\t\t\trhs\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"stringList\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tterm: \"','\"\n\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"optSpace\"\n\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"string\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tnonterm: \"string\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t;: \";\"\n\t\t\t\t\tsp: \"\\n\"\n\t\t\tsp: \"\"\n\t\t\tline\n\t\t\t\tnonterm: \"string\"\n\t\t\t\tsp: \"\"\n\t\t\t\t:: \":\"\n\t\t\t\tsp: \" \"\n\t\t\t\trhs\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tregex: \"\\\"\\\\\\\".*?[^\\\\\\\\]\\\\\\\"\\\"\"\n\t\t\t\tsp: \"\"\n\t\t\t\t;: \";\"\n\t\t\t\tsp: \"\\n\"\n\t\tsp: \"\"\n\t\tline\n\t\t\tnonterm: \"space\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telement\n\t\t\t\t\t\tregex: \"\\\"\\\\s+\\\"\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \"\\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"optSpace\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\s*\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\"\n", t.toTextTree());
	}

	@Test
	public void test50727() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("g: g sp line | line;\nline: nonterm sp ':' sp rhs sp ';' sp;\nrhs: rhs sp '|' sp elements | elements;\nsp: \"\\s*\";\nspacing: \"\\s+\";\nelements: elements spacing element | element;\nelement: term | nonterm | regex;\nterm: \"'.*?[^\\\\]'\";\nnonterm: \"\\w+\";\nregex: \"\\\".*?[^\\\\]\\\"\";\n");
		assertEquals("g\n\tg\n\t\tg\n\t\t\tg\n\t\t\t\tg\n\t\t\t\t\tg\n\t\t\t\t\t\tg\n\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\tg\n\t\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"g\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"g\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"line\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"line\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\t\tnonterm: \"line\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"nonterm\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"':'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"rhs\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"';'\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\t\tnonterm: \"rhs\"\n\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"rhs\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tterm: \"'|'\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"elements\"\n\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"elements\"\n\t\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tregex: \"\\\"\\\\s*\\\"\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\tline\n\t\t\t\t\t\t\tnonterm: \"spacing\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tregex: \"\\\"\\\\s+\\\"\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\tline\n\t\t\t\t\t\tnonterm: \"elements\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\trhs\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"elements\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"spacing\"\n\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"element\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"element\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\tsp: \"\"\n\t\t\t\tline\n\t\t\t\t\tnonterm: \"element\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t:: \":\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\trhs\n\t\t\t\t\t\trhs\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"term\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"nonterm\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tnonterm: \"regex\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t;: \";\"\n\t\t\t\t\tsp: \"\\n\"\n\t\t\tsp: \"\"\n\t\t\tline\n\t\t\t\tnonterm: \"term\"\n\t\t\t\tsp: \"\"\n\t\t\t\t:: \":\"\n\t\t\t\tsp: \" \"\n\t\t\t\trhs\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tregex: \"\\\"'.*?[^\\\\\\\\]'\\\"\"\n\t\t\t\tsp: \"\"\n\t\t\t\t;: \";\"\n\t\t\t\tsp: \"\\n\"\n\t\tsp: \"\"\n\t\tline\n\t\t\tnonterm: \"nonterm\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telement\n\t\t\t\t\t\tregex: \"\\\"\\\\w+\\\"\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \"\\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"regex\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\\\\".*?[^\\\\\\\\]\\\\\\\"\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\\n\"\n", t.toTextTree());
	}

	@Test
	public void test58127() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("sum: number '+' number; \nnumber: '0' | '1';");
		assertEquals(false, t.isError());
	}

	@Test
	public void test58337() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("sum: number '+' number; \nnumber: '0' | '1';");
		assertEquals("g\n\tg\n\t\tline\n\t\t\tnonterm: \"sum\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telements\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'+'\"\n\t\t\t\t\tspacing: \" \"\n\t\t\t\t\telement\n\t\t\t\t\t\tnonterm: \"number\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \" \\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"number\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telement\n\t\t\t\t\t\tterm: \"'0'\"\n\t\t\tsp: \" \"\n\t\t\t|: \"|\"\n\t\t\tsp: \" \"\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tterm: \"'1'\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\"\n", t.toTextTree());
	}

	@Test
	public void test34125() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("sum: number '+' number; \nnumber: \"\\d+\";");
		assertEquals(false, t.isError());
	}

	@Test
	public void test34334() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("sum: number '+' number; \nnumber: \"\\d+\";");
		assertEquals("g\n\tg\n\t\tline\n\t\t\tnonterm: \"sum\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telements\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'+'\"\n\t\t\t\t\tspacing: \" \"\n\t\t\t\t\telement\n\t\t\t\t\t\tnonterm: \"number\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \" \\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"number\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\d+\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\"\n", t.toTextTree());
	}

	@Test
	public void test34330() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("sum: sum sp op sp number; \nsp: \"\\s*\"; \nop: '+'; \nnumber: \"\\d+\";");
		assertEquals(false, t.isError());
	}

	@Test
	public void test34568() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("sum: sum sp op sp number; \nsp: \"\\s*\"; \nop: '+'; \nnumber: \"\\d+\";");
		assertEquals("g\n\tg\n\t\tg\n\t\t\tg\n\t\t\t\tline\n\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t:: \":\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\trhs\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"op\"\n\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t;: \";\"\n\t\t\t\t\tsp: \" \\n\"\n\t\t\tsp: \"\"\n\t\t\tline\n\t\t\t\tnonterm: \"sp\"\n\t\t\t\tsp: \"\"\n\t\t\t\t:: \":\"\n\t\t\t\tsp: \" \"\n\t\t\t\trhs\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tregex: \"\\\"\\\\s*\\\"\"\n\t\t\t\tsp: \"\"\n\t\t\t\t;: \";\"\n\t\t\t\tsp: \" \\n\"\n\t\tsp: \"\"\n\t\tline\n\t\t\tnonterm: \"op\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telement\n\t\t\t\t\t\tterm: \"'+'\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \" \\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"number\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\d+\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\"\n", t.toTextTree());
	}

	@Test
	public void test34556() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("expr: sum sp;\nsum: sum sp addOp sp product | product;\nproduct: product sp mulOp sp number | number;\nsp: \"\\s*\";\nmulOp: '*' | '/';\naddOp: '+' | '-';\nnumber: \"\\d+\";");
		assertEquals(false, t.isError());
	}

	@Test
	public void test34895() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("expr: sum sp;\nsum: sum sp addOp sp product | product;\nproduct: product sp mulOp sp number | number;\nsp: \"\\s*\";\nmulOp: '*' | '/';\naddOp: '+' | '-';\nnumber: \"\\d+\";");
		assertEquals("g\n\tg\n\t\tg\n\t\t\tg\n\t\t\t\tg\n\t\t\t\t\tg\n\t\t\t\t\t\tg\n\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\tnonterm: \"expr\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\tline\n\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"addOp\"\n\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\tline\n\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\trhs\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"mulOp\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\tsp: \"\"\n\t\t\t\tline\n\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t:: \":\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\trhs\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tregex: \"\\\"\\\\s*\\\"\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t;: \";\"\n\t\t\t\t\tsp: \"\\n\"\n\t\t\tsp: \"\"\n\t\t\tline\n\t\t\t\tnonterm: \"mulOp\"\n\t\t\t\tsp: \"\"\n\t\t\t\t:: \":\"\n\t\t\t\tsp: \" \"\n\t\t\t\trhs\n\t\t\t\t\trhs\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tterm: \"'*'\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\t|: \"|\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'/'\"\n\t\t\t\tsp: \"\"\n\t\t\t\t;: \";\"\n\t\t\t\tsp: \"\\n\"\n\t\tsp: \"\"\n\t\tline\n\t\t\tnonterm: \"addOp\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\trhs\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'+'\"\n\t\t\t\tsp: \" \"\n\t\t\t\t|: \"|\"\n\t\t\t\tsp: \" \"\n\t\t\t\telements\n\t\t\t\t\telement\n\t\t\t\t\t\tterm: \"'-'\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \"\\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"number\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\d+\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\"\n", t.toTextTree());
	}

	@Test
	public void test34877() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("expr: sum sp;\nsum: sum sp addOp sp product | product;\nproduct: product sp mulOp sp number | number;\nsp: \"\\s*\";\nmulOp: '*' | '/';\naddOp: '+' | '-';\nnumber: \"\\d+\";");
		assertEquals(false, t.isError());
	}

	@Test
	public void test35216() {
		edu.hendrix.grambler.Grammar g = new CFG();
		edu.hendrix.grambler.Tree t = g.parse2("expr: sum sp;\nsum: sum sp addOp sp product | product;\nproduct: product sp mulOp sp number | number;\nsp: \"\\s*\";\nmulOp: '*' | '/';\naddOp: '+' | '-';\nnumber: \"\\d+\";");
		assertEquals("g\n\tg\n\t\tg\n\t\t\tg\n\t\t\t\tg\n\t\t\t\t\tg\n\t\t\t\t\t\tg\n\t\t\t\t\t\t\tline\n\t\t\t\t\t\t\t\tnonterm: \"expr\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\tline\n\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sum\"\n\t\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"addOp\"\n\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\tline\n\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t:: \":\"\n\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\trhs\n\t\t\t\t\t\t\trhs\n\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"product\"\n\t\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\t\tnonterm: \"mulOp\"\n\t\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\t|: \"|\"\n\t\t\t\t\t\t\tsp: \" \"\n\t\t\t\t\t\t\telements\n\t\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\t\tnonterm: \"number\"\n\t\t\t\t\t\tsp: \"\"\n\t\t\t\t\t\t;: \";\"\n\t\t\t\t\t\tsp: \"\\n\"\n\t\t\t\tsp: \"\"\n\t\t\t\tline\n\t\t\t\t\tnonterm: \"sp\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t:: \":\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\trhs\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tregex: \"\\\"\\\\s*\\\"\"\n\t\t\t\t\tsp: \"\"\n\t\t\t\t\t;: \";\"\n\t\t\t\t\tsp: \"\\n\"\n\t\t\tsp: \"\"\n\t\t\tline\n\t\t\t\tnonterm: \"mulOp\"\n\t\t\t\tsp: \"\"\n\t\t\t\t:: \":\"\n\t\t\t\tsp: \" \"\n\t\t\t\trhs\n\t\t\t\t\trhs\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tterm: \"'*'\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\t|: \"|\"\n\t\t\t\t\tsp: \" \"\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'/'\"\n\t\t\t\tsp: \"\"\n\t\t\t\t;: \";\"\n\t\t\t\tsp: \"\\n\"\n\t\tsp: \"\"\n\t\tline\n\t\t\tnonterm: \"addOp\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\trhs\n\t\t\t\t\telements\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'+'\"\n\t\t\t\tsp: \" \"\n\t\t\t\t|: \"|\"\n\t\t\t\tsp: \" \"\n\t\t\t\telements\n\t\t\t\t\telement\n\t\t\t\t\t\tterm: \"'-'\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tsp: \"\\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"number\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\d+\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tsp: \"\"\n", t.toTextTree());
	}

	
	public void grammarTest(String grammarInput) {
		CFGParser c = new CFGParser();
		Grammar result = c.makeGrammarFrom(grammarInput);
		assertEquals(grammarInput, result.toString());
	}
	
	@Test
	public void grammar1() {
		grammarTest("sum: number '+' number;\nnumber: '0' | '1';\n");
	}
	
	@Test
	public void grammar2() {
		grammarTest("sum: number '+' number;\nnumber: \"\\d+\";\n");
	}
	
	@Test
	public void grammar3() {
		grammarTest("sum: sum sp op sp number;\nsp: \"\\s*\";\nop: '+';\nnumber: \"\\d+\";\n");
	}
	
	@Test
	public void grammar4() {
		grammarTest("lines: line lines | line;\nline: 'thing' \"\n\";\n");
	}
	
	@Test
	public void grammar5() {
		grammarTest("lines: line lines | line;\nline: 'thing' '\\n';\n");
	}
	
	@Test
	public void metaGrammarCheck() {
		grammarTest(metaGrammarString());
	}
	
	@Test
	public void metaTest1() {
		simpleTest(metaGrammar(), "s: n '+' n;\nn: \"\\d+\";\n");
	}
	
	@Ignore
	@Test
	public void metaParseTree1() {
		this.treeTest(metaGrammar(), "s: n '+' n;\nn: \"\\d+\";\n", "g\n\tg\n\t\tline\n\t\t\tnonterm: \"s\"\n\t\t\tsp: \"\"\n\t\t\t:: \":\"\n\t\t\tsp: \" \"\n\t\t\trhs\n\t\t\t\telements\n\t\t\t\t\telements\n\t\t\t\t\t\telements\n\t\t\t\t\t\t\telement\n\t\t\t\t\t\t\t\tnonterm: \"n\"\n\t\t\t\t\t\tspacing: \" \"\n\t\t\t\t\t\telement\n\t\t\t\t\t\t\tterm: \"'+'\"\n\t\t\t\t\tspacing: \" \"\n\t\t\t\t\telement\n\t\t\t\t\t\tnonterm: \"n\"\n\t\t\tsp: \"\"\n\t\t\t;: \";\"\n\t\t\tspacing: \"\\n\"\n\tsp: \"\"\n\tline\n\t\tnonterm: \"n\"\n\t\tsp: \"\"\n\t\t:: \":\"\n\t\tsp: \" \"\n\t\trhs\n\t\t\telements\n\t\t\t\telement\n\t\t\t\t\tregex: \"\\\"\\\\d+\\\"\"\n\t\tsp: \"\"\n\t\t;: \";\"\n\t\tspacing: \"\\n\"\n");
	}
	
	public String metaGrammarString() {
		return "g: g sp line | line;\nline: nonterm sp ':' sp rhs sp ';' spacing;\nrhs: rhs sp '|' sp elements | elements;\nsp: \"\\s*\";\nspacing: \"\\s+\";\nelements: elements spacing element | element;\nelement: term | nonterm | regex;\nterm: \"'.*?[^\\\\]'\";\nnonterm: \"\\w+\";\nregex: \"\\\".*?[^\\\\]\\\"\";\n";
	}
	
	public Grammar metaGrammar() {
		return CFG.makeFrom(metaGrammarString());
	}
	
	public static Grammar makeGrammar1() {
		return Grammar.makeFrom("sum: number op number; op: '+'; number: number digit | digit; digit: '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'; ");
	}
	
	public static Grammar makeGrammar2() {
		return Grammar.makeFrom("sum: number op number; op: '+'; number: \"\\d+\"; ");
	}
	
	public static Grammar makeGrammar3() {
		return new SimpleMath();
	}
	
	public static Grammar makeGrammar4() {
		return Grammar.makeFrom("lines: line lines | line;\nline: 'thing' \"\n\";");
	}
	
	public static Grammar makeGrammar5() {
		return Grammar.makeFrom("lines: line lines | line;\nline: 'thing' '\n';");
	}
	
	@Test(expected=MalformedGrammarException.class)
	public void testBadGrammar() throws MalformedGrammarException {
		Grammar g = Grammar.makeFrom("sum: number op number; op: '+';"); 
		g.assertAllNonterminalsDefined();
	}
	
	@Test
	public void testBugReport1() {
		errorTest(makeGrammar2(), "cry", "Syntax error in line 1");
	}
	
	@Test
	public void testJavaOutput1() throws IOException {
		Grammar g = new CFG();
		String prog = Util.toJavaProgram("CFGTestTest", g);
		File f = new File("/Users/gabriel/software/grambler/src/edu/hendrix/grambler/tests/CFGTestTest.java");
		Util.stringToFile(f, "package edu.hendrix.grambler.tests;\n\n" + prog);
		boolean compiled = Util.compile(f.getAbsolutePath());
		assertTrue(compiled);
		
		URL[] target = {new File("/Users/gabriel/software/grambler/src/").toURI().toURL()};
		URLClassLoader loader = new URLClassLoader(target);
		Grammar g2 = Grammar.makeFromClassFile("edu.hendrix.grambler.tests.CFGTestTest", loader);
		assertEquals(g, g2);
	}
	
	@Test
	public void testJavaInput() {
		Grammar g1 = new JavaImportGrammar();
		JIGParser parser = new JIGParser();
		String program = Util.toJavaProgram("JavaImportGrammar", g1);
		Grammar g2 = parser.makeGrammarFrom(program);
		assertEquals(g1, g2);
	}
	
	public void testChartRow(String input, Grammar g, int last, int prev1, int prev2) {
		Chart c = new Chart(input, g);
		ChartRowTracker crt = new ChartRowTracker(c);
		int row = crt.getMaxUsableRow();
		assertEquals(last, row);
		assertTrue(crt.isLegalRow(row));
		
		assertTrue(crt.isLegalRow(crt.getMaxRow()));
		assertFalse(crt.isLegalRow(crt.getMaxRow() + 1));

		row = crt.getNextUsableRow(row, -1);
		assertTrue(crt.isLegalRow(row));
		assertEquals(prev1, row);

		int row2 = crt.getNextUsableRow(row, -1);
		//assertTrue(crt.isLegalRow(row2));
		assertEquals(prev2, row2);
		if (row2 < row) {
			assertEquals(prev1, crt.getNextUsableRow(row2, 1));
		}

		assertTrue(crt.isLegalRow(crt.getMinRow()));
		assertEquals(0, crt.getMinRow());
		assertFalse(crt.isLegalRow(crt.getMinRow() - 1));
	}
	
	@Test
	public void testChartRow1() {
		testChartRow("2+333+4444", makeGrammar3(), 10, 6, 5);
	}
	
	@Test
	public void testChartRow2() {
		testChartRow("2+333*4444", makeGrammar3(), 5, 2, 1);
	}
	
	@Test
	public void testChartRow3() {
		testChartRow("2*3", makeGrammar3(), 1, 0, 0);
	}
	
	@Test
	public void testRowOutput() {
		Grammar g = makeGrammar3();
		String s = "1+1";
		Chart c = new Chart(s, g);
		assertEquals("Row 0\n[0..0] sum: . sum sp op sp number\n[0..0] sum: . number\n[0..0] number: . \"\\d+\"\n\nRow 1\n[0..1] number: \"\\d+\" .\n[0..1] sum: number .\n[0..1] sum: sum . sp op sp number\n[1..1] sp: . \"\\s*\"\n[1..1] sp: \"\\s*\" .\n[0..1] sum: sum sp . op sp number\n[1..1] op: . '+'\n[1..1] op: . '-'\n\nRow 2\n[1..2] op: '+' .\n[0..2] sum: sum sp op . sp number\n[2..2] sp: . \"\\s*\"\n[2..2] sp: \"\\s*\" .\n[0..2] sum: sum sp op sp . number\n[2..2] number: . \"\\d+\"\n\nRow 3\n[2..3] number: \"\\d+\" .\n[0..3] sum: sum sp op sp number .\n[0..3] sum: sum . sp op sp number\n[3..3] sp: . \"\\s*\"\n[3..3] sp: \"\\s*\" .\n[0..3] sum: sum sp . op sp number\n[3..3] op: . '+'\n[3..3] op: . '-'\n\n", c.getRows());
	}
}
