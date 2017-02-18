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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class OpenSaveTextPane extends JTextPane implements CaretListener {
	private String prevText;
	private boolean userUpdating, needsSaving;
	
	public OpenSaveTextPane() {
		super();
		prevText = "";
		addCaretListener(this);
		userUpdating = true;
		needsSaving = false;
	}
	
	public boolean needsSaving() {
		return needsSaving;
	}
	
	private void toggleNeedsSaving(boolean needsSaving) {
		this.needsSaving = needsSaving;
	}
	
	public void save(File f) throws IOException {
		PrintWriter p = new PrintWriter(new FileWriter(f));
		p.println(getText());
		p.close();
		toggleNeedsSaving(false);
	}
	
	public void open(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		String textRead = "";
		while (s.hasNextLine()) {
			textRead += s.nextLine() + "\n";
		}
		changeText(textRead, 0);
		toggleNeedsSaving(false);
	}
	
	private void changeText(String text, int where) {
		userUpdating = false;
		setText(text);
		setCaretPosition(where);
		userUpdating = true;
		toggleNeedsSaving(true);
	}

	public void caretUpdate(CaretEvent e) {
		if (userUpdating && !prevText.equals(getText())) {
			toggleNeedsSaving(true);
		} 
		prevText = getText();
	}
}
