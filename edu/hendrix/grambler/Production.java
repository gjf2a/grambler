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

import java.util.ArrayList;
import java.util.regex.*;

class Production {
	private String left;
	private ArrayList<String> symbols;
	private ArrayList<Pattern> regexes;
	private ArrayList<Boolean> isTerminal;
	
	public Production() {
		left = "";
		symbols = new ArrayList<String>();
		regexes = new ArrayList<Pattern>();
		isTerminal = new ArrayList<Boolean>();
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
	
	public String toString() {
		return stringWithDot(-1);
	}
	
	public String stringWithDot(int dotSpot) {
		return left + ":" + rightHandSideWithDot(dotSpot);
	}
	
	public String rightHandSide() {
		return rightHandSideWithDot(-1);
	}
	
	public String rightHandSideWithDot(int dotSpot) {
		String result = "";
		for (int i = 0; i < symbols.size(); ++i) {
			if (i == dotSpot) {
				result += " .";
			}
			result += " " + symbolToString(i);
		}
		if (dotSpot == symbols.size()) {
			result += " .";
		}
		return result;
	}
	
	public String symbolToString(int i) {
		if (isRegex(i)) {
			return Util.displayRegex(symbols.get(i));
		} else if (isTerminal(i)) {
			return Util.displayTerminal(symbols.get(i));
		} else {
			return symbols.get(i);
		}
	}
	
	public String symbolToJava(int i) {
		if (isRegex(i)) {
			return Util.javafy(Util.displayRegex(symbols.get(i)));
		} else if (isTerminal(i)) {
			return Util.javafy("'" + symbols.get(i) + "'");
		} else {
			return Util.javafy(symbols.get(i));
		}
	}
	
	public void setLeftSide(String s) {
		left = s;
	}
	
	public void addTerminal(String s) {
		addSymbol(s);
		isTerminal.add(true);
	}
	
	public void addRegex(String s) {
		addTerminal(s);
		regexes.set(regexes.size() - 1, Pattern.compile(s));
	}
	
	public void addNonterminal(String s) {
		addSymbol(s);
		isTerminal.add(false);
	}
	
	private void addSymbol(String s) {
		symbols.add(s);
		regexes.add(null);
	}
	
	public String getLeft() {
		return left;
	}
	
	public String getSymbol(int n) {
		return symbols.get(n);
	}
	
	public boolean isTerminal(int n) {
		return isTerminal.get(n);
	}
	
	public boolean hasNonterminals() {
		for (Boolean b: isTerminal) {
			if (!b) {return true;}
		}
		return false;
	}
	
	public boolean isRegex(int n) {
		return regexes.get(n) != null;
	}

	public TerminalMatch getCharsMatched(int n, String input, int position) {
		assert isTerminal(n);
		if (isRegex(n)) {
			Matcher m = regexes.get(n).matcher(input.substring(position));
			if (m.lookingAt()) {
				return new TerminalMatch(m.group());
			} else {
				return new TerminalMatch();
			}
		} else {
			int end = position + symbols.get(n).length();
			if (end <= input.length()) {
				String target = input.substring(position, end);
				if (symbols.get(n).equals(target)) {	
					return new TerminalMatch(target);
				}
			} 
				
			return new TerminalMatch();
		}
	}
	
	public int size() {
		return symbols.size();
	}
}
