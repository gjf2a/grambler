package edu.hendrix.grambler.grammars;

import java.util.Scanner;

import edu.hendrix.grambler.*;

public class SimpleCalculator {
	private Grammar parser = new SimpleMath();
	
	public int evaluate(String expr) throws ParseException {
		return walk(parser.parse(expr));
	}
	
	private int walk(Tree t) {
		if (t.isNamed("sum")) {
			if (t.hasNamed("sum")) {
				int x = walk(t.getNamedChild("sum"));
				int y = walk(t.getNamedChild("number"));
				String opStr = t.getNamedChild("op").toString();
				if (opStr.equals("+")) {return x + y;}
				else if (opStr.equals("-")) {return x - y;}
				else {throw new UnknownSymbolException(opStr);}
			} else {
				return walk(t.getNamedChild("number"));
			}
		} else if (t.isNamed("number")) {
			return Integer.parseInt(t.toString());
		} else {
			throw new UnknownSymbolException(t);
		}
	}
	
	public static void main(String[] args) {
		SimpleCalculator c = new SimpleCalculator();
		Scanner s = new Scanner(System.in);
		for (;;) {
			try {
				String line = s.nextLine();
				if (line.toLowerCase().startsWith("q")) {
					System.out.println("Goodbye");
					System.exit(0);
				}
				System.out.println(c.evaluate(line));
			} catch (ParseException pe) {
				System.out.println(pe.getMessage());
			}
		}
	}
}
