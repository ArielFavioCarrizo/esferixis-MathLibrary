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

import com.esferixis.math.numberSet.NumberSetFunction;

/**
 * @author ariel
 *
 */
class FloatClosedIntervalFunctionEvaluation {
	private final FloatClosedInterval domainInterval;
	private final FloatClosedIntervalSet imageSet;
	
	public static class Factory {
		private final NumberSetFunction function;
		
		/**
		 * @pre La función no puede ser nula
		 * @post Crea una fábrica con la función especificada
		 */
		public Factory(NumberSetFunction function) {
			if ( function != null ) {
				this.function = function;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre El intervalo de dominio no puede ser nulo
		 * @post Crea una evaluación con el intervalo de dominio especificado
		 */
		public FloatClosedIntervalFunctionEvaluation make(FloatClosedInterval domainInterval) {
			if ( domainInterval != null ) {
				return new FloatClosedIntervalFunctionEvaluation(this.function, domainInterval);
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	/**
	 * @pre La función ni el intervalo de dominio pueden ser nulos
	 * @post Crea una evaluación de la función especificada
	 * 		 con la función y el intervalo de dominio
	 * 		 especificados
	 */
	private FloatClosedIntervalFunctionEvaluation(NumberSetFunction function, FloatClosedInterval domainInterval) {
		this.domainInterval = domainInterval;
		this.imageSet = function.evaluate(new FloatClosedIntervalSet(this.domainInterval) );
	}
	
	/**
	 * @post Devuelve el intervalo de dominio
	 */
	public FloatClosedInterval getDomainInterval() {
		return this.domainInterval;
	}
	
	/**
	 * @post Devuelve el conjunto imagen
	 */
	public FloatClosedIntervalSet getImageSet() {
		return this.imageSet;
	}
}
