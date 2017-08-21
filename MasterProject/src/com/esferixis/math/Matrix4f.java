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
import java.nio.FloatBuffer;
import java.util.Arrays;

import com.esferixis.misc.nio.BufferUtils;

public class Matrix4f implements Serializable {
	private static final long serialVersionUID = 6775149439126783724L;

	protected final float m[][]; // Valores de cada elemento
	
	protected static final int LENGTH = 4;
	
	// Matriz identidad
	public final static ProportionalMatrix4f IDENTITY = new ProportionalMatrix4f( new float[][]{
			new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 1.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 1.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f, 1.0f }
	});
	
	// Matriz nula
	public final static ProportionalMatrix4f ZERO = new ProportionalMatrix4f( new float[][]{
			new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f, 0.0f }
	});
	
	/**
	 * @pre Tiene que ser una matriz de 4x4
	 * @post Crea una matriz de 4x4 con los elementos especificados, indicando si
	 * 		 tiene escalado proporcional
	 */
	public Matrix4f(float source[][]) {
		if ( source.length == 4 ) {
			this.m = new float[4][];
			for ( int i = 0 ; i < 4 ; i++ ) {
				if ( source[i].length == 4 ) {
					this.m[i] = new float[4];
					for ( int j = 0 ; j < 4 ; j++ ) {
						this.m[i][j] = source[i][j];
					}
				}
				else {
					throw new IllegalArgumentException("Attemped to use a matrix without 4x4 size");
				}
			}
		}
		else {
			throw new IllegalArgumentException("Attemped to use a matrix without 4x4 size");
		}
	}
	
	/**
	 * @post Crea una matriz de transformación para dos dimensiones
	 * 		 con los vectores especificados
	 */
	public Matrix4f(Vector3f localX, Vector3f localY) {
		this(new float[][]{
				{ localX.getX(), localY.getX(), 0.0f, 0.0f },
				{ localX.getY(), localY.getY(), 0.0f, 0.0f },
				{ localX.getZ(), localY.getZ(), 0.0f, 0.0f },
				{ 0.0f,	0.0f, 0.0f, 1.0f }
		} );
	}
	
	/**
	 * @post Devuelve el resultado de sumar la matriz con otra
	 */
	public Matrix4f add(Matrix4f other) {
		final float resultarray[][] = new float[4][];
		for ( int i = 0 ; i < 4; i++ ) {
			resultarray[i] = new float[4];
			for ( int j = 0 ; j < 4 ; j++ ) {
				resultarray[i][j] = this.m[i][j] + other.m[i][j];
			}
		}
		return new Matrix4f(resultarray);
	}
	
	/**
	 * @post Devuelve el resultado de la multiplicación de la matriz con otra
	 */
	public Matrix4f mul(Matrix4f other) {
		final float resultarray[][] = new float[4][];
		for ( int i = 0 ; i < 4 ; i++ ) {
			resultarray[i] = new float[4];
		}
		for ( int i = 0 ; i < 4 ; i++ ) {
			for ( int j = 0 ; j < 4 ; j++ ) {
				resultarray[j][i] = 0.0f;
				for ( int k = 0 ; k < 4 ; k++ ) {
					resultarray[j][i] += this.m[k][i] * other.m[j][k];
				}
			}
		}
		return new Matrix4f(resultarray);
	}
	
	/**
	 * @post Devuelve el resultado de la translación con el vector especificado
	 */
	public Matrix4f translate(Vector3f vector) {
		return
				new Matrix4f( new float[][]{
						new float[]{ 1.0f, 0.0f, 0.0f, 0.0f },
						new float[]{ 0.0f, 1.0f, 0.0f, 0.0f },
						new float[]{ 0.0f, 0.0f, 1.0f, 0.0f },
						new float[]{ vector.getX(), vector.getY(), vector.getZ(), 1.0f }
				} ).mul(this);
	}
	
	/**
	 * @post Devuelve la matriz rotada con el ángulo y el eje especificado
	 */
	public Matrix4f rotate(float angle, Vector3f unitAxis) {
		final float sinProduct = (float) Math.sin(angle);
		final float cosProduct = (float) Math.cos(angle);
		return
			new Matrix4f( new float[][]{
						new float[]{ cosProduct + unitAxis.getX() * unitAxis.getX() * ( 1 - cosProduct), unitAxis.getX() * unitAxis.getY() * ( 1 - cosProduct) - unitAxis.getZ() * sinProduct, unitAxis.getX() * unitAxis.getZ() * cosProduct + unitAxis.getY() * sinProduct, 0.0f },
						new float[]{ unitAxis.getY() * unitAxis.getX() * ( 1 - cosProduct) + unitAxis.getZ() * sinProduct , cosProduct + unitAxis.getY() * unitAxis.getY() * ( 1 - cosProduct), unitAxis.getY() * unitAxis.getZ() * cosProduct - unitAxis.getX() * sinProduct, 0.0f },
						new float[]{ unitAxis.getZ() * unitAxis.getX() * ( 1 - cosProduct) - unitAxis.getY() * sinProduct, unitAxis.getZ() * unitAxis.getY() * ( 1 - cosProduct ) + unitAxis.getX() * sinProduct, cosProduct + unitAxis.getZ() * unitAxis.getZ() * (1 - cosProduct ), 0.0f },
						new float[]{ 0.0f, 0.0f, 0.0f, 1.0f }
			} ).mul(this);	
	}
	
	/**
	 * @post Devuelve la matriz escalada con el vector especificado
	 */
	public Matrix4f scale(Vector3f vector) {
		return new Matrix4f(new float[][]{
			new float[]{ vector.getX(), 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, vector.getY(), 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, vector.getZ(), 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f, 1.0f }
		}).mul(this);
	}
	
	/**
	 * @post Devuelve la matriz multiplicada con el escalar especificado
	 */
	public Matrix4f scale(float scalar) {
		return this.scale(new Vector3f(scalar, scalar, scalar));
	}
	
	/**
	 * @post Devuelve la matriz transpuesta
	 */
	public Matrix4f transpose() {
		final float[][] matrixArray = new float[4][];
		for ( int j = 0 ; j < 4 ; j++ ) {
			matrixArray[j] = new float[4];
			for ( int i = 0; i < 4 ; i++ ) {
				matrixArray[j][i] = this.m[i][j];
			}
		}
		return new Matrix4f(matrixArray);
	}
	
	/**
	 * @post Devuelve el determinante de la matriz
	 */
	public float determinant() {
		return this.m[0][3]*this.m[1][2]*this.m[2][1]*this.m[3][0] - this.m[0][2]*this.m[1][3]*this.m[2][1]*this.m[3][0] - this.m[0][3]*this.m[1][1]*this.m[2][2]*this.m[3][0] + this.m[0][1]*this.m[1][3]*this.m[2][2]*this.m[3][0]+
		this.m[0][2]*this.m[1][1]*this.m[2][3]*this.m[3][0] - this.m[0][1]*this.m[1][2]*this.m[2][3]*this.m[3][0] - this.m[0][3]*this.m[1][2]*this.m[2][0]*this.m[3][1] + this.m[0][2]*this.m[1][3]*this.m[2][0]*this.m[3][1]+
		this.m[0][3]*this.m[1][0]*this.m[2][2]*this.m[3][1] - this.m[0][0]*this.m[1][3]*this.m[2][2]*this.m[3][1] - this.m[0][2]*this.m[1][0]*this.m[2][3]*this.m[3][1] + this.m[0][0]*this.m[1][2]*this.m[2][3]*this.m[3][1]+
		this.m[0][3]*this.m[1][1]*this.m[2][0]*this.m[3][2] - this.m[0][1]*this.m[1][3]*this.m[2][0]*this.m[3][2] - this.m[0][3]*this.m[1][0]*this.m[2][1]*this.m[3][2] + this.m[0][0]*this.m[1][3]*this.m[2][1]*this.m[3][2]+
		this.m[0][1]*this.m[1][0]*this.m[2][3]*this.m[3][2] - this.m[0][0]*this.m[1][1]*this.m[2][3]*this.m[3][2] - this.m[0][2]*this.m[1][1]*this.m[2][0]*this.m[3][3] + this.m[0][1]*this.m[1][2]*this.m[2][0]*this.m[3][3]+
		this.m[0][2]*this.m[1][0]*this.m[2][1]*this.m[3][3] - this.m[0][0]*this.m[1][2]*this.m[2][1]*this.m[3][3] - this.m[0][1]*this.m[1][0]*this.m[2][2]*this.m[3][3] + this.m[0][0]*this.m[1][1]*this.m[2][2]*this.m[3][3];
	}
	
	/**
	 * @post Devuelve la inversa de la matriz
	 */
	public Matrix4f invert() {
		final float determinant = this.determinant();
		if ( determinant != 0.0f) {
			return ( new Matrix4f( new float[][]{
			new float[]{
				this.m[1][2]*this.m[2][3]*this.m[3][1] - this.m[1][3]*this.m[2][2]*this.m[3][1] + this.m[1][3]*this.m[2][1]*this.m[3][2] - this.m[1][1]*this.m[2][3]*this.m[3][2] - this.m[1][2]*this.m[2][1]*this.m[3][3] + this.m[1][1]*this.m[2][2]*this.m[3][3], 
				this.m[0][3]*this.m[2][2]*this.m[3][1] - this.m[0][2]*this.m[2][3]*this.m[3][1] - this.m[0][3]*this.m[2][1]*this.m[3][2] + this.m[0][1]*this.m[2][3]*this.m[3][2] + this.m[0][2]*this.m[2][1]*this.m[3][3] - this.m[0][1]*this.m[2][2]*this.m[3][3],
				this.m[0][2]*this.m[1][3]*this.m[3][1] - this.m[0][3]*this.m[1][2]*this.m[3][1] + this.m[0][3]*this.m[1][1]*this.m[3][2] - this.m[0][1]*this.m[1][3]*this.m[3][2] - this.m[0][2]*this.m[1][1]*this.m[3][3] + this.m[0][1]*this.m[1][2]*this.m[3][3],
				this.m[0][3]*this.m[1][2]*this.m[2][1] - this.m[0][2]*this.m[1][3]*this.m[2][1] - this.m[0][3]*this.m[1][1]*this.m[2][2] + this.m[0][1]*this.m[1][3]*this.m[2][2] + this.m[0][2]*this.m[1][1]*this.m[2][3] - this.m[0][1]*this.m[1][2]*this.m[2][3]
				}
				,
				new float[]{
				this.m[1][3]*this.m[2][2]*this.m[3][0] - this.m[1][2]*this.m[2][3]*this.m[3][0] - this.m[1][3]*this.m[2][0]*this.m[3][2] + this.m[1][0]*this.m[2][3]*this.m[3][2] + this.m[1][2]*this.m[2][0]*this.m[3][3] - this.m[1][0]*this.m[2][2]*this.m[3][3],
				this.m[0][2]*this.m[2][3]*this.m[3][0] - this.m[0][3]*this.m[2][2]*this.m[3][0] + this.m[0][3]*this.m[2][0]*this.m[3][2] - this.m[0][0]*this.m[2][3]*this.m[3][2] - this.m[0][2]*this.m[2][0]*this.m[3][3] + this.m[0][0]*this.m[2][2]*this.m[3][3],
				this.m[0][3]*this.m[1][2]*this.m[3][0] - this.m[0][2]*this.m[1][3]*this.m[3][0] - this.m[0][3]*this.m[1][0]*this.m[3][2] + this.m[0][0]*this.m[1][3]*this.m[3][2] + this.m[0][2]*this.m[1][0]*this.m[3][3] - this.m[0][0]*this.m[1][2]*this.m[3][3],
				this.m[0][2]*this.m[1][3]*this.m[2][0] - this.m[0][3]*this.m[1][2]*this.m[2][0] + this.m[0][3]*this.m[1][0]*this.m[2][2] - this.m[0][0]*this.m[1][3]*this.m[2][2] - this.m[0][2]*this.m[1][0]*this.m[2][3] + this.m[0][0]*this.m[1][2]*this.m[2][3]
				}
				,
				new float[]{
				this.m[1][1]*this.m[2][3]*this.m[3][0] - this.m[1][3]*this.m[2][1]*this.m[3][0] + this.m[1][3]*this.m[2][0]*this.m[3][1] - this.m[1][0]*this.m[2][3]*this.m[3][1] - this.m[1][1]*this.m[2][0]*this.m[3][3] + this.m[1][0]*this.m[2][1]*this.m[3][3],
				this.m[0][3]*this.m[2][1]*this.m[3][0] - this.m[0][1]*this.m[2][3]*this.m[3][0] - this.m[0][3]*this.m[2][0]*this.m[3][1] + this.m[0][0]*this.m[2][3]*this.m[3][1] + this.m[0][1]*this.m[2][0]*this.m[3][3] - this.m[0][0]*this.m[2][1]*this.m[3][3],
				this.m[0][1]*this.m[1][3]*this.m[3][0] - this.m[0][3]*this.m[1][1]*this.m[3][0] + this.m[0][3]*this.m[1][0]*this.m[3][1] - this.m[0][0]*this.m[1][3]*this.m[3][1] - this.m[0][1]*this.m[1][0]*this.m[3][3] + this.m[0][0]*this.m[1][1]*this.m[3][3],
				this.m[0][3]*this.m[1][1]*this.m[2][0] - this.m[0][1]*this.m[1][3]*this.m[2][0] - this.m[0][3]*this.m[1][0]*this.m[2][1] + this.m[0][0]*this.m[1][3]*this.m[2][1] + this.m[0][1]*this.m[1][0]*this.m[2][3] - this.m[0][0]*this.m[1][1]*this.m[2][3]
				}
				,
				new float[]{
				this.m[1][2]*this.m[2][1]*this.m[3][0] - this.m[1][1]*this.m[2][2]*this.m[3][0] - this.m[1][2]*this.m[2][0]*this.m[3][1] + this.m[1][0]*this.m[2][2]*this.m[3][1] + this.m[1][1]*this.m[2][0]*this.m[3][2] - this.m[1][0]*this.m[2][1]*this.m[3][2],
				this.m[0][1]*this.m[2][2]*this.m[3][0] - this.m[0][2]*this.m[2][1]*this.m[3][0] + this.m[0][2]*this.m[2][0]*this.m[3][1] - this.m[0][0]*this.m[2][2]*this.m[3][1] - this.m[0][1]*this.m[2][0]*this.m[3][2] + this.m[0][0]*this.m[2][1]*this.m[3][2],
				this.m[0][2]*this.m[1][1]*this.m[3][0] - this.m[0][1]*this.m[1][2]*this.m[3][0] - this.m[0][2]*this.m[1][0]*this.m[3][1] + this.m[0][0]*this.m[1][2]*this.m[3][1] + this.m[0][1]*this.m[1][0]*this.m[3][2] - this.m[0][0]*this.m[1][1]*this.m[3][2],
				this.m[0][1]*this.m[1][2]*this.m[2][0] - this.m[0][2]*this.m[1][1]*this.m[2][0] + this.m[0][2]*this.m[1][0]*this.m[2][1] - this.m[0][0]*this.m[1][2]*this.m[2][1] - this.m[0][1]*this.m[1][0]*this.m[2][2] + this.m[0][0]*this.m[1][1]*this.m[2][2]
				}
			} ) ).scale(1.0f/determinant);
		}
		else {
			throw new ArithmeticException("The inverse doesn't exists");
		}
	}
	
	/**
	 * @post Devuelve el punto especificado transformado por la matriz
	 */
	public Vector3f transformPoint(Vector3f vector) {
		return new Vector3f(
				this.m[0][0] * vector.getX() + this.m[1][0] * vector.getY() + this.m[2][0] * vector.getZ() + this.m[3][0],
				this.m[0][1] * vector.getX() + this.m[1][1] * vector.getY() + this.m[2][1] * vector.getZ() + this.m[3][1],
				this.m[0][2] * vector.getX() + this.m[1][2] * vector.getY() + this.m[2][2] * vector.getZ() + this.m[3][2]
		);
	}
	
	/**
	 * @post Devuelve la dirección especificada transformada por la matriz
	 */
	public Vector3f transformDirection(Vector3f vector) {
		return new Vector3f(
				this.m[0][0] * vector.getX() + this.m[1][0] * vector.getY() + this.m[2][0] * vector.getZ(),
				this.m[0][1] * vector.getX() + this.m[1][1] * vector.getY() + this.m[2][1] * vector.getZ(),
				this.m[0][2] * vector.getX() + this.m[1][2] * vector.getY() + this.m[2][2] * vector.getZ()
		);
	}
	
	/**
	 * @post Devuelve el punto especificado transformado por la matriz
	 */
	public Vector3f transformPoint(Vector2f vector) {
		return new Vector3f(
				this.m[0][0] * vector.getX() + this.m[1][0] * vector.getY() + this.m[3][0],
				this.m[0][1] * vector.getX() + this.m[1][1] * vector.getY() + this.m[3][1],
				this.m[0][2] * vector.getX() + this.m[1][2] * vector.getY() + this.m[3][2]
		);
	}
	
	/**
	 * @post Devuelve la dirección especificada transformada por la matriz
	 */
	public Vector3f transformDirection(Vector2f vector) {
		return new Vector3f(
				this.m[0][0] * vector.getX() + this.m[1][0] * vector.getY(),
				this.m[0][1] * vector.getX() + this.m[1][1] * vector.getY(),
				this.m[0][2] * vector.getX() + this.m[1][2] * vector.getY()
		);
	}
	
	/**
	 * @post Devuelve la componente en la ubicación especificada
	 */
	public float get(int i, int j) {
		Float value = null;
		if ( ( i >= 0 ) && ( i < LENGTH ) ) {
			if ( ( j >= 0 ) && ( j < LENGTH ) ) {
				value = this.m[i][j];
			}
		}
		
		if ( value != null ) {
			return value;
		}
		else {
			throw new IllegalArgumentException("Invalid component position");
		}
	}
	
	/**
	 * @post Devuelve el hash de la matriz
	 */
	public int hashCode() {
		return Arrays.deepHashCode(this.m);
	}
	
	/**
	 * @post Devuelve la matriz en un buffer
	 */
	public FloatBuffer store() {
		float[] unionArray = new float[16];
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		for ( int i=0 ; i < this.m.length ; i++ ) {
			for ( int j=0 ; j < this.m.length ; j++ ) {
				unionArray[i*4+j] = this.m[i][j];
			}
		}
		buffer.put(unionArray);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * @post Devuelve si la matriz al objeto especificado
	 */
	public boolean equals(Object other) {
		if ( other != null ) {
			if ( other instanceof Matrix4f ) {
				return Arrays.deepEquals( ((Matrix4f) other).m, this.m);
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
	 * @post Realiza una conversión a cadena de texto
	 */
	public String toString() {
		String result = "Matrix4f( [";
		for ( int i = 0; i<this.m.length ; i++ ) {
			result += "[";
			for ( int j = 0; j<this.m.length ; j++ ) {
				result += Float.toString(this.m[i][j]);
				if ( j < this.m.length-1 ) {
					result += ", ";
				}
			}
			result += "]";
			
			if ( i < this.m.length-1 ) {
				result += ", ";
			}
		}
		result += "] )";
		return result;
	}
}
