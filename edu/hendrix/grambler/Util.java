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

import java.io.*;
import java.util.*;
import javax.tools.*;

public class Util {
	/**
	 * Returns a String that can be used in Java source code.
	 * 
	 * @param s 
	 * @return s formatted for copy-and-paste into Java source code.
	 */
	public static String javafy(String s) {
		return "\"" + cleanUpEscapes(s).replace("\"", "\\\"") + "\"";
		//return "\"" + s.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t").replace("\"", "\\\"") + "\"";
	}
	
	/**
	 * Checks if a String has been java-fied.
	 * 
	 * @param s
	 * @return true if s has been java-fied, false otherwise
	 */
	
	public static boolean isJavafied(String s) {
		return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"';
	}
	
	/** 
	 * The inverse of javafy.
	 * 
	 * @param s isJavafied(s) == true
	 * @return a String without Java formatting
	 */
	public static String dejavafy(String s) {
		assert isJavafied(s);
		return putBackEscapes(s.substring(1, s.length() - 1)).replace("\\\"", "\"");
		//return s.substring(2, s.length() - 2).replace("\\\"", "\"").replace("\\t", "\t").replace("\\n", "\n").replace("\\\\", "\\");
	}
	
	/**
	 * Reads in a File and dumps it into a String.
	 * 
	 * @param f
	 * @return a String containing all text from f.
	 * @throws FileNotFoundException
	 */
	public static String fileToString(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		StringBuilder sb = new StringBuilder();
		while (s.hasNextLine()) {
			sb.append(s.nextLine() + '\n');
		}
		s.close();
		return sb.toString();
	}
	
	/**
	 * Takes a String and dumps it into a File.
	 * @throws IOException 
	 * 
	 * 
	 */
	public static void stringToFile(File f, String s) throws IOException {
		PrintWriter p = new PrintWriter(new FileWriter(f));
		p.println(s);
		p.close();
	}
	
	/**
	 * Replaces tabs with spaces.
	 * 
	 * @param s
	 * @param tabSpaces
	 * @return s, with every tab replaced by tabSpaces spaces.
	 */
	public static String rehabTab(String s, int tabSpaces) {
		String spacer = "";
		for (int i = 0; i < tabSpaces; ++i) {
			spacer += " ";
		}
		return s.replace("\t", spacer);
	}
	
	/**
	 * Compiles the java program specified by filename.
	 * 
	 * @param filename
	 * @return true if it compiles, false otherwise
	 */
	public static boolean compile(String filename) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult =	compiler.run(null, null, null, filename);
		return compilationResult == 0;
	}

	/**
	 * Exports a Grammar to a String encoding a Java program.
	 * 
	 * @param className
	 * @param g
	 * @return encoded Java program
	 */
	public static String toJavaProgram(String className, Grammar g) {
		StringBuilder sb = new StringBuilder();
		sb.append("public class " + className + " extends edu.hendrix.grambler.Grammar {\n");
		sb.append("    public " + className + "() {\n");
		sb.append("        super();\n");
		for (String name: g.getDefinedNonterminals()) {
			sb.append("        addProduction(\"" + name + "\"");
			for (Production p: g.allProductionsFor(name)) {
				sb.append(", new String[]{");
				for (int i = 0; i < p.size(); ++i) {
					sb.append(p.symbolToJava(i));
					if (i < p.size() - 1) {
						sb.append(", ");
					}
				}
				sb.append("}");
			}
			sb.append(");\n");
		}
		sb.append("    }\n");
		sb.append("}\n");
		return sb.toString();
	}
	
	/**
	 * Saves a Grammar as a Java file.
	 * 
	 * @param target destination file
	 * @param g Grammar to be saved
	 * @throws IOException
	 */
	public static void toJavaFile(File target, Grammar g) throws IOException {
		String className = target.getName();
		if (className.endsWith(".java")) {
			className = className.substring(0, className.length() - 5);
		} else {
			target = new File(target.getAbsolutePath() + ".java");
		}
		String programText = addPackageName(target, toJavaProgram(className, g));
		stringToFile(target, programText);
	}
	
	/**
	 * Searches the file hierarchy for the package name corresponding to the Java file, if it can be determined.
	 * 
	 * @param javaFile destination of a Grammar export
	 * @param program text of a Grammar export
	 * @return Program text modified to include package specifier, if it can be determined; if not, just returns program text
	 */
	public static String addPackageName(File javaFile, String program) {
		LinkedList<String> packageParents = new LinkedList<String>();
		for (File iter = javaFile.getParentFile(); iter != null; iter = iter.getParentFile()) {
			if (iter.getName().equals("src")) {
				StringBuilder header = new StringBuilder();
				header.append("package ");
				for (String element: packageParents) {
					header.append(element + ".");
				}
				header.setCharAt(header.length() - 1, ';');
				header.append("\n\n");
				program = header + program;
				break;
			} else {
				packageParents.addFirst(iter.getName());
			}
		}
		
		return program;
	}

	static String cleanUpRegex(String rawRegex) {
		return rawRegex.substring(1, rawRegex.length() - 1);
	}

	static boolean isTerminalTerm(String term) {
		return term.charAt(0) == '\'' && term.charAt(term.length() - 1) == '\'';
	}

	static boolean isRegexTerm(String term) {
		return term.charAt(0) == '"' && term.charAt(term.length() - 1) == '"';
	}

	static String displayRegex(String regex) {
		return "\"" + regex + "\"";
	}

	private static String[] escapeSrc = {"\\\\", "\\n", "\\t", "\\r", "\\f", "\\b"};
	private static String[] escapeOut = {"\\", "\n", "\t", "\r", "\f", "\b"};
	
	static String cleanUpEscapes(String escaped) {
		for (int i = 0; i < escapeSrc.length; ++i) {
			escaped = escaped.replace(escapeOut[i], escapeSrc[i]);
		}
		return escaped;
	}
	
	static String putBackEscapes(String unescaped) {
		for (int i = 0; i < escapeSrc.length; ++i) {
			unescaped = unescaped.replace(escapeSrc[i], escapeOut[i]);
		}
		return unescaped;
	}
	
	static String cleanUpTerminal(String rawTerminal) {
		return putBackEscapes(rawTerminal.substring(1, rawTerminal.length() - 1)).replace("\\'", "'");
	}

	static String displayTerminal(String terminal) {
		return "'" + cleanUpEscapes(terminal).replace("'", "\\'") + "'";
	}
}
