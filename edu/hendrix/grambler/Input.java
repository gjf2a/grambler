// Copyright 2012 by Gabriel J. Ferrer
//
// This program is part of the Grambler project.
// 
// Grambler is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as 
// published by the Free Software Foundation, either version 3 of 
// the License, or (at your option) any later version.
//
// Grambler is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with Grambler.  If not, see <http://www.gnu.org/licenses/>..

package edu.hendrix.grambler;

class Input {
    private String input;
    private int[] lineNumbers;
    
    public Input(String s) {
        input = s;
        lineNumbers = new int[length()];
        int currentLine = 1;
        for (int i = 0; i < length(); ++i) {
            lineNumbers[i] = currentLine;
            if (endsLine(i)) {++currentLine;}
        }
    }
    
    public int length() {return input.length();}
    
    public boolean legal(int i) {
        return i >= 0 && i < length();
    }
    
    public char charAt(int i) {return input.charAt(i);}
    
    public int lineNumberAt(int i) {return lineNumbers[i];}
    
    public boolean endsLine(int i) {
        return charAt(i) == '\n';
    }
    
    public int startOfLine(int i) {
        int pos = i;
        while (pos > 0 && !endsLine(pos - 1)) {
            pos -= 1;
        }
        return pos;
    }

    public int endOfLine(int i) {
        if (!legal(i)) {throw new IllegalArgumentException(i + " is out of bounds");}
        
        int pos = i;
        while (pos + 1 < length() && !endsLine(pos)) {
            pos += 1;
        }
        return pos;
    }
    
    public String toString() {return input;}
    
    public boolean hasStringAt(int start, int length) {
    	return start >= 0 && start + length < length() && start <= start + length;
    }
    
    public String stringAt(int start, int length) {
        return input.substring(start, start + length);
    }
    
    public String stringAt(int start) {
        return input.substring(start);
    }
    
    public String toLineEnd(int start) {
        return legal(start) ? stringAt(start, 1 + endOfLine(start) - start) : "";
    }
    
    public String lineContaining(int start) {
        int first = startOfLine(start);
        int length = 1 + endOfLine(start) - first;
        return stringAt(first, length);
    }
}
