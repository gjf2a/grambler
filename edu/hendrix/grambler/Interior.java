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

class Interior extends Tree {
    private ArrayList<Tree> children;
    private HashMap<String,ArrayList<Tree>> nameToChild;
    private boolean isError;
    private String errorMsg;
    
    public Interior(String name, Input in, int start, int length, ArrayList<Tree> children) {
        super(name, in, start, length);
        this.children = children;
        isError = false;
        nameToChild = new HashMap<String,ArrayList<Tree>>();
        for (Tree t: children) {
            if (!nameToChild.containsKey(t.getName())) {
                nameToChild.put(t.getName(), new ArrayList<Tree>());
            }
            nameToChild.get(t.getName()).add(t);
            if (t.isError()) {
                isError = true;
                errorMsg = t.getErrorMessage();
            }
        }
    }
    
    public boolean isLeaf() {return false;}
    
    public boolean isError() {return isError;}
    
    public String getErrorMessage() {
        if (isError) {
            return errorMsg;
        } else {
            throw new IllegalStateException("Not an error");
        }
    }
    
    public int getNumChildren() {return children.size();}
    
    public Tree getChild(int c) {
        return children.get(c);
    }
    
    public int getNumNamed(String name) {
        return nameToChild.containsKey(name) ? nameToChild.get(name).size() : 0;
    }
    
    public Tree getNamedChild(String name, int n) {
        return nameToChild.get(name).get(n);
    }
    
    protected void appendTreeNodeStr(StringBuilder sb) {
        sb.append(getName());
    }
}
