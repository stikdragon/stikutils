package uk.co.stikman.table;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FixedWidthExporter implements DataTableExport {
	private OutputStream			output;
	private Charset					encoding	= StandardCharsets.UTF_8;
	private boolean					header;
	private Map<String, Alignment>	alignover	= new HashMap<>();

	@Override
	public void export(DataTable dt, OutputStream out) throws IOException {
		this.output = out;

		int cnt = dt.getFieldCount();
		int[] widths = new int[cnt];
		Alignment[] align = new Alignment[cnt];

		if (isHeader()) {
			for (int i = 0; i < cnt; ++i)
				widths[i] = wid(dt.getField(i).getName());
		} else {
			for (int i = 0; i < cnt; ++i)
				widths[i] = 0;
		}

		for (DataRecord r : dt) {
			for (int i = 0; i < cnt; ++i)
				widths[i] = Math.max(widths[i], wid(r.getString(i)));
		}

		//
		// sort out alignment.  by default any numeric column is right-aligned, 
		// otherwise left.  override with alignover
		//
		for (int i = 0; i < cnt; ++i) {
			DataField f = dt.getField(i);
			if (alignover.containsKey(f.getName()))
				align[i] = alignover.get(f.getName());
			else
				align[i] = f.getType() != DataType.STRING ? Alignment.RIGHT : Alignment.LEFT;

			if (align[i] == Alignment.CENTRE) // cba to support this
				align[i] = Alignment.LEFT;
		}

		StringBuilder sb = new StringBuilder();
		if (isHeader()) {
			String sep = "";
			for (int i = 0; i < cnt; ++i) {
				DataField f = dt.getField(i);
				sb.append(sep);
				sep = " | ";
				just(sb, f.getName(), widths[i], align[i]);
			}
			sb.append("\n");
			sep = "";
			for (int i = 0; i < cnt; ++i) {
				sb.append(sep);
				sep = "-+-";
				for (int j = 0; j < widths[i]; ++j)
					sb.append("-");
			}
			sb.append("\n");
		}

		for (DataRecord rec : dt) {
			String sep = "";
			for (int i = 0; i < cnt; ++i) {
				sb.append(sep);
				sep = " | ";
				just(sb, rec.getString(i), widths[i], align[i]);
			}
			sb.append("\n");
		}

		// TODO: write this in pieces instead of one giant byte[]
		out.write(sb.toString().getBytes(getEncoding()));
	}

	private void just(StringBuilder out, String s, int width, Alignment align) {
		if (s == null)
			s = "";
		int n = width - s.length();
		if (n <= 0) {
			out.append(s);
			return;
		}

		if (align == Alignment.LEFT) {
			out.append(s);
			while (n-- > 0)
				out.append(" ");
		} else {
			while (n-- > 0)
				out.append(" ");
			out.append(s);
		}
	}

	public void setAlignmentForField(String fieldname, Alignment al) {
		alignover.put(fieldname, al);
	}

	private int wid(String s) {
		if (s == null)
			return 0;
		return s.length();
	}

	public OutputStream getOutput() {
		return output;
	}

	public Charset getEncoding() {
		return encoding;
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

}
