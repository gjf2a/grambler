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

public class Chart {
	private ArrayList<ArrayList<State>> states;
	private ArrayList<Set<State>> visited;
	private Input input;
	private Grammar g;
	
	/**
	 * Parses an input, creating an Earley Chart in the process.
	 * 
	 * @param input String to be parsed
	 * @param g Grammar object to guide the parsing
	 * @throws MalformedGrammarException if g is malformed
	 */
	public Chart(String input, Grammar g) {
		g.assertAllNonterminalsDefined();
		
		this.input = new Input(input);
		this.g = g;
		
		visited = new ArrayList<Set<State>>();
		states = new ArrayList<ArrayList<State>>();
		for (int i = 0; i <= input.length(); ++i) {
			states.add(new ArrayList<State>());
			visited.add(new HashSet<State>());
		}
		
		for (Production top: g.allProductionsFor(g.getStartSymbol())) {
			addState(new State(top), 0);
		}
		for (int i = 0; i < states.size(); ++i) {
			for (int j = 0; j < states.get(i).size(); ++j) {
				update(states.get(i).get(j), i);
			}
		}
	}
	
	private void update(State s, int position) {
		if (s.isPredicting()) {
			predict(s, position);
		} else if (s.isScanning()) {
			scan(s, position);
		} else {
			complete(s, position);
		}
	}
	
	private void predict(State s, int position) {
		for (Production next: getGrammar().allProductionsFor(s.getCurrentSymbol())) {
			State successor = new State(next, position, 0, position, null);
			addState(successor, position);
		}
	}
	
	private void scan(State s, int position) {
		TerminalMatch charsMatched = s.getCharsMatched(this.getInputString(), position);
		if (charsMatched.isMatched()) {
			int newRow = position + charsMatched.getNumCharsMatched();
			if (newRow < size()) {
				addState(s.advancedProduction(newRow), newRow);
			}
		}
	}
	
	private void complete(State s1, int position) {
		// Extended "for" loop avoided in order to avoid ConcurrentModificationException
		ArrayList<State> states = this.getStatesAt(s1.getOrigin());
		for (int i = 0; i < states.size(); ++i) {
			State s = states.get(i);
			if (s.isPredicting() && s.getCurrentSymbol().equals(s1.getLeft())) {
				this.addState(s.advancedProduction(position), position);
			}
		}
	}

	/**
	 * Parses an input, creating an Earley Chart in the process.
	 * 
	 * @param input String to be parsed
	 * @param grammarText Text of a grammar
	 * @throws MalformedGrammarException if grammarText is not a valid grammar
	 */
	public Chart(String input, String grammarText) {
		this(input, Grammar.makeFrom(grammarText));
	}
	
	/**
	 * Parses an input, creating an Earley Chart in the process.
	 * 
	 * @param input String to be parsed
	 * @param grammarFile file containing a grammar
	 * @throws MalformedGrammarException if grammarFile does not contain a valid grammar
	 */
	public Chart(String input, File grammarFile) throws FileNotFoundException {
		this(input, Grammar.makeFrom(grammarFile));
	}
	
	/**
	 * Returns a parse tree extracted from this Chart.
	 * 
	 * @return parse tree corresponding to the parse conducted by the constructor
	 */
	public Tree getParseTree() {
		State finalTop = getFinalTopState();
		if (finalTop.isFinal() && finalTop.getRow() == this.size() - 1) {
			return finalTop.makeTree(this);
		} else {
			return new Error(finalTop.getLeft(), input, getLastOccupiedRow()); 
		}
	}
	
	/**
	 * Returns the last row corresponding to matched text.
	 * 
	 * @return Number of last row containing matched text.
	 */
	public int getLastOccupiedRow() {
		int last = size() - 1;
		while (last > 0 && states.get(last).isEmpty()) {
			last--;
		}
		return last;
	}
	
	/**
	 * Returns Grammar object used for parsing this Chart.
	 * 
	 * @return Grammar object used for parsing this Chart.
	 */
	public Grammar getGrammar() {return g;}
	
	/**
	 * Returns parsed input string.
	 * 
	 * @return Input string parsed for building this Chart.
	 */
	public String getInputString() {return input.toString();}
	
	Input getInput() {return input;}
	
	void addState(State s, int position) {
		if (!visited.get(position).contains(s)) {
			states.get(position).add(s);	
			visited.get(position).add(s);
		}
	}
	
	ArrayList<State> getStatesAt(int position) {
		return states.get(position);
	}
	
	ArrayList<State> getCompletedStatesAt(int position) {
		ArrayList<State> result = new ArrayList<State>();
		for (State s: getStatesAt(position)) {
			if (s.isComplete()) {result.add(s);}
		}
		return result;
	}
	
	/**
	 * Returns the number of rows in this Chart.
	 * 
	 * @return number of rows in this Chart.
	 */
	public int size() {
		return states.size();
	}
	
	State getFinalTopState() {
		String topTerm = g.getStartSymbol();
		for (State candidate: getCompletedStatesAt(size() - 1)) {
			if (candidate.isFinal() && candidate.isLeftSymbol(topTerm)) {
				return candidate;
			}
		}
		
		for (int row = size() - 1; row >= 0; row--) {
			for (State candidate: getStatesAt(row)) {
				if (candidate.isLeftSymbol(topTerm)) {
					return candidate;
				}
			}
		}
		throw new IllegalStateException("The top symbol does not appear in ANY row!!!!");
	}	

	State getBestCompletionOf(State s) {
		int predRow = s.hasPredecessor() ? s.getPredecessor().getRow() : 0;
		String nonterm = s.getLastSymbolCompleted();
		for (State candidate: getCompletedStatesAt(s.getRow())) {
			if (candidate.isComplete() && candidate.isLeftSymbol(nonterm) && candidate.getOrigin() == predRow) {
				return candidate;
			}
		}
		throw new IllegalArgumentException("nonterminal " + nonterm + " has no match in row " + s.getRow());
	}
	
	/** 
	 * Test to see if parse was successful.
	 * 
	 * @return true if parse succeeded, false otherwise
	 */
	public boolean reachesAcceptingState() {
		return !getParseTree().isError();
	}
	
	/**
	 * Test to see if row contains any production matches.
	 * 
	 * @param row
	 * @return true if any productions had a match at row; false otherwise
	 */
	public boolean isEmptyRow(int row) {
		return getStatesAt(row).size() == 0;
	}
	
	/**
	 * Returns a String describing the contents of a row.
	 * 
	 * @param row chart row, 0 <= row < size()
	 * @return a String describing the Chart contents at row
	 */
	public String getRow(int row) {
		String result = "";
		result += "Row " + row + "\n";
		for (State s: getStatesAt(row)) {
			result += s + "\n";
		}
		result += "\n";
		return result;
	}
	
	/**
	 * Returns a single String describing all rows.
	 * 
	 * @return Contents of all rows.
	 */
	public String getRows() {
		String result = "";
		for (int i = 0; i < size(); ++i) {
			result += getRow(i);
		}
		return result;
	}
}
