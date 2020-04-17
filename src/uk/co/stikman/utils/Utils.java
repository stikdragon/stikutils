package uk.co.stikman.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Utils extends GwtUtils {

	public static String hashPassword(String pass, String salt) {
		try {
			byte[] bsalt = new byte[16];
			byte[] src = salt.getBytes();
			for (int i = 0; i < bsalt.length; ++i) {
				if (i < src.length)
					bsalt[i] = src[i];
				else
					bsalt[i] = 0;
			}
			// TODO: cache this?
			KeySpec spec = new PBEKeySpec(pass.toCharArray(), bsalt, 65536, 128);
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			return new BigInteger(1, hash).toString(16);
		} catch (NoSuchAlgorithmException ex1) {
			throw new RuntimeException(ex1);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	//http://stackoverflow.com/questions/1086123/string-conversion-to-title-case
	public static String properCase(String s) {
		final String ACTIONABLE_DELIMITERS = " -/"; // these cause the character following to be capitalized
		StringBuilder sb = new StringBuilder();
		boolean capNext = true;
		for (char c : s.toCharArray()) {
			c = (capNext) ? Character.toUpperCase(c) : Character.toLowerCase(c);
			sb.append(c);
			capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
		}
		return sb.toString();
	}

	public static String readFully(InputStream inputStream, String encoding) throws IOException {
		return new String(readFully(inputStream), encoding);
	}

	private static byte[] readFully(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) != -1)
			baos.write(buffer, 0, length);
		return baos.toByteArray();
	}

	public static String formatBytes(byte[] buffer) {
		return formatBytes(buffer, 0, buffer.length, new HexConfig(HexMode.SYMBOLS_HEX, 256));
	}

	public static String formatBytes(byte[] buffer, int off, int len) {
		return formatBytes(buffer, 0, buffer.length, new HexConfig(HexMode.SYMBOLS_HEX, 256));
	}

	public enum HexMode {
		SYMBOLS,
		SYMBOLS_HEX,
		HEX_SYMBOLS,
		HEX
	}

	public static class HexConfig {
		private HexMode	mode;
		private int		maxLength;
		private int		linewidth	= -1;

		public HexMode getMode() {
			return mode;
		}

		public void setMode(HexMode mode) {
			this.mode = mode;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}

		/**
		 * if <code>-1</code> then renders as a single line, otherwise this is
		 * the linewidth
		 * 
		 * @return
		 */
		public int getLinewidth() {
			return linewidth;
		}

		/**
		 * if <code>-1</code> then renders as a single line, otherwise this is
		 * the linewidth
		 * 
		 * @return
		 */
		public void setLinewidth(int linewidth) {
			this.linewidth = linewidth;
		}

		public HexConfig(HexMode mode, int maxLength, int linewidth) {
			super();
			this.mode = mode;
			this.maxLength = maxLength;
			if (this.maxLength == -1)
				this.maxLength = Integer.MAX_VALUE;
			this.linewidth = linewidth;
		}

		public HexConfig(HexMode mode, int maxLength) {
			this(mode, maxLength, -1);
		}

	}

	public static String formatBytes(byte[] buffer, int off, int len, HexConfig config) {
		if (config.getLinewidth() != -1) {
			HexConfig config2 = new HexConfig(config.getMode(), config.getMaxLength(), -1);
			int lw = config.getLinewidth();
			StringBuilder sb = new StringBuilder();
			String sep = "";
			while (len > 0) {
				int n = Math.min(len, lw);
				sb.append(sep);
				sb.append(formatBytes(buffer, off, n, config2));
				len -= n;
				off += n;
				sep = "\n";
			}
			return sb.toString();
		}

		StringBuilder sb = new StringBuilder();

		if (buffer == null) {
			sb.append(" [null]");
		} else {
			int to = len + off;

			for (int pass = 0; pass < 3; ++pass) {

				if (pass == 1 && (config.getMode() == HexMode.HEX_SYMBOLS || config.getMode() == HexMode.SYMBOLS_HEX))
					sb.append(" ");

				if ((pass == 0 && (config.getMode() == HexMode.SYMBOLS || config.getMode() == HexMode.SYMBOLS_HEX)) || (pass == 2 && (config.getMode() == HexMode.HEX_SYMBOLS))) {
					int i = 0;
					for (int p = off; p < to; ++p) {
						byte b = buffer[p];
						if (b < 32 || b > 128)
							sb.append("·");
						else
							sb.append((char) b);
						if (++i > config.getMaxLength()) {
							sb.append("...");
							break;
						}
					}
				}

				if ((pass == 0 && (config.getMode() == HexMode.HEX || config.getMode() == HexMode.HEX_SYMBOLS)) || (pass == 2 && (config.getMode() == HexMode.SYMBOLS_HEX))) {
					int i = 0;
					String t = "";
					sb.append("[");
					i = 0;
					for (int p = off; p < to; ++p) {
						byte b = buffer[p];
						sb.append(t);
						t = " ";
						int v = b & 0xff;
						if (v <= 0xf)
							sb.append("0").append(Integer.toHexString(v));
						else
							sb.append(Integer.toHexString(v));
						if (++i > config.getMaxLength()) {
							sb.append("...");
							break;
						}
					}
					sb.append("]");
				}
			}
		}
		return sb.toString();
	}

	public static String stringOf(String pattern, int len) {
		if (len < 0)
			throw new IllegalArgumentException();
		if (len == 0)
			return "";
		char[] pat = pattern.toCharArray();
		int n = pat.length;
		char[] res = new char[len];
		for (int i = 0; i < res.length; ++i)
			res[i] = pat[i % n];
		return new String(res);
	}

/*	public static String makeSafeFilename(String s) {
		char[] chars = new char[s.length()];
		for (char ch : s.toCharArray()) {
			boolean b = false;
			switch (ch) {
				case '-':
				case '=':
				case '$':
				case '+':
				case '(':
				case ')':
			}
			
			if ((ch >= 'a' && ch <= 'z')||(ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <='9') || (ch == '_')) 
				
			
		}
			
		return null;
	}*/

}
