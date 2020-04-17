package uk.co.stikman.table;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import uk.co.stikman.csv.CSVReader;

@SuppressWarnings("resource")
public class TestCSVTableStuff {
	@Test
	public void testSimpleCSVParse() throws IOException {
		CSVReader csv = new CSVReader(new StringReader("a,b,c"), ',', '"');
		String[] bits = csv.readNext();
		assertArrayEquals(new String[] { "a", "b", "c" }, bits);
		bits = csv.readNext();
		assertNull(bits);
	}

	@Test
	public void testComplexCSVParse1() throws IOException {
		CSVReader csv = new CSVReader(new StringReader("a,,c,"), ',', '"');
		String[] bits = csv.readNext();
		assertArrayEquals(new String[] { "a", "", "c", "" }, bits);
		bits = csv.readNext();
		assertNull(bits);
	}

	@Test
	public void testComplexCSVParse2() throws IOException {
		CSVReader csv = new CSVReader(new StringReader(""), ',', '"');
		String[] bits = csv.readNext();
		assertNull(bits);
	}
	
	@Test
	public void testComplexCSVParse3() throws IOException {
		CSVReader csv = new CSVReader(new StringReader("\"a\"\"a,a\"bbb,c,d\""), ',', '"');
		String[] bits = csv.readNext();
		assertArrayEquals(new String[] { "a\"a,abbb", "c", "d" }, bits);
		bits = csv.readNext();
		assertNull(bits);
	}

}
