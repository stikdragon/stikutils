package uk.co.stikman.utils;

import java.io.ByteArrayInputStream;

import uk.co.stikman.utils.Utils.HexConfig;
import uk.co.stikman.utils.Utils.HexMode;

/**
 * version of {@link ByteArrayInputStream} that exposes the internal array
 * 
 * @author stikd
 *
 */
public class StikByteArrayInputStream extends ByteArrayInputStream {

	public StikByteArrayInputStream(byte[] buf) {
		super(buf);
	}

	public byte[] getArray() {
		return buf;
	}

	public int getPosition() {
		return pos;
	}

	@Override
	public String toString() {
		HexConfig cnf = new HexConfig(HexMode.HEX_SYMBOLS, 512, 32);
		return Utils.formatBytes(buf, 0, buf.length, cnf);
	}

	
	
}
