package uk.co.stikman.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StikDataOutputStream extends FilterOutputStream {

	private Charset	charset;
	private byte[]	tmp	= new byte[8];

	public StikDataOutputStream(OutputStream out) {
		this(out, Charset.forName("UTF-8"));
	}

	public StikDataOutputStream(OutputStream out, Charset charset) {
		super(out);
		this.charset = charset;
	}

	public void writeString(String s) throws IOException {
		byte[] ba = s.getBytes(charset);
		int v = ba.length;
		out.write((v >>> 24) & 0xFF);
		out.write((v >>> 16) & 0xFF);
		out.write((v >>> 8) & 0xFF);
		out.write((v >>> 0) & 0xFF);
		out.write(ba, 0, ba.length);
		out.flush();
	}

	public void writeShortString(String s) throws IOException {
		byte[] b = s.getBytes(charset);
		int len = b.length;
		if (len > 255)
			throw new IOException("Encoded string is too long for writeShortString");
		out.write(b, 0, len);
	}

	public void writeByte(byte n) throws IOException {
		out.write(n);
	}

	public void writeShort(short n) throws IOException {
		out.write((n >>> 8) & 0xFF);
		out.write((n >>> 0) & 0xFF);
	}

	public void writeInt(int n) throws IOException {
		out.write((n >>> 24) & 0xFF);
		out.write((n >>> 16) & 0xFF);
		out.write((n >>> 8) & 0xFF);
		out.write((n >>> 0) & 0xFF);
	}

	public void writeLong(long v) throws IOException {
		tmp[0] = (byte) (v >>> 56);
		tmp[1] = (byte) (v >>> 48);
		tmp[2] = (byte) (v >>> 40);
		tmp[3] = (byte) (v >>> 32);
		tmp[4] = (byte) (v >>> 24);
		tmp[5] = (byte) (v >>> 16);
		tmp[6] = (byte) (v >>> 8);
		tmp[7] = (byte) (v >>> 0);
		out.write(tmp, 0, 8);
	}

}
