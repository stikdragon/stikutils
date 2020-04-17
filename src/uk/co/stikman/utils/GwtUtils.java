package uk.co.stikman.utils;

public class GwtUtils {
	/**
	 * Returns extension, including the .
	 * 
	 * @param name
	 * @return
	 */
	public static String getFileExtension(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1)
			return ""; // empty extension
		return name.substring(lastIndexOf);
	}

	public static String padString(String s, char ch, int n) {
		if (s.length() >= n)
			return s;
		char x[] = new char[n - s.length()];
		for (int i = 0; i < x.length; ++i)
			x[i] = ch;
		return s + String.valueOf(x);
	}

	public static String trimMax(String s, int max) {
		if (s.length() > max)
			s = s.substring(0, max - 1);
		return s;
	}

	public static double clamp(double val, double min, double max) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}

	public static float clamp(float val, float min, float max) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}

	public static int clamp(int val, int min, int max) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}

	public static String escapeHTML(String s) {
		StringBuilder out = new StringBuilder(Math.max(16, s.length()));
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
				out.append("&#");
				out.append((int) c);
				out.append(';');
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}

	public static String htmlColor(int colour) {
		String s = "000000" + Integer.toHexString(colour);
		s = s.substring(s.length() - 6);
		return s; // s.substring(4, 6) + s.substring(2, 4) + s.substring(0, 2);
	}

	public static native void consoleLog(String msg)/*-{
													console.log(msg);
													}-*/;

	public static float lerp(float start, float end, float mu) {
		return start * mu + end * (1 - mu);
	}
}
