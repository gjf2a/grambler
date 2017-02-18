package edu.hendrix.grambler.tests;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.hendrix.grambler.*;
import edu.hendrix.grambler.grammars.CFG;

public class QuoteRegex {
	public static void main(String[] args) {
		Pattern p = Pattern.compile("\'([^\']|\\')+\'");
		check(p, "'\''");
		
		Pattern p2 = Pattern.compile("\"([^\\\"]|\\\")+\"");
		check(p2, "\"\\w+\"");
		check(p2, "\"\\s+\"");

		Pattern p3 = Pattern.compile("\".*?[^\\\\]\"");
		check(p3, "\"hello\"");
		check(p3, "\".*?[^\\\\]\"");
		check2(p3, "\".*?[^\\\\]\";\n");
		check2(p3, "\"\\s*\";\nspacing: \"\\s+\";\n");
		
		Pattern p4 = Pattern.compile("'.*?[^\\\\]'");
		check(p4, "'\''");
		check2(p4, "'+' ten");
		check2(p4, "'+' number; number: '0' | '1';");
		
		CFG g = new CFG();
		System.out.println(Util.javafy(g.toString()));
		
		Tree t = g.parse2("s: n '+' n;\nn: \"\\d+\";\n");
		System.out.println(t.toTextTree());
		System.out.println(Util.javafy(t.toTextTree()));
	}
	
	public static void check(Pattern p, String input) {
		Matcher m = p.matcher(input);
		if (m.matches()) {
			System.out.println("Match");
			System.out.println(m.group());
		} else {
			System.out.println("No match on " + input);
		}
	}
	
	public static void check2(Pattern p, String input) {
		Matcher matcher = p.matcher(input);
		if (matcher.lookingAt()) {
			System.out.println("Match");
			System.out.println(matcher.group());
		} else {
			System.out.println("No match on " + input);
		}
	}
}
