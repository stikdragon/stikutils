package uk.co.stikman.csv;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter implements AutoCloseable, Flushable {

	public enum DataType {
		TEXT, NUMERIC
	}

	public enum WriterMode {
		/**
		 * Excel mode tries to behave like Excel 2010 (v14.0). Newlines are
		 * allowed in values, quotes are only used where a value contains
		 * special symbols. " symbols are escaped as a double "".
		 */
		EXCEL,

		/**
		 * Simple mode quotes all string values and replaces linebreaks in
		 * values
		 */
		SIMPLE

	}

	/**
	 * The default separator to use if none is supplied to the constructor.
	 */
	public static final char	DEFAULT_SEPARATOR		= ',';
	/**
	 * The default quote character to use if none is supplied to the
	 * constructor.
	 */
	public static final char	DEFAULT_QUOTE_CHARACTER	= '"';
	/**
	 * The quote constant to use when you wish to suppress all quoting.
	 */
	public static final char	NO_QUOTE_CHARACTER		= '\u0000';
	/**
	 * The escape constant to use when you wish to suppress all escaping.
	 */
	public static final char	NO_ESCAPE_CHARACTER		= '\u0000';
	/**
	 * Default line terminator uses platform encoding.
	 */
	public static final String	DEFAULT_LINE_END		= "\n";
	private Writer				rawWriter;
	private PrintWriter			pw;
	private char				separator;
	private char				quotechar;
	private String				lineEnd;
	private int					valueCount				= 0;
	private WriterMode			mode					= WriterMode.EXCEL;

	/**
	 * Constructs CSVWriter using a comma for the separator.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 */
	public CSVWriter(Writer writer) {
		this(writer, DEFAULT_SEPARATOR);
	}

	/**
	 * Constructs CSVWriter with supplied separator.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries.
	 */
	public CSVWriter(Writer writer, char separator) {
		this(writer, separator, DEFAULT_QUOTE_CHARACTER);
	}

	public CSVWriter(Writer writer, char separator, char quotechar) {
		this(writer, separator, quotechar, DEFAULT_LINE_END);
	}

	public CSVWriter(Writer writer, char separator, String lineEnd) {
		this(writer, separator, DEFAULT_QUOTE_CHARACTER, lineEnd);
	}

	public CSVWriter(Writer writer, char separator, char quotechar, String lineEnd) {
		this.rawWriter = writer;
		this.pw = new PrintWriter(writer);
		this.separator = separator;
		this.quotechar = quotechar;
		this.lineEnd = lineEnd;
	}

	/**
	 * Flush underlying stream to writer.
	 *
	 * @throws IOException
	 *             if bad things happen
	 */
	public void flush() throws IOException {
		pw.flush();
	}

	/**
	 * Close the underlying stream writer flushing any buffered content.
	 *
	 * @throws IOException
	 *             if bad things happen
	 */
	public void close() throws IOException {
		flush();
		pw.close();
		rawWriter.close();
	}

	/**
	 * Checks to see if the there has been an error in the printstream.
	 *
	 * @return <code>true</code> if the print stream has encountered an error,
	 *         either on the underlying output stream or during a format
	 *         conversion.
	 */
	public boolean checkError() {
		return pw.checkError();
	}

	/**
	 * flushes the writer without throwing any exceptions.
	 */
	public void flushQuietly() {
		try {
			flush();
		} catch (IOException e) {
			// catch exception and ignore.
		}
	}

	public void startFile() {
	}

	public void startRecord() {
		valueCount = 0;
	}

	/**
	 * Writes a string, escapes it correctly according to what type it is
	 *
	 * @param s
	 */
	public void writeValue(String s, DataType type) {
		if (valueCount > 0)
			pw.write(separator);
		s = cleanValue(s, type);
		pw.write(s);
		++valueCount;
	}

	/**
	 * Writes a string without any modification. If you write something
	 * containing unsafe chars (like a ,) then it'll mess up your CSV file
	 * 
	 * @param s
	 */
	public void writeRaw(String s) {
		if (valueCount > 0)
			pw.write(separator);
		pw.write(s);
		++valueCount;
	}

	private String cleanValue(String s, DataType type) {
		switch (mode) {
		case SIMPLE: {
			//
			// Strip out newlines, quote all strings, and other types if necessary. escape quote chars
			//
			boolean quoteme = type == DataType.TEXT;
			StringBuilder sb = new StringBuilder();
			for (char c : s.toCharArray()) {
				if (c == quotechar) {
					sb.append(quotechar);
					sb.append(quotechar);
					quoteme = true;
				} else if (c == '\r' || c == '\n') {
					sb.append(' ');
					quoteme = true;
				} else if (c == separator) {
					sb.append(separator);
					quoteme = true;
				} else {
					sb.append(c);
				}
			}
			if (quoteme)
				return "\"" + sb.toString() + "\"";
			return sb.toString();
		}
		default: {
			//
			// Only quote things with linebreaks, quotes or separators in
			//
			boolean quoteme = false;
			StringBuilder sb = new StringBuilder();
			for (char c : s.toCharArray()) {
				if (c == quotechar) {
					sb.append(quotechar);
					sb.append(quotechar);
					quoteme = true;
				} else if (c == separator) {
					sb.append(separator);
					quoteme = true;
				} else if (c == '\r' || c == '\n') {
					sb.append(c);
					quoteme = true;
				} else {
					sb.append(c);
				}
			}
			if (quoteme)
				return "\"" + sb.toString() + "\"";
			return sb.toString();
		}
		}
	}

	public void writeStrings(String[] sl) {
		startRecord();
		for (String s : sl)
			writeValue(s, DataType.TEXT);
		endRecord();
	}

	public void writeStrings(Iterable<String> sl) {
		startRecord();
		for (String s : sl)
			writeValue(s, DataType.TEXT);
		endRecord();
	}

	public void endRecord() {
		pw.write(lineEnd);
	}

	public void endFile() {

	}

	public WriterMode getMode() {
		return mode;
	}

	public void setMode(WriterMode mode) {
		this.mode = mode;
		if (mode == WriterMode.EXCEL)
			lineEnd = "\r\n";
	}

	public void setSeparator(char c) {
		this.separator = c;
	}

	public char getSeparator() {
		return separator;
	}

	public char getQuotechar() {
		return quotechar;
	}

	public void setQuotechar(char quotechar) {
		this.quotechar = quotechar;
	}

	public String getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(String lineEnd) {
		this.lineEnd = lineEnd;
	}

	public PrintWriter getWriter() {
		return pw;
	}

}
