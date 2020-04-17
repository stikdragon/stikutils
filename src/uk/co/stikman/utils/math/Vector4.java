package uk.co.stikman.utils.math;

import uk.co.stikman.utils.GwtIncompatible;

public class Vector4 {
	public float	x;
	public float	y;
	public float	z;
	public float	w;

	public Vector4() {
	}

	public Vector4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4(Vector4 copy) {
		x = copy.x;
		y = copy.y;
		z = copy.z;
		w = copy.w;
	}

	public final float length() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	public final float lengthSquared() {
		return (x * x) + (y * y) + (z * z) + (w * w);
	}

	public final Vector4 add(Vector4 v2, Vector4 dest) {
		dest.x = x + v2.x;
		dest.y = y + v2.y;
		dest.z = z + v2.z;
		dest.w = w + v2.w;
		return dest;
	}

	public final Vector4 add(Vector4 v2) {
		x += v2.x;
		y += v2.y;
		z += v2.z;
		w += v2.w;
		return this;
	}

	public final Vector4 sub(Vector4 v2, Vector4 dest) {
		dest.x = x - v2.x;
		dest.y = y - v2.y;
		dest.z = z - v2.z;
		dest.w = w - v2.w;
		return dest;
	}

	public final Vector4 sub(Vector4 v2) {
		x -= v2.x;
		y -= v2.y;
		z -= v2.z;
		w -= v2.w;
		return this;
	}

	public final Vector4 multiply(float s) {
		x *= s;
		y *= s;
		z *= s;
		w *= s;
		return this;
	}

	public final Vector4 multiply(float s, Vector4 dest) {
		dest.x = x * s;
		dest.y = y * s;
		dest.z = z * s;
		dest.w = w * s;
		return dest;
	}

	public final Vector4 divide(float d) {
		x /= d;
		y /= d;
		z /= d;
		w /= d;
		return this;
	}

	public final Vector4 divide(float d, Vector4 dest) {
		dest.x = x / d;
		dest.y = y / d;
		dest.z = z / d;
		dest.w = w / d;
		return dest;
	}

	public final Vector4 normalize() {
		float in = (float) (1.0f / Math.sqrt((x * x) + (y * y) + (z * z) + (w * w)));
		x *= in;
		y *= in;
		z *= in;
		w *= in;
		return this;
	}

	public final float dot(float vx, float vy, float vz) {
		return vx * x + vy * y + vz * z;
	}

	public final Vector4 set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public void copy(Vector4 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}

	public static void lerp(Vector4 a, Vector4 b, float f, Vector4 res) {
		float f2 = 1.0f - f;
		res.x = a.x * f + b.x * f2;
		res.y = a.y * f + b.y * f2;
		res.z = a.z * f + b.z * f2;
		res.w = a.w * f + b.w * f2;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z + "," + w;
	}
	
	/**
	 * formats to <code>dp</code> decimal places
	 * 
	 * @param dp
	 * @return
	 */
	@GwtIncompatible
	public String toString(int dp) {
		return String.format("%." + dp + "f, %." + dp + "f, %." + dp + "f, %." + dp + "f", x, y, z, w);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(w);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector4 other = (Vector4) obj;
		if (Float.floatToIntBits(w) != Float.floatToIntBits(other.w))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	public void set(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = 1;
	}
}
