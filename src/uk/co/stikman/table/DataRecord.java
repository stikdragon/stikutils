package uk.co.stikman.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataRecord implements Serializable {

	private static final long	serialVersionUID	= -184894188287545964L;
	private DataTable			table;
	private List<Object>		values				= new ArrayList<>();

	public DataRecord() {

	}

	public DataRecord(DataTable table) {
		this.table = table;
	}

	public DataTable getTable() {
		return table;
	}

	public Object getValue(int idx) {
		if (idx >= values.size())
			return null;
		return values.get(idx);
	}

	public void setValue(int idx, Object o) {
		while (values.size() <= idx)
			values.add(null);
		values.set(idx, o);
	}

	public void setValue(int idx, int n) {
		setValue(idx, Integer.valueOf(n));
	}

	public void setValue(int idx, double d) {
		setValue(idx, Double.valueOf(d));
	}

	public void setValues(Object... vals) {
		for (Object o : vals)
			values.add(o);
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
		return ((Integer) o).intValue();
	}

	public double getDouble(int i) {
		Object o = getValue(i);
		if (o == null)
			return 0.0;
		return ((Double) o).doubleValue();

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (Object o : values) {
			sb.append(sep).append(o);
			sep = ", ";
		}
		return sb.toString();
	}

	public List<Object> getValues() {
		return values;
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

}
