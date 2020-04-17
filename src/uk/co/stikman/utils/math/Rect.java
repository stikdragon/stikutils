package uk.co.stikman.utils.math;

import uk.co.stikman.utils.HAlign;
import uk.co.stikman.utils.VAlign;

public class Rect {
	public float	x;
	public float	y;
	public float	w;
	public float	h;

	public Rect(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rect() {

	}

	public Rect(Rect rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.w = rect.w;
		this.h = rect.h;
	}

	public void adjust(float dx, float dy, float dw, float dh) {
		x += dx;
		y += dy;
		w += dw;
		h += dh;
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public boolean contains(float x, float y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public boolean contains(Vector2 v) {
		return v.x >= this.x && v.x <= this.x + this.w && v.y >= this.y && v.y <= this.y + this.h;
	}

	
	public boolean intersects(Rect r) {
		return ((x < r.x + w && x + w > r.x) && (y < r.y + r.h && y + h > r.y));
	}

	public Rect setBounds(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		return this;
	}

	public Rect setBounds(Rect r) {
		this.x = r.x;
		this.y = r.y;
		this.w = r.w;
		this.h = r.h;
		return this;
	}

	@Override
	public boolean equals(Object ob) {
		if (ob == null)
			return false;
		if (ob == this)
			return true;

		if (!(ob instanceof Rect))
			return false;
		Rect r = (Rect) ob;
		return (r.x == x) && (r.y == y) && (r.w == w) && (r.h == h);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Float.floatToIntBits(h);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Float.floatToIntBits(w);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Float.floatToIntBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Float.floatToIntBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public Rect copy(Rect r) {
		x = r.x;
		y = r.y;
		w = r.w;
		h = r.h;
		return this;
	}

	public void positionIn(Rect r, HAlign halign, VAlign valign) {
		switch (halign) {
			case CENTRE:
				x = r.x + (r.w - w) / 2.0f;
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
				y = r.y + (r.h - h) / 2.0f;
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
	public void alignTo(Rect r, HAlign alignh, VAlign alignv) {
		switch (alignh) {
			case CENTRE:
				x = r.x + (r.w - w) / 2.0f;
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
				y = r.y + (r.h - h) / 2.0f;
				break;
			case TOP:
				y = r.y;
				break;
		}
	}

	public void adjust(float amt) {
		x += amt;
		y += amt;
		w -= amt * 2.0f;
		h -= amt * 2.0f;
	}

	public Rect intersectWith(Rect r, Rect result) {
		result.x = Math.max(x, r.x);
		result.w = Math.min(x + w, r.x + r.w) - result.x;

		result.y = Math.max(y, r.y);
		result.h = Math.min(y + h, r.y + r.h) - result.y;
		if (result.w < 0.0f || result.h < 0.0f)
			result.setBounds(0, 0, 0, 0); // don't overlap
		return result;
	}

	public Vector2 centre(Vector2 res) {
		res.x = x + w / 2.0f;
		res.y = y + h / 2.0f;
		return res;
	}

}