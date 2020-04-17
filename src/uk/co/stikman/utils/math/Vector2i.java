package uk.co.stikman.utils.math;

public class Vector2i {
	public int	x;
	public int	y;

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

}
