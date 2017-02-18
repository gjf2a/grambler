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
import java.util.Collections;

class State {
	private Production p;
	private int originPosition, productionPosition, currentPosition, hash;
	private State predecessor;
	private String stringRep;
	
	public State(Production p, int originPosition, int productionPosition, int currentPosition, State predecessor) {
		this.p = p;
		this.originPosition = originPosition;
		this.currentPosition = currentPosition;
		this.productionPosition = productionPosition;
		this.predecessor = predecessor;
		stringRep = "[" + originPosition + ".." + currentPosition + "] " + p.stringWithDot(productionPosition);
		hash = stringRep.hashCode();
	}
	
	public State(Production p) {
		this(p, 0, 0, 0, null);
	}
	
	public String getLeft() {
		return p.getLeft();
	}
	
	public boolean isPredicting() {
		return !isComplete() && !p.isTerminal(productionPosition);
	}
	
	public boolean isScanning() {
		return !isComplete() && p.isTerminal(productionPosition);
	}
	
	public boolean isComplete() {
		return productionPosition == p.size();
	}
	
	public boolean isFinal() {
		return isComplete() && getOrigin() == 0;
	}
	
	public boolean atStart() {
		return productionPosition == 0;
	}
	
	public boolean isLeftSymbol(String candidate) {
		return getLeft().equals(candidate);
	}
	
	public State advancedProduction(int row) {
		return new State(p, originPosition, productionPosition + 1, row, this);
	}
	
	public TerminalMatch getCharsMatched(String input, int position) {
		return p.getCharsMatched(productionPosition, input, position);
	}
	
	public boolean hasPredecessor() {
		return predecessor != null;
	}
	
	public State getPredecessor() {
		assert hasPredecessor();
		return predecessor;
	}
	
	public Tree makeTree(Chart c) {
		assert isComplete();
		int length = getRow() - originPosition;
		if (p.hasNonterminals()) {
			return makeInteriorNode(c, length);
		} else {
			return makeLeafNode(c, length);
		}
	}
	
	private Tree makeInteriorNode(Chart c, int length) {
		ArrayList<Tree> children = new ArrayList<Tree>();
		State s = this;
		while (!s.atStart()) {
			int lastSymbol = s.productionPosition - 1;
			if (p.isTerminal(lastSymbol)) {
				int lastRow = s.getPredecessor().getRow();
				children.add(new Leaf(s.getLastSymbolCompleted(), c.getInput(), lastRow, s.getRow() - lastRow));
			} else {
				State substate = c.getBestCompletionOf(s);
				children.add(substate.makeTree(c));
			}
 			s = s.getPredecessor();
 		}
		Collections.reverse(children);
		return new Interior(p.getLeft(), c.getInput(), originPosition, length, children);
	}
	
	private Tree makeLeafNode(Chart c, int length) {
		return new Leaf(p.getLeft(), c.getInput(), originPosition, length);
	}
	
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
	
	public String toString() {
		return stringRep;
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String getLastSymbolCompleted() {
		assert !atStart();
		return p.getSymbol(productionPosition - 1);
	}
	
	public int getRow() {
		return currentPosition;
	}
	
	public int getOrigin() {
		return originPosition;
	}
	
	public String getCurrentSymbol() {
		return p.getSymbol(productionPosition);
	}
}
