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

class Leaf extends Tree {
    public Leaf(String name, Input in, int start, int length) {
        super(name, in, start, length);
    }
    
    public boolean isError() {return false;}
    
    public String getErrorMessage() {
        throw new IllegalStateException("Not an error");
    }
    
    protected void appendTreeNodeStr(StringBuilder sb) {
    	String cleanedup = Util.cleanUpEscapes(getName());
    	//String cleanedup = Util.javafy(getName());
    	//cleanedup = cleanedup.substring(1, cleanedup.length() - 1);
        sb.append(cleanedup);
        sb.append(": ");
        sb.append(Util.javafy(toString()));
    }
}
