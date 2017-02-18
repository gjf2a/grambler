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

import edu.hendrix.grambler.grammars.CFG;

// The main purpose of this class is to generate strings to use when building JUnit tests.
class TestOutputs {
	public static void main(String[] args) {
		Grammar g1 = GramblerTest.makeGrammar1();
		Grammar g2 = GramblerTest.makeGrammar2();
		Grammar g3 = GramblerTest.makeGrammar3();
		
		tester(1, g1, "2+3");
		tester(2, g1, "27+3");
		tester(3, g2, "27+3");
		tester(4, g2, "27+");
		tester(5, g3, "543+432+321");
		tester(6, g3, "543 + 432 + 321");
		tester(7, g2, "27-3");
		tester(8, g3, "543 + 432\n+321");
		tester(9, g3, "543 + 432\n-321");
		
		CFG builder = new CFG();
		
		cfgTest(builder, "sum: number '+' number; number: '0' | '1';\n");
		cfgTest(builder, "sum: number '+' number; number: \"\\d+\";\n");
		cfgTest(builder, "sum: sum sp op sp number; sp: \"\\s*\"; op: '+'; number: \"\\d+\";\n");
	}
	
	public static void tester(int n, Grammar g, String input) {
		System.out.println("Test " + n);
		Chart c = new Chart(input, g);
		System.out.println(c.getRows());
		Tree t = c.getParseTree();
		System.out.println(t.toTextTree());
		System.out.println();
	}
	
	public static void testGrammar(String testString) {
		Grammar result = CFG.makeFrom(testString);
		System.out.println(result);
		System.out.println(Util.javafy(result.toString()));
	}
	
	public static void testParseTree(CFG cfg, String testString) {
		Chart c = new Chart(testString, cfg);
		Tree t = c.getParseTree();
		System.out.println(t.toTextTree());
		System.out.println(Util.javafy(t.toTextTree()));
	}
	
	public static void cfgTest(CFG cfg, String testString) {
		testParseTree(cfg, testString);
		testGrammar(testString);
	}
}
