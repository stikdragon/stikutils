package uk.co.stikman.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataTable implements Serializable, Iterable<DataRecord> {
	private static final long	serialVersionUID	= 1L;

	public interface FilterCondition {
		boolean test(DataRecord r);
	}

	private List<DataRecord>	records	= new ArrayList<>();
	List<DataField>				fields	= new ArrayList<>();

	public DataTable() {

	}

	public DataField addField(String name, DataType type) {
		if (findField(name) != null)
			throw new DataTableException("Field " + name + " already exists");
		DataField df = new DataField(this, name);
		df.setIndex(fields.size());
		df.setType(type);
		if (type == DataType.STRING)
			df.setAggregateMethod(AggregateMethod.MAX);
		fields.add(df);
		return df;
	}

	public DataField addField(String name) {
		return addField(name, DataType.DOUBLE);
	}

	/**
	 * REturns <code>null</code> if not found
	 * 
	 * @param name
	 * @return
	 */
	public DataField findField(String name) {
		for (DataField df : fields)
			if (df.getName().equals(name))
				return df;
		return null;
	}

	/**
	 * Throws a {@link DataTableException} if not found
	 * 
	 * @param name
	 * @return
	 */
	public DataField getField(String name) {
		for (DataField df : fields)
			if (df.getName().equals(name))
				return df;
		throw new DataTableException("Field " + name + " not found");
	}

	public DataRecord addRecord() {
		DataRecord rec = new DataRecord(this);
		records.add(rec);
		return rec;
	}

	public static class KeySet implements Serializable {
		private static final long	serialVersionUID	= 6209428947027102486L;
		private Object[]			keys;

		public KeySet() {
		}

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
	}

	@SuppressWarnings("incomplete-switch")
	public DataTable aggregate(Set<DataField> keys) {
		DataTable res = new DataTable();
		Set<DataField> vals = new HashSet<>();
		for (DataField fld : fields)
			if (!keys.contains(fld))
				vals.add(fld);
		Map<KeySet, List<DataRecord>> map = new HashMap<>();
		for (DataRecord rec : this) {
			KeySet ks = new KeySet(keys.size());
			int j = 0;
			for (DataField df : keys)
				ks.set(j++, rec.getValue(df.getIndex()));
			List<DataRecord> lst = map.get(ks);
			if (lst == null) {
				lst = new ArrayList<DataRecord>();
				map.put(ks, lst);
			}
			lst.add(rec);
		}

		for (DataField fld : fields) {
			DataField copy = res.addField(fld.getName());
			copy.setType(fld.getType());
		}

		// add up...
		for (Entry<KeySet, List<DataRecord>> e : map.entrySet()) {
			DataRecord output = res.addRecord();
			List<DataRecord> lst = e.getValue();
			DataRecord first = lst.get(0);
			for (DataField f : keys)
				output.setValue(f.getIndex(), first.getValue(f.getIndex()));
			for (DataField f : vals) {
				switch (f.getType()) {
					case DOUBLE:
						double d = first.getDouble(f.getIndex());
						for (int idx = 1; idx < lst.size(); ++idx) {
							DataRecord rec = lst.get(idx);
							switch (f.getAggregateMethod()) {
								case AVG:
								case SUM:
									d += rec.getDouble(f.getIndex());
									break;
								case MAX:
									d = Math.max(d, rec.getDouble(f.getIndex()));
									break;
								case MIN:
									d = Math.min(d, rec.getDouble(f.getIndex()));
									break;
							}
						}
						if (f.getAggregateMethod() == AggregateMethod.AVG)
							d /= lst.size();
						if (f.getAggregateMethod() == AggregateMethod.NONE)
							d = 0.0;
						output.setValue(f.getIndex(), d);
						break;
					case INT:
						int n = first.getInt(f.getIndex());
						for (int idx = 1; idx < lst.size(); ++idx) {
							DataRecord rec = lst.get(idx);
							switch (f.getAggregateMethod()) {
								case AVG:
								case SUM:
									n += rec.getInt(f.getIndex());
									break;
								case MAX:
									n = Math.max(n, rec.getInt(f.getIndex()));
									break;
								case MIN:
									n = Math.min(n, rec.getInt(f.getIndex()));
									break;
							}
						}
						if (f.getAggregateMethod() == AggregateMethod.AVG)
							n = (int) ((double) n) / lst.size();
						if (f.getAggregateMethod() == AggregateMethod.NONE)
							n = 0;
						output.setValue(f.getIndex(), n);
						break;
					case STRING:
						switch (f.getAggregateMethod()) {
							case MAX: {
								String s = first.getString(f.getIndex());
								for (int idx = 1; idx < lst.size(); ++idx) {
									DataRecord rec = lst.get(idx);
									String t = rec.getString(f.getIndex());
									if (t.compareTo(s) > 0)
										s = t;
								}
								output.setValue(f.getIndex(), s);
								break;
							}
							case MIN: {
								String s = first.getString(f.getIndex());
								for (int idx = 1; idx < lst.size(); ++idx) {
									DataRecord rec = lst.get(idx);
									String t = rec.getString(f.getIndex());
									if (t.compareTo(s) < 0)
										s = t;
								}
								output.setValue(f.getIndex(), s);
								break;
							}
							case NONE:
							case SUM:
							case AVG:
							default:
								break;
						}
						break;
					default:
						break;

				}
			}
		}

		return res;
	}

	@Override
	public Iterator<DataRecord> iterator() {
		return records.iterator();
	}

	@Override
	public String toString() {
		TextDataTableFormatter f = new TextDataTableFormatter();
		return f.format(this);
	}

	public DataTable aggregate(String... keyfields) {
		Set<DataField> keys = new HashSet<>();
		for (String s : keyfields) {
			DataField f = findField(s);
			if (f == null)
				throw new RuntimeException("Field " + s + " not found");
			keys.add(f);
		}
		return aggregate(keys);
	}

	public DataTable filter(FilterCondition filter) {
		DataTable res = new DataTable();
		for (DataField fld : fields) {
			DataField f = res.addField(fld.getName(), fld.getType());
			f.setAggregateMethod(fld.getAggregateMethod());
		}

		for (DataRecord r : this) {
			if (filter.test(r)) {
				DataRecord r2 = res.addRecord();
				int i = 0;
				for (Object o : r.getValues())
					r2.setValue(i++, o);
			}
		}

		return res;
	}

	/**
	 * Return number of records
	 * 
	 * @return
	 */
	public int size() {
		return records.size();
	}

	public DataField getFieldByIndex(int idx) {
		return fields.get(idx);
	}

	public int getFieldCount() {
		return fields.size();
	}

	public Collection<DataField> fields() {
		return Collections.unmodifiableCollection(fields);
	}

	public DataRecord get(int idx) {
		return records.get(idx);
	}
}
