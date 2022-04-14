package uk.co.stikman.utils;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class ObjectInputStream implements Closeable {

	private DataInputStream								in;
	private int											version;
	private Map<Long, Streamable>						objects	= new HashMap<>();
	private Map<Integer, Class<? extends Streamable>>	classes	= new HashMap<>();

	public ObjectInputStream(InputStream in, int version) {
		super();
		this.in = new DataInputStream(in);
		this.version = version;
	}

	public int readInt() throws IOException {
		return in.readInt();
	}

	public float readFloat() throws IOException {
		return in.readFloat();
	}

	@SuppressWarnings("unchecked")
	public <T extends Streamable> T xreadObjectByType(Class<T> clazz) throws IOException {
		try {
			byte b = in.readByte();
			if (b == ObjectOutputStream.OBJECT_NULL)
				return null;
			Long id = Long.valueOf(in.readLong());
			if (b == ObjectOutputStream.OBJECT_LITERAL) {
				T x = clazz.getConstructor().newInstance();
				objects.put(id, x);
				x.fromStream(this, version);
				return x;
			} else if (b == ObjectOutputStream.OBJECT_REF) {
				return (T) objects.get(id);
			} else
				throw new IOException("Invalid stream");

		} catch (Exception e) {
			throw new IOException("Failed to construct instance of " + clazz.getName() + " (does it have a visible no-arg constructor?)", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Streamable> T readObject() throws IOException {
		byte b = in.readByte();
		if (b == ObjectOutputStream.OBJECT_NULL)
			return null;

		Class<T> cls;
		Integer id = Integer.valueOf(in.readInt());
		if (b == ObjectOutputStream.CLASS_LITERAL) {
			String name = readString();
			try {
				cls = (Class<T>) Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new IOException("Failed to construct an object from stream", e);
			}
			classes.put(id, cls);
		} else if (b == ObjectOutputStream.CLASS_REF) {
			cls = (Class<T>) classes.get(id);
		} else
			throw new IOException("Invalid stream");

		return xreadObjectByType(cls);
	}

	public String readString() throws IOException {
		int length = in.readInt();
		if (length == -1)
			return null;
		byte[] data = new byte[length];
		in.readFully(data);
		return new String(data, "UTF-8");
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public byte readByte() throws IOException {
		return in.readByte();
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double readDouble() throws IOException {
		return in.readDouble();
	}

}
