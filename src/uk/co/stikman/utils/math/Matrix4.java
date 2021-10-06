package uk.co.stikman.utils.math;

import java.text.NumberFormat;

import uk.co.stikman.utils.GwtIncompatible;

/**
 * Annoyingly i've written this the wrong way round for web-gl, it expects
 * column-major, not row-major. asfloatArray() transposes it, so it works ok
 * 
 * @author Stik
 *
 */
public class Matrix4 {
	private float					m00;
	private float					m01;
	private float					m02;
	private float					m03;
	private float					m10;
	private float					m11;
	private float					m12;
	private float					m13;
	private float					m20;
	private float					m21;
	private float					m22;
	private float					m23;
	private float					m30;
	private float					m31;
	private float					m32;
	private float					m33;

	/**
	 * Since this is intended for use in GWT it's assumed that these will NOT
	 * get shared between threads, so beware if you repurpose for non-web stuff
	 */
	private static final Matrix4	tmpM		= new Matrix4();
	private static final float[]	tmpFA		= new float[16];
	private static final Matrix4	IDENTITY	= new Matrix4().makeIdentity();

	@GwtIncompatible
	private NumberFormat			fmt			= NumberFormat.getInstance();

	/**
	 * Creates an empty matrix. All elements are 0.
	 */
	public Matrix4() {
	}

	/**
	 * Compute the matrix determinant.
	 * 
	 * @return determinant of this matrix
	 */
	public final float determinant() {
		float A0 = m00 * m11 - m01 * m10;
		float A1 = m00 * m12 - m02 * m10;
		float A2 = m00 * m13 - m03 * m10;
		float A3 = m01 * m12 - m02 * m11;
		float A4 = m01 * m13 - m03 * m11;
		float A5 = m02 * m13 - m03 * m12;

		float B0 = m20 * m31 - m21 * m30;
		float B1 = m20 * m32 - m22 * m30;
		float B2 = m20 * m33 - m23 * m30;
		float B3 = m21 * m32 - m22 * m31;
		float B4 = m21 * m33 - m23 * m31;
		float B5 = m22 * m33 - m23 * m32;

		return A0 * B5 - A1 * B4 + A2 * B3 + A3 * B2 - A4 * B1 + A5 * B0;
	}

	/**
	 * Compute the inverse of this matrix and return it as a new object. If the
	 * matrix is not invertible, <code>null</code> is returned.
	 * 
	 * @return the inverse of this matrix, or <code>null</code> if not
	 *         invertible
	 */
	public final Matrix4 inverse() {
		float A0 = m00 * m11 - m01 * m10;
		float A1 = m00 * m12 - m02 * m10;
		float A2 = m00 * m13 - m03 * m10;
		float A3 = m01 * m12 - m02 * m11;
		float A4 = m01 * m13 - m03 * m11;
		float A5 = m02 * m13 - m03 * m12;

		float B0 = m20 * m31 - m21 * m30;
		float B1 = m20 * m32 - m22 * m30;
		float B2 = m20 * m33 - m23 * m30;
		float B3 = m21 * m32 - m22 * m31;
		float B4 = m21 * m33 - m23 * m31;
		float B5 = m22 * m33 - m23 * m32;

		float det = A0 * B5 - A1 * B4 + A2 * B3 + A3 * B2 - A4 * B1 + A5 * B0;
		if (Math.abs(det) < 1e-12f)
			return null; // matrix is not invertible
		float invDet = 1 / det;
		Matrix4 inv = new Matrix4();
		inv.m00 = (+m11 * B5 - m12 * B4 + m13 * B3) * invDet;
		inv.m10 = (-m10 * B5 + m12 * B2 - m13 * B1) * invDet;
		inv.m20 = (+m10 * B4 - m11 * B2 + m13 * B0) * invDet;
		inv.m30 = (-m10 * B3 + m11 * B1 - m12 * B0) * invDet;
		inv.m01 = (-m01 * B5 + m02 * B4 - m03 * B3) * invDet;
		inv.m11 = (+m00 * B5 - m02 * B2 + m03 * B1) * invDet;
		inv.m21 = (-m00 * B4 + m01 * B2 - m03 * B0) * invDet;
		inv.m31 = (+m00 * B3 - m01 * B1 + m02 * B0) * invDet;
		inv.m02 = (+m31 * A5 - m32 * A4 + m33 * A3) * invDet;
		inv.m12 = (-m30 * A5 + m32 * A2 - m33 * A1) * invDet;
		inv.m22 = (+m30 * A4 - m31 * A2 + m33 * A0) * invDet;
		inv.m32 = (-m30 * A3 + m31 * A1 - m32 * A0) * invDet;
		inv.m03 = (-m21 * A5 + m22 * A4 - m23 * A3) * invDet;
		inv.m13 = (+m20 * A5 - m22 * A2 + m23 * A1) * invDet;
		inv.m23 = (-m20 * A4 + m21 * A2 - m23 * A0) * invDet;
		inv.m33 = (+m20 * A3 - m21 * A1 + m22 * A0) * invDet;
		return inv;
	}

	/**
	 * Returns IDENTITY if not invertable
	 * 
	 * @param out
	 * @return
	 */
	public final Matrix4 inverse(Matrix4 out) {
		float A0 = m00 * m11 - m01 * m10;
		float A1 = m00 * m12 - m02 * m10;
		float A2 = m00 * m13 - m03 * m10;
		float A3 = m01 * m12 - m02 * m11;
		float A4 = m01 * m13 - m03 * m11;
		float A5 = m02 * m13 - m03 * m12;

		float B0 = m20 * m31 - m21 * m30;
		float B1 = m20 * m32 - m22 * m30;
		float B2 = m20 * m33 - m23 * m30;
		float B3 = m21 * m32 - m22 * m31;
		float B4 = m21 * m33 - m23 * m31;
		float B5 = m22 * m33 - m23 * m32;

		float det = A0 * B5 - A1 * B4 + A2 * B3 + A3 * B2 - A4 * B1 + A5 * B0;
		if (Math.abs(det) < 1e-12f)
			return Matrix4.IDENTITY; // matrix is not invertible
		float invDet = 1 / det;
		out.m00 = (+m11 * B5 - m12 * B4 + m13 * B3) * invDet;
		out.m10 = (-m10 * B5 + m12 * B2 - m13 * B1) * invDet;
		out.m20 = (+m10 * B4 - m11 * B2 + m13 * B0) * invDet;
		out.m30 = (-m10 * B3 + m11 * B1 - m12 * B0) * invDet;
		out.m01 = (-m01 * B5 + m02 * B4 - m03 * B3) * invDet;
		out.m11 = (+m00 * B5 - m02 * B2 + m03 * B1) * invDet;
		out.m21 = (-m00 * B4 + m01 * B2 - m03 * B0) * invDet;
		out.m31 = (+m00 * B3 - m01 * B1 + m02 * B0) * invDet;
		out.m02 = (+m31 * A5 - m32 * A4 + m33 * A3) * invDet;
		out.m12 = (-m30 * A5 + m32 * A2 - m33 * A1) * invDet;
		out.m22 = (+m30 * A4 - m31 * A2 + m33 * A0) * invDet;
		out.m32 = (-m30 * A3 + m31 * A1 - m32 * A0) * invDet;
		out.m03 = (-m21 * A5 + m22 * A4 - m23 * A3) * invDet;
		out.m13 = (+m20 * A5 - m22 * A2 + m23 * A1) * invDet;
		out.m23 = (-m20 * A4 + m21 * A2 - m23 * A0) * invDet;
		out.m33 = (+m20 * A3 - m21 * A1 + m22 * A0) * invDet;
		return out;
	}

	/**
	 * Computes this*m and store in self
	 * 
	 * @param m
	 *            right hand side of the multiplication
	 */
	public final Matrix4 multiply(Matrix4 m) {
		// matrix multiplication is m[r][c] = (row[r]).(col[c])
		float rm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30;
		float rm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31;
		float rm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32;
		float rm03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33;

		float rm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30;
		float rm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
		float rm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
		float rm13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33;

		float rm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30;
		float rm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
		float rm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
		float rm23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33;

		float rm30 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30;
		float rm31 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
		float rm32 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
		float rm33 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33;

		m00 = rm00;
		m01 = rm01;
		m02 = rm02;
		m03 = rm03;
		m10 = rm10;
		m11 = rm11;
		m12 = rm12;
		m13 = rm13;
		m20 = rm20;
		m21 = rm21;
		m22 = rm22;
		m23 = rm23;
		m30 = rm30;
		m31 = rm31;
		m32 = rm32;
		m33 = rm33;

		return this;
	}

	/**
	 * Computes this*v
	 */
	public final Vector3 multiply(Vector3 v, Vector3 out) {
		out.x = m00 * v.x + m01 * v.y + m02 * v.z + m03;
		out.y = m10 * v.x + m11 * v.y + m12 * v.z + m13;
		out.z = m20 * v.x + m21 * v.y + m22 * v.z + m23;
		return out;
	}

	public Vector4 multiply(Vector4 v, Vector4 out) {
		out.x = m00 * v.x + m01 * v.y + m02 * v.z + m03 * v.w;
		out.y = m10 * v.x + m11 * v.y + m12 * v.z + m13 * v.w;
		out.z = m20 * v.x + m21 * v.y + m22 * v.z + m23 * v.w;
		out.w = m30 * v.x + m31 * v.y + m32 * v.z + m33 * v.w;
		return out;
	}

	public final Matrix4 scale(float f) {
		tmpM.makeScale(f, f, f);
		multiply(tmpM);
		return this;
	}

	public final Matrix4 scale(float x, float y, float z) {
		tmpM.makeScale(x, y, z);
		multiply(tmpM);
		return this;
	}

	public final Matrix4 translate(Vector3 v) {
		tmpM.makeTranslation(v);
		multiply(tmpM);
		return this;
	}

	public Matrix4 translate(Vector2 v) {
		tmpM.makeTranslation(v);
		multiply(tmpM);
		return this;
	}

	public Matrix4 makeScale(float fx, float fy, float fz) {
		makeIdentity();
		m00 = fx;
		m11 = fy;
		m22 = fz;
		m33 = 1.0f;
		return this;
	}

	public final Matrix4 makeRotation(float vx, float vy, float vz, float ang) {
		makeIdentity();
		float s = (float) Math.sin(ang);
		float c = (float) Math.cos(ang);
		float t = 1 - c;
		m00 = t * vx * vx + c;
		m11 = t * vy * vy + c;
		m22 = t * vz * vz + c;
		float txy = t * vx * vy;
		float sz = s * vz;
		m01 = txy - sz;
		m10 = txy + sz;
		float txz = t * vx * vz;
		float sy = s * vy;
		m02 = txz + sy;
		m20 = txz - sy;
		float tyz = t * vy * vz;
		float sx = s * vx;
		m12 = tyz - sx;
		m21 = tyz + sx;
		m33 = 1;
		return this;
	}

	public final Matrix4 makeTranslation(float vx, float vy, float vz) {
		makeIdentity();
		m03 = vx;
		m13 = vy;
		m23 = vz;
		return this;
	}

	public Matrix4 makeTranslation(Vector3 v) {
		return makeTranslation(v.x, v.y, v.z);
	}

	public Matrix4 makeTranslation(Vector2 v) {
		return makeTranslation(v.x, v.y, 0.0f);
	}

	public final Matrix4 makeIdentity() {
		m00 = 1.0f;
		m01 = 0.0f;
		m02 = 0.0f;
		m03 = 0.0f;
		m10 = 0.0f;
		m11 = 1.0f;
		m12 = 0.0f;
		m13 = 0.0f;
		m20 = 0.0f;
		m21 = 0.0f;
		m22 = 1.0f;
		m23 = 0.0f;
		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;
		return this;
	}

	public final Matrix4 makePerspective(int fov, float aspect, float near, float far) {
		/*
		 * float angle = (float) ((fov / 180.0f) * Math.PI); float f = (float)
		 * (1.0f / Math.tan( angle * 0.5f ));
		 * 
		 * makeIdentity(); m00 = f / aspect; m11 = f; m22 = (far + near) / (near
		 * - far); m32 = -1.0f; m23 = (2.0f * far*near) / (near - far); m33 =
		 * 0.0f;
		 */

		float top = (float) (near * Math.tan(fov * Math.PI / 360.0));
		float bottom = -top;
		float left = bottom * aspect;
		float right = top * aspect;

		float X = 2 * near / (right - left);
		float Y = 2 * near / (top - bottom);
		float A = (right + left) / (right - left);
		float B = (top + bottom) / (top - bottom);
		float C = -(far + near) / (far - near);
		float D = -2 * far * near / (far - near);

		m00 = X;
		m01 = 0.0f;
		m02 = A;
		m03 = 0.0f;
		m10 = 0.0f;
		m11 = Y;
		m12 = B;
		m13 = 0.0f;
		m20 = 0.0f;
		m21 = 0.0f;
		m22 = C;
		m23 = D;
		m30 = 0.0f;
		m31 = 0.0f;
		m32 = -1.0f;
		m33 = 0.0f;

		return this;
	}

	/**
	 * Returns this matrix as an array of float[]. Beware that it uses a
	 * temporary array object, so it's not valid between calls, copy if needed
	 * 
	 * @return
	 */
	public float[] asFloatArray() {

		tmpFA[0] = (float) m00;
		tmpFA[1] = (float) m10;
		tmpFA[2] = (float) m20;
		tmpFA[3] = (float) m30;
		tmpFA[4] = (float) m01;
		tmpFA[5] = (float) m11;
		tmpFA[6] = (float) m21;
		tmpFA[7] = (float) m31;
		tmpFA[8] = (float) m02;
		tmpFA[9] = (float) m12;
		tmpFA[10] = (float) m22;
		tmpFA[11] = (float) m32;
		tmpFA[12] = (float) m03;
		tmpFA[13] = (float) m13;
		tmpFA[14] = (float) m23;
		tmpFA[15] = (float) m33;

		/*
		 * tmpFA[0] = (float) m00; tmpFA[1] = (float) m01; tmpFA[2] = (float)
		 * m02; tmpFA[3] = (float) m03; tmpFA[4] = (float) m10; tmpFA[5] =
		 * (float) m11; tmpFA[6] = (float) m12; tmpFA[7] = (float) m13; tmpFA[8]
		 * = (float) m20; tmpFA[9] = (float) m21; tmpFA[10] = (float) m22;
		 * tmpFA[11] = (float) m23; tmpFA[12] = (float) m30; tmpFA[13] = (float)
		 * m31; tmpFA[14] = (float) m32; tmpFA[15] = (float) m33;
		 */
		return tmpFA;
	}

	public Matrix4 copy(Matrix4 from) {
		m00 = from.m00;
		m01 = from.m01;
		m02 = from.m02;
		m03 = from.m03;
		m10 = from.m10;
		m11 = from.m11;
		m12 = from.m12;
		m13 = from.m13;
		m20 = from.m20;
		m21 = from.m21;
		m22 = from.m22;
		m23 = from.m23;
		m30 = from.m30;
		m31 = from.m31;
		m32 = from.m32;
		m33 = from.m33;
		return this;
	}

	public Matrix4 makeOrtho(float left, float right, float bottom, float top, float near, float far) {
		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = (far + near) / (far - near);

		m00 = 2.0f / (right - left);
		m01 = 0.0f;
		m02 = 0.0f;
		m03 = tx;

		m10 = 0.0f;
		m11 = 2.0f / (top - bottom);
		m12 = 0.0f;
		m13 = ty;

		m20 = 0.0f;
		m21 = 0.0f;
		m22 = -2.0f / (far - near);
		m23 = tz;

		m30 = 0.0f;
		m31 = 0.0f;
		m32 = 0.0f;
		m33 = 1.0f;

		return this;
	}

	public Matrix4 makeViewport(float x, float y, float w, float h) {
		m00 = w / 2f;
		m10 = 0;
		m20 = 0;
		m30 = 0;

		m01 = 0;
		m11 = h / 2f;
		m21 = 0;
		m31 = 0;

		m02 = 0;
		m12 = 0;
		m22 = 2;
		m32 = 0;

		m03 = x + w / 2f;
		m13 = y + h / 2f;
		m23 = -1;
		m33 = 1;

		return this;
	}

	@Override
	@GwtIncompatible
	public String toString() {
		StringBuilder sb = new StringBuilder();
		float[] fa = asFloatArray();
		sb.append("[ ");
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				sb.append(fmt.format(fa[j * 4 + i]));
				if (j == 3)
					break;
				sb.append(", ");
			}
			sb.append(" ]");
			if (i == 3)
				break;
			sb.append("\n[ ");
		}
		return sb.toString();
	}

	public final Matrix4 rotate(float vx, float vy, float vz, float ang) {
		tmpM.makeRotation(vx, vy, vz, ang);
		multiply(tmpM);
		return this;
	}

	public final Matrix4 translate(float x, float y, float z) {
		tmpM.makeTranslation(x, y, z);
		multiply(tmpM);
		return this;
	}

	public final Matrix4 makeZero() {
		m00 = 0;
		m01 = 0;
		m02 = 0;
		m03 = 0;
		m10 = 0;
		m11 = 0;
		m12 = 0;
		m13 = 0;
		m20 = 0;
		m21 = 0;
		m22 = 0;
		m23 = 0;
		m30 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 0;
		return this;
	}

}
