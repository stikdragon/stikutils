package uk.co.stikman.utils.math;

import uk.co.stikman.utils.HAlign;
import uk.co.stikman.utils.VAlign;

public class Recti {
	public int	x;
	public int	y;
	public int	w;
	public int	h;

	public Recti(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Recti() {

	}

	public Recti(Recti rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.w = rect.w;
		this.h = rect.h;
	}

	public void adjust(int dx, int dy, int dw, int dh) {
		x += dx;
		y += dy;
		w += dw;
		h += dh;
	}
	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public boolean contains(Vector2 v) {
		return v.x >= this.x && v.x <= this.x + this.w && v.y >= this.y && v.y <= this.y + this.h;
	}

	
	public boolean intersects(Recti r) {
		return ((x < r.x + w && x + w > r.x) && (y < r.y + r.h && y + h > r.y));
	}

	public Recti setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		return this;
	}

	public Recti setBounds(Recti r) {
		this.x = r.x;
		this.y = r.y;
		this.w = r.w;
		this.h = r.h;
		return this;
	}

	public Recti setBounds(Vector2i v) {
		this.w = v.x;
		this.h = v.y;
		return this;
	}
	

	public Recti copy(Recti r) {
		x = r.x;
		y = r.y;
		w = r.w;
		h = r.h;
		return this;
	}

	public void positionIn(Recti r, HAlign halign, VAlign valign) {
		switch (halign) {
			case CENTRE:
				x = r.x + (r.w - w) / 2;
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
				y = r.y + (r.h - h) / 2;
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
	public void alignTo(Recti r, HAlign alignh, VAlign alignv) {
		switch (alignh) {
			case CENTRE:
				x = r.x + (r.w - w) / 2;
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
				y = r.y + (r.h - h) / 2;
				break;
			case TOP:
				y = r.y;
				break;
		}
	}


	public Recti centreIn(Recti container) {
		int dx = container.w - w;
		int dy = container.h - h;
		x = container.x + dx / 2;
		y = container.y + dy / 2;
		return this;
	}

	public Recti centreIn(int w, int h) {
		int dx = w - this.w;
		int dy = h - this.h;
		this.x = x + dx / 2;
		this.y = y + dy / 2;
		return this;
	}

	/**
	 * Round everything to integer (with a simple cast)
	 */
	public void quantize() {
		y = (int) y;
		w = (int) w;
		h = (int) h;
		x = (int) x;
	}

	
	public void adjust(int amt) {
		x += amt;
		y += amt;
		w -= amt * 2.0f;
		h -= amt * 2.0f;
	}

	public Recti intersectWith(Recti r, Recti result) {
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