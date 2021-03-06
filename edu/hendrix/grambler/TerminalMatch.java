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

class TerminalMatch {
	private String matchedText;
	private boolean isMatched;
	
	public TerminalMatch() {
		matchedText = "";
		isMatched = false;
	}
	
	public TerminalMatch(String text) {
		matchedText = text;
		isMatched = true;
	}
	
	public int getNumCharsMatched() {
		assert isMatched;
		return matchedText.length();
	}
	
	public String getTextMatched() {
		assert isMatched;
		return matchedText;
	}
	
	public boolean isMatched() {
		return isMatched;
	}
}
