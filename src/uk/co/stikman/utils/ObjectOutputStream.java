package uk.co.stikman.utils;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ObjectOutputStream implements Closeable {

	public static final int			OBJECT_LITERAL	= 0;
	public static final int			OBJECT_REF		= 1;
	public static final int			OBJECT_NULL		= 2;
	public static final byte		CLASS_LITERAL	= 3;
	public static final byte		CLASS_REF		= 4;

	private DataOutputStream		out;
	private Map<Streamable, Long>	objects			= new HashMap<>();
	private Map<Class<?>, Integer>	classes			= new HashMap<>();

	public ObjectOutputStream(OutputStream os) {
		this.out = new DataOutputStream(os);
	}

	/**
	 * Does not write class information, so you can only read the object back in
	 * if you already know what class it is
	 * 
	 * @param object
	 * @throws IOException
	 */
	public void xwriteObjectFixed(Streamable object) throws IOException {
		//
		// See if we've seen this object before
		//
		if (object == null) {
			out.writeByte(OBJECT_NULL);
			return;
		}
		Long id = objects.get(object);
		if (id == null) {
			//
			// Write object and tag it
			//
			out.writeByte(OBJECT_LITERAL);
			id = new Long(objects.size());
			out.writeLong(id);
			objects.put(object, id);
			object.toStream(this);
		} else {
			//
			// Reference to existing object
			//
			out.writeByte(OBJECT_REF);
			out.writeLong(id);
		}
	}

	public void writeObject(Streamable object) throws IOException {
		if (object == null) {
			out.writeByte(OBJECT_NULL);
			return;
		}
		Integer id = classes.get(object.getClass());
		if (id == null) {
			// 
			// Not seen before so write a record of it and cache
			//
			id = new Integer(classes.size());
			classes.put(object.getClass(), id);
			writeByte(CLASS_LITERAL);
			writeInt(id.intValue());
			writeString(object.getClass().getName());
		} else {
			writeByte(CLASS_REF);
			writeInt(id.intValue());
		}
		xwriteObjectFixed(object);
	}

	public void writeInt(int i) throws IOException {
		out.writeInt(i);
	}

	public void writeByte(int i) throws IOException {
		out.writeByte(i);
	}

	public void writeString(String s) throws IOException {
		if (s == null) {
			out.writeInt(-1);
			return;
		}
		byte[] data = s.getBytes("UTF-8");
		out.writeInt(data.length);
		out.write(data);
	}

	public void writeFloat(float f) throws IOException {
		out.writeFloat(f);
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	public void writeDouble(double z) throws IOException {
		out.writeDouble(z);
	}

	public void writeEnum(Enum<?> x) throws IOException {
		if (x == null)
			writeString(null);
		else
			writeString(x.name());
	}

}
