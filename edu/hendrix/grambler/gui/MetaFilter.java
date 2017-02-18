package edu.hendrix.grambler.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MetaFilter extends FileFilter {
	private String suffix;
	private String description;
	
	public MetaFilter(String suffix, String description) {
		this.suffix = suffix;
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(suffix);
	}

	@Override
	public String getDescription() {
		return description;
	}
}
