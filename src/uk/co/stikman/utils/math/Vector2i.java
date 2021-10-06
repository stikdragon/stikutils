package uk.co.stikman.utils.math;

public class Vector2i {
	public static final Vector2i	ZERO	= new Vector2i(0, 0);
	public int						x;
	public int						y;

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

	public Vector2i(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Vector2i(Vector2i copy) {
		super();
		this.x = copy.x;
		this.y = copy.y;
	}

	public Vector2i() {
		super();
	}

	public Vector2i(Vector2 v) {
		this.x = (int) v.x;
		this.y = (int) v.y;
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
		Vector2i other = (Vector2i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public boolean equals(int x, int y) {
		return (this.x == x) && (this.y == y);
	}

	public Vector2i set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public int distanceToSq(int x, int y) {
		x = x - this.x;
		y = y - this.y;
		return x * x + y * y;
	}

	public void set(Vector2i v) {
		this.x = v.x;
		this.y = v.y;
	}

	public static Vector2i parse(String s) {
		if (s == null)
			throw new NullPointerException();
		String[] bits = s.split(",");
		if (bits.length != 2)
			throw new IllegalArgumentException("Expected x,y");
		Vector2i v = new Vector2i();
		v.x = Integer.parseInt(bits[0]);
		v.y = Integer.parseInt(bits[1]);
		return v;
	}

	public Vector2i copy(Vector2i v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}

	public Vector2i sub(int x, int y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector2i sub(Vector2i v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}

	public Vector2i sub(Vector2i v, Vector2i res) {
		res.x = x - v.x;
		res.y = y - v.y;
		return res;
	}

	public Vector2i add(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2i add(Vector2i v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vector2i add(Vector2i v, Vector2i res) {
		res.x = x + v.x;
		res.y = y + v.y;
		return res;
	}

	/**
	 * this divides and rounds down (by casting)
	 * 
	 * @param f
	 * @return
	 */
	public Vector2i divide(float f) {
		this.x /= f;
		this.y /= f;
		return this;
	}

	public Vector2i multiply(int f) {
		this.x *= f;
		this.y *= f;
		return this;
	}

}
