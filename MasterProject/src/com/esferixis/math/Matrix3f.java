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
import java.util.List;

import com.esferixis.misc.nio.BufferUtils;
import com.esferixis.misc.strings.parser.ConstantFunctionParser;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.FunctionParser;
import com.esferixis.misc.strings.parser.ParametrizedFunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public class Matrix3f implements Serializable {
	private static final long serialVersionUID = -3319823569631840145L;

	protected final float m[][]; // Valores de cada elemento
	
	protected static final int LENGTH = 3;
	
	// Matriz identidad
	public final static ProportionalMatrix3f IDENTITY = new ProportionalMatrix3f( new float[][]{
			new float[]{ 1.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 1.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 1.0f },
	});
	
	// Matriz nula
	public final static ProportionalMatrix3f ZERO = new ProportionalMatrix3f( new float[][]{
			new float[]{ 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f },
			new float[]{ 0.0f, 0.0f, 0.0f },
	});
	
	private static final ExpressionParser<Matrix3f> PARSER = new ExpressionParser<Matrix3f>(
		new ConstantFunctionParser<Matrix3f>("IDENTITY", Matrix3f.IDENTITY),
		new ConstantFunctionParser<Matrix3f>("ZERO", Matrix3f.ZERO),
		new ParametrizedFunctionParser<Matrix3f>("add") {

			@Override
			public Matrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return Matrix3f.parse(parameters.get(0)).add(Matrix3f.parse(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<Matrix3f>("mul") {

			@Override
			public Matrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return Matrix3f.parse(parameters.get(0)).mul(Matrix3f.parse(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<Matrix3f>("translate") {

			@Override
			public Matrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return Matrix3f.parse(parameters.get(0)).translate(Vector2f.parse(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<Matrix3f>("rotate") {

			@Override
			public Matrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return Matrix3f.parse(parameters.get(0)).rotate(Float.parseFloat(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<Matrix3f>("scale") {

			@Override
			public Matrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return Matrix3f.parse(parameters.get(0)).scale(Vector2f.parse(parameters.get(1)));
			}
			
		},
		new FunctionParser<Matrix3f>("transpose") {

			@Override
			public Matrix3f parse(String string) throws ParseException {
				return Matrix3f.parse(string).transpose();
			}
			
		},
		new FunctionParser<Matrix3f>("invert") {

			@Override
			public Matrix3f parse(String string) throws ParseException {
				return Matrix3f.parse(string).invert();
			}
			
		}
	);
	
	public static Matrix3f parse(String string) {
		return PARSER.parse(string, Matrix3f.class);
	}
	
	/**
	 * @pre Tiene que ser una matriz de 4x4
	 * @post Crea una matriz de 4x4 con los elementos especificados, indicando si
	 * 		 tiene escalado proporcional
	 */
	public Matrix3f(float source[][]) {
		if ( source.length == LENGTH ) {
			this.m = new float[LENGTH][LENGTH];
			for ( int i = 0 ; i < LENGTH ; i++ ) {
				if ( source[i].length == LENGTH ) {
					this.m[i] = new float[LENGTH];
					for ( int j = 0 ; j < LENGTH ; j++ ) {
						this.m[i][j] = source[i][j];
					}
				}
				else {
					throw new IllegalArgumentException("Attemped to create a matrix without 3x3 size");
				}
			}
		}
		else {
			throw new IllegalArgumentException("Attemped to create a matrix without 3x3 size");
		}
	}
	
	/**
	 * @post Crea una matriz de transformación para dos dimensiones
	 * 		 con los vectores especificados
	 */
	public Matrix3f(Vector3f localX, Vector3f localY) {
		this(new float[][]{
				{ localX.getX(), localY.getX(), 0.0f },
				{ localX.getY(), localY.getY(), 0.0f },
				{ 0.0f,	0.0f, 1.0f }
		} );
	}
	
	/**
	 * @post Devuelve el resultado de sumar la matriz con otra
	 */
	public Matrix3f add(Matrix3f other) {
		final float resultarray[][] = new float[LENGTH][];
		for ( int i = 0 ; i < LENGTH; i++ ) {
			resultarray[i] = new float[LENGTH];
			for ( int j = 0 ; j < LENGTH ; j++ ) {
				resultarray[i][j] = this.m[i][j] + other.m[i][j];
			}
		}
		return new Matrix3f(resultarray);
	}
	
	/**
	 * @post Devuelve el resultado de la multiplicación de la matriz con otra
	 */
	public Matrix3f mul(Matrix3f other) {
		final float resultarray[][] = new float[LENGTH][];
		for ( int i = 0 ; i < LENGTH ; i++ ) {
			resultarray[i] = new float[LENGTH];
		}
		for ( int i = 0 ; i < LENGTH ; i++ ) {
			for ( int j = 0 ; j < LENGTH ; j++ ) {
				resultarray[j][i] = 0.0f;
				for ( int k = 0 ; k < LENGTH ; k++ ) {
					resultarray[j][i] += this.m[k][i] * other.m[j][k];
				}
			}
		}
		return new Matrix3f(resultarray);
	}
	
	/**
	 * @post Devuelve el resultado de la translación con el vector especificado
	 */
	public Matrix3f translate(Vector2f vector) {
		return
			new Matrix3f( new float[][]{
						new float[]{ 1.0f, 0.0f, 0.0f },
						new float[]{ 0.0f, 1.0f, 0.0f },
						new float[]{ vector.getX(), vector.getY(), 1.0f }
			} ).mul(this);
	}
	
	/**
	 * @post Devuelve la matriz rotada con el ángulo especificado
	 */
	public Matrix3f rotate(float angle) {
		final float sinProduct = (float) Math.sin(angle);
		final float cosProduct = (float) Math.cos(angle);
		return 
			new Matrix3f( new float[][]{
						new float[]{ cosProduct, sinProduct, 0.0f },
						new float[]{ -sinProduct, cosProduct, 0.0f },
						new float[]{ 0.0f, 0.0f, 1.0f }
			} ).mul(this);
	}
	
	/**
	 * @post Devuelve la matriz escalada con el vector especificado
	 */
	public Matrix3f scale(Vector2f vector) {
		return new Matrix3f(new float[][]{
			new float[]{ vector.getX(), 0.0f, 0.0f },
			new float[]{ 0.0f, vector.getY(), 0.0f },
			new float[]{ 0.0f, 0.0f, 1.0f }
		}).mul(this);
	}
	
	/**
	 * @post Devuelve la matriz multiplicada con el escalar especificado
	 */
	public Matrix3f scale(float scalar) {
		return this.scale(new Vector2f(scalar, scalar));
	}
	
	/**
	 * @post Devuelve la matriz transpuesta
	 */
	public Matrix3f transpose() {
		final float[][] matrixArray = new float[LENGTH][];
		for ( int j = 0 ; j < LENGTH ; j++ ) {
			matrixArray[j] = new float[LENGTH];
			for ( int i = 0; i < LENGTH ; i++ ) {
				matrixArray[j][i] = this.m[i][j];
			}
		}
		return new Matrix3f(matrixArray);
	}
	
	/**
	 * @post Devuelve el determinante de la matriz
	 */
	public float determinant() {
		return this.m[0][0]*this.m[1][1]*this.m[2][2]+this.m[1][0]*this.m[2][1]*this.m[0][2]+this.m[2][0]*this.m[0][1]*this.m[1][2]-this.m[2][0]*this.m[1][1]*this.m[0][2]-this.m[1][0]*this.m[0][1]*this.m[2][2]-this.m[0][0]*this.m[2][1]*this.m[1][2];
	}
	
	/**
	 * @post Devuelve la inversa de la matriz
	 */
	public Matrix3f invert() {
		final float determinant = this.determinant();
		if ( determinant != 0.0f) {
			return ( new Matrix3f( new float[][]{
				new float[]{
					this.m[1][1] * this.m[2][2] - this.m[2][1] * this.m[1][2],
					this.m[2][1] * this.m[0][2] - this.m[0][1] * this.m[2][2],
					this.m[0][1] * this.m[1][2] - this.m[1][1] * this.m[0][2]
				},
				new float[]{
					this.m[2][0] * this.m[1][2] - this.m[1][0] * this.m[2][2],
					this.m[0][0] * this.m[2][2] - this.m[2][0] * this.m[0][2],
					this.m[1][0] * this.m[0][2] - this.m[0][0] * this.m[1][2]
				},
				new float[]{
					this.m[1][0] * this.m[2][1] - this.m[2][0] * this.m[1][1],
					this.m[2][0] * this.m[0][1] - this.m[0][0] * this.m[2][1],
					this.m[0][0] * this.m[1][1] - this.m[1][0] * this.m[0][1]
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
	public Vector2f transformPoint(Vector2f vector) {
		return new Vector2f(
				this.m[0][0] * vector.getX() + this.m[1][0] * vector.getY() + this.m[2][0],
				this.m[0][1] * vector.getX() + this.m[1][1] * vector.getY() + this.m[2][1]
		);
	}
	
	/**
	 * @post Devuelve la dirección especificada transformada por la matriz
	 */
	public Vector2f transformDirection(Vector2f vector) {
		return new Vector2f(
				this.m[0][0] * vector.getX() + this.m[1][0] * vector.getY(),
				this.m[0][1] * vector.getX() + this.m[1][1] * vector.getY()
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
		float[] unionArray = new float[9];
		FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
		for ( int i=0 ; i < LENGTH ; i++ ) {
			for ( int j=0 ; j < LENGTH ; j++ ) {
				unionArray[i*LENGTH+j] = this.m[i][j];
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
			if ( other instanceof Matrix3f ) {
				return Arrays.deepEquals( ((Matrix3f) other).m, this.m);
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
		String result = "Matrix3f( [";
		for ( int i = 0; i<LENGTH ; i++ ) {
			result += "[";
			for ( int j = 0; j<LENGTH ; j++ ) {
				result += Float.toString(this.m[i][j]);
				if ( j < LENGTH-1 ) {
					result += ", ";
				}
			}
			result += "]";
			
			if ( i < LENGTH-1 ) {
				result += ", ";
			}
		}
		result += "] )";
		return result;
	}
}
