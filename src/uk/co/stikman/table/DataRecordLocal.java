package uk.co.stikman.table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class DataRecordLocal extends DataRecord {
	private ArrayList<Object> values = new ArrayList<>();

	public DataRecordLocal(DataTable dt) {
		super(dt);
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
		changed();
	}

	public void setValues(Object... vals) {
		values = new ArrayList<>(vals.length);
		for (Object o : vals)
			values.add(o);
		changed();
	}

	@Override
	public Iterator<Object> iterator() {
		return values.iterator();
	}

	@Override
	void removeValue(int idx) {
		if (values.size() > idx) {
			values.remove(idx);
			changed();
		}
	}

	@Override
	public void tidy() {
		values.trimToSize();
	}

	@Override
	public void toStream(DataOutputStream dos) throws IOException {
		dos.writeInt(values.size());
		for (Object o : values) 
			DTStreamUtil.writeObject(dos, o);
	}

	@Override
	public void fromStream(DataInputStream dis) throws IOException {
		int n = dis.readInt();
		values = new ArrayList<>();
		while (n-- > 0) 
			values.add(DTStreamUtil.readObject(dis));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + values.hashCode();
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
		DataRecordLocal other = (DataRecordLocal) obj;
		if (!values.equals(other.values))
			return false;
		return true;
	}

}
