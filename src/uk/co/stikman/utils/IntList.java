package uk.co.stikman.utils;

public class IntList {
	public interface IntListConsumer {
		void go(int n);
	}

	public int[]	list;
	private int		size;
	private int		increment	= 50;

	public IntList() {
		list = new int[increment];
		size = 0;
	}

	public IntList(int prealloc) {
		if (prealloc < increment)
			prealloc = increment;
		list = new int[prealloc];
		size = 0;
	}

	public final void add(int val) {
		set(size, val);
	}

	
	public void add(int[] data, int off, int len) {
		int n = size + len;
		if (n >= list.length) {
			n = list.length + len + increment;
			int[] tmp = new int[n];
			System.arraycopy(list, 0, tmp, 0, size);
			list = tmp;
		}
		
		System.arraycopy(data, off, list, size, len);
		size += len;
	}
	
	public final void set(int index, int val) {
		if (index >= list.length) {
			int[] tmp = new int[index + 1 + increment];
			System.arraycopy(list, 0, tmp, 0, size);
			list = tmp;
		}
		if (index >= size) {
			int n = size;
			while (n < index) {
				list[n] = 0;
				++n;
			}
			size = index + 1;
		}
		list[index] = val;
	}

	public final int get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(Integer.toString(index));
		return list[index];
	}

	public void remove(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(Integer.toString(index));
		--size;
		for (int i = index; i < size; ++i)
			list[i] = list[i + 1];
	}

	public final int size() {
		return size;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public void clear() {
		size = 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			if (i == size - 1)
				sb.append(list[i]);
			else
				sb.append(list[i]).append(", ");
		}
		return sb.toString();
	}

	public int last() {
		if (size == 0)
			throw new IndexOutOfBoundsException("List is empty");
		return list[size - 1];
	}

	/**
	 * 
	 * allocates space for this log
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		if (size <= this.size) {
			this.size = size;
		} else {
			set(size - 1, 0);
		}
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int removeLast() {
		if (size < 1)
			throw new IndexOutOfBoundsException("No more elements");
		--size;
		return list[size];
	}

	/**
	 * This returns the backing array, which will be longer than the number of
	 * elements in this list! Make sure you don't use <code>array.length</code>,
	 * instead look at the {@link #size()} method
	 * 
	 * @return
	 */
	public int[] toArray() {
		return list;
	}

	/**
	 * Returns a copy of the backing array, sized correctly
	 * 
	 * @return
	 */
	public int[] toSizedArray() {
		int[] res = new int[size];
		System.arraycopy(list, 0, res, 0, size);
		return res;
	}

	public void forEach(IntListConsumer consumer) {
		for (int i = 0; i < size; ++i)
			consumer.go(list[i]);
	}


}