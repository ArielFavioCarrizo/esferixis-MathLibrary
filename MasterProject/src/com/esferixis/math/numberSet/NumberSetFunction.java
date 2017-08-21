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
package com.esferixis.math.numberSet;

/**
 * @author ariel
 *
 */
public abstract class NumberSetFunction {
	/**
	 * @post Evalúa el operando especificado
	 */
	public abstract <V extends NumberSet<V>> V evaluate(V operand);
	
	/**
	 * @pre El valor de dominio no puede ser nulo
	 * @post Efectúa la aproximación de la derivada
	 * 
	 * 		 Ésta implementación puede ser reemplazada con la evaluación de la
	 * 		 derivada exacta
	 */
	public <T extends NumberSet<T>> T derivateApproximation(T domainValue, float domainDelta) {
		if ( domainValue != null ) {
			final NumberSetFactory<T> factory = domainValue.factory();
			T domainDeltaSet = factory.convert(domainDelta);
			return factory.divide(factory.sub( this.evaluate(factory.add(domainValue, domainDeltaSet)), this.evaluate(domainValue)), domainDeltaSet);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El límite inferior y superior no pueden ser nulos
	 * @post Realiza la aproximación de la integral de la función con los límites
	 * 		 inferior y superior especificados con la cantidad de divisiones especificadas
	 * 
	 * 		 Ésta implementación puede ser reemplazada con la evaluación de la
	 * 		 integral exacta
	 */
	public <T extends NumberSet<T>> T integralApproximation(T inferiorBound, T superiorBound, int domainDivision) {
		if ( ( inferiorBound != null ) && ( superiorBound != null ) ) {
			NumberSetFactory<T> factory = inferiorBound.factory();
			T image1 = this.evaluate(inferiorBound), image2;
			
			final T domainDivisionQuantity = factory.convert(domainDivision);
			final T domainIntervalLength = factory.sub(superiorBound, inferiorBound);
			final T domainDelta = factory.divide(domainIntervalLength, domainDivisionQuantity);
			
			final T halfDomainDelta = factory.divide(domainDelta, factory.convert(2.0f));
			
			T result = factory.zero();
			
			for (int i = 0 ; i < domainDivision ; i++) {
				image2 = this.evaluate( factory.add(inferiorBound, factory.divide(factory.multiply(domainIntervalLength, factory.convert(i+1)), domainDivisionQuantity)) );
				
				final T imageDelta = factory.sub(image2, image1);
				result = factory.add(result, factory.multiply(halfDomainDelta, imageDelta));
				
				image1 = image2;
			}
			
			return result;
		}
		else {
			throw new NullPointerException();
		}
	}
}
