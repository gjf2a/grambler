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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Grammar {
	// TODO: I could probably drop "productions" and put in a String representing the start symbol, but I'm not sure it's worth the effort.
	private ArrayList<Production> productions;
	private LinkedHashMap<String,ArrayList<Production>> leftToProductions;
	
	/**
	 * Parses a grammar string, and returns a corresponding Grammar object.
	 * 
	 * The grammar for grammar strings is:
	 * <pre>
	 * g: g sp line | line;
	 * line: nonterm sp ':' sp rhs sp ';' spacing;
	 * rhs: rhs sp '|' sp elements | elements;
	 * sp: "\s*";
	 * spacing: "\s+";
	 * elements: elements spacing element | element;
	 * element: term | nonterm | regex;
	 * term: "'.*?[^\\]'";
	 * nonterm: "\w+";
	 * regex: "\".*?[^\\]\"";
	 * </pre>
	 * 
	 * @param grammarInput a String, accepted by the above grammar.
	 * @return a Grammar object corresponding to grammarInput
	 * @throws MalformedGrammarException If grammarInput itself has a parse error.
	 */
	public static Grammar makeFrom(String grammarInput) {
		CFGParser cfg = new CFGParser();
		return cfg.makeGrammarFrom(grammarInput);
	}
	
	public static Grammar makeFromClassFile(String classname, ClassLoader loader) {
		try {
			@SuppressWarnings("unchecked")
			Class<Grammar> g = (Class<Grammar>) Class.forName(classname, true, loader);
			return g.newInstance();
		} catch (ClassCastException e) {
			throw new MalformedGrammarException(classname + " was not of the Grammar class");
		} catch (ClassNotFoundException e) {
			throw new MalformedGrammarException(classname + " could not be located");
		} catch (InstantiationException e) {
			throw new MalformedGrammarException("An object of class " + classname + " could not be instantiated");
		} catch (IllegalAccessException e) {
			throw new MalformedGrammarException("An object of class " + classname + " has a non-public constructor");
		}
	}
	
	/**
	 * Creates a Grammar object from a File. 
	 * 
	 * File contents should be formatted as in @link {@link #makeFrom(String)}.
	 * 
	 * @param grammarFile a File object, containing acceptable text
	 * @return a Grammar object corresponding to grammarFile
	 * @throws MalformedGrammarException
	 * @throws FileNotFoundException
	 */
	public static Grammar makeFrom(File grammarFile) throws FileNotFoundException {
		return makeFrom(Util.fileToString(grammarFile));
	}
	
	/** 
	 * Creates a Grammar object with no productions.
	 * 
	 */
	public Grammar() {
		productions = new ArrayList<Production>();
		leftToProductions = new LinkedHashMap<String,ArrayList<Production>>();
	}
	
	/**
	 * Parses a String into a Tree.
	 * 
	 * @param input a String to be parsed.
	 * @return a parse tree corresponding to input, parsed by this Grammar.
	 * @throws MalformedGrammarException
	 * @throws ParseException, if the resulting tree is a parse error.
	 */
	public Tree parse(String input) throws ParseException {
		Tree result = parse2(input);
		if (result.isError()) {
			throw new ParseException(result);
		}
		return result;
	}
	
	/**
	 * Parses a String into a Tree.
	 * 
	 * @param input a String to be parsed.
	 * @return a parse tree corresponding to input, parsed by this Grammar.
	 * @throws MalformedGrammarException
	 */
	public Tree parse2(String input) {
		Chart c = new Chart(input, this);
		return c.getParseTree();
	}
	
	/**
	 * Does nothing, unless there are undefined terminals.
	 * 
	 * @throws MalformedGrammarException if there are undefined terminals.
	 */
	public void assertAllNonterminalsDefined() {
		if (!allNonterminalsDefined()) {
			String bad = "Undefined nonterminals in grammar:";
			for (String undef: getUndefinedNonterminals()) {
				bad += " " + undef;
			}
			throw new MalformedGrammarException(bad);
		}
	}
	
	/**
	 * Checks to see if all nonterminals are defined in this Grammar.
	 * 
	 * @return true if all nonterminals have definitions, false otherwise.
	 */
	public boolean allNonterminalsDefined() {
		return getUndefinedNonterminals().isEmpty();
	}
	
	Set<String> getDefinedNonterminals() {
		Set<String> result = new LinkedHashSet<String>();
		for (Production p: productions) {
			result.add(p.getLeft());
		}
		return result;
	}
	
	/**
	 * Returns all undefined nonterminals.
	 * 
	 * In the event that this Grammar has undefined nonterminals, this method will
	 * return all of them for inspection.
	 * 
	 * @return all undefined nonterminal symbols.
	 */
	public Set<String> getUndefinedNonterminals() {
		Set<String> defined = getDefinedNonterminals();
		Set<String> result = new HashSet<String>();
		for (Production p: productions) {
			for (int i = 0; i < p.size(); ++i) {
				if (!p.isTerminal(i) && !defined.contains(p.getSymbol(i))) {
					result.add(p.getSymbol(i));
				}
			}
		}
		return result;
	}
	
	/**
	 * Adds a production to this grammar.
	 * 
	 * @param lhs The left-hand side nonterminal symbol
	 * @param rhs Each String array in this list is a list of symbols for a particular right-hand side alternative.
	 */
	public void addProduction(String lhs, String[]... rhs) {
		for (int option = 0; option < rhs.length; ++option) {
			Production p = new Production();
			p.setLeftSide(lhs);
			for (String term: rhs[option]) {
				if (Util.isTerminalTerm(term)) {
					p.addTerminal(Util.cleanUpTerminal(term));
				} else if (Util.isRegexTerm(term)) {
					p.addRegex(Util.cleanUpRegex(term));
				} else {
					p.addNonterminal(term);
				}
			}
			addProduction(p);
		}
	}
	
	void addProduction(Production p) {
		productions.add(p);
		if (!leftToProductions.containsKey(p.getLeft())) {
			leftToProductions.put(p.getLeft(), new ArrayList<Production>());
		}
		leftToProductions.get(p.getLeft()).add(p);
	}
	
	Iterable<Production> allProductionsFor(String left) {
		return leftToProductions.get(left);
	}
	
	/**
	 * Returns the name of the start symbol.
	 * 
	 * The start symbol is the nonterminal that will be employed to begin the parsing process.
	 * 
	 * @return name of this Grammar's start symbol.
	 */
	public String getStartSymbol() {
		return productions.get(0).getLeft();
	}
	
	/**
	 * Returns a String version of this Grammar.
	 * 
	 * When a Grammar is converted into a String, that String can be parsed right back into the source Grammar.
	 * 
	 * @return String version of this Grammar.
	 */
	@Override
	public String toString() {
		String result = "";
		for (String left: leftToProductions.keySet()) {
			result += left + ":";
			boolean bar = false;
			for (Production p: leftToProductions.get(left)) {
				if (bar) {result += " |";} else {bar = true;}
				result += p.rightHandSide();
			}
			result += ";\n";
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Grammar) {
			return toString().equals(obj.toString());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
