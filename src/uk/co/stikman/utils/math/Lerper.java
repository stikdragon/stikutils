package uk.co.stikman.utils.math;

/**
 * Interpolates (linear) between a set of points
 * 
 * @author Stik
 *
 */
public class Lerper implements FloatFunction {
	private float[]	keys;
	private float[]	values;
	private float	minKey;
	private float	maxKey;
	private float	minVal;
	private float	maxVal;

	/**
	 * Pass KEY,VALUE,KEY,VALUE... Should be in order.
	 * 
	 * @param data
	 */
	public void setAll(float... data) {
		if (data.length % 2 != 0)
			throw new IllegalArgumentException("Must pass even number of floats");
		int size = data.length / 2;
		keys = new float[size];
		values = new float[size];

		if (data.length < 2) {
			minVal = 0.0f;
			maxVal = 0.0f;
			maxKey = 0.0f;
			minKey = 0.0f;
		} else {
			maxKey = data[1];
			minKey = data[1];
		}

		for (int i = 0; i < size; ++i) {
			keys[i] = data[i * 2];
			float f = data[i * 2 + 1];
			values[i] = f;
			if (f < minVal)
				minVal = f;
			if (f > maxVal)
				maxVal = f;
		}
		if (keys.length > 0) {
			maxKey = keys[size - 1];
			minKey = keys[0];
		}
	}

	public float lerp(float key) {
		if (keys.length < 2)
			return 0.0f;

		int n = -1;
		for (int i = 0; i < keys.length; ++i) {
			if (key <= keys[i]) {
				n = i;
				break;
			}
		}

		if (n == -1) // key is bigger than largest one
			return values[values.length - 1];

		if (n == 0)
			return values[0];

		float dkey = keys[n] - keys[n - 1];
		if (dkey == 0.0f) // identical keys
			return values[n];
		float dval = values[n] - values[n - 1];
		float mu = (key - keys[n - 1]) / dkey;
		return values[n - 1] + dval * mu;
	}

	@Override
	public float f(float x) {
		return lerp(x);
	}

	@Override
	public final float getMinX() {
		return minKey;
	}

	@Override
	public final float getMaxX() {
		return maxKey;
	}

	@Override
	public float getMinY() {
		return minVal;
	}

	@Override
	public float getMaxY() {
		return maxVal;
	}
}