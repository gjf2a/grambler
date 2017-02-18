package edu.hendrix.grambler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.hendrix.grambler.grammars.JavaImportGrammar;

public class JIGParser {
	private Grammar g = new JavaImportGrammar();
	
	public Grammar makeGrammarFrom(File f) throws FileNotFoundException {
		return makeGrammarFrom(Util.fileToString(f));
	}
	
	public Grammar makeGrammarFrom(String javaFileText) {
		Chart c = new Chart(javaFileText, g);
		Tree t = c.getParseTree();
		if (t.isError()) {
			throw new MalformedGrammarException(t.getErrorMessage());
		}
		
		Grammar result = new Grammar();
		treeWalk(t, result);
		return result;
	}
	
	private void treeWalk(Tree t, Grammar result) {
		if (t.getName().equals("top")) {
			treeWalk(t.getNamedChild("class"), result);
		} else if (t.getName().equals("class")) {
			treeWalk(t.getNamedChild("constructor"), result);
		} else if (t.getName().equals("constructor")) {
			treeWalk(t.getNamedChild("productions"), result);
		} else if (t.getName().equals("productions")) {
			if (t.hasNamed("productions")) {
				treeWalk(t.getNamedChild("productions"), result);
			}
			addProduction(t.getNamedChild("production"), result);
		}
	}
	
	private void addProduction(Tree t, Grammar result) {
		String lhs = t.getNamedChild("string").toString();
		lhs = lhs.substring(1, lhs.length() - 1);
		ArrayList<String[]> rhs = new ArrayList<String[]>();
		addRhs(t.getNamedChild("rhs"), rhs);
		String[][] args = new String[rhs.size()][];
		for (int i = 0; i < rhs.size(); ++i) {
			args[i] = rhs.get(i);
		}
		result.addProduction(lhs, args);
	}
	
	private void addRhs(Tree t, ArrayList<String[]> rhs) {
		if (t.hasNamed("rhs")) {
			addRhs(t.getNamedChild("rhs"), rhs);
		}
		addAlternative(t.getNamedChild("alternative"), rhs);
	}
	
	private void addAlternative(Tree t, ArrayList<String[]> rhs) {
		ArrayList<String> elements = new ArrayList<String>();
		parseStringList(t.getNamedChild("stringList"), elements);
		String[] args = new String[elements.size()];
		for (int i = 0; i < elements.size(); ++i) {
			args[i] = elements.get(i);
		}
		rhs.add(args);
	}
	
	private void parseStringList(Tree t, ArrayList<String> elements) {
		if (t.hasNamed("stringList")) {
			parseStringList(t.getNamedChild("stringList"), elements);
		} 
		String element = t.getNamedChild("string").toString();
		element = element.substring(1, element.length() - 1);
		if (needsExtrication(element)) {
			element = "\"" + extricate(element) + "\"";
		}
		elements.add(element);
	}
	
	private static String extricate(String s) {
		return Util.putBackEscapes(s.substring(2, s.length() - 2)).replace("\\\"", "\"");
	}
	
	private static boolean needsExtrication(String s) {
		return s.charAt(0) == '\\' && s.charAt(1) == '"' && s.charAt(s.length() - 2) == '\\' && s.charAt(s.length() - 1) == '"';
	}
}
