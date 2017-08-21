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

/**
 * Matriz de transformación proporcional
 * 
 * @author ariel
 *
 */
public final class ProportionalMatrix4f extends Matrix4f {
	private static final long serialVersionUID = 1888693706930218918L;

	/**
	 * @param source
	 */
	ProportionalMatrix4f(float[][] source) {
		super(source);
	}
	
	/**
	 * @param source
	 */
	ProportionalMatrix4f(Matrix4f source) {
		this(source.m);
	}

	/**
	 * @post Devuelve la transformación del escalar especificado
	 */
	public float transformScalar(float scalar) {
		return this.m[0][0] * scalar + this.m[0][1] * scalar + this.m[0][2] * scalar;
	}
	
	/**
	 * @post Devuelve el resultado de la multiplicación de la matriz con otra
	 */
	public ProportionalMatrix4f mul(ProportionalMatrix4f other) {
		return new ProportionalMatrix4f(this.mul( (Matrix4f) other));
	}
	
	/**
	 * @post Devuelve el resultado de la translación con el vector especificado
	 */
	@Override
	public ProportionalMatrix4f translate(Vector3f vector) {
		return new ProportionalMatrix4f(super.translate(vector));
	}
	
	/**
	 * @post Devuelve la matriz rotada con el ángulo y el eje especificado
	 */
	@Override
	public ProportionalMatrix4f rotate(float angle, Vector3f unitAxis) {
		return new ProportionalMatrix4f(super.rotate(angle, unitAxis));
	}
	
	/**
	 * @post Devuelve la matriz multiplicada con el escalar especificado
	 */
	@Override
	public ProportionalMatrix4f scale(float scalar) {
		return new ProportionalMatrix4f(super.scale(scalar));
	}
	
	/**
	 * @post Devuelve la inversa de la matriz
	 */
	@Override
	public ProportionalMatrix4f invert() {
		return new ProportionalMatrix4f(super.invert());
	}
}
