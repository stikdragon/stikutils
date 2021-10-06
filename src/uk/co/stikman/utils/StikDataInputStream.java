package uk.co.stikman.utils;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;

public class StikDataInputStream extends FilterInputStream {

	private static final int	DEFAULT_MAX_LEN	= 1024 * 1024;
	private int					maxLen			= DEFAULT_MAX_LEN;
	private Charset				charset;
	private byte				tmp[]			= new byte[8];

	public StikDataInputStream(InputStream in, int maxLen) {
		this(in, Charset.forName("UTF-8"), maxLen);
	}

	public StikDataInputStream(InputStream in) {
		this(in, Charset.forName("UTF-8"), DEFAULT_MAX_LEN);
	}

	public StikDataInputStream(InputStream in, Charset charset) {
		this(in, charset, DEFAULT_MAX_LEN);
	}

	public StikDataInputStream(InputStream in, Charset charset, int maxLen) {
		super(in);
		this.maxLen = maxLen;
		this.charset = charset;
	}

	public String readString() throws IOException {
		int len = readInt();
		if (len > maxLen)
			throw new StreamCorruptedException("String in stream is larger than maxLen");

		byte[] buf = new byte[len];
		readFully(buf, 0, len);
		return new String(buf, charset);
	}

	/**
	 * See the general contract of the <code>readFully</code> method of
	 * <code>DataInput</code>.
	 * <p>
	 * Bytes for this operation are read from the contained input stream.
	 *
	 * @param b
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset of the data.
	 * @param len
	 *            the number of bytes to read.
	 * @exception EOFException
	 *                if this input stream reaches the end before reading all
	 *                the bytes.
	 * @exception IOException
	 *                the stream has been closed and the contained input stream
	 *                does not support reading after close, or another I/O error
	 *                occurs.
	 * @see java.io.FilterInputStream#in
	 */
	private final void readFully(byte b[], int off, int len) throws IOException {
		if (len < 0)
			throw new IndexOutOfBoundsException();
		int n = 0;
		while (n < len) {
			int count = in.read(b, off + n, len - n);
			if (count < 0)
				throw new EOFException();
			n += count;
		}
	}

	public int readInt() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	public long readLong() throws IOException {
		readFully(tmp, 0, 8);
		return (((long) tmp[0] << 56) + ((long) (tmp[1] & 255) << 48) + ((long) (tmp[2] & 255) << 40) + ((long) (tmp[3] & 255) << 32) + ((long) (tmp[4] & 255) << 24) + ((tmp[5] & 255) << 16) + ((tmp[6] & 255) << 8) + ((tmp[7] & 255) << 0));
	}

	public int readByte() throws IOException {
		int x = in.read();
		if (x == -1)
			throw new EOFException();
		return x;
	}

	public short readShort() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	public String readShortString() throws IOException {
		int len = readShort();
		byte[] buf = new byte[len];
		readFully(buf, 0, len);
		return new String(buf, charset);
	}

	public byte[] readBytes(int len) throws IOException {
		byte[] res = new byte[len];
		return readBytes(len, res);
	}

	public byte[] readBytes(int len, byte[] res) throws IOException {
		readFully(res, 0, len);
		return res;
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public int getMaxLen() {
		return maxLen;
	}

	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}

}
