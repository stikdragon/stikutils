package uk.co.stikman.table;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import uk.co.stikman.csv.CSVWriter;

public class CSVExporter implements DataTableExport {
	private OutputStream	output;
	private Charset			encoding	= StandardCharsets.UTF_8;
	private boolean			header;
	private char			separator	= ',';
	private char			quote		= '"';

	@Override
	public void export(DataTable dt, OutputStream out) throws IOException {
		this.output = out;

		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, encoding), separator, quote)) {
			int fldcnt = dt.getFieldCount();
			writer.startFile();
			if (isHeader()) {
				writer.startRecord();
				for (int i = 0; i < fldcnt; ++i)
					writer.writeValue(dt.getFieldByIndex(i).getName(), CSVWriter.DataType.TEXT);
				writer.endRecord();
			}
			for (DataRecord rec : dt) {
				writer.startRecord();
				for (int i = 0; i < fldcnt; ++i)
					writer.writeValue(rec.getString(i), CSVWriter.DataType.TEXT);
				writer.endRecord();
			}
			writer.endFile();
		}
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

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public char getQuote() {
		return quote;
	}

	public void setQuote(char quote) {
		this.quote = quote;
	}

}