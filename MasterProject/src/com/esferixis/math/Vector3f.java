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
import java.util.Collection;

import com.esferixis.misc.ArraysExtra;
import com.esferixis.misc.collection.set.BinarySet;
import com.esferixis.misc.nio.BufferUtils;

/**
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class Vector3f extends Vectorf implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6795526022127555942L;
	
	private final float x, y, z;
	
	public static final Vector3f ZERO = new Vector3f(0.0f, 0.0f, 0.0f);
	public static final Vector3f INFINITESIMAL1THQUADRANT = new Vector3f(Float.MIN_NORMAL, Float.MIN_NORMAL, Float.MIN_NORMAL);
	
	/**
	 * @post Crea un vector con los valores de componente especificados
	 */
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * @post Crea un vector a partir del array especificado
	 */
	public Vector3f(float[] components) {
		if ( components.length == 3 ) {
			this.x = components[0];
			this.y = components[1];
			this.z = components[2];
		}
		else {
			throw new IllegalArgumentException("Invalid array length");
		}
	}
	
	/**
	 * @post Crea un vector leyendo de un FloatBuffer
	 */
	public static Vector3f read(FloatBuffer buffer) {
		if ( buffer.capacity() >= 3 ) {
			return new Vector3f(buffer.get(0), buffer.get(1), buffer.get(2) );
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
		buffer.put(new float[]{this.x, this.y, this.z});
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
	 * @post Devuelve la tercer componente
	 */
	public float getZ() {
		return this.z;
	}
	
	/**
	 * @post Devuelve un vector con sentido opuesto
	 */
	public Vector3f opposite() {
		return new Vector3f(-this.x, -this.y, -this.z);
	}
	
	/**
	 * @post Suma el vector con otro y devuelve el resultado
	 */
	public Vector3f add(Vector3f other) {
		return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	/**
	 * @post Resta el vector con otro y devuelve el resultado
	 */
	public Vector3f sub(Vector3f other) {
		return this.add( other.opposite() );
	}
	
	/**
	 * @post Devuelve el producto escalar con el otro vector
	 */
	public float dot(Vector3f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	/**
	 * @post Devuelve el producto vectorial con el otro vector
	 */
	public Vector3f cross(Vector3f other) {
		return new Vector3f(this.y * other.z - other.y * this.z, other.x * this.z - this.x * other.z, this.x * other.y - other.x * this.y);
	}
	
	/**
	 * @post Devuelve el producto del vector con el escalar especificado
	 */
	public Vector3f scale(float b) {
		return new Vector3f(this.x * b, this.y * b, this.z * b);
	}
	
	/**
	 * @post Proyecta el vector especificado sobre éste y devuelve el resultado
	 */
	public Vector3f vectorProjection(Vector3f other) {
		return other.scale( this.dot(this) / other.dot(other) );
	}
	
	/**
	 * @post Calcula la proyección del escalar del vector especificado sobre éste
	 */
	public float scalarProjection(Vector3f other) {
		return other.dot( other.normalise() );
	}
	
	/**
	 * @post Devuelve un par perpendicular unitario entre sí de igual módulo de vectores arbitrario a
	 * 		 éste vector
	 */
	public BinarySet<Vector3f> arbitraryEquallyLengthedPerpendicularPair() {
		/*
		 * Demostración
		 * 
		 * A = (a1, a2, a3)
		 * B = (b1, b2, b3)
		 * C = (c1, c2, c3)
		 * 
		 * A perpendicular B <=> A * B = 0
		 * a1 * b1 + a2 * b2 + a3 * b3 = 0
		 * 
		 * a3 * b3 = -a1 * b1 - a2 * b2
		 * b3 = (-a1 * b1 - a2 * b2) / a3
		 * 
		 * Si b1=b2=1
		 * 
		 * b3 = (-a1 - a2) / a3
		 * 
		 * Por cuestiones de precisión se permutan
		 * las coordenadas de tal manera que el módulo
		 * de a3 sea lo más parecido a 1.
		 * 
		 * |A x B| = |A| * |B| * sin(ang)
		 * Como A es perpendicular a B entonces sin(ang) = 1
		 * => |A x B| = |A| * |B|
		 * Como |C| = |B| => C = (A x B) / |A|
		 * 
		 * A x B = (a2 * b3 - b2 * a3, b1 * a3 - a1 * b3, a1 * b2 - b1 * a2)
		 * Como b1=b2
		 * A x B = (a2 * b3 - a3, a3 - a1 * b3, a1 - a2)
		 * 
		 * Si |A|=1 => C = (A x B)
		 */
		float a[] = this.normalise().toArray();
		float b[] = new float[]{1.0f, 1.0f, 1.0f};
		float c[];
		
		float diffa1 = Math.abs(a[0] - 1.0f);
		float diffa2 = Math.abs(a[1] - 1.0f);
		float diffa3 = Math.abs(a[2] - 1.0f);
		
		int coordenadas[];
		if ( diffa1 < diffa2 ) {
			if ( diffa1 < diffa3 ) {
				coordenadas = new int[]{1, 2, 0};
			}
			else {
				coordenadas = new int[]{0, 1, 2};
			}
		}
		else {
			if ( diffa2 < diffa3 ) {
				coordenadas = new int[]{0, 2, 1};
			}
			else {
				coordenadas = new int[]{0, 1, 2};
			}
		}
		
		a = (float[]) ArraysExtra.permutatedOriginCopy(Float.TYPE, a, coordenadas);
		
		b[2] = (a[0] - a[1]) / a[2];
		c = new float[]{a[1] * b[2] - a[2], a[2] - a[0] * b[2], a[1] - a[2]};
		return new BinarySet<Vector3f>(new Vector3f((float[]) ArraysExtra.permutatedDestinationCopy(Float.TYPE, b, coordenadas)), new Vector3f((float[]) ArraysExtra.permutatedDestinationCopy(Float.TYPE, c, coordenadas)));
	}
	
	/**
	 * @post Devuelve la longitud elevada al cuadrado
	 */
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	/**
	 * @post Devuelve el vector normalizado
	 */
	public Vector3f normalise() {
		return this.scale( 1.0f / (float) Math.sqrt( this.lengthSquared()) );
	}
	
	/**
	 * @post Devuelve el hash
	 */
	public int hashCode() {
		return ( Float.valueOf(this.x).hashCode() + Float.valueOf(this.y).hashCode() + Float.valueOf(this.z).hashCode() );
	}
	
	/**
	 * @post Transforma el vector en un array
	 */
	public float[] toArray() {
		return new float[]{this.x, this.y, this.z};
	}
	
	/**
	 * @post Calcula el centro de la colección de puntos especificada
	 */
	public static Vector3f getCenter(Collection<Vector3f> points) {
		float totalx=0.0f, totaly=0.0f, totalz=0.0f;
		int totalpoints=0;
		for ( Vector3f eachPoint : points ) {
			totalx += eachPoint.x;
			totaly += eachPoint.y;
			totalz += eachPoint.z;
			totalpoints++;
		}
		return new Vector3f(totalx / (float) totalpoints, totaly / (float) totalpoints, totalz / (float) totalpoints);
	}
	
	/**
	 * @post Devuelve si es igual al al otro objeto
	 */
	public boolean equals(Object other) {
		if ( other != null ) {
			if ( other instanceof Vector3f ) {
				Vector3f otherVector = (Vector3f) other;
				return ( this.x == otherVector.x ) && ( this.y == otherVector.y ) && ( this.z == otherVector.z );
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
	 * @post Devuelve un representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.ByteBuffer)
	 */
	@Override
	public void store(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.x);
		byteBuffer.putFloat(this.y);
		byteBuffer.putFloat(this.z);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.FloatBuffer)
	 */
	@Override
	public void store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
		floatBuffer.put(this.z);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#accept(com.esferixis.math.Vectorf.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
