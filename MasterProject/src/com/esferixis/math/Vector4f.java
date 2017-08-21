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

/**
 * Vector tetradimensional en el juego de Super Esferix 3D
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class Vector4f extends Vectorf implements Serializable {
	private static final long serialVersionUID = 5461461707824360835L;

	public static final Vector4f ZERO = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
	
	private final float x, y, z, w;
	private Integer hash;
	
	/**
	 * @post Crea un vector de 4 dimensiones
	 */
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.hash = null;
	}
	
	/**
	 * @post Devuelve la primer componente
	 * @return
	 */
	public float getX() {
		return this.x;
	}
	
	/**
	 * @post Devuelve la segunda componente
	 * @return
	 */
	public float getY() {
		return this.y;
	}
	
	/**
	 * @post Devuelve la tercer componente
	 * @return
	 */
	public float getZ() {
		return this.z;
	}
	
	/**
	 * @post Devuelve la cuarta componente
	 * @return
	 */
	public float getW() {
		return this.w;
	}
	
	/**
	 * @post Crea un vector leyendo de un FloatBuffer
	 */
	public static Vector4f read(FloatBuffer buffer) {
		if ( buffer.capacity() >= 4 ) {
			return new Vector4f(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3) );
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
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(new float[]{this.x, this.y, this.z, this.w});
		buffer.flip();
		return buffer;
	}
	
	@Override
	public int hashCode() {
		if ( this.hash == null ) {
			this.hash = ( Float.valueOf(this.x).hashCode() + Float.valueOf(this.y).hashCode() + Float.valueOf(this.z).hashCode() + Float.valueOf(this.w).hashCode() );
		}
		return this.hash;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null ) {
			if ( other instanceof Vector4f ) {
				Vector4f otherVector = (Vector4f) other;
				return ( this.x == otherVector.x ) && ( this.y == otherVector.y ) && ( this.z == otherVector.z ) && ( this.w == otherVector.w );
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
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#lengthSquared()
	 */
	@Override
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.ByteBuffer)
	 */
	@Override
	public void store(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.x);
		byteBuffer.putFloat(this.y);
		byteBuffer.putFloat(this.z);
		byteBuffer.putFloat(this.w);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.FloatBuffer)
	 */
	@Override
	public void store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.x);
		floatBuffer.put(this.y);
		floatBuffer.put(this.z);
		floatBuffer.put(this.w);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#accept(com.esferixis.math.Vectorf.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
