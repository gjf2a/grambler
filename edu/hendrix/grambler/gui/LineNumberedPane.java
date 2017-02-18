package edu.hendrix.grambler.gui;

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

// This class uses code from:
//   http://www.javaprogrammingforums.com/java-swing-tutorials/915-how-add-line-numbers-your-jtextarea.html
// 
// Another source of ideas for further consideration:
//   http://tips4java.wordpress.com/2009/05/23/text-component-line-number/#more-1019

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class LineNumberedPane extends JScrollPane {
	private JTextArea lines;
	private JTextComponent text;
	
	private Color lineNumberColor = Color.black;//new Color(0, 100, 100);
	private Color lineBackdrop = new Color(220,220,220);
	
	public LineNumberedPane(JTextComponent jtc) {
		super(jtc);
		text = jtc;
		lines = new JTextArea();
		lines.setBackground(lineBackdrop);
		lines.setForeground(lineNumberColor);
		lines.setFont(text.getFont());
		lines.setEditable(false);
		
		text.getDocument().addDocumentListener(new LineUpdater());
		setRowHeaderView(lines);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	private class LineUpdater implements DocumentListener {
		public String getText(){
			int caretPosition = text.getDocument().getLength();
			Element root = text.getDocument().getDefaultRootElement();
			String lineNums = "";
			for(int i = 1; i < root.getElementIndex( caretPosition ) + 2; i++){
				lineNums += i + " " + System.getProperty("line.separator");
			}
			return lineNums;
		}
		@Override
		public void changedUpdate(DocumentEvent de) {
			lines.setText(getText());
		}

		@Override
		public void insertUpdate(DocumentEvent de) {
			lines.setText(getText());
		}

		@Override
		public void removeUpdate(DocumentEvent de) {
			lines.setText(getText());
		}		
	}
}
