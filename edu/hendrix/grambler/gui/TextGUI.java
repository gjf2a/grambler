package edu.hendrix.grambler.gui;

//Copyright 2012-2014 by Gabriel J. Ferrer
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import edu.hendrix.grambler.*;
import edu.hendrix.grambler.grammars.*;

@SuppressWarnings("serial")
public class TextGUI extends JFrame {
	
	private JMenuItem openGrammar, openInput, importGrammar, 
		saveGrammar, saveInput, exportGrammar, 
		exportUnitTree, exportUnitCheck, overview, sample1, sample2, exit;
	
	private JButton parse;
	private JTextField messages;
	
	private OpenSaveTextPane grammar;
	private InputTextArea input;
	private JTextArea tree;
	private ChartPanel chart;
	
	private File lastFileAccessed;
	
	private Quitter q;
	
	private JFileChooser grammarChooser, javaChooser, inputChooser;
	
	private final static String PROGRAM_TITLE = "Grambler: Grammar Editor and Parse Tree Viewer";
	private final static double VERSION = 0.26;
	private final static Font COURIER = new Font("Courier", Font.PLAIN, 12);
	
	public TextGUI() {
		setupFrame();
		setupWindows();
		setupMenus();
		
		lastFileAccessed = null;
	}
	
	public String getInputString() {
		return input.getText();
	}
	
	private void setupMenus() {
		grammarChooser = new JFileChooser();
		grammarChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		javaChooser = new JFileChooser();
		javaChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		javaChooser.setFileFilter(new MetaFilter(".java", "Java source files"));
		
		inputChooser = new JFileChooser();
		inputChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		openGrammar = new JMenuItem("Open Grammar File");
		openGrammar.addActionListener(new Opener(grammarChooser, grammar));
		openInput = new JMenuItem("Open Input File");
		openInput.addActionListener(new Opener(inputChooser, input));
		
		importGrammar = new JMenuItem("Import Grammar File from Java...");
		importGrammar.addActionListener(new JavaImporter());
		
		saveGrammar = new JMenuItem("Save Grammar File As...");
		saveGrammar.addActionListener(new Saver(grammarChooser, grammar));
		saveInput = new JMenuItem("Save Input File As...");
		saveInput.addActionListener(new Saver(inputChooser, input));
		
		exportGrammar = new JMenuItem("Export Grammar File to Java...");
		exportGrammar.addActionListener(new JavaExporter());
		
		exit = new JMenuItem("Quit");
		exit.addActionListener(q);
		
		file.add(openGrammar);
		file.add(openInput);
		file.addSeparator();
		file.add(importGrammar);
		file.addSeparator();
		file.add(saveGrammar);
		file.add(saveInput);
		file.addSeparator();
		file.add(exportGrammar);
		file.addSeparator();
		file.add(exit);
		
		JMenu help = new JMenu("Help");
		// TODO: Create some helpful text for the overview JMenuItem to display

		JMenu examples = new JMenu("Example Grammars");
		sample1 = new JMenuItem("Grammar for Grammars");
		sample1.addActionListener(new Sampler());
		sample2 = new JMenuItem("Grammar for basic arithmetic");
		sample2.addActionListener(new Sampler());
		examples.add(sample2);
		examples.add(sample1);
		
		JMenuItem version = new JMenuItem("About");
		version.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {messages.setText("Version " + VERSION + " (Maintained by Gabriel Ferrer: ferrer@hendrix.edu)");}});
		JMenuItem docs = new JMenuItem("JavaDoc Link");
		docs.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {messages.setText("http://ozark.hendrix.edu/~ferrer/software/grambler/doc/");}});		
		
		help.add(version);
		help.add(examples);
		help.add(docs);
		
		JMenu unit = new JMenu("Unit Tests");
		exportUnitCheck = new JMenuItem("Export JUnit acceptance check");
		exportUnitCheck.addActionListener(new UnitTestExporter());
		exportUnitTree = new JMenuItem("Export JUnit tree check");
		exportUnitTree.addActionListener(new UnitTestExporter());
		
		unit.add(exportUnitCheck);
		unit.add(exportUnitTree);
		
		bar.add(file);
		bar.add(unit);
		bar.add(help);
		this.setJMenuBar(bar);
	}
	
	private void setupFrame() {
		setSize(900, 600);
		setTitle(PROGRAM_TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		q = new Quitter();
		addWindowListener(q);
	}
	
	private void setupWindows() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(setupControlPanel(), BorderLayout.NORTH);
		getContentPane().add(setupWindowPanel(), BorderLayout.CENTER);
	}
	
	private JPanel setupControlPanel() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		
		parse = new JButton("Parse");
		parse.addActionListener(new ParseShower());
		controlPanel.add(parse, BorderLayout.WEST);
		
		messages = new JTextField();
		messages.setFont(COURIER);
		messages.setEditable(false);
		controlPanel.add(messages, BorderLayout.CENTER);
		
		return controlPanel;
	}
	
	private JPanel setupWindowPanel() {
		JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new GridLayout(2, 2));
		
		grammar = new OpenSaveTextPane();
		addTextArea(grammar, windowPanel, "Grammar");
		tree = addDisplay(windowPanel, "Parse Tree");
		input = new InputTextArea();
		input.addCaretListener(new InputListener());
		addTextArea(input, windowPanel, "Text Input");
		chart = new ChartPanel();
		windowPanel.add(chart);
		chart.addChartPanelListener(input);

		return windowPanel;
	}
	
	private void addTextArea(OpenSaveTextPane area, JPanel where, String title) {
		area.setFont(COURIER);
		LineNumberedPane lnp = new LineNumberedPane(area);
		lnp.setBorder(BorderFactory.createTitledBorder(title));
		where.add(lnp);
	}
	
	private JTextArea addDisplay(JPanel where, String title) {
		JTextArea jta = new JTextArea();
		jta.setFont(COURIER);
		jta.setEditable(false);
		scrollify(where, jta, title);
		return jta;
	}
	
	private void scrollify(JPanel where, JComponent thing, String title) {
		JScrollPane jsp = new JScrollPane(thing);
		jsp.setBorder(BorderFactory.createTitledBorder(title));
		where.add(jsp);
	}
	
	private class Quitter extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent arg0) {
			quit();
		}

		public void actionPerformed(ActionEvent e) {
			quit();
		}
	}
	
	private class ParseShower implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Chart c = new Chart(getInputString(), grammar.getText());
				Tree result = c.getParseTree();
				chart.setChart(c);
				if (result.isError()) {
					messages.setText("Input error: " + result.getErrorMessage());
					tree.setText("");
				} else {
					tree.setText(Util.rehabTab(result.toTextTree(), 2));
					messages.setText("");
				}
			} catch (MalformedGrammarException e1) {
				messages.setText("Grammar error: " + e1.getMessage());
				chart.clear();
				tree.setText("");
			} catch (PatternSyntaxException e1) {
				messages.setText("Regular expression error: " + e1.getMessage());
				chart.clear();
				tree.setText("");
			} catch (Exception e1) {
				messages.setText("Unanticipated exception: " + e1.getMessage());
				e1.printStackTrace();
				chart.clear();
				tree.setText("");
			}
		}
	}
	
	private void clear() {
		messages.setText("");
		tree.setText("");
		chart.clear();
		input.setEditingMode();
	}
	
	private class Opener implements ActionListener {
		private JFileChooser chooser;
		private OpenSaveTextPane target;
		
		public Opener(JFileChooser chooser, OpenSaveTextPane target) {
			this.chooser = chooser;
			this.target = target;
		}
		
		public void actionPerformed(ActionEvent e) {
			int option = chooser.showOpenDialog(TextGUI.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File choice = chooser.getSelectedFile();
				try {
					target.open(choice);
					messages.setText("File " + choice.getName() + " opened");
					lastFileAccessed = null;
				} catch (FileNotFoundException e1) {
					messages.setText("Problem opening " + choice.getName());
				} catch (Exception e1) {
					messages.setText("Unanticipated exception: " + e1.getMessage());
					chart.clear();
					tree.setText("");
				}
			}
		}
	}
	
	private class JavaImporter implements ActionListener {
		private JIGParser parser = new JIGParser();
		
		public void actionPerformed(ActionEvent e) {
			int option = javaChooser.showOpenDialog(TextGUI.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File choice = javaChooser.getSelectedFile();
				try {
					grammar.setText(parser.makeGrammarFrom(choice).toString());
					messages.setText("File " + choice.getName() + " imported");
					lastFileAccessed = choice;
				} catch (FileNotFoundException e1) {
					messages.setText("Could not open file " + choice.getName());
				} catch (MalformedGrammarException e1) {
					messages.setText("Grammar in file " + choice.getName() + " has errors; cannot import");
				} catch (Exception e1) {
					messages.setText("Unanticipated exception: " + e1.getMessage());
					e1.printStackTrace();
					chart.clear();
					tree.setText("");
				}
			}
		}
	}
	
	private class Sampler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Grammar g = e.getSource() == sample1 ? new CFG() : new Arithmetic();
			grammar.setText(g.toString());
		}
	}
	
	private class Saver implements ActionListener {
		private JFileChooser chooser;
		private OpenSaveTextPane target;
		
		public Saver(JFileChooser chooser, OpenSaveTextPane target) {
			this.chooser = chooser;
			this.target = target;
		}
		
		public void actionPerformed(ActionEvent e) {
			int option = chooser.showSaveDialog(TextGUI.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File choice = chooser.getSelectedFile();
				try {
					target.save(choice);
					messages.setText("File " + choice.getName() + " saved");
					lastFileAccessed = null;
				} catch (IOException e1) {
					messages.setText("Problem saving " + choice.getName());
				} catch (Exception e1) {
					messages.setText("Unanticipated exception: " + e1.getMessage());
					chart.clear();
					tree.setText("");
				}
			}
		}
	}

	private class JavaExporter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int option = javaChooser.showSaveDialog(TextGUI.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File choice = javaChooser.getSelectedFile();
				export2Java(choice);
			}
		}
	}

	private void export2Java(File choice) {
		try {
			Util.toJavaFile(choice, Grammar.makeFrom(grammar.getText()));
			messages.setText("File " + choice.getName() + " exported");
			lastFileAccessed = choice;
		} catch (IOException e1) {
			messages.setText("Problem exporting " + choice.getName());
		} catch (MalformedGrammarException e1) {
			messages.setText("Errors in grammar; cannot export to Java");
		} catch (Exception e1) {
			messages.setText("Unanticipated exception: " + e1.getMessage());
			chart.clear();
			tree.setText("");
		}	
	}
	
	private class UnitTestExporter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (lastFileAccessed != null) {
				addUnitCheck(e.getSource(), lastFileAccessed);
			} else {
				int option = javaChooser.showOpenDialog(TextGUI.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					addUnitCheck(e.getSource(), javaChooser.getSelectedFile());
				}
			}
		}
	}
	
	private void addUnitCheck(Object src, File choice) {
		try {
			if (src == exportUnitCheck) {
				UnitTestGenerator.addUnitCheck(choice, input.getText());
				messages.setText("Unit acceptance test added");
			} else if (src == exportUnitTree) {
				UnitTestGenerator.addUnitTree(choice, input.getText());
				messages.setText("Unit tree test added");
			}
		} catch (FileNotFoundException e1) {
			messages.setText("Could not open a file for JUnit test generation");
		} catch (MalformedGrammarException e1) {
			messages.setText("Could not form a grammar from " + choice.getName());
		} catch (IOException e1) {
			messages.setText("Could not output the JUnit test case");
		} catch (Exception e1) {
			messages.setText("Unanticipated exception: " + e1.getMessage());
			chart.clear();
			tree.setText("");
		}
	}
	
	private class InputListener implements CaretListener {
		@Override
		public void caretUpdate(CaretEvent arg0) {
			messages.setText("");
			if (chart.hasChart()) {clear();}
		}
	}

	private void quit() {
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new TextGUI().setVisible(true);
	}
}
