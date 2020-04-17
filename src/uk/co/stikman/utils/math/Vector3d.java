package uk.co.stikman.utils.math;

import uk.co.stikman.utils.GwtIncompatible;

public class Vector3d {
	public static final Vector3d	ZERO	= new Vector3d(0, 0, 0);
	public double					x;
	public double					y;
	public double					z;

	public Vector3d() {
	}

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(Vector3d copy) {
		x = copy.x;
		y = copy.y;
		z = copy.z;
	}

	public final double length() {
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public final double lengthSquared() {
		return (x * x) + (y * y) + (z * z);
	}

	public final Vector3d add(Vector3d v2, Vector3d dest) {
		dest.x = x + v2.x;
		dest.y = y + v2.y;
		dest.z = z + v2.z;
		return dest;
	}

	public final Vector3d add(Vector3d v2) {
		x += v2.x;
		y += v2.y;
		z += v2.z;
		return this;
	}

	public final Vector3d sub(Vector3d v2, Vector3d dest) {
		dest.x = x - v2.x;
		dest.y = y - v2.y;
		dest.z = z - v2.z;
		return dest;
	}

	public final Vector3d sub(Vector3d v2) {
		x -= v2.x;
		y -= v2.y;
		z -= v2.z;
		return this;
	}

	public final Vector3d multiply(double s) {
		x *= s;
		y *= s;
		z *= s;
		return this;
	}

	public final Vector3d multiply(double s, Vector3d dest) {
		dest.x = x * s;
		dest.y = y * s;
		dest.z = z * s;
		return dest;
	}

	public final Vector3d divide(double d) {
		x /= d;
		y /= d;
		z /= d;
		return this;
	}

	public final Vector3d divide(double d, Vector3d dest) {
		dest.x = x / d;
		dest.y = y / d;
		dest.z = z / d;
		return dest;
	}

	public final Vector3d normalize() {
		double in = (1.0f / Math.sqrt((x * x) + (y * y) + (z * z)));
		x *= in;
		y *= in;
		z *= in;
		return this;
	}

	public final double dot(double vx, double vy, double vz) {
		return vx * x + vy * y + vz * z;
	}

	public double dot(Vector3d t) {
		return t.x * x + t.y * y + t.z * z;
	}

	public static final Vector3d cross(Vector3d v1, Vector3d v2, Vector3d dest) {
		dest.x = (v1.y * v2.z) - (v1.z * v2.y);
		dest.y = (v1.z * v2.x) - (v1.x * v2.z);
		dest.z = (v1.x * v2.y) - (v1.y * v2.x);
		return dest;
	}

	public final Vector3d set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public void copy(Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}

	/**
	 * formats to <code>dp</code> decimal places
	 * 
	 * @param dp
	 * @return
	 */
	@GwtIncompatible
	public String toString(int dp) {
		return String.format("%." + dp + "f, %." + dp + "f, %." + dp + "f", x, y, z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
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
		Vector3d other = (Vector3d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	public double distanceSq(Vector3d v) {
		double dx = v.x - x;
		double dy = v.y - y;
		double dz = v.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public Vector3d set(Vector4d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	public Vector3d addMult(Vector3d n, double f) {
		this.x += n.x * f;
		this.y += n.y * f;
		this.z += n.z * f;
		return this;
	}

}
