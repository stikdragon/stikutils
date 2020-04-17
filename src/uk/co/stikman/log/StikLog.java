package uk.co.stikman.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StikLog {
	private static Map<String, StikLog>	loggers	= new HashMap<>();
	private final String				name;
	private static List<LogTarget>		targets	= new ArrayList<>();

	static {
		targets.add(new ConsoleLogTarget());
	}

	public static StikLog getLogger(String name) {
		synchronized (loggers) {
			StikLog l = loggers.get(name);
			if (l == null)
				loggers.put(name, l = new StikLog(name));
			return l;
		}
	}

	public static StikLog getLogger(Class<?> cls) {
		if (cls == null)
			return getLogger("");
		return getLogger(cls.getName());
	}

	public StikLog(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public StikLog log(Level lvl, String msg) {
		doLog(this, lvl, msg);
		return this;
	}

	private static void doLog(StikLog logger, Level lvl, String msg) {
		LogEntry le = new LogEntry(logger.getName(), lvl, msg, currentTime(), Thread.currentThread().getName());
		synchronized (targets) {
			for (LogTarget t : targets)
				if (t.filter(le))
					t.log(le);
		}
	}

	private static long currentTime() {
		return System.currentTimeMillis();
	}

	public void error(String msg) {
		error(msg, null);
	}

	public void error(Throwable th) {
		error(null, th);
	}

	public void error(String msg, Throwable th) {
		if (msg != null)
			log(Level.ERROR, msg);
		if (th != null) {
			StringWriter sw = new StringWriter();
			th.printStackTrace(new PrintWriter(sw));
			for (String s : sw.toString().split("\\r?\\n"))
				error(s);
		}
	}

	public void info(String msg) {
		log(Level.INFO, msg);
	}

	public void warn(String msg, Throwable th) {
		if (msg != null)
			log(Level.WARN, msg);
		if (th != null) {
			StringWriter sw = new StringWriter();
			th.printStackTrace(new PrintWriter(sw));
			for (String s : sw.toString().split("\\r?\\n"))
				warn(s);
		}
	}

	public void warn(String msg) {
		warn(msg, null);
	}
	public void warn(Throwable th) {
		warn(null, th);
	}

	public void debug(String msg) {
		log(Level.DEBUG, msg);
	}

	/**
	 * Add a new {@link LogTarget}, this is where logged messages will get sent
	 * to. You can add a new target of your own to redirect things to a file,
	 * for example
	 * 
	 * @param tgt
	 */
	public static void addTarget(LogTarget tgt) {
		synchronized (targets) {
			targets.add(tgt);
		}
	}

	public static Iterable<LogTarget> getTargets() {
		synchronized (targets) {
			return new ArrayList<>(targets);
		}
	}

	public static void removeTarget(LogTarget tgt) {
		synchronized (targets) {
			targets.remove(tgt);
		}
	}

	/**
	 * Once you've called this you won't see anything logged anywhere until you
	 * create a new {@link LogTarget} and add it via
	 * {@link #addTarget(LogTarget)}
	 */
	public static void clearTargets() {
		synchronized (targets) {
			targets.clear();
		}
	}

}
