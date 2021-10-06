package uk.co.stikman.utils.math;

public class Vector3i {
	public int	x;
	public int	y;
	public int	z;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Vector3i(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i(Vector3i copy) {
		super();
		this.x = copy.x;
		this.y = copy.y;
		this.z = copy.z;
	}

	public Vector3i() {
		super();
	}

	@Override
	public String toString() {
		return x + "," + y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
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
		Vector3i other = (Vector3i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public boolean equals(int x, int y, int z) {
		return (this.x == x) && (this.y == y) && (this.z == z);
	}

	public Vector3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public int distanceToSq(int x, int y, int z) {
		x = x - this.x;
		y = y - this.y;
		z = z - this.z;
		return x * x + y * y + z * z;
	}

	public void set(Vector3i v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public static Vector3i parse(String s) {
		if (s == null)
			throw new NullPointerException();
		String[] bits = s.split(",");
		if (bits.length != 3)
			throw new IllegalArgumentException("Expected x,y,z");
		Vector3i v = new Vector3i();
		v.x = Integer.parseInt(bits[0]);
		v.y = Integer.parseInt(bits[1]);
		v.z = Integer.parseInt(bits[2]);
		return v;
	}

	public Vector3i copy(Vector3i v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	/**
	 * does <code>(int)x</code>, etc
	 * 
	 * @param point
	 * @return
	 */
	public Vector3i set(Vector3 v) {
		this.x = (int) v.x;
		this.y = (int) v.y;
		this.z = (int) v.z;
		return this;
	}

	public Vector3i set(float x, float y, float z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
		return this;
	}

}
