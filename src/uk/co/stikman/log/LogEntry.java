package uk.co.stikman.log;

import java.util.Date;

public final class LogEntry {
	private String			message;
	private final String	thread;
	private final Date		time;
	private final Level		level;
	private final String	logger;

	public LogEntry(String logger, Level level, String message, long time, String thread) {
		super();
		this.logger = logger;
		this.level = level;
		this.message = message;
		this.time = new Date(time);
		this.thread = thread;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public String getThread() {
		return thread;
	}

	public Date getTime() {
		return time;
	}

	public Level getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return "LogEntry [message=" + message + ", thread=" + thread + ", time=" + time + ", level=" + level + ", logger=" + logger + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((thread == null) ? 0 : thread.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogEntry other = (LogEntry) obj;
		if (level != other.level)
			return false;
		if (logger == null) {
			if (other.logger != null)
				return false;
		} else if (!logger.equals(other.logger))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (thread == null) {
			if (other.thread != null)
				return false;
		} else if (!thread.equals(other.thread))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	public String getLogger() {
		return logger;
	}

}
