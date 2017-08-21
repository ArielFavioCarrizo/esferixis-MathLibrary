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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author ariel
 *
 */
public abstract class Vectorf {
	public static interface Visitor<V, T extends Throwable> {
		public V visit(Vector1f vector1f) throws T;
		public V visit(Vector2f vector2f) throws T;
		public V visit(Vector3f vector3f) throws T;
		public V visit(Vector4f vector4f) throws T;
	}
	
	/**
	 * @post Crea el vector
	 */
	Vectorf() {
		
	}
	
	/**
	 * @post Almacena los componentes del vector en el buffer y lo devuelve
	 */
	public abstract FloatBuffer store();
	
	/**
	 * @post Almacena el vector en la posición actual del buffer especificado
	 * 		 y lo devuelve
	 */
	public abstract void store(ByteBuffer byteBuffer);
	
	/**
	 * @post Almacena el vector en la posición actual del buffer especificado
	 * 		 y lo devuelve
	 */
	public abstract void store(FloatBuffer floatBuffer);
	
	/**
	 * @post Visita el vector con el visitor especificado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
	
	/**
	 * @post Devuelve la longitud elevada al cuadrado
	 */
	public abstract float lengthSquared();
	
	/**
	 * @post Devuelve la longitud
	 */
	public float length() {
		return (float) Math.sqrt( this.lengthSquared() );
	}
}
