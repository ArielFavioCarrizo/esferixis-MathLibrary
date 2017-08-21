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

package com.esferixis.math;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Ecuación de segundo grado
 *  
 * @author Ariel Favio Carrizo
 *
 */
public final class QuadraticEquation {
	private QuadraticEquation() {};
	
	/**
	 * @post Devuelve la solución con los coeficientes especificados
	 */
	public static List<Double> resolve(double a, double b, double c) {
		List<Double> roots;
		
		final double discriminant = b * b - 4.0f * a * c;
		if ( discriminant < 0.0f ) { // No hay soluciones
			roots = Collections.unmodifiableList( Arrays.asList( new Double[0] ) );
		}
		else if ( discriminant == 0.0f ) { // Hay una solución
			roots = Collections.unmodifiableList( Arrays.asList( -b / (2.0f * a) ) );
		}
		else { // Hay dos soluciones
			final double discriminantSquareRoot = Math.sqrt(discriminant);
			
			roots = Arrays.asList( (-b - discriminantSquareRoot) / (2.0d * a), ( -b + discriminantSquareRoot ) / (2.0d * a) );
		}
		return roots;
	}
	
	/**
	 * @post Devuelve la solución con los coeficientes especificados
	 */
	public static List<Float> resolve(float a, float b, float c) {
		List<Float> roots;
		
		if ( a == 0.0f ) {
			roots = Collections.unmodifiableList( Arrays.asList( -c/b  ) );
		}
		else {
			
			final double discriminant = b * b - 4.0f * a * c;
			if ( discriminant < 0.0f ) { // No hay soluciones
				roots = Collections.unmodifiableList( Arrays.asList( new Float[0] ) );
			}
			else if ( discriminant == 0.0f ) { // Hay una solución
				roots = Collections.unmodifiableList( Arrays.asList( -b / (2.0f * a) ) );
			}
			else { // Hay dos soluciones
				final float discriminantSquareRoot = (float) Math.sqrt(discriminant);
				
				roots = Arrays.asList((-b - discriminantSquareRoot) / (2.0f * a), (-b + discriminantSquareRoot) / (2.0f * a));
				
				/*
				if ( b < 0.0d ) {
					roots = Arrays.asList( (-b + discriminantSquareRoot) / (2.0f * a), 2.0f * c / ( -b + discriminantSquareRoot ) );
				}
				else {
					roots = Arrays.asList( (-b - discriminantSquareRoot) / (2.0f / a), 2.0f * c / ( -b - discriminantSquareRoot ) );
				}
				*/
			}
		}
		return roots;
	}
}
