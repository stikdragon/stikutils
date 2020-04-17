package uk.co.stikman.utils.math;

import java.text.NumberFormat;

import uk.co.stikman.utils.GwtIncompatible;

/**
 * Annoyingly i've written this the wrong way round for web-gl, it expects
 * column-major, not row-major. asdoubleArray() transposes it, so it works ok
 * 
 * @author Stik
 *
 */
public class Matrix3d {
	private double					m00;
	private double					m01;
	private double					m02;
	private double					m10;
	private double					m11;
	private double					m12;
	private double					m20;
	private double					m21;
	private double					m22;

	/**
	 * Since this is intended for use in GWT it's assumed that these will NOT
	 * get shared between threads, so beware if you repurpose for non-web stuff
	 */
	private static final Matrix3d	tmpM	= new Matrix3d();
	private static final double[]	tmpFA	= new double[9];

	@GwtIncompatible
	private NumberFormat			fmt		= NumberFormat.getInstance();

	/**
	 * Creates an empty matrix. All elements are 0.
	 */
	public Matrix3d() {
	}

	/**
	 * Compute the matrix determinant.
	 * 
	 * @return determinant of this matrix
	 */
	public final double determinant() {
		double A0 = m11 * m22 - m12 * m21;
		double A1 = m10 * m22 - m12 * m20;
		double A2 = m10 * m21 - m11 * m20;
		return m00 * A0 - m01 * A1 - m02 * A2;
	}

	/**
	 * Compute the inverse of this matrix and return it as a new object. If the
	 * matrix is not invertible, <code>null</code> is returned.
	 * 
	 * @return the inverse of this matrix, or <code>null</code> if not
	 *         invertible
	 */
	public final Matrix3d inverse() {
		return inverse(new Matrix3d());
	}

	/**
	 * Returns IDENTITY if not invertable
	 * 
	 * @param out
	 * @return
	 */
	public final Matrix3d inverse(Matrix3d out) {
		double A0 = m11 * m22 - m12 * m21;
		double A1 = m10 * m22 - m12 * m20;
		double A2 = m10 * m21 - m11 * m20;
		double det = m00 * A0 - m01 * A1 - m02 * A2;

		if (Math.abs(det) < 1e-12f)
			return new Matrix3d().makeIdentity(); // matrix is not invertible
		double invDet = 1 / det;
		out.m00 = m11 * m22 - m21 * m12;
		out.m10 = m20 * m12 - m10 * m22;
		out.m20 = m10 * m21 - m20 * m11;
		out.m01 = m21 * m02 - m01 * m22;
		out.m11 = m00 * m22 - m20 * m02;
		out.m21 = m20 * m01 - m00 * m21;
		out.m02 = m01 * m12 - m11 * m02;
		out.m12 = m10 * m02 - m00 * m12;
		out.m22 = m00 * m11 - m10 * m01;

		out.m00 = invDet * out.m00;
		out.m10 = invDet * out.m10;
		out.m20 = invDet * out.m20;
		out.m01 = invDet * out.m01;
		out.m11 = invDet * out.m11;
		out.m21 = invDet * out.m21;
		out.m02 = invDet * out.m02;
		out.m12 = invDet * out.m12;
		out.m22 = invDet * out.m22;
		return out;
	}

	/**
	 * Computes this*m and store in self
	 * 
	 * @param m
	 *            right hand side of the multiplication
	 */
	public final Matrix3d multiply(Matrix3d m) {
		double rm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20;
		double rm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21;
		double rm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22;
		double rm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20;
		double rm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21;
		double rm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22;
		double rm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20;
		double rm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21;
		double rm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22;

		m00 = rm00;
		m01 = rm01;
		m02 = rm02;
		m10 = rm10;
		m11 = rm11;
		m12 = rm12;
		m20 = rm20;
		m21 = rm21;
		m22 = rm22;

		return this;
	}

	/**
	 * Computes this*v
	 */
	public final Vector3d multiply(Vector3d v, Vector3d out) {
		out.x = m00 * v.x + m01 * v.y + m02 * v.z;
		out.y = m10 * v.x + m11 * v.y + m12 * v.z;
		out.z = m20 * v.x + m21 * v.y + m22 * v.z;
		return out;
	}

	/**
	 * Computes this*v
	 */
	public final Vector2d multiply(Vector2d v, Vector2d out) {
		out.x = m00 * v.x + m01 * v.y + m02;
		out.y = m10 * v.x + m11 * v.y + m12;
		return out;
	}

	public final Matrix3d scale(double f) {
		tmpM.makeScale(f, f);
		multiply(tmpM);
		return this;
	}

	public final Matrix3d scale(double x, double y) {
		tmpM.makeScale(x, y);
		multiply(tmpM);
		return this;
	}

	public final Matrix3d skew(double x, double y) {
		tmpM.makeIdentity();
		tmpM.set(1, 0, x);
		tmpM.set(0, 1, y);
		multiply(tmpM);
		return this;
	}
	
	public final Matrix3d translate(Vector2d v) {
		tmpM.makeTranslation(v);
		multiply(tmpM);
		return this;
	}

	public Matrix3d makeScale(double fx, double fy) {
		makeIdentity();
		m00 = fx;
		m11 = fy;
		m22 = 1.0f;
		return this;
	}

	public final Matrix3d makeRotation(double ang) {
		makeIdentity();
		double s = (double) Math.sin(ang);
		double c = (double) Math.cos(ang);
		m00 = c;
		m01 = -s;
		m10 = s;
		m11 = c;
		return this;
	}

	public final Matrix3d makeTranslation(double vx, double vy) {
		makeIdentity();
		m02 = vx;
		m12 = vy;
		return this;
	}

	public Matrix3d makeTranslation(Vector2d v) {
		return makeTranslation(v.x, v.y);
	}

	public final Matrix3d makeIdentity() {
		m00 = 1.0f;
		m01 = 0.0f;
		m02 = 0.0f;
		m10 = 0.0f;
		m11 = 1.0f;
		m12 = 0.0f;
		m20 = 0.0f;
		m21 = 0.0f;
		m22 = 1.0f;
		return this;
	}

	/**
	 * Returns this matrix as an array of double[]. Beware that it uses a
	 * temporary array object, so it's not valid between calls, copy if needed
	 * 
	 * @return
	 */
	public double[] asDoubleArray() {
		tmpFA[0] = (double) m00;
		tmpFA[1] = (double) m10;
		tmpFA[2] = (double) m20;
		tmpFA[3] = (double) m01;
		tmpFA[4] = (double) m11;
		tmpFA[5] = (double) m21;
		tmpFA[6] = (double) m02;
		tmpFA[7] = (double) m12;
		tmpFA[8] = (double) m22;
		return tmpFA;
	}

	public void copy(Matrix3d from) {
		m00 = from.m00;
		m01 = from.m01;
		m02 = from.m02;
		m10 = from.m10;
		m11 = from.m11;
		m12 = from.m12;
		m20 = from.m20;
		m21 = from.m21;
		m22 = from.m22;
	}

	@Override
	@GwtIncompatible
	public String toString() {
		StringBuilder sb = new StringBuilder();
		double[] fa = asDoubleArray();
		sb.append("[ ");
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				sb.append(fmt.format(fa[j * 3 + i]));
				if (j == 2)
					break;
				sb.append(", ");
			}
			sb.append(" ]");
			if (i == 2)
				break;
			sb.append("\n[ ");
		}
		return sb.toString();
	}

	public final Matrix3d rotate(double ang) {
		tmpM.makeRotation(ang);
		multiply(tmpM);
		return this;
	}

	public final Matrix3d translate(double x, double y) {
		tmpM.makeTranslation(x, y);
		multiply(tmpM);
		return this;
	}

	public void set(int i, int j, double val) {
		if (i == 0) {
			if (j == 0)
				m00 = val;
			else if (j == 1)
				m01 = val;
			else if (j == 2)
				m02 = val;
		} else if (i == 1) {
			if (j == 0)
				m10 = val;
			else if (j == 1)
				m11 = val;
			else if (j == 2)
				m12 = val;
		} else if (i == 2) {
			if (j == 0)
				m20 = val;
			else if (j == 1)
				m21 = val;
			else if (j == 2)
				m22 = val;
		}
	}

}
