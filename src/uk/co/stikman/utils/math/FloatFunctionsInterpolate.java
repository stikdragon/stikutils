package uk.co.stikman.utils.math;

/**
 * These are intended to be used for interpolations. They all map 0..1 on to
 * 0..1
 * 
 * @author Stik
 *
 */
public class FloatFunctionsInterpolate {

	private static FloatFunction	ffSine		= null;
	private static FloatFunction	ffLinear	= null;

	private abstract static class BaseFunc implements FloatFunction {
		@Override
		public float getMinY() {
			return 0;
		}

		@Override
		public float getMinX() {
			return 0;
		}

		@Override
		public float getMaxY() {
			return 1;
		}

		@Override
		public float getMaxX() {
			return 1;
		}
	}

	public static FloatFunction linear() {
		if (ffLinear == null) {
			ffLinear = new BaseFunc() {
				@Override
				public float f(float x) {
					return x;
				}
			};
		}
		return ffLinear;
	}

	public static FloatFunction sine() {
		if (ffSine == null) {
			ffSine = new BaseFunc() {
				@Override
				public float f(float x) {
					return ((float) (Math.sin(x * Math.PI + Math.PI / 2.0) + 1.0)) / 2.0f;
				}
			};
		}
		return ffSine;
	}

}
