package uk.co.stikman.log;

import java.text.SimpleDateFormat;

public class DefaultLogFormat extends LogFormat {

	private SimpleDateFormat	dateFormat	= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public String format(LogEntry le) {
		StringBuilder sb = new StringBuilder();
		sb.append(padLeft(dateFormat.format(le.getTime()), 20));
		sb.append(" ");
		sb.append(padLeft(le.getLevel().name(), 6));
		sb.append(" [");
		sb.append(le.getLogger());
		sb.append("] ");
		sb.append(le.getMessage());
		return sb.toString();
	}

	private static String padLeft(String s, int len) {
		if (s.length() >= len)
			return s;
		char[] buf = new char[len];
		int slen = s.length();
		System.arraycopy(s.toCharArray(), 0, buf, len - slen, slen);
		len -= slen;
		for (int i = 0; i < len; ++i)
			buf[i] = ' ';
		return new String(buf);
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

}
