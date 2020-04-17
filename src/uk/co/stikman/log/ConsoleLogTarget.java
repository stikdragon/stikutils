package uk.co.stikman.log;

public class ConsoleLogTarget extends LogTarget {

	private LogFormat	format	= new DefaultLogFormat();

	@Override
	public void log(LogEntry le) {
		if (le.getLevel() == Level.ERROR)
			System.err.println(format(le));
		else
			System.out.println(format(le));
	}

	private String format(LogEntry le) {
		return format.format(le);
	}

	public LogFormat getFormat() {
		return format;
	}

	public void setFormat(LogFormat format) {
		this.format = format;
	}

}
