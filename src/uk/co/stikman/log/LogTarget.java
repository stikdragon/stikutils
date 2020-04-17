package uk.co.stikman.log;

import java.util.EnumMap;

public abstract class LogTarget {
	private EnumMap<Level, Boolean>	enabled	= new EnumMap<>(Level.class);

	{
		enableAllLevels(true);
	}

	public abstract void log(LogEntry le);

	/**
	 * Returns <code>true</code> if the LogEntry passes any current filters on
	 * this target
	 * 
	 * @param le
	 * @return
	 */
	boolean filter(LogEntry le) {
		return enabled.get(le.getLevel());
	}

	public void enableLevel(Level l, boolean b) {
		enabled.put(l, b);
	}

	public void enableAllLevels(boolean b) {
		for (Level x : Level.values())
			enabled.put(x, b);
	}

}
