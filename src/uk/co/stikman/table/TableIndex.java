package uk.co.stikman.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TableIndex {

	private final String[]					fields;
	private final DataTable					owner;
	private Map<KeySet, List<DataRecord>>	map;

	public TableIndex(DataTable owner, String[] fields) {
		this.fields = fields;
		this.owner = owner;
	}

	public String[] getFields() {
		return fields;
	}

	public DataTable getOwner() {
		return owner;
	}

	public void rebuild() {
		Map<KeySet, List<DataRecord>> m = new HashMap<>();

		int n = fields.length;
		int[] positions = new int[n];
		int i = 0;
		for (String s : fields)
			positions[i++] = owner.findField(s).getIndex();
		KeySet key = new KeySet(n);
		for (DataRecord r : owner) {
			for (i = 0; i < n; ++i)
				key.keys[i] = r.getValue(positions[i]);
			List<DataRecord> lst = m.get(key);
			if (lst == null) {
				m.put(key, lst = new ArrayList<>());
				key = new KeySet(n);
			}
			lst.add(r);
		}
		map = m;
	}

	public void invalidate() {
		map = null;
	}

	public boolean usesField(DataField f) {
		for (String s : fields)
			if (f.getName().equals(s))
				return true;
		return false;
	}

	/**
	 * Always returns a List, but it might be empty
	 * 
	 * @param key
	 * @return
	 */
	public List<DataRecord> findBy(KeySet key) {
		List<DataRecord> res = map.get(key);
		if (res == null)
			return Collections.emptyList();
		return res;
	}

	public boolean isInvalid() {
		return map == null;
	}

}
