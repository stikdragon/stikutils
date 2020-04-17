package uk.co.stikman.table;
import java.util.Arrays;

public final class KeySet {
	public Object[] keys;

	KeySet(int size) {
		keys = new Object[size];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keys);
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
		KeySet other = (KeySet) obj;
		if (!Arrays.equals(keys, other.keys))
			return false;
		return true;
	}

	void set(int idx, Object obj) {
		keys[idx] = obj;
	}

	@Override
	public String toString() {
		return Arrays.toString(keys);
	}

}