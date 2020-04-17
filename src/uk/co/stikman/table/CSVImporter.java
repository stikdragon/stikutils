package uk.co.stikman.table;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import uk.co.stikman.csv.CSVReader;


public class CSVImporter implements DataTableImporter {
	private Charset	encoding	= StandardCharsets.UTF_8;
	private boolean	header		= true;
	private char	separator	= ',';
	private char	quote		= '"';

	@Override
	public void inport(DataTable dt, InputStream in) throws IOException {
		try (CSVReader csv = new CSVReader(new InputStreamReader(in, getEncoding()), getSeparator(), getQuote())) {
			int linecount = 0;
			int fieldcount = 0;
			while (true) {
				String[] line = csv.readNext();
				if (line == null)
					break;
				if (header && linecount == 0) { // header
					for (String s : line)
						dt.addField(s);
				} else {
					DataRecord r = dt.addRecord();
					//
					// Do them in reverse, this helps the records internally allocate their buffers in a more efficient way
					//
					for (int i = line.length - 1; i >= 0; --i) {
						String s = line[i];
//						if (s != null)
//							s = s.intern();
						r.setValue(i, s);
					}
					fieldcount = fieldcount < line.length ? line.length : fieldcount;
				}
				++linecount;
			}

			if (!header)
				for (int i = 0; i < fieldcount; ++i)
					if (i >= dt.getFieldCount())
						dt.addField("Field " + (i + 1));

		} catch (Exception e) {
			throw new IOException(e);
		}
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
