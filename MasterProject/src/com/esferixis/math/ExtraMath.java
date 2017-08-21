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

public class ExtraMath {
	private ExtraMath() {}
	
	private static final double ROOTTWO = Math.sqrt(2.0f);
	
	public static final float doublePI = (float) Math.PI * 2.0f;
	
	/**
	 * @post Calcula el factorial del número especificado
	 * @return
	 */
	public static int factorial(int n) {
		if ( n <= 1 ) {
			return 1;
		}
		else {
			return n * factorial(n-1);
		}
	}
	
	/**
	 * @post Devuelve un número aleatorio entre -1.0 y 1.0, en base al entero especificado
	 * @param t
	 * @return
	 */
	public static float rng_int(int x) {
	   	x = (x<<13) ^ x;
    	return 1.0f - (float) ( (x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0f;
	}
	
	/**
	 * @post Efectúa una interpolación polinómica
	 */
	public static float polynomialInterpolation(float a, float b, float x) {
		return a+(b-a)*x*x*(3-2*x);
	}
	
	/**
	 * @post Devuelve el cuadro del valor especificado
	 */
	public static float square(float value) {
		return value * value;
	}
	
	/**
	 * @pre El iterable de valores no puede ser nulo ni tampoco ninguno de sus elementos
	 * @post Devuelve el mínimo, en caso de que no haya elementos devuelve null
	 */
	public static Float min(Iterable<Float> values) {
		if ( values != null ) {
			Float minValue = null;
			
			for ( Float eachValue : values ) {
				
				if ( eachValue != null ) {
					if ( ( minValue == null ) || ( eachValue < minValue ) ) {
						minValue = eachValue;
					}
				}
				else {
					throw new NullPointerException();
				}
			}
			
			return minValue;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El iterable de valores no puede ser nulo ni tampoco ninguno de sus elementos
	 * @post Devuelve el máximo, en caso de que no haya valores devuelve null
	 */
	public static Float max(Iterable<Float> values) {
		Float maxValue = null;
		
		if ( values != null ) {
			for ( Float eachValue : values ) {
				
				if ( eachValue != null ) {
					if ( ( maxValue == null ) || ( eachValue > maxValue ) ) {
						maxValue = eachValue;
					}
				}
				else {
					throw new NullPointerException();
				}
			}
			
			return maxValue;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El array no puede ser nulo ni tampoco ninguno de sus elementos
	 * @post Devuelve el mínimo, en caso de que no haya elementos devuelve null
	 */
	public static Float min(Float... values) {
		if ( values != null ) {
			return min(Arrays.asList(values));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El array no puede ser nulo ni tampoco ninguno de sus elementos
	 * @post Devuelve el máximo, en caso de que no haya elementos devuelve null
	 */
	public static Float max(Float... values) {
		if ( values != null ) {
			return max(Arrays.asList(values));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve si está dentro del intervalo especificado
	 */
	public static boolean containedByInterval(float scalar, float min, float max) {
		return (( scalar >= min ) && ( scalar <= max ));
	}
	
	/**
	 * @post Interpola linealmente los valores especificados, con el factor especificado
	 */
	public static float linearInterpolation(float value1, float value2, float factor) {
		return value1 * (1.0f - factor) + value2 * factor;
	}
	
	/**
	 * @pre Los puntos no pueden ser nulos
	 * @post Interpola linealmente los puntos especificados, con el factor especificado
	 */
	public static Vector2f linearInterpolation(Vector2f point1, Vector2f point2, float factor) {
		if ( ( point1 != null ) && ( point2 != null ) ) {
			return point1.add(point2.sub(point1).scale(factor));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Los puntos no pueden ser nulos
	 * @post Interpola linealmente los puntos especificados, con el factor especificado
	 */
	public static Vector3f linearInterpolation(Vector3f point1, Vector3f point2, float factor) {
		if ( ( point1 != null ) && ( point2 != null ) ) {
			return point1.add(point2.sub(point1).scale(factor));
		}
		else {
			throw new NullPointerException();
		}
	}
}
