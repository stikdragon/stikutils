package uk.co.stikman.table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import uk.co.stikman.utils.IntList;

public class DataRecordStorage extends DataRecord {

	private ValueStorage	storage;
	private IntList	indexes	= new IntList();

	public DataRecordStorage(DataTable table, ValueStorage storage) {
		super(table);
		this.storage = storage;
	}

	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {
			private int idx = 0;

			@Override
			public boolean hasNext() {
				return idx < indexes.size();
			}

			@Override
			public Object next() {
				return getValue(idx++);
			}
		};
	}

	@Override
	public Object getValue(int idx) {
		return storage.get(indexes.get(idx));
	}

	@Override
	public void setValue(int idx, Object o) {
		while (indexes.size() <= idx)
			indexes.add(-1);
		indexes.set(idx, storage.add(o));
		changed();
	}

	@Override
	public void setValues(Object... vals) {
		indexes = new IntList();
		for (Object o : vals)
			indexes.add(storage.add(o));
		changed();
	}

	@Override
	void removeValue(int idx) {
		indexes.remove(idx);
		changed();
	}


	@Override
	public void tidy() {
	}

	@Override
	public void toStream(DataOutputStream dos) throws IOException {
		dos.writeInt(indexes.size());
		for (int i = 0; i < indexes.size(); ++i)
			dos.writeInt(indexes.get(i));
	}

	@Override
	public void fromStream(DataInputStream dis) throws IOException {
		indexes = new IntList();
		int n = dis.readInt();
		while (n-- > 0)
			indexes.add(dis.readInt());
	}

}
