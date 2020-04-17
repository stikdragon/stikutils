package uk.co.stikman.table;

import uk.co.stikman.utils.GwtUtils;

public class TextDataTableFormatter implements DataTableFormatter {
	private char	lineChar	= '=';
	private int		minColWidth	= 1;
	private int		colPadding	= 1;

	public char getLineChar() {
		return lineChar;
	}

	public void setLineChar(char lineChar) {
		this.lineChar = lineChar;
	}

	public int getMinColWidth() {
		return minColWidth;
	}

	public void setMinColWidth(int minColWidth) {
		this.minColWidth = minColWidth;
	}

	public int getColPadding() {
		return colPadding;
	}

	public void setColPadding(int colPadding) {
		this.colPadding = colPadding;
	}

	public String format(DataTable dt) {
		int[] widths = new int[dt.fields.size()];
		for (int i = 0; i < widths.length; ++i)
			widths[i] = Math.max(getMinColWidth(), dt.fields.get(i).getName().length());
		for (DataRecord r : dt)
			for (int i = 0; i < widths.length; ++i)
				widths[i] = Math.max(widths[i], r.getString(i).length());

		StringBuilder sb = new StringBuilder();
		int j = 0;
		int pad = getColPadding();
		for (DataField fld : dt.fields)
			sb.append(GwtUtils.padString(fld.getName(), ' ', widths[j++])).append(GwtUtils.padString("", ' ', pad));
		sb.append("\n");
		for (int i = 0; i < widths.length; ++i)
			sb.append(GwtUtils.padString("", lineChar, widths[i])).append(GwtUtils.padString("", ' ', pad));
		sb.append("\n");
		for (DataRecord r : dt) {
			for (int i = 0; i < widths.length; ++i)
				sb.append(GwtUtils.padString(r.getString(i), ' ', widths[i])).append(GwtUtils.padString("", ' ', pad));
			sb.append("\n");
		}
		return sb.toString();
	}
}
