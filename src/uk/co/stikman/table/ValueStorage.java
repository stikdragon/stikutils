package uk.co.stikman.table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An ordered Set of Object. You can add an Object several times and it'll only
 * store one copy of it. When you add you get the index it was added at, or the
 * index of the one that's already there
 * 
 * @author frenchd
 *
 */
public class ValueStorage {
	public static final int			NO_ENTRY_VAL	= -1;

	private Map<Object, Integer>	map				= new HashMap<>();
	private List<Object>			list			= new ArrayList<>();

	public ValueStorage() {
		super();
	}

	/**
	 * Adds a value to the store and returns its index.
	 * 
	 * @param o
	 * @return
	 */
	public int add(Object o) {
		Integer idx = map.get(o);
		if (idx == null) {
			map.put(o, idx = Integer.valueOf(list.size()));
			list.add(o);
		}
		return idx.intValue();
	}

	/**
	 * Returns the {@link Object} associated with the given index. Will throw
	 * {@link IndexOutOfBoundsException} if invalid index
	 * 
	 * @param idx
	 * @return
	 */
	public Object get(int idx) {
		return list.get(idx);
	}

	public void clear() {
		list = new ArrayList<>();
		map = new HashMap<>();
	}

	public int size() {
		return list.size();
	}

	public void toStream(DataOutputStream dos) throws IOException {
		dos.writeInt(list.size());
		for (Object o : list)
			DTStreamUtil.writeObject(dos, o);
	}

	public void fromStream(DataInputStream dis) throws IOException {
		int n = dis.readInt();
		list = new ArrayList<>();
		map = new HashMap<>();
		while (n-- > 0) 
			list.add(DTStreamUtil.readObject(dis));
		
		//
		// rebuild the map
		//
		int idx = 0;
		for (Object o : list) 
			map.put(o, idx++);
	}
}
