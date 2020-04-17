package uk.co.stikman.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTests {
	@Test
	public void testProperCase() {
		assertEquals("Stik Dragon", Utils.properCase("stik DRAGON"));
		assertEquals("", Utils.properCase(""));
		assertEquals("Stik-Dragon", Utils.properCase("stik-dragon"));
		assertEquals("Stik_dragon", Utils.properCase("stik_dragon")); // _ considered a continuation
		assertEquals("It's", Utils.properCase("it's"));
	}

	@Test(expected = NullPointerException.class)
	public void testProperCaseNulls() {
		Utils.properCase(null);
	}
}
