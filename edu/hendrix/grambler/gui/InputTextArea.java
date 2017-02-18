package edu.hendrix.grambler.gui;

import java.awt.Color;

import javax.swing.text.*;

import edu.hendrix.grambler.Chart;

@SuppressWarnings("serial")
public class InputTextArea extends OpenSaveTextPane implements ChartPanelListener {
	public InputTextArea() {
		super();
		
		Style style = addStyle("Editing", null);
		StyleConstants.setForeground(style, Color.BLACK);
		
		style = addStyle("Before", null);
		StyleConstants.setForeground(style, Color.BLUE);
		
		style = addStyle("After", null);
		StyleConstants.setForeground(style, Color.MAGENTA);
		
		style = addStyle("Unmatched", null);
		StyleConstants.setForeground(style, Color.RED);
		StyleConstants.setItalic(style, true);
		StyleConstants.setBold(style, true);
	}

	@Override
	public void rowChanged(int newRow, Chart c) {
		//setEditable(false);
		StyledDocument doc = getStyledDocument();
		int matchedEnd = c.getLastOccupiedRow();
		int end = c.size() - 1;
		
		int beforeLength = Math.min(matchedEnd, newRow);
		int afterLength = Math.max(0, matchedEnd - newRow);
		int unmatchedLength = end - matchedEnd;
		
		doc.setCharacterAttributes(0, beforeLength, getStyle("Before"), true);
		doc.setCharacterAttributes(newRow, afterLength, getStyle("After"), true);
		doc.setCharacterAttributes(matchedEnd, unmatchedLength, getStyle("Unmatched"), true);
	}
	
	public void setEditingMode() {
		//setEditable(true);
		getStyledDocument().setCharacterAttributes(0, this.getText().length(), getStyle("Editing"), true);
	}
}
