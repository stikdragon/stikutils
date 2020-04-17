package uk.co.stikman.utils;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
	public static final Map<String, String>	MIME_TYPES	= new HashMap<>();

	static {
		MIME_TYPES.put(".css", "text/css");
		MIME_TYPES.put(".js", "text/javascript");
		MIME_TYPES.put(".html", "text/html");
		MIME_TYPES.put(".htm", "text/html");
		MIME_TYPES.put(".jpg", "image/jpeg");
		MIME_TYPES.put(".jpeg", "image/jpeg");
		MIME_TYPES.put(".png", "image/png");
		MIME_TYPES.put(".gif", "image/gif");
	}

	public static String find(String filename) {
		return find(filename, "application/octet-stream");
	}

	public static String find(String filename, String def) {
		String s = filename;
		int pos = s.lastIndexOf('.');
		if (pos != -1)
			s = s.substring(pos);
		String x = MIME_TYPES.get(s);
		if (x == null)
			return def;
		return x;
	}
}
