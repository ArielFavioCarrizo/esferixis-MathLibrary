/**
 * Copyright (c) 2017 Ariel Favio Carrizo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'esferixis' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.esferixis.math.numberSet.linearsystem;

import org.junit.Assert;
import org.junit.Test;

import com.esferixis.math.numberSet.NumberSetFactory;
import com.esferixis.math.numberSet.NumberSetMatrix;
import com.esferixis.math.numberSet.NumberSetVector;
import com.esferixis.math.numberSet.linearsystem.LinearSystem;
import com.esferixis.math.pointarithmetic.FloatNumberPoint;

/**
 * @author ariel
 *
 */
public class LinearSystemTest {
	private static final NumberSetFactory<FloatNumberPoint> factory = FloatNumberPoint.FACTORY;
	private static final float TOLERANCE = 0.001f;
	
	private static void assertEqualsVector(NumberSetVector<FloatNumberPoint> expected, NumberSetVector<FloatNumberPoint> actual) {
		Assert.assertTrue( expected.sub(actual).lengthSquared().getValue() < TOLERANCE * TOLERANCE );
	}
	
	private static void testGauss(float[] expected, float[][] A, float[] b) {
		assertEqualsVector( new NumberSetVector<FloatNumberPoint>(factory, expected), LinearSystem.gaussSolve(NumberSetMatrix.create(factory, A), new NumberSetVector<FloatNumberPoint>(factory, b)) );
	}
	
	private static void testGauss(float[] variablesValues, float[][] A) {
		int n_columns = A.length, n_rows = A[0].length;
		float[] b = new float[n_rows];
		for ( int i=0; i<n_rows ; i++ ) {
			float result=0.0f;
			for ( int j=0 ; j<n_columns ; j++ ) {
				result += A[j][i] * variablesValues[j];
			}
			b[i] = result;
		}
		testGauss(variablesValues, A, b);
	}
	
	@Test
	public void trivialTest() {
		testGauss(new float[]{1.0f}, new float[][]{ {1.0f} }, new float[]{1.0f});
	}
	
	@Test
	public void simpleAssignementTest1() {
		testGauss(new float[]{3.0f, 2.0f}, new float[][]{ {1.0f, 0.0f}, {0.0f, 1.0f} }, new float[]{3.0f, 2.0f});
	}
	
	@Test
	public void simpleAssignementTest2() {
		testGauss(new float[]{2.0f, 3.0f}, new float[][]{ {0.0f, 1.0f}, {1.0f, 0.0f} }, new float[]{3.0f, 2.0f});
	}
	
	@Test
	public void system1() {
		testGauss(new float[]{12.0f, 4.0f}, new float[][]{ {1.0f, 2.0f}, {1.0f, -4.0f} }, new float[]{16.0f, 8.0f});
	}
	
	@Test
	public void system2() {
		testGauss(new float[]{12.0f, 4.0f}, new float[][]{ {1.0f, 2.0f}, {1.0f, -4.0f} }, new float[]{16.0f, 8.0f});
	}
	
	@Test
	public void system3() {
		testGauss(new float[]{3.0f, 4.0f, 5.0f}, new float[][]{ {2.0f, 1.0f, -5.0f}, {-6.0f, 7.0f, 2.0f}, {-1.0f, 2.0f, 15.0f} });
	}
	
	@Test
	public void system4() {
		testGauss(new float[]{6.0f, -3.0f, 8.0f, 1.0f}, new float[][]{ {2.0f, -3.0f, 1.0f, -5.0f}, {-6.0f, 20.0f, 7.0f, 2.0f}, {-1.0f, -5.0f, 2.0f, 15.0f}, { 7.0f, 125.0f, -3.0f, 1.0f } });
	}
}
