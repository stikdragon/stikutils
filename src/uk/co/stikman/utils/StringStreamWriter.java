package uk.co.stikman.utils;

import uk.co.stikman.utils.math.Vector3;

public class StringStreamWriter {

	private StringBuilder	sb	= new StringBuilder();

	public void writeString(String s) {
		writeInt(s.length());
		sb.append(s);
	}

	public void writeInt(int n) {
		sb.append(Integer.toString(n, 16)).append(",");
	}

	public void writeFloat(float f) {
		writeInt(Float.floatToRawIntBits(f));
	}

	public void writeVec3(Vector3 v) {
		writeDouble(v.x);
		writeDouble(v.y);
		writeDouble(v.z);
	}

	public void writeDouble(double z) {
		writeLong(Double.doubleToRawLongBits(z));
	}

	public void writeLong(long n) {
		sb.append(Long.toString(n, 16)).append(",");
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
