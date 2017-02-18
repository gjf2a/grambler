package edu.hendrix.grambler;

public class ChartRowTracker {
	private Chart chart;
	
	public ChartRowTracker(Chart c) {
		this.chart = c;
	}
	
	public boolean isLegalRow(int row) {
		return 0 <= row && row <= getMaxRow();
	}
	
	public int getMinRow() {return 0;}
	
	public int getMaxRow() {return chart.size() - 1;}
	
	public int getMaxUsableRow() {
		return chart.isEmptyRow(getMaxRow()) ? getNextUsableRow(getMaxRow(), -1) : getMaxRow();
	}
	
	public int getNextUsableRow(int row, int direction) {
		int startRow = row;
		do {
			row = Math.min(Math.max(0, row + direction), getMaxRow());
		} while (row > 0 && row < getMaxRow() && chart.isEmptyRow(row));
		return chart.isEmptyRow(row) ? startRow : row;
	}
}
