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
package com.esferixis.math.intervalarithmetic;

import java.io.Serializable;

/**
 * @author ariel
 *
 */
public final class FloatClosedInterval implements Comparable<FloatClosedInterval>, Serializable {
	private static final long serialVersionUID = -5685188328985627645L;
	
	public static final FloatClosedInterval WIDESTFINITE = new FloatClosedInterval(-Float.MAX_VALUE, Float.MAX_VALUE);
	public static final FloatClosedInterval ALLRANGE = new FloatClosedInterval(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	
	private final float min, max;
	
	/**
	 * @pre El mínimo y el máximo no pueden ser nulos,
	 * 		y el mínimo tiene que ser inferior al máximo
	 * @post Crea el intervalo cerrado con el mínimo y el máximo especificados
	 */
	public FloatClosedInterval(float min, float max) {
		if ( min <= max ) {
			this.min = min;
			this.max = max;
		}
		else {
			throw new IllegalArgumentException("Invalid min, max values");
		}
	}
	
	/**
	 * @post Crea el intervalo cerrado con el valor especificado
	 */
	public FloatClosedInterval(float value) {
		this(value, value);
	}
	
	/**
	 * @post Devuelve el mínimo
	 */
	public float getMin() {
		return this.min;
	}
	
	/**
	 * @post Devuelve el máximo
	 */
	public float getMax() {
		return this.max;
	}
	
	/**
	 * @post Devuelve el punto medio
	 */
	public float midPoint() {
		return ( this.min + this.max ) / 2.0f;
	}
	
	/**
	 * @post Devuelve el opuesto
	 */
	public FloatClosedInterval opposite() {
		return new FloatClosedInterval(-this.max, -this.min);
	}
	
	/**
	 * @post Devuelve el resultado de realizar la suma con el número especificado
	 */
	public FloatClosedInterval add(float value) {
		return new FloatClosedInterval(this.getMin()+value, this.getMax()+value);
	}
	
	/**
	 * @post Devuelve el resultado de realizar la resta con el número especificado
	 */
	public FloatClosedInterval sub(float value) {
		return this.add(-value);
	}
	
	/**
	 * @post Multiplica el intervalo por el valor especificado
	 */
	public FloatClosedInterval mul(float value) {
		if ( value >= 0 ) {
			return new FloatClosedInterval(this.getMin()*value, this.getMax()*value);
		}
		else {
			return new FloatClosedInterval(this.getMax()*value, this.getMin()*value);
		}
	}
	
	/**
	 * @post Divide el intervalo por el valor especificado
	 */
	public FloatClosedInterval divide(float value) {
		final FloatClosedInterval result;
		
		if ( value >= 0.0f) {
			result = new FloatClosedInterval(this.min / value, this.max / value);
		}
		else {
			result = new FloatClosedInterval(this.max / value, this.min / value);
		}
		
		return result;
	}
	
	/**
	 * @post Devuelve si contiene el valor especificado
	 */
	public boolean contains(float value) {
		return ( value >= this.min ) && ( value <= this.max );
	}
	
	/**
	 * @post Devuelve si contiene el valor especificado, excluyendo los extremes
	 */
	public boolean containsExcludingExtremes(float value) {
		return ( value > this.min ) && ( value < this.max );
	}
	
	/**
	 * @post Devuelve si es igual al entero especificado
	 */
	public boolean equals(float value) {
		return ( this.getMin() == value ) && ( this.getMax() == value );
	}
	
	/**
	 * @post Devuelve una interpolación lineal con el parámetro
	 * 		 especificado
	 */
	public float linearInterpolation(float t) {
		return this.min + (this.max - this.min) * t;
	}
	
	/**
	 * @post Devuelve la longitud
	 */
	public float length() {
		return this.getMax() - this.getMin();
	}
	
	/**
	 * @post Devuelve el valor si se trata de uno, caso contrario devuelve null
	 */
	public Float getValue() {
		if ( this.isPoint() ) {
			return this.min;
		}
		else {
			return null;
		}
	}
	
	/**
	 * @post Devuelve el valor especificado si
	 * 		 está dentro del intervalo, caso contrario devuelve
	 * 		 null.
	 * 		 También devuelve null si el valor especificado es nulo
	 */
	public Float filter(Float value) {
		return ( value != null && this.contains(value) ) ? value : null;
	}
	
	/**
	 * @post Devuelve la intersección con el intervalo especificado,
	 * 		 si es nulo o es vacío devuelve null
	 */
	public FloatClosedInterval intersection(FloatClosedInterval other) {
		final FloatClosedInterval result;
		
		if ( other != null ) {
			float min = Math.max(this.getMin(), other.getMin());
			float max = Math.min(this.getMax(), other.getMax());
			
			if ( max >= min ) {
				result = new FloatClosedInterval(min, max);
			}
			else {
				result = null;
			}
		}
		else {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * @post Devuelve la envoltura de la unión de los intervalos especificados
	 */
	public FloatClosedInterval unionBound(FloatClosedInterval other) {
		final FloatClosedInterval result;
		
		if ( other != null ) {
			float min = Math.min(this.getMin(), other.getMin());
			float max = Math.max(this.getMax(), other.getMax());
			
			result = new FloatClosedInterval(min, max);
		}
		else {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * @post Devuelve si es un punto
	 */
	public boolean isPoint() {
		return ( this.getMin() == this.getMax() );
	}
	
	/**
	 * @post Devuelve la comparación del intervalo con el intervalo especificado
	 * 		 Observación: Si los intervalos se intersecan devuelve cero
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(FloatClosedInterval other) {
		if ( other != null ) {
			if ( this.getMax() < other.getMin() ) {
				return -1;
			}
			else if ( this.getMin() > other.getMax() ) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	
	/**
	 * @post Parsea la cadena de carácteres especificada y devuelve el intervalo
	 */
	public static FloatClosedInterval parse(String valueString) {
		if ( valueString != null ) {
			valueString = valueString.trim();
			
			if ( ( valueString.charAt(0) == '[' ) && ( valueString.charAt(valueString.length()-1) == ']' ) ) {
				final String[] parameters = valueString.substring(1, valueString.length()-1).split(",");
				
				if ( parameters.length == 2 ) {
					return new FloatClosedInterval( Float.parseFloat(parameters[0]), Float.parseFloat(parameters[1]) );
				}
				else {
					throw new NumberFormatException("Expected two coordinates");
				}
			}
			else {
				throw new NumberFormatException("Missing square brackets");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return Float.valueOf(this.getMin()).hashCode() + Float.valueOf(this.getMax()).hashCode() * 31;
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof FloatClosedInterval ) ) {
			final FloatClosedInterval otherFloatClosedInterval = (FloatClosedInterval) other;
			
			return (otherFloatClosedInterval.getMin() == this.getMin()) && ( otherFloatClosedInterval.getMax() == this.getMax() );
		}
		else {
			return false;
		}
	}
	
	/**
	 * @post Devuelve una representación del intervalo
	 */
	@Override
	public String toString() {
		return "[" + this.getMin() + "," + this.getMax() + "]";
	}
}
