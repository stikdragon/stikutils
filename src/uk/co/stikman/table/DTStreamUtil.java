package uk.co.stikman.table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.stikman.utils.StreamUtil;

class DTStreamUtil {

	public static void writeObject(DataOutputStream dos, Object o) throws IOException {
		if (o == null) {
			dos.writeByte(-1);
		} else if (o instanceof String) {
			dos.writeByte(0);
			StreamUtil.writeString(dos, (String) o);
		} else if (o instanceof Double) {
			dos.writeByte(1);
			dos.writeDouble((Double) o);
		} else if (o instanceof Float) {
			dos.writeByte(2);
			dos.writeFloat((Float) o);
		} else if (o instanceof Integer) {
			dos.writeByte(3);
			dos.writeInt((Integer) o);
		} else if (o instanceof Long) {
			dos.writeByte(4);
			dos.writeLong((Long) o);
		} else
			throw new IOException("Unsupported datatype for streaming: " + o.getClass());

	}

	public static Object readObject(DataInputStream dis) throws IOException {
		int q = dis.readByte();
		switch (q) {
			case -1:
				return null;
			case 0:
				return StreamUtil.readString(dis);
			case 1:
				return Double.valueOf(dis.readDouble());
			case 2:
				return Float.valueOf(dis.readFloat());
			case 3:
				return Integer.valueOf(dis.readInt());
			case 4:
				return Long.valueOf(dis.readLong());
			default:
				throw new IOException("Unsupported datatype for streaming: " + q);

		}
	}

}
