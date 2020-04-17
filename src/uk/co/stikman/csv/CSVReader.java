package uk.co.stikman.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements AutoCloseable {

	private char			quote;
	private char			sep;
	private BufferedReader	reader;

	public CSVReader(Reader reader, char separator, char quote) {
		this.reader = new BufferedReader(reader);
		this.sep = separator;
		this.quote = quote;
	}

	public char getQuote() {
		return quote;
	}

	public char getSep() {
		return sep;
	}

	public Reader getReader() {
		return reader;
	}

	@Override
	public void close() throws Exception {
		reader.close();
	}

	public String[] readNext() throws IOException {
		// TODO: this can be optimised a lot
		String line = reader.readLine();
		if (line == null)
			return null; // eof

		List<String> lst = new ArrayList<>();
		char[] chars = line.toCharArray();
		int ptr = 0;
		boolean b = false;
		StringBuilder sb = new StringBuilder();
		while (ptr < chars.length) {
			char ch = chars[ptr++];
			char nx = ptr < chars.length ? chars[ptr] : 0;
			if (ch == quote) {
				if (b) {
					if (nx == quote) {
						sb.append(quote);
						++ptr;
					} else {
						b = false;
					}
				} else {
					b = true;
				}
			} else if (ch == sep) {
				if (b) {
					sb.append(sep);
				} else {
					lst.add(sb.toString());
					sb = new StringBuilder();
				}
			} else {
				sb.append(ch);
			}
		}
		lst.add(sb.toString());
		String[] res = new String[lst.size()];
		lst.toArray(res);
		return res;
	}

}
