package edu.hendrix.grambler.grammars;

import java.util.Scanner;

import edu.hendrix.grambler.*;

public class Calculator {
	private Arithmetic parser = new Arithmetic();
	
	public int evaluate(String expr) {
		Tree t = parser.parse2(expr);
		if (t.isError()) {
			throw new IllegalArgumentException(t.getErrorMessage());
		} else {
			return walk(t);
		}
	}
	
	private int walk(Tree t) {
		if (t.isNamed("expr")) {
			return walk(t.getNamedChild("sum"));
		} else if (t.isNamed("sum")) {
			if (t.hasNamed("sum")) {
				return compute(walk(t.getNamedChild("sum")), walk(t.getNamedChild("product")), t.getNamedChild("addOp"));
			} else {
				return walk(t.getNamedChild("product"));
			}
		} else if (t.isNamed("product")) {
			if (t.hasNamed("product")) {
				return compute(walk(t.getNamedChild("product")), walk(t.getNamedChild("number")), t.getNamedChild("mulOp"));
			} else {
				return walk(t.getNamedChild("number"));
			}
		} else if (t.isNamed("number")) {
			return Integer.parseInt(t.toString());
		} else {
			throw new UnknownSymbolException(t);
		}
	}
	
	private int compute(int x, int y, Tree op) {
		String opStr = op.toString();
		if (opStr.equals("+")) {return x + y;}
		else if (opStr.equals("-")) {return x - y;}
		else if (opStr.equals("*")) {return x * y;}
		else if (opStr.equals("/")) {return x / y;}
		else {throw new IllegalArgumentException("'" + opStr + "' is not a legal operator");}
	}
	
	public static void main(String[] args) {
		Calculator c = new Calculator();
		Scanner s = new Scanner(System.in);
		for (;;) {
			String line = s.nextLine();
			if (line.toLowerCase().startsWith("q")) {
				System.out.println("Goodbye");
				System.exit(0);
			}
			System.out.println(c.evaluate(line));
		}
	}
}
