package uk.co.stikman.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import uk.co.stikman.utils.math.Vector3;

public class StreamUtil {

	public static void writeVector(ObjectOutputStream os, Vector3 v) throws IOException {
		os.writeFloat(v.x);
		os.writeFloat(v.y);
		os.writeFloat(v.z);
	}

	public static void readVector(ObjectInputStream is, Vector3 v) throws IOException {
		v.x = is.readFloat();
		v.y = is.readFloat();
		v.z = is.readFloat();
	}

	public static String readString(DataInputStream is) throws IOException {
		return readString(is, StandardCharsets.UTF_8);

	}

	public static String readString(DataInputStream is, Charset cs) throws IOException {
		int n = is.readInt();
		if (n == -1)
			return null;
		if (n == 0)
			return "";
		byte[] b = new byte[n];
		is.readFully(b);
		return new String(b, cs);
	}

	public static void writeString(DataOutputStream os, String s) throws IOException {
		writeString(os, s, StandardCharsets.UTF_8);

	}

	public static void writeString(DataOutputStream os, String s, Charset cs) throws IOException {
		if (s == null) {
			os.writeInt(-1);
			return;
		} else if (s.isEmpty()) {
			os.writeInt(0);
		} else {
			byte[] b = s.getBytes(cs);
			os.writeInt(b.length);
			os.write(b);
		}
	}
}
