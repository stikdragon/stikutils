package uk.co.stikman.utils;

import org.junit.Test;

import static org.junit.Assert.*;
import uk.co.stikman.utils.math.Matrix3;
import uk.co.stikman.utils.math.Vector2;

public class TestMatrix3 {

	@Test
	public void testIdentity() {
		Matrix3 m = new Matrix3();
		m.makeIdentity();
		assertEquals(m.asFloatArray()[0], 1, 0.00001);
		assertEquals(m.asFloatArray()[1], 0, 0.00001);
		assertEquals(m.asFloatArray()[2], 0, 0.00001);
		assertEquals(m.asFloatArray()[3], 0, 0.00001);
		assertEquals(m.asFloatArray()[4], 1, 0.00001);
		assertEquals(m.asFloatArray()[5], 0, 0.00001);
		assertEquals(m.asFloatArray()[6], 0, 0.00001);
		assertEquals(m.asFloatArray()[7], 0, 0.00001);
		assertEquals(m.asFloatArray()[8], 1, 0.00001);
	}

	@Test
	public void testInvert() {
		Matrix3 m = new Matrix3();
		m.makeIdentity();
		m.translate(-5, 7);
		m.rotate(23);
		m.scale(0.6f, 2.3f);
		m.translate(8, 4.5f);

		Matrix3 inv = m.inverse();
		assertNotNull(inv);
		Matrix3 inv2 = inv.inverse();
		assertNotNull(inv2);

		float[] fa1 = m.asFloatArray();
		float[] fa2 = inv2.asFloatArray();
		for (int i = 0; i < 9; ++i)
			assertEquals(fa1[i], fa2[i], 0.0001);
	}

	@Test
	public void testVectorTransform() {
		Matrix3 m = new Matrix3();
		m.makeIdentity();
		testTransform(m, 10, 5, 10, 5);

		m.translate(-5, -3);
		testTransform(m, 10, 5, 5, 2);

		m.makeRotation((float) (Math.PI / 2));
		testTransform(m, 10, 5, -5, 10);
	}

	@Test
	public void testSkew() {
		Matrix3 m = new Matrix3();
		m.makeIdentity();
		m.scale(1, (float) Math.sqrt(3.0 / 4));
		m.set(0, 1, 0.5f);

		printTransform(m, 10, 5);
		printTransform(m, 1, 2);
		printTransform(m, 0, 0);

//		System.out.println(m);
		
		Matrix3 skewMatrix = new Matrix3();
		skewMatrix.makeIdentity();
		skewMatrix.scale(1, (float) Math.sqrt(3.0 / 4));
		skewMatrix.skew(0, 0.5f);
//		skewMatrix.set(0, 1, 0.5f);
		System.out.println(skewMatrix.toString());
		
	}

	private void printTransform(Matrix3 m, float x1, float y1) {
		Vector2 v = new Vector2(x1, y1);
		Vector2 u = new Vector2();
		m.multiply(v, u);
		System.out.println(v + " -> " + u);
	}

	private void testTransform(Matrix3 m, float x1, float y1, float x2, float y2) {
		Vector2 v = new Vector2(x1, y1);
		Vector2 u = new Vector2();
		m.multiply(v, u);
		System.out.println(v + " -> " + u);
		assertEquals(x2, u.x, 0.0001);
		assertEquals(y2, u.y, 0.0001);
	}
}
