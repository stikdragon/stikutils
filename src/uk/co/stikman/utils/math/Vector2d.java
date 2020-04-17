package uk.co.stikman.utils.math;

import com.sun.org.apache.regexp.internal.RE;

import uk.co.stikman.utils.GwtIncompatible;

public class Vector2d {
	public static final Vector2d	ZERO	= new Vector2d(0, 0);
	public static final Vector2d	ONE		= new Vector2d(1, 1);

	public double				x;
	public double				y;

	public Vector2d() {
	}

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2d(Vector2d copy) {
		x = copy.x;
		y = copy.y;
	}

	public final double length() {
		return  Math.sqrt((x * x) + (y * y));
	}

	public final double lengthSquared() {
		return (x * x) + (y * y);
	}

	public final Vector2d add(Vector2d v2, Vector2d dest) {
		dest.x = x + v2.x;
		dest.y = y + v2.y;
		return dest;
	}
	
	public Vector2d add(Vector2d v, double scale, Vector2d res) {
		res.x = x + v.x * scale;
		res.y = y + v.y * scale;
		return res;
	}


	public final Vector2d add(Vector2d v2) {
		x += v2.x;
		y += v2.y;
		return this;
	}

	public final Vector2d sub(Vector2d v2, Vector2d dest) {
		dest.x = x - v2.x;
		dest.y = y - v2.y;
		return dest;
	}

	public final Vector2d sub(Vector2d v2) {
		x -= v2.x;
		y -= v2.y;
		return this;
	}

	/**
	 * Multiply in place
	 * 
	 * @param s
	 * @return
	 */
	public final Vector2d multiply(double s) {
		x *= s;
		y *= s;
		return this;
	}

	public final Vector2d multiply(double s, Vector2d dest) {
		dest.x = x * s;
		dest.y = y * s;
		return dest;
	}

	public final Vector2d divide(double d) {
		x /= d;
		y /= d;
		return this;
	}

	public final Vector2d divide(double d, Vector2d dest) {
		dest.x = x / d;
		dest.y = y / d;
		return dest;
	}

	public final Vector2d normalize() {
		double in = (double) (1.0f / Math.sqrt((x * x) + (y * y)));
		x *= in;
		y *= in;
		return this;
	}

	public final double dot(double vx, double vy) {
		return vx * x + vy * y;
	}
	
	public final double dot(Vector2d v) {
		return v.x * x + v.y * y;
	}

	public final Vector2d set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2d copy(Vector2d v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}

	@Override
	public String toString() {
		return x + "," + y;
	}

	public void addMultiplyLocal(Vector2d v, double scale) {
		x += v.x * scale;
		y += v.y * scale;
	}

	public double distanceToSq(Vector2d v) {
		double dx = x - v.x;
		double dy = y - v.y;
		return dx * dx + dy * dy;
	}

	public double distanceTo(Vector2d v) {
		double dx = x - v.x;
		double dy = y - v.y;
		return (double) Math.sqrt(dx * dx + dy * dy);
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
		Vector2d other = (Vector2d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	public Vector2d set(Vector2d copy) {
		this.x = copy.x;
		this.y = copy.y;
		return this;
	}

	public Vector2d addLocal(Vector2d v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vector2d addLocal(double dx, double dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}

	public Vector2d set(Vector2d v, double scale) {
		this.x = v.x * scale;
		this.y = v.y * scale;
		return this;
	}

	/**
	 * formats to <code>dp</code> decimal places
	 * 
	 * @param dp
	 * @return
	 */
	@GwtIncompatible
	public String toString(int dp) {
		return String.format("%." + dp + "f, %." + dp + "f", x, y);
	}

	public static double distanceSq(Vector2d a, Vector2d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return dx * dx + dy * dy;
	}

	/**
	 * Returns the minimum of the given list. So it's really
	 * "vec2(min(v.x), min(v.y))"
	 * 
	 * @param vs
	 * @param out
	 * @return
	 */
	public static Vector2d min(Vector2d[] vs, Vector2d out) {
		if (vs.length == 0)
			return Vector2d.ZERO;
		if (vs.length == 1)
			return vs[0];
		double mx = vs[0].x;
		double my = vs[0].y;
		for (int i = 1; i < vs.length; ++i) {
			Vector2d v = vs[i];
			if (v.x < mx)
				mx = v.x;
			if (v.y < my)
				my = v.y;
		}
		return out.set(mx, my);
	}

	/**
	 * Returns the maximum of the given list. So it's really
	 * "vec2(max(v.x), max(v.y))"
	 * 
	 * @param vs
	 * @param out
	 * @return
	 */
	public static Vector2d max(Vector2d[] vs, Vector2d out) {
		if (vs.length == 0)
			return Vector2d.ZERO;
		if (vs.length == 1)
			return vs[0];
		double mx = vs[0].x;
		double my = vs[0].y;
		for (int i = 1; i < vs.length; ++i) {
			Vector2d v = vs[i];
			if (v.x > mx)
				mx = v.x;
			if (v.y > my)
				my = v.y;
		}
		return out.set(mx, my);
	}


}
