package edu.hendrix.grambler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UnitTestGenerator {
	private JIGParser parser = new JIGParser();
	private File targetFile;
	private String baseName, contentPrefix;
	private Grammar g;
	private int testNum;
	
	public UnitTestGenerator(File selectedFile) throws FileNotFoundException {
		String grammarFilename = selectedFile.getName();
		baseName = grammarFilename.substring(0, grammarFilename.indexOf(".java"));
		String targetName = baseName + "Test";
		targetFile = new File(selectedFile.getParentFile(), targetName + ".java");
		String contents;
		if (targetFile.exists()) {
			contents = Util.fileToString(targetFile);
		} else {
			contents = Util.addPackageName(targetFile, "import static org.junit.Assert.*;\nimport org.junit.*;\n\npublic class " + targetName + " {\n}\n");
		}

		contentPrefix = contents.substring(0, contents.lastIndexOf('}'));
		g = parser.makeGrammarFrom(selectedFile);	
		testNum = contentPrefix.length();
	}
	
	public void addUnitCheck(String text) throws IOException {
		Tree t = g.parse2(text);
		String testMethod = "\t@Test\n\tpublic void test" + testNum + "() {\n" + 
				"\t\tedu.hendrix.grambler.Grammar g = new " + baseName + "();\n" +
				"\t\tedu.hendrix.grambler.Tree t = g.parse2(" + Util.javafy(text) + ");\n" +
				"\t\tassertEquals(" + t.isError() + ", t.isError());\n\t}\n";
		String result = contentPrefix + testMethod + "\n}\n";
		Util.stringToFile(targetFile, result);
	}
	
	public void addTreeCheck(String text) throws IOException {
		Tree t = g.parse2(text);
		String targetText = Util.javafy(t.toTextTree());
		String testMethod = "\t@Test\n\tpublic void test" + testNum + "() {\n" + 
				"\t\tedu.hendrix.grambler.Grammar g = new " + baseName + "();\n" +
				"\t\tedu.hendrix.grambler.Tree t = g.parse2(" + Util.javafy(text) + ");\n" +
				"\t\tassertEquals(" + targetText + ", t.toTextTree());\n\t}\n";
		String result = contentPrefix + testMethod + "\n}\n";
		Util.stringToFile(targetFile, result);
	}

	public static void addUnitTree(File selectedFile, String text) throws IOException {
		UnitTestGenerator utg = new UnitTestGenerator(selectedFile);
		utg.addTreeCheck(text);
	}

	public static void addUnitCheck(File selectedFile, String text) throws IOException {
		UnitTestGenerator utg = new UnitTestGenerator(selectedFile);
		utg.addUnitCheck(text);
	}
	
	
}
