package uk.co.stikman.utils.math;

/**
 * The function defines min and max values and argument values, these are not
 * always meaningful. <code>f(x) = m.x</code>, for instance doesn't have a start
 * or end, but <code>f(x) = sin(x)</code> can be considered to run from
 * <code>0..2*PI</code> and <code>-1..1</code>
 * 
 * @author Stik
 *
 */
public interface FloatFunction {

	/**
	 * Evaluate the function. It's valid to call it with any value of x, the
	 * function must return something, even if it's NaN.
	 * 
	 * @param x
	 * @return
	 */
	float f(float x);

	/**
	 * Can return {@value Float#POSITIVE_INFINITY}
	 * 
	 * @return
	 */
	float getMinX();

	/**
	 * Can return {@value Float#NEGATIVE_INFINITY}
	 * 
	 * @return
	 */

	float getMaxX();

	/**
	 * Can return {@value Float#POSITIVE_INFINITY}
	 * 
	 * @return
	 */
	float getMinY();

	/**
	 * Can return {@value Float#NEGATIVE_INFINITY}
	 * 
	 * @return
	 */
	float getMaxY();

}
