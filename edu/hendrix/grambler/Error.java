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

class Error extends Tree {
	private int lineNumber;
	private String msg;
	private static boolean extendedMsg = true;
	
	public Error(String name, Input input, int where) {
		super(name, input, where, 0);
		if (where < input.length()) {
			lineNumber = input.lineNumberAt(where);
			setMessageWithLine("Syntax error");
		} else {
			lineNumber = 0;
			setMessage("Syntax error: Input exhausted");
		}
	}
	
	private void setMessage(String message) {
		msg = message;
	}
	
	private void setMessageWithLine(String message) {
		msg = message + " in line " + lineNumber;
		if (extendedMsg) {
			int lineStart = getInput().startOfLine(getEndingIndex());
			int linePart = getEndingIndex() - lineStart;
			if (getInput().hasStringAt(lineStart, linePart)) {
				String matched = getInput().stringAt(lineStart, linePart + 1);
				if (matched.charAt(matched.length() - 1) == '\n') {
					matched = matched.substring(0, matched.length() - 1);
				}
				msg += " (matched up to: [" + matched + "])";
			}
		}
	}

	@Override
	public boolean isError() {
		return true;
	}

	@Override
	public String getErrorMessage() {
		return msg;
	}

	@Override
	protected void appendTreeNodeStr(StringBuilder sb) {
		sb.append(getName());
        sb.append(": \"");
        sb.append(getErrorMessage());
        sb.append('"');
	}

}
