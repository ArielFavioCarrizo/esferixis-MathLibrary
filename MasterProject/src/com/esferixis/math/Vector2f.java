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

import java.io.Serializable;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.esferixis.misc.nio.BufferUtils;

public final class Vector2f extends Vectorf implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1744699308902205458L;

	private final float x, y;
	
	public static final Vector2f ZERO = new Vector2f(0.0f, 0.0f);
	
	/**
	 * @post Crea un vector con los valores de componente especificados
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @post Crea un vector con el ángulo especificado
	 */
	public static Vector2f unitPolar(float angle) {
		return new Vector2f((float) Math.cos( (float) angle), (float) Math.sin( (float) angle));
	}
	
	/**
	 * @post Crea un vector a partir del array especificado
	 */
	public Vector2f(float[] components) {
		if ( components.length == 2 ) {
			this.x = components[0];
			this.y = components[1];
		}
		else {
			throw new IllegalArgumentException("Invalid array length");
		}
	}
	
	/**
	 * @post Crea un vector leyendo de un FloatBuffer
	 */
	public static Vector2f read(FloatBuffer buffer) {
		if ( buffer.capacity() >= 2 ) {
			return new Vector2f(buffer.get(0), buffer.get(1) );
		}
		else {
			throw new BufferUnderflowException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store()
	 */
	@Override
	public FloatBuffer store() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		buffer.put(new float[]{this.x, this.y});
		buffer.flip();
		return buffer;
	}
	
	/**
	 * @post Devuelve la primer componente
	 */
	public float getX() {
		return this.x;
	}
	
	/**
	 * @post Devuelve la segunda componente
	 */
	public float getY() {
		return this.y;
	}
	
	/**
	 * @post Devuelve un vector con sentido opuesto
	 */
	public Vector2f opposite() {
		return new Vector2f(-this.x, -this.y);
	}
	
	/**
	 * @post Suma el vector con otro y devuelve el resultado
	 */
	public Vector2f add(Vector2f other) {
		return new Vector2f(this.x + other.x, this.y + other.y);
	}
	
	/**
	 * @post Resta el vector con otro y devuelve el resultado
	 */
	public Vector2f sub(Vector2f other) {
		return this.add( other.opposite() );
	}
	
	/**
	 * @post Devuelve el producto escalar con el otro vector
	 */
	public float dot(Vector2f other) {
		return this.x * other.x + this.y * other.y;
	}
	
	/**
	 * @post Devuelve el producto del vector con el escalar especificado
	 */
	public Vector2f scale(float b) {
		return new Vector2f(this.x * b, this.y * b);
	}
	
	/**
	 * @post Rota el vector 90° en sentido antihorario
	 */
	public Vector2f rotate90AnticlockWise() {
		return new Vector2f(-this.y, this.x);
	}
	
	/**
	 * @post Rota el vector 90° en sentido horario
	 */
	public Vector2f rotate90ClockWise() {
		return new Vector2f(this.y, -this.x);
	}
	
	/**
	 * @pre El vector especificado no puede ser nulo
	 * @post Devuelve si es paralelo
	 */
	public boolean isParallel(Vector2f other) {
		if ( other != null ) {
			return ( this.dot(other) * this.dot(other) == this.lengthSquared() * other.lengthSquared());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Proyecta el vector especificado sobre éste y devuelve el resultado
	 */
	public Vector2f vectorProjection(Vector2f other) {
		return this.scale( other.dot(this) / this.lengthSquared() );
	}
	
	/**
	 * @post Calcula la proyección del escalar del vector especificado sobre éste
	 */
	public float scalarProjection(Vector2f other) {
		if ( other != null ) {
			return this.normalise().dot(other);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la longitud elevada al cuadrado
	 */
	@Override
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}
	
	/**
	 * @post Devuelve el vector normalizado
	 */
	public Vector2f normalise() {
		return this.scale( 1.0f / (float) Math.sqrt( this.lengthSquared()) );
	}
	
	/**
	 * @post Devuelve el ángulo formado con el eje X
	 */
	public float getAngle() {
		return (float) Math.atan2(this.y, this.x);
	}
	
	/**
	 * @post Devuelve el vector unitario con el ángulo que forma con el eje X especificado
	 */
	public static Vector2f getUnitVectorWithAngle(float angle) {
		return new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
	}
	
	/**
	 * @post Devuelve el hash
	 */
	public int hashCode() {
		return ( Float.valueOf(this.x).hashCode() + Float.valueOf(this.y).hashCode());
	}
	
	/**
	 * @post Devuelve si es igual al al otro objeto
	 */
	public boolean equals(Object other) {
		if ( other != null ) {
			if ( other instanceof Vector2f ) {
				Vector2f otherVector = (Vector2f) other;
				return ( this.x == otherVector.x ) && ( this.y == otherVector.y );
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * @post Parsea el string especificado
	 */
	public static Vector2f parse(String valueString) throws NumberFormatException {
		if ( valueString != null ) {
			valueString = valueString.trim();
			
			if ( ( valueString.charAt(0) == '(' ) && ( valueString.charAt(valueString.length()-1) == ')' ) ) {
				valueString = valueString.substring(1, valueString.length()-1);
				
				String[] coordinates = valueString.split(",");
				
				if ( coordinates.length == 2 ) {
					return new Vector2f(Float.parseFloat(coordinates[0]), Float.parseFloat(coordinates[1]));
				}
				else {
					throw new NumberFormatException("Expected two coordinates");
				}
			}
			else {
				throw new NumberFormatException("Missing braces");
			}
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * @post Devuelve un representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.ByteBuffer)
	 */
	@Override
	public void store(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.x);
		byteBuffer.putFloat(this.y);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.FloatBuffer)
	 */
	@Override
	public void store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#accept(com.esferixis.math.Vectorf.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
