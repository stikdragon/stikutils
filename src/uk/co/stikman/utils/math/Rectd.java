package uk.co.stikman.utils.math;

import uk.co.stikman.utils.HAlign;
import uk.co.stikman.utils.VAlign;

public class Rectd {
	public double	x;
	public double	y;
	public double	w;
	public double	h;

	public Rectd(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rectd() {

	}

	public Rectd(Rectd rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.w = rect.w;
		this.h = rect.h;
	}

	public void adjust(double dx, double dy, double dw, double dh) {
		x += dx;
		y += dy;
		w += dw;
		h += dh;
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public boolean contains(double x, double y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public boolean contains(Vector2 v) {
		return v.x >= this.x && v.x <= this.x + this.w && v.y >= this.y && v.y <= this.y + this.h;
	}

	
	public boolean intersects(Rectd r) {
		return ((x < r.x + w && x + w > r.x) && (y < r.y + r.h && y + h > r.y));
	}

	public Rectd setBounds(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		return this;
	}

	public Rectd setBounds(Rectd r) {
		this.x = r.x;
		this.y = r.y;
		this.w = r.w;
		this.h = r.h;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rectd other = (Rectd) obj;
		if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h))
			return false;
		if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(h);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(w);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public Rectd copy(Rectd r) {
		x = r.x;
		y = r.y;
		w = r.w;
		h = r.h;
		return this;
	}

	public void positionIn(Rectd r, HAlign halign, VAlign valign) {
		switch (halign) {
			case CENTRE:
				x = r.x + (r.w - w) / 2.0;
				break;
			case LEFT:
				x = r.x;
				break;
			case RIGHT:
				x = r.x + r.w - w;
				break;
			default:
		}

		switch (valign) {
			case BOTTOM:
				y = r.y + r.h - h;
				break;
			case CENTRE:
				y = r.y + (r.h - h) / 2.0;
				break;
			case TOP:
				y = r.y;
				break;
			default:
				break;
		}

	}

	@Override
	public String toString() {
		return "Rect [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + "]";
	}

	@SuppressWarnings("incomplete-switch")
	public void alignTo(Rectd r, HAlign alignh, VAlign alignv) {
		switch (alignh) {
			case CENTRE:
				x = r.x + (r.w - w) / 2.0;
				break;
			case LEFT:
				x = r.x;
				break;
			case RIGHT:
				x = r.w + r.x - w;
				break;
		}

		switch (alignv) {
			case BOTTOM:
				y = r.y + r.h - h;
				break;
			case CENTRE:
				y = r.y + (r.h - h) / 2.0;
				break;
			case TOP:
				y = r.y;
				break;
		}
	}

	public void adjust(double amt) {
		x += amt;
		y += amt;
		w -= amt * 2.0;
		h -= amt * 2.0;
	}

	public Rectd intersectWith(Rectd r, Rectd result) {
		result.x = Math.max(x, r.x);
		result.w = Math.min(x + w, r.x + r.w) - result.x;

		result.y = Math.max(y, r.y);
		result.h = Math.min(y + h, r.y + r.h) - result.y;
		if (result.w < 0.0 || result.h < 0.0)
			result.setBounds(0, 0, 0, 0); // don't overlap
		return result;
	}

	public Vector2d centre(Vector2d res) {
		res.x = x + w / 2.0;
		res.y = y + h / 2.0;
		return res;
	}

}