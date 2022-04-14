package uk.co.stikman.table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DataRecord implements Iterable<Object> {

	private DataTable table;

	public DataRecord(DataTable table) {
		if (table == null)
			throw new IllegalArgumentException("Table cannot be null.");
		this.table = table;
	}

	public DataTable getTable() {
		return table;
	}

	public abstract Object getValue(int idx);

	public abstract void setValue(int idx, Object o);

	public void setValue(int idx, int n) {
		setValue(idx, Integer.valueOf(n));
	}

	public void setValue(int idx, double d) {
		setValue(idx, Double.valueOf(d));
	}

	public void setValue(String field, int n) {
		setValue(table.getField(field).getIndex(), n);
	}
	
	public void setValue(String field, double d) {
		setValue(table.getField(field).getIndex(), d);
	}
	
	public void setValue(String field, Object o) {
		setValue(table.getField(field).getIndex(), o);
	}
	
	public Object getValue(String field) {
		return getValue(table.getField(field).getIndex());
	}
	
	public abstract void setValues(Object... vals);

	public String getString(DataField f) {
		return getString(f.getIndex());
	}
	
	public int getInt(DataField f) {
		return getInt(f.getIndex());
	}
	
	public double getDouble(DataField f) {
		return getDouble(f.getIndex());
	}
	
	public String getString(int i) {
		Object o = getValue(i);
		if (o == null)
			return "";
		return o.toString();
	}

	public int getInt(int i) {
		Object o = getValue(i);
		if (o == null)
			return 0;
		if (o instanceof Number)
			return ((Number) o).intValue();
		String s = o.toString();
		return s.isEmpty() ? 0 : Integer.parseInt(s);
	}

	public double getDouble(int i) {
		Object o = getValue(i);
		if (o == null)
			return 0.0d;
		if (o instanceof Number)
			return ((Number) o).doubleValue();
		String s = o.toString();
		return s.isEmpty() ? 0.0d : Double.parseDouble(s);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (Object o : this) {
			sb.append(sep).append(o);
			sep = ", ";
		}
		return sb.toString();
	}

	public int getInt(String fld) {
		return getInt(table.getField(fld).getIndex());
	}

	public String getString(String fld) {
		return getString(table.getField(fld).getIndex());
	}

	public double getDouble(String fld) {
		return getDouble(table.getField(fld).getIndex());
	}

	/**
	 * Removes this record from its parent table. If it's not in a table, it
	 * does nothign
	 */
	public void remove() {
		if (table == null)
			return;
		table.remove(this);
		table = null;
	}

	/**
	 * Removes a value from the datarecord. If it's outside of the range then it
	 * does nothing
	 * 
	 * @param idx
	 */
	abstract void removeValue(int idx);

	/**
	 * This is an optional operation that has no effect on behavior whatsoever.
	 * It should be called after modifications to a record's data have been
	 * finished, and some implementations of <code>DataRecord</code> may use it
	 * as a indication they can tidy up memory internally.
	 * 
	 * Eg. when the 5th item was added to a datarecord it might have extended
	 * its array of values from 4 to 8, wasting 24 bytes of storage. Calling
	 * <code>tidy</code> lets it trim it back down to exactly the storage needed
	 */
	public void tidy() {

	}

	public void toStream(DataOutputStream dos) throws IOException {
		throw new RuntimeException("toStream not supported");
	}

	public void fromStream(DataInputStream dis) throws IOException {
		throw new RuntimeException("toStream not supported");
	}
	
	void changed() {
		table.recordChanged(this);
	}


}
