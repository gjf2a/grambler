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

import java.util.*;

abstract public class Tree {
    private String name;
    private Input input;
    private int start, length;
    
    public Tree(String name, Input in, int start, int length) {
        this.name = name;
        input = in;
        this.start = start;
        this.length = length;
    }
    
    /**
     * Returns the name of the nonterminal or terminal that corresponds to this node.
     * 
     * @return The name of the nonterminal or terminal that corresponds to this node.
     * 
     */
    public String getName() {return name;}
    
    /**
     * Returns true if getName().equals(name); returns false otherwise
     * 
     * @return Whether this Tree has the given name
     *
     */
    
    public boolean isNamed(String name) {
    	return getName().equals(name);
    }
    
    /**
     * Returns an Input object corresponding to the parsed text.
     * 
     * @return An Input object corresponding to the parsed text.
     */
    Input getInput() {return input;}
    
    /**
     * Returns the line number of the first input character matched by this Tree.
     * 
     * @return The line number of the first input character matched by this Tree.
     */
    public int getFirstLine() {
        return (start < input.length()) ? input.lineNumberAt(start) : 0;
    }
    
    /**
     * Returns the index into the parsed text of the first character matched by this Tree.
     * 
     * @return The index into the parsed text of the first character matched by this Tree.
     */
    public int getStartingIndex() {return start;}
    
    /**
     * Returns the index into the parsed text of the final character matched by this Tree.
     * 
     * @return The index into the parsed text of the final character matched by this Tree.
     */
    public int getEndingIndex() {return start + length - 1;}
    
    /**
     * Returns the number of characters matched by this Tree.
     * 
     * @return The number of characters matched by this Tree.
     */
    public int getLength() {return length;}
        
    /**
     * Indicates whether this is a leaf.
     * 
     * @return true if this is a leaf node, false otherwise.
     */
    public boolean isLeaf() {return getNumChildren() == 0;}
    
    /**
     * Indicates whether this is a syntax error.
     * 
     * @return true if this tree represents a syntax error, false if it is a valid parse.
     */
    abstract public boolean isError();
    
    /**
     * Returns an error message.
     * 
     * In the event of a syntax error, an error message is returned containing the following information:
     * <br>1. The line number where the error occurred.
     * <br>2. All of the successfully-matched text on the line where the error occurred.
     * <br>If this Tree represents a valid parse, an exception is thrown.  
     * 
     * @return error message, only if this tree represents a syntax error
     * @throws IllegalStateException if isError() is false
     */
    abstract public String getErrorMessage();
    
    /**
     * Indicates whether this Tree matches a zero-length input.
     * 
     * Some grammars include regular expressions that can successfully 
     * match zero characters.  This method returns true when nodes
     * corresponding to those matches are encountered.
     * 
     * @return true if this Tree has a zero-length matching input, false otherwise
     */
    public boolean isEmpty() {
        return !isError() && getLength() == 0;
    }
    
    /**
     * Returns the substring of the input text matched by this Tree.
     * 
     * @return the substring of the input text matched by this Tree.
     */
    public String toString() {
        return getInput().stringAt(getStartingIndex(), getLength());
    }
    
    /**
     * Returns number of child nodes.
     * 
     * @return the number of child nodes this Tree has.
     */
    public int getNumChildren() {return 0;}
    
    /**
     * Returns whether there are any children for whom name().equals(name).
     * 
     * @param name name of a terminal or nonterminal symbol
     * @return true if children are present with name, false otherwise
     */
    public boolean hasNamed(String name) {
    	return getNumNamed(name) > 0;
    }
    
    /**
     * Returns the number of children for whom getName().equals(name).
     * 
     * @param name name of a terminal or nonterminal symbol
     * @return number of child nodes with the given name
     */
    public int getNumNamed(String name) {return 0;}
    
    /**
     * Returns the nth child from this Tree.
     * 
     * @param n index value, 0 <= n < getNumChildren()
     * @return tree corresponding to nth child
     * @throws UnsupportedOperationException if not an Interior node
     * @throws ArrayIndexOutOfBoundsException if n is out of bounds
     */
    public Tree getChild(int n) {
        throw new UnsupportedOperationException("Tree " + this + " has no children");
    }
    
    /**
     * Returns the rightmost child of this Tree.
     * 
     * @return Rightmost child of this tree
     * @throws UnsupportedOperationException if not an Interior node
     * @throws ArrayIndexOutOfBoundsException if there are no children
     */
    public Tree getLastChild() {
        return getChild(getNumChildren() - 1);
    }
    
    /**
     * Returns the first child with the given name.
     * 
     * @param name name of a terminal or nonterminal symbol
     * @return first child with the given name
     */
    public Tree getNamedChild(String name) {
        return getNamedChild(name, 0);
    }
    
    /**
     * Returns the nth child with the given name.
     * 
     * @param name name of a terminal or nonterminal symbol
     * @param n 0 <= n < numNamed(name)
     * @return nth child with the given name
     * @throws UnsupportedOperationException if not an Interior node
     * @throws ArrayIndexOutOfBoundsException if n is out of bounds
     */
    public Tree getNamedChild(String name, int n) {
        throw new UnsupportedOperationException("Tree " + this + " has no children");
    }
    
    /**
     * Returns all recursive children (descendants) of this Tree with a given name.  A preorder 
     * traversal is performed.
     * 
     * @param name name of descendants to retrieve
     */
    public ArrayList<Tree> getNamedDescendants(String name) {
    	ArrayList<Tree> result = new ArrayList<Tree>();
    	if (this.name.equals(name)) {
    		result.add(this);
    	}
    	
    	for (int i = 0; i < getNumChildren(); ++i) {
    		result.addAll(getChild(i).getNamedDescendants(name));
    	}
    	
    	return result;
    }
    
    /**
     * Returns a String corresponding to a preorder traversal of this Tree.
     * 
     * @return a String corresponding to a preorder traversal of this Tree.
     */
    public String toTextTree() {
        StringBuilder sb = new StringBuilder();
        appendPreorderString(sb, 0);
        return sb.toString();
    }
    
    abstract protected void appendTreeNodeStr(StringBuilder sb);
    
    protected void appendPreorderString(StringBuilder sb, int indent) {
        appendTabStr(sb, indent);
        appendTreeNodeStr(sb);
        sb.append('\n');
        for (int c = 0; c < getNumChildren(); ++c) {
            getChild(c).appendPreorderString(sb, indent + 1);
        }
    }
    
    private void appendTabStr(StringBuilder sb, int indent) {
        while (indent > 0) {
            sb.append('\t');
            --indent;
        }
    }
}
