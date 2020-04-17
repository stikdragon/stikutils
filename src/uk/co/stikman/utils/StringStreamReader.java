package uk.co.stikman.utils;

import java.io.IOException;

import uk.co.stikman.utils.math.Vector3;

public class StringStreamReader {
	private String	data;
	private int		ptr;
	private int		len;

	/**
	 * @param data
	 */
	public StringStreamReader(String data) {
		super();
		this.data = data;
		ptr = 0;
		len = data.length();
	}

	public String readString() throws IOException {
		int n = readInt();
		if (n + ptr > len)
			throw new IOException("End of stream");
		String s = data.substring(ptr, n + ptr);
		ptr += n;
		return s;
	}

	public int readInt() throws IOException {
		//
		// Read until a comma
		//
		int n = 0;
		while (n < 10) { // it can have a - on the front, making it 9 chars long
			if (n + ptr >= len)
				throw new IOException("End of stream");
			if (data.charAt(n + ptr) == ',') {
				int r = Integer.parseInt(data.substring(ptr, ptr + n), 16);
				ptr += n + 1;
				return r;
			}
			++n;
		}
		throw new IOException("Invalid stream, could not read integer");
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public void readVec3(Vector3 v) throws IOException {
		v.x = readFloat();
		v.y = readFloat();
		v.z = readFloat();
	}

	@Override
	public String toString() {
		return data;
	}

	public boolean atEnd() {
		return ptr == len;
	}

}
