package uk.co.stikman.table;

import static uk.co.stikman.utils.GwtUtils.escapeHTML;
import static uk.co.stikman.utils.GwtUtils.padString;
import static uk.co.stikman.utils.GwtUtils.trimMax;

import java.util.HashMap;
import java.util.Map;

/**
 * This one is very similar to the {@link TextDataTableFormatter} except it
 * sticks it in a &lt;div&gt; with &lt;pre&gt; tags for each bit so you can jam
 * it in an html page somewhere. also escapes values so should be safe
 * 
 * @author Stik
 *
 */
public class PreDataTableFormatter implements DataTableFormatter {
	private char				lineChar		= '=';
	private int					minColWidth		= 1;
	private int					colPadding		= 1;
	private String				styleHeader		= "header";
	private String				styleLine		= "line";
	private String				styleValue		= "val";
	private Map<String, String>	columnClasses	= new HashMap<>();
	private int[]				fixedWidths		= null;

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
		for (int i = 0; i < widths.length; ++i) {
			if (getFixedWidth(i) != -1)
				widths[i] = getFixedWidth(i);
			else
				widths[i] = Math.max(getMinColWidth(), dt.fields.get(i).getName().length());
		}
		for (DataRecord r : dt)
			for (int i = 0; i < widths.length; ++i)
				if (getFixedWidth(i) != -1)
					widths[i] = Math.max(widths[i], r.getString(i).length());

		StringBuilder sb = new StringBuilder();
		sb.append("<pre>");
		int j = 0;
		int pad = getColPadding();
		for (DataField fld : dt.fields) {
			String xtra = columnClasses.get(fld.getName());
			xtra = xtra == null ? "" : (" " + xtra);
			sb.append("<span class=\"" + styleHeader + xtra + "\">").append(escapeHTML(padString(fld.getName(), ' ', widths[j++]))).append("</span><span>").append(padString("", ' ', pad)).append("</span>");
		}
		sb.append("</pre>\n<pre>");
		for (int i = 0; i < widths.length; ++i)
			sb.append("<span class=\"" + styleLine + "\">").append(escapeHTML(trimMax(padString("", lineChar, widths[i]), widths[i]))).append("</span><span>").append(padString("", ' ', pad)).append("</span>");
		sb.append("</pre>\n<pre>");
		for (DataRecord r : dt) {
			for (int i = 0; i < widths.length; ++i) {
				if (!preRender(sb, r, i)) {
					String xtra = getValueClass(r, i);
					xtra = xtra == null ? "" : (" " + xtra);
					sb.append("<span class=\"" + styleValue + xtra + "\">").append(escapeHTML(trimMax(padString(r.getString(i), ' ', widths[i]), widths[i]))).append("</span><span>").append(padString("", ' ', pad)).append("</span>");
				}
			}
			sb.append("</pre>\n<pre>");
		}
		sb.append("</pre>");
		return sb.toString();
	}

	protected String getValueClass(DataRecord rec, int fld) {
		return columnClasses.get(rec.getTable().getField(fld).getName());
	}

	protected boolean preRender(StringBuilder sb, DataRecord rec, int fld) {
		return false;
	}

	public String getStyleHeader() {
		return styleHeader;
	}

	public void setStyleHeader(String styleHeader) {
		this.styleHeader = styleHeader;
	}

	public String getStyleLine() {
		return styleLine;
	}

	public void setStyleLine(String styleLine) {
		this.styleLine = styleLine;
	}

	public String getStyleValue() {
		return styleValue;
	}

	public void setStyleValue(String styleValue) {
		this.styleValue = styleValue;
	}

	public Map<String, String> getColumnClasses() {
		return columnClasses;
	}

	public int getFixedWidth(int field) {
		if (fixedWidths == null)
			return -1;
		if (field >= fixedWidths.length)
			return -1;
		return fixedWidths[field];
	}

	public void setFixedWidth(int field, int width) {
		if (fixedWidths == null)
			fixedWidths = new int[field + 1];
		if (field >= fixedWidths.length) {
			int[] n = new int[field + 1];
			for (int i = 0; i < n.length; ++i)
				n[i] = i < fixedWidths.length ? fixedWidths[i] : -1;
			fixedWidths = n;
		}
		fixedWidths[field] = width;
	}
}
