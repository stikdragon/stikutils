package uk.co.stikman.utils;

/**
 * In milliseconds
 * 
 * @author Stik
 *
 */
public class TimeThinger {
	private long	period;
	private long	timer;
	private boolean	enabled		= true;
	private boolean	autoReset	= true;
	private long	lastGo;

	/**
	 * In milliseconds
	 * 
	 * @param period
	 */
	public TimeThinger(long period) {
		super();
		this.period = period;
		this.timer = period;
		lastGo = System.currentTimeMillis();
	}

	public boolean go(long dt) {
		if (!enabled)
			return false;
		timer -= dt;
		if (timer < 0) {
			if (autoReset)
				timer += period;
			return true;
		}
		return false;
	}

	public void reset() {
		timer = period;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAutoReset() {
		return autoReset;
	}

	public void setAutoReset(boolean autoReset) {
		this.autoReset = autoReset;
	}

	/**
	 * This one will track its own dt
	 */
	public boolean go() {
		long dt = System.currentTimeMillis() - lastGo;
		lastGo = System.currentTimeMillis();
		return go(dt);
	}
	
	public long getPeriod() {
		return period;
	}
}
