package edu.hendrix.grambler;

import edu.hendrix.grambler.grammars.CFG;

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

class CFGParser {
	Grammar g = new CFG();
	
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
	public Grammar makeGrammarFrom(String grammarInput) {
		Chart c = new Chart(grammarInput, g);
		Tree t = c.getParseTree();
		if (t.isError()) {
			throw new MalformedGrammarException(t.getErrorMessage());
		}
		Grammar result = new Grammar();
		buildGrammar(result, t);
		return result;
	}
	
	private void buildGrammar(Grammar target, Tree t) {
		if (t.getName().equals("g")) {
			if (t.hasNamed("g")) {
				buildGrammar(target, t.getNamedChild("g"));
			}
			buildGrammar(target, t.getNamedChild("line"));
			
		} else if (t.getName().equals("line")) {
			buildRightHandSides(target, t.getNamedChild("rhs"), t.getNamedChild("nonterm").toString());
			
		} else {
			throw new IllegalStateException("Illegally processing node with name: " + t.getName());
		}
	}
	
	private void buildRightHandSides(Grammar target, Tree t, String name) {
		if (t.getName().equals("rhs")) {
			if (t.hasNamed("rhs")){
				buildRightHandSides(target, t.getNamedChild("rhs"), name);
			}
			Production p = new Production();
			p.setLeftSide(name);
			buildSingleProduction(target, t.getNamedChild("elements"), p);
			target.addProduction(p);
			
		} else {
			throw new IllegalStateException("Illegally processing node with name: " + t.getName());
		}
	}
	
	private void buildSingleProduction(Grammar target, Tree t, Production p) {
		if (t.getName().equals("elements")) {
			if (t.hasNamed("elements")) {
				buildSingleProduction(target, t.getNamedChild("elements"), p);
			}
			addProductionElement(target, t.getNamedChild("element"), p);
		} else {
			throw new IllegalStateException("Illegally processing node with name: " + t.getName());
		}
	}
	
	private void addProductionElement(Grammar target, Tree t, Production p) {
		if (t.getName().equals("element")) {
			addProductionElement(target, t.getChild(0), p);
		
		} else if (t.getName().equals("nonterm")) {
			p.addNonterminal(t.toString());
			
		} else if (t.getName().equals("term")) {
			//System.out.println("Adding terminal (CFGParser): [" + t.toString() + "] cleaned: [" + Grammar.cleanUpTerminal(t.toString()) + "]");
			p.addTerminal(Util.cleanUpTerminal(t.toString()));
			
		} else if (t.getName().equals("regex")) {
			p.addRegex(Util.cleanUpRegex(t.toString()));
		
		} else {
			throw new IllegalStateException("Illegally processing node with name: " + t.getName());
		}
	}
}
