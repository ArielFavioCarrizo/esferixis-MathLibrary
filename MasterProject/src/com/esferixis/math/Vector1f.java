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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.esferixis.misc.nio.BufferUtils;

/**
 * Escalar
 * @author ariel
 *
 */
public final class Vector1f extends Vectorf implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8807333432361086085L;
	
	private final float value;
	
	/**
	 * @post Crea el escalar con el valor especificado
	 */
	public Vector1f(float value) {
		this.value = value;
	}
	
	/**
	 * @post Devuelve el valor
	 */
	public float getValue() {
		return this.value;
	}
	
	/**
	 * @post Devuelve un representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return Float.toString(this.value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store()
	 */
	@Override
	public FloatBuffer store() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		buffer.put(this.value);
		buffer.flip();
		return buffer;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#lengthSquared()
	 */
	@Override
	public float lengthSquared() {
		return this.value * this.value;
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#length()
	 */
	@Override
	public float length() {
		return Math.abs( this.value );
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.ByteBuffer)
	 */
	@Override
	public void store(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#store(java.nio.FloatBuffer)
	 */
	@Override
	public void store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.Vectorf#accept(com.esferixis.math.Vectorf.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
