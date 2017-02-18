package edu.hendrix.grambler.tests;

import java.util.regex.*;

public class MatchEmptyString {
	public static void main(String[] args) {
		System.out.println(("".matches("\\s*")));
		System.out.println(("hello".substring(5).matches("\\s*")));
		System.out.println(("hello".substring(6).matches("\\s*")));
		
		Matcher m = Pattern.compile("\\s*").matcher("hello".substring(5));
		System.out.println(m.matches());
		System.out.println(m.lookingAt());
	}
}
