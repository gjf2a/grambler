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
//along with Grambler.  If not, see <http://www.gnu.org/licenses/>.

@SuppressWarnings("serial")
public class ParseException extends Exception {
	public ParseException(Tree t) {
		super(t.getErrorMessage());
	}
	
	private ParseException(String msg) {
		super(msg);
	}
	
	public ParseException withAddedMsg(String addedMsg) {
		return new ParseException(addedMsg + getMessage());
	}
}
