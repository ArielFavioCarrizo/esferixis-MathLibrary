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
package com.esferixis.math.numberSet;

import org.junit.Assert;

import org.junit.Test;

import com.esferixis.math.numberSet.NumberSetFactory;
import com.esferixis.math.numberSet.NumberSetMatrix;
import com.esferixis.math.pointarithmetic.FloatNumberPoint;

/**
 * @author ariel
 *
 */
public class NumberSetMatrixTest {
	private final NumberSetFactory<FloatNumberPoint> factory = FloatNumberPoint.FACTORY;
	private static final float tolerance = 0.001f;
	
	private static void assertEqualsMatrix(NumberSetMatrix<FloatNumberPoint> expected, NumberSetMatrix<FloatNumberPoint> actual) {
		Assert.assertTrue( expected.sub(actual).frobeniusNormSquared().getValue() < tolerance * tolerance );
	}
	
	@Test
	public void identityEqualsTest() {
		assertEqualsMatrix(NumberSetMatrix.identity(factory, 3), NumberSetMatrix.identity(factory, 3));
	}
	
	@Test
	public void inverseTest1() {
		assertEqualsMatrix(NumberSetMatrix.identity(factory, 3), NumberSetMatrix.identity(factory, 3).inverse());
	}
	
	@Test
	public void inverseTest2() {
		NumberSetMatrix<FloatNumberPoint> original = NumberSetMatrix.create(factory, new float[][]
				{
					{ 4, 8, 10 },
					{ 2, 3, 6},
					{ 6, 1, -15 }
				}
		);
		assertEqualsMatrix(NumberSetMatrix.identity(factory, 3), original.multiply( original.inverse() ));
	}
}
