package uk.co.stikman.utils;

import java.io.IOException;

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

}
