package edu.hendrix.grambler.gui;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.hendrix.grambler.*;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class ChartPanel extends JPanel {
	private Chart chart;
	private ChartRowTracker charter;
	private JSpinner rowSelector;
	private SpinnerNumberModel selection;
	private JTextArea contents;
	private JButton first, last, next, prev;
	private ArrayList<ChartPanelListener> listeners;
	
	public ChartPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Earley Chart"));
		rowSelector = new JSpinner();
		rowSelector.addChangeListener(new Changer());
		contents = new JTextArea();
		contents.setEditable(false);
		contents.setFont(new Font("Courier", Font.PLAIN, 12));
		clear();
		
		first = new JButton("Start");
		first.addActionListener(new Starter());
		last = new JButton("End");
		last.addActionListener(new Ender());
		next = new JButton("Next");
		next.addActionListener(new NextPrev(1));
		prev = new JButton("Previous");
		prev.addActionListener(new NextPrev(-1));
		
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout());
		top.add(first);
		top.add(last);
		top.add(next);
		top.add(prev);
		top.add(new JLabel("Row:"));
		top.add(rowSelector);
		
		setLayout(new BorderLayout());
		add(top, BorderLayout.NORTH);
		add(new JScrollPane(contents), BorderLayout.CENTER);
		
		listeners = new ArrayList<ChartPanelListener>();
	}
	
	public void addChartPanelListener(ChartPanelListener cpl) {
		listeners.add(cpl);
	}
	
	public int getNumRows() {
		return chart.size();
	}
	
	public int getCurrentRow() {
		return selection.getNumber().intValue();
	}
	
	public void setCurrentRow(int row) {
		if (hasChart() && charter.isLegalRow(row)) {
			selection.setValue(row);
		} else {
			throw new IllegalArgumentException("Row " + row + " is out of bounds");
		}
	}
	
	public void clear() {
		selection = new SpinnerNumberModel(0, 0, 0, 0);
		rowSelector.setModel(selection);
		contents.setText("");
		chart = null;	
		charter = null;
	}
	
	public void setChart(Chart c) {
		chart = c;
		charter = new ChartRowTracker(c);
		selection = new SpinnerNumberModel(charter.getMaxUsableRow(), charter.getMinRow(), charter.getMaxRow(), -1);
		rowSelector.setModel(selection);
		refreshRow();
	}
	
	public boolean hasChart() {
		return chart != null;
	}
	
	private void refreshRow() {
		contents.setText(chart.getRow(getCurrentRow()));
		for (ChartPanelListener cpl: listeners) {
			cpl.rowChanged(getCurrentRow(), chart);
		}
	}
	
	private class Changer implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent arg0) {
			if (hasChart()) {refreshRow();}
		}
	}
	
	private class Starter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (hasChart()) {setCurrentRow(charter.getMinRow());}
		}
	}
	
	private class Ender implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (hasChart()) {setCurrentRow(charter.getMaxUsableRow());}
		}
	}

	private class NextPrev implements ActionListener {
		private int direction; 
		
		public NextPrev(int direction) {
			this.direction = direction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (hasChart()) {
				setCurrentRow(charter.getNextUsableRow(getCurrentRow(), direction));
			}
		}
	}
}
