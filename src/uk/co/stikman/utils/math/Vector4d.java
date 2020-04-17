package uk.co.stikman.utils.math;

import uk.co.stikman.utils.GwtIncompatible;

public class Vector4d {
	public double	x;
	public double	y;
	public double	z;
	public double	w;

	public Vector4d() {
	}

	public Vector4d(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4d(Vector4d copy) {
		x = copy.x;
		y = copy.y;
		z = copy.z;
		w = copy.w;
	}

	public final double length() {
		return (double) Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	public final double lengthSquared() {
		return (x * x) + (y * y) + (z * z) + (w * w);
	}

	public final Vector4d add(Vector4d v2, Vector4d dest) {
		dest.x = x + v2.x;
		dest.y = y + v2.y;
		dest.z = z + v2.z;
		dest.w = w + v2.w;
		return dest;
	}

	public final Vector4d add(Vector4d v2) {
		x += v2.x;
		y += v2.y;
		z += v2.z;
		w += v2.w;
		return this;
	}

	public final Vector4d sub(Vector4d v2, Vector4d dest) {
		dest.x = x - v2.x;
		dest.y = y - v2.y;
		dest.z = z - v2.z;
		dest.w = w - v2.w;
		return dest;
	}

	public final Vector4d sub(Vector4d v2) {
		x -= v2.x;
		y -= v2.y;
		z -= v2.z;
		w -= v2.w;
		return this;
	}

	public final Vector4d multiply(double s) {
		x *= s;
		y *= s;
		z *= s;
		w *= s;
		return this;
	}

	public final Vector4d multiply(double s, Vector4d dest) {
		dest.x = x * s;
		dest.y = y * s;
		dest.z = z * s;
		dest.w = w * s;
		return dest;
	}

	public final Vector4d divide(double d) {
		x /= d;
		y /= d;
		z /= d;
		w /= d;
		return this;
	}

	public final Vector4d divide(double d, Vector4d dest) {
		dest.x = x / d;
		dest.y = y / d;
		dest.z = z / d;
		dest.w = w / d;
		return dest;
	}

	public final Vector4d normalize() {
		double in = (1.0f / Math.sqrt((x * x) + (y * y) + (z * z) + (w * w)));
		x *= in;
		y *= in;
		z *= in;
		w *= in;
		return this;
	}

	public final double dot(double vx, double vy, double vz) {
		return vx * x + vy * y + vz * z;
	}

	public final Vector4d set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public void copy(Vector4d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}

	public static void lerp(Vector4d a, Vector4d b, double f, Vector4d res) {
		double f2 = 1.0f - f;
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
		long temp;
		temp = Double.doubleToLongBits(w);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Vector4d other = (Vector4d) obj;
		if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	public void set(Vector3d v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = 1;
	}
}
