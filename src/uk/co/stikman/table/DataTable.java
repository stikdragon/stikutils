package uk.co.stikman.table;

import static uk.co.stikman.utils.StreamUtil.readString;
import static uk.co.stikman.utils.StreamUtil.writeString;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * DataTables are not thread safe
 * 
 * @author frenchd
 *
 */
public class DataTable implements Iterable<DataRecord> {

	public interface FilterCondition {
		boolean test(DataRecord r);
	}

	private static final byte		VERSION_1		= 1;
	private static final byte		VERSION_2		= 2;

	private static final byte		DTMODE_STORAGE	= 1;
	private static final byte		DTMODE_SIMPLE	= 2;

	private List<DataRecord>		records			= new ArrayList<>();
	List<DataField>					fields			= new ArrayList<>();
	private Map<String, DataField>	fieldMap		= new HashMap<>();
	private ValueStorage			storage			= null;
	private Map<KeySet, TableIndex>	indexes			= new HashMap<>();

	public DataTable() {
		this(false);
	}

	public DataTable(boolean useStorage) {
		super();
		if (useStorage)
			storage = new ValueStorage();
	}

	public DataField addField(String name, DataType type) {
		return addField(name, type, null, -1);
	}

	public DataField addField(String name, DataType type, String display) {
		return addField(name, type, display, -1);
	}

	public DataField addField(String name, DataType type, int maxwidth) {
		return addField(name, type, null, maxwidth);
	}

	public DataField addField(String name, DataType type, String display, int maxwidth) {
		if (findField(name) != null)
			throw new DataTableException("Field " + name + " already exists");
		DataField df = new DataField(this, name);
		df.setIndex(fields.size());
		df.setType(type);
		df.setDisplay(display);
		df.setMaxDisplayWidth(maxwidth);
		if (type == DataType.STRING)
			df.setAggregateMethod(AggregateMethod.MAX);
		fields.add(df);
		fieldMap.put(df.getName(), df);
		return df;

		//
		// adding fields is one of the few things we do that doesn't require an index rebuild
		//
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

	public DataRecord addRecord(String... vals) {
		DataRecord r = addRecord();
		int i = 0;
		for (String s : vals)
			r.setValue(i++, s);
		invalidateIndexes();
		return r;
	}

	private void invalidateIndexes() {
		indexes.values().forEach(x -> x.invalidate());
	}

	public DataRecord addRecord() {
		DataRecord rec;
		if (storage == null)
			rec = new DataRecordLocal(this);
		else
			rec = new DataRecordStorage(this, storage);
		records.add(rec);
		invalidateIndexes();
		return rec;
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

	public static String padString(String s, char ch, int n, Alignment align) {
		if (s.length() >= n)
			return s;
		char[] x = new char[n];
		char[] src = s.toCharArray();
		int a;
		switch (align) {
			case CENTRE:
				a = (n - src.length) / 2;
				int b = src.length + a;
				for (int i = 0; i < n; ++i)
					x[i] = (i < a || i >= b) ? ch : src[i - a];
				break;
			case LEFT:
				for (int i = 0; i < n; ++i)
					x[i] = i < src.length ? src[i] : ch;
				break;
			case RIGHT:
				a = n - src.length;
				for (int i = 0; i < n; ++i)
					x[i] = i < a ? ch : src[i - a];
				break;
		}

		return new String(x);
	}

	@Override
	public String toString() {
		return toString(true, Integer.MAX_VALUE);
	}

	public String toString(boolean header, int firstNrows) {
		int[] widths = new int[fields.size()];
		Alignment[] alignments = new Alignment[fields.size()];
		for (int i = 0; i < widths.length; ++i) {
			widths[i] = fields.get(i).getActualFieldDisplay().length();
			alignments[i] = fields.get(i).getAlignment();
		}
		int cnt = 0;
		for (DataRecord r : this) {
			if (cnt++ > firstNrows)
				break;
			for (int i = 0; i < widths.length; ++i)
				widths[i] = Math.max(widths[i], r.getString(i).length());
		}

		//
		// Apply maximum field widths
		//
		int k = 0;
		for (DataField df : fields) {
			if (df.getMaxDisplayWidth() != -1 && df.getMaxDisplayWidth() < widths[k])
				widths[k] = df.getMaxDisplayWidth();
			++k;
		}

		StringBuilder sb = new StringBuilder();
		if (header) {
			int j = 0;
			for (DataField fld : fields)
				sb.append(makeCell(fld.getActualFieldDisplay(), widths[j++], fld.getAlignment())).append("  ");
			sb.append("\n");
			for (int i = 0; i < widths.length; ++i)
				sb.append(padString("", '=', widths[i], alignments[i])).append("  ");
			sb.append("\n");
		}
		cnt = 0;
		for (DataRecord r : this) {
			if (cnt++ > firstNrows)
				break;
			for (int i = 0; i < widths.length; ++i)
				sb.append(makeCell(r.getString(i), widths[i], alignments[i])).append("  ");
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Forces <code>s</code> to be <code>width</code> characters wide, pads with
	 * a space character, trims excess with a "..."
	 * 
	 * @param s
	 * @param width
	 * @param alignment
	 * @return
	 */
	private String makeCell(String s, int width, Alignment align) {
		if (s.length() > width) {
			if (width < 3)
				s = s.substring(0, width);
			else
				s = s.substring(0, width - 3) + "...";
		}
		if (s.length() < width)
			return padString(s, ' ', width, align);
		return s;
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
				for (Object o : r)
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

	public boolean isEmpty() {
		return size() == 0;
	}

	public void saveCSV(String file) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			CSVExporter csv = new CSVExporter();
			csv.setEncoding(StandardCharsets.UTF_8);
			csv.export(this, fos);
		}
	}

	public DataRecord get(int idx) {
		return records.get(idx);
	}

	/**
	 * Finds the first {@link DataRecord} that has the given value in
	 * <code>field</code>. Returns <code>null</code> if not found. If an index
	 * is present then it uses that
	 * 
	 * @param field
	 * @param value
	 * @return <code>null</code> if not found
	 */
	private final KeySet key0 = new KeySet(1);

	/**
	 * <p>
	 * Finds a record with a specified field value. If there's more than one
	 * record then it returns the first one it comes to.
	 * <p>
	 * If indexes are present on the table, then it uses them. See the doc for
	 * {@link #createIndex(String...)}
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataRecord findRecord(int field, String value) {
		TableIndex idx = null;
		if (!indexes.isEmpty()) {
			key0.keys[0] = fields.get(field).getName();
			idx = indexes.get(key0);
		}

		if (idx == null) {
			for (DataRecord rec : records) {
				String x = rec.getString(field);
				if ((value == null && x == null) || (value.equals(x)))
					return rec;
			}
			return null;
		} else {
			if (idx.isInvalid())
				idx.rebuild();
			key0.keys[0] = value;
			List<DataRecord> lst = idx.findBy(key0);
			if (lst.isEmpty())
				return null;
			return lst.get(0);
		}
	}

	/**
	 * See {@link #findRecord(int, String)}
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataRecord findRecord(String field, String value) {
		return findRecord(findField(field).getIndex(), value);
	}

	public int getFieldCount() {
		return fields.size();
	}

	public DataField getField(int idx) {
		return fields.get(idx);
	}

	public void addFields(String... names) {
		for (String s : names)
			addField(s);
	}

	public void sortOnIntegerField(int idx) {
		records.sort((a, b) -> b.getInt(idx) - a.getInt(idx));
		invalidateIndexes();
	}

	public void sortOnStringField(int idx) {
		records.sort((a, b) -> a.getString(idx).compareTo(b.getString(idx)));
		invalidateIndexes();
	}

	public void sort(Comparator<DataRecord> comparator) {
		records.sort(comparator);
		invalidateIndexes();
	}

	/**
	 * Attempts to remove the specified record, returns <code>true</code> if it
	 * was removed, <code>false</code> if it didn't exist
	 * 
	 * @param rec
	 * @return
	 */
	public boolean remove(DataRecord rec) {
		boolean b = records.remove(rec);
		if (b)
			invalidateIndexes();
		return b;
	}

	/**
	 * Removes all records
	 */
	public void clear() {
		records.clear();
		invalidateIndexes();
	}

	/**
	 * Removes everything in the list, and returns how many records were
	 * actually removed. If there's a {@link DataRecord} in the {@link List}
	 * that doesn't exist in this table then it's ignored
	 * 
	 * @param records
	 * @return
	 */
	public int removeAll(List<DataRecord> lst) {
		Set<DataRecord> set = new HashSet<>(lst);
		int cnt = 0;
		ListIterator<DataRecord> iter = records.listIterator();
		while (iter.hasNext()) {
			DataRecord r = iter.next();
			if (set.contains(r)) {
				iter.remove();
				++cnt;
			}
		}
		if (cnt > 0)
			invalidateIndexes();
		return cnt;
	}

	/**
	 * Renames a field. This will fail if the new field name would make it
	 * non-unique
	 * 
	 * @param f
	 * @param newname
	 */
	public void renameField(DataField field, String newname) {
		if (field == null)
			throw new NullPointerException("Field must be provided");
		if (newname == null)
			throw new NullPointerException("New name cannot be null");
		if (field.getName().equals(newname)) // trying to rename it to itself, ignore
			return;
		if (this != field.getTable())
			throw new DataTableException("The field [" + field + "] is not part of this DataTable");

		//
		// check if it's involved in any indexes
		//
		if (fieldInUseByIndex(field))
			throw new DataTableException("Field [" + field + "] is currently part of an index on this DataTable and cannot be renamed.  Remove the indexes first");

		DataField f = findField(newname);
		if (f != null)
			throw new DataTableException("Cannot rename field [" + field.getName() + "] to [" + newname + "] beacuse the new field name already exists");
		fieldMap.remove(field.getName());
		field.setName(newname);
		fieldMap.put(field.getName(), field);
	}

	private boolean fieldInUseByIndex(DataField f) {
		int c = 0;
		for (TableIndex idx : indexes.values())
			if (idx.usesField(f))
				++c;
		return c > 0;
	}

	public void removeField(DataField field) {
		if (field == null)
			throw new NullPointerException("Field must be provided");
		if (this != field.getTable())
			throw new DataTableException("The field is not part of this DataTable");

		//
		// check if it's involved in any indexes
		//
		if (fieldInUseByIndex(field))
			throw new DataTableException("Field [" + field + "] is currently part of an index on this DataTable and cannot be renamed.  Remove the indexes first");

		int idx = field.getIndex();

		//
		// need to remove this value from any records that have it
		//
		for (DataRecord r : this)
			r.removeValue(idx);
		fields.remove(field);
		fieldMap.remove(field.getName());

		//
		// Re-index the remaining fields
		//
		idx = 0;
		for (DataField df : fields)
			df.setIndex(idx++);
	}

	ValueStorage getStorage() {
		return storage;
	}

	public void toStream(OutputStream os) throws IOException {
		os.write(VERSION_1);

		if (storage != null) {
			os.write(DTMODE_STORAGE);
		} else {
			os.write(DTMODE_SIMPLE);
		}
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeInt(fields.size());
			for (DataField f : fields) {
				writeString(dos, f.getName());
				writeString(dos, f.getDisplay());
				writeString(dos, f.getType() == null ? null : f.getType().name());
				writeString(dos, f.getAggregateMethod() == null ? null : f.getAggregateMethod().name());
				dos.writeInt(f.getIndex());
				dos.writeInt(f.getMaxDisplayWidth());
			}

			if (storage != null)
				storage.toStream(dos);

			dos.writeInt(records.size());
			for (DataRecord r : records)
				r.toStream(dos);

			//
			// We don't save the actual indexes, just their field list.  They should
			// be rebuilt when we load the stream back in
			//
			dos.writeInt(indexes.size());
			for (TableIndex idx : indexes.values()) {
				dos.writeInt(idx.getFields().length);
				for (String s : idx.getFields())
					writeString(dos, s);
			}
		}
	}

	public void fromStream(InputStream is) throws IOException {
		int n = is.read();
		switch (n) {
			case VERSION_1:
				try (DataInputStream dis = new DataInputStream(is)) {
					readV1(dis);
				}
				break;
			case VERSION_2:
				try (DataInputStream dis = new DataInputStream(is)) {
					readV2(dis);
				}
				break;

			default:
				throw new IOException("Stream version is not supported: " + n);
		}
	}

	private void readV2(DataInputStream dis) throws IOException {
		readV1(dis);

		//
		// Indexes are new
		//
		int n = dis.readInt();
		while (n-- > 0) {
			int cnt = dis.readInt();
			String[] flds = new String[cnt];
			while (cnt-- > 0)
				flds[cnt] = readString(dis);
			TableIndex idx = new TableIndex(this, flds);
			KeySet keyset = new KeySet(flds.length);
			int i = 0;
			for (String s : flds)
				keyset.keys[i++] = s;
			indexes.put(keyset, idx);
		}

		indexes.values().forEach(x -> x.rebuild());
	}

	private void readV1(DataInputStream dis) throws IOException {
		int mode = dis.read();
		if (mode != DTMODE_SIMPLE && mode != DTMODE_STORAGE)
			throw new IOException("Invalid mode: " + mode);

		int cnt = dis.readInt();
		while (cnt-- > 0) {
			DataField df = new DataField(this, readString(dis));
			df.setDisplay(readString(dis));
			df.setType(DataType.valueOf(readString(dis)));
			df.setAggregateMethod(AggregateMethod.valueOf(readString(dis)));
			df.setIndex(dis.readInt());
			df.setMaxDisplayWidth(dis.readInt());
			fields.add(df);
		}

		if (mode == DTMODE_STORAGE) {
			storage = new ValueStorage();
			storage.fromStream(dis);

			cnt = dis.readInt();
			while (cnt-- > 0) {
				DataRecord r = new DataRecordStorage(this, storage);
				r.fromStream(dis);
				records.add(r);
			}
		} else {
			cnt = dis.readInt();
			while (cnt-- > 0) {
				DataRecord r = new DataRecordLocal(this);
				r.fromStream(dis);
				records.add(r);
			}
		}
	}

	/**
	 * <p>
	 * Indexes will speed up the behaviour of {@link #findRecord(int, String)}
	 * considerably by creating a {@link HashMap} based lookup for each record.
	 * Indexes are invalidated on record changes/adds/deletes, and are rebuild
	 * when needed, so calling {@link #findRecord(int, String)} will be slow the
	 * first time.
	 * 
	 * <p>
	 * Indexes only really make sense on large tables (>50 records), although
	 * the performance hit on a small table is likely to be so small that you
	 * don't care. They also don't make sense for a table that has a lot of
	 * write operations, since that will constantly invalidate the index
	 * 
	 * <p>
	 * You can have multiple indexes, but not on the same set of fields. The
	 * most appropriate index for the situation will be used, if present.
	 * <code>findRecord</code> will still work fine without any indexes, but it
	 * will fall back to a linear search instead of close to O(1)
	 * 
	 * <p>
	 * Beware that once you've created an index you can't remove or rename
	 * fields that are a part of it
	 * 
	 * <p>
	 * Adding an index to a table will significantly increase its memory usage.
	 * For extremely large tables you should consider alternatives
	 * 
	 * @param fields
	 */
	public void createIndex(String... fields) {
		KeySet key = new KeySet(fields.length);
		int i = 0;
		for (String s : fields)
			key.keys[i++] = s;
		if (indexes.containsKey(key))
			throw new IllegalStateException("Index [" + fields + "] already exists");

		TableIndex idx = new TableIndex(this, fields);
		idx.rebuild();
		indexes.put(key, idx);
	}

	public void clearIndexes() {
		indexes.clear();
	}

	void recordChanged(DataRecord r) {
		invalidateIndexes();
	}

}
