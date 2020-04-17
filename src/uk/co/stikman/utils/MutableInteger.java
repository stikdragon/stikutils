package uk.co.stikman.utils;

public class MutableInteger extends Number {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1850479282471646550L;
	private int					value				= 0;

	public MutableInteger(int value) {
		super();
		this.value = value;
	}

	public MutableInteger() {
		super();
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
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
		MutableInteger other = (MutableInteger) obj;
		if (value != other.value)
			return false;
		return true;
	}

	public int set(int newvalue) {
		int old = value;
		this.value = newvalue;
		return old;
	}

	public int increment() {
		return value++;
	}

	public int decrement() {
		return value--;
	}

	public int increment(int by) {
		return value += by;
	}

}
