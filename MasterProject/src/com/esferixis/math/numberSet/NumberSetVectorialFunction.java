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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ariel
 *
 */
public abstract class NumberSetVectorialFunction {
	/**
	 * @post Evalúa el operando especificado
	 */
	public abstract <V extends NumberSet<V>> NumberSetVector<V> evaluate(NumberSetVector<V> operand);
	
	/**
	 * @pre El punto especificado no puede ser nulo
	 * @post Devuelve la aproximación del jacobiano en el punto especificado
	 * 		 con el delta de aproximación especificado
	 * 
	 * 		 Ésta implementación puede ser reemplazada con la evaluación
	 * 		 del jacobiano real
	 */
	public <T extends NumberSet<T>> NumberSetMatrix<T> jacobianApproximation(NumberSetVector<T> domainValue, float xDeltaDerivative) {
		if ( domainValue != null ) {
			final NumberSetFactory<T> factory = domainValue.elementsFactory();
			NumberSetVector<T> imageDomainValue = this.evaluate(domainValue);
			int n_domain = domainValue.components().size();
			int n_image = imageDomainValue.components().size();
			T xDelta = factory.convert(xDeltaDerivative);
			
			T[][] elementsMatrix = (T[][]) Array.newInstance(NumberSet.class, n_domain, n_image);
			
			for ( int i=0;i<n_domain; i++ ) {
				final NumberSetVector<T> domainValuePlusDelta;
				{
					List<T> domainValuePlusDeltaValues = new ArrayList<T>(n_domain);
					for ( int j=0;j<n_domain; j++ ) {
						T componentValue = domainValue.components().get(j);
						if ( j == i ) {
							componentValue = factory.add(componentValue, factory.convert(xDeltaDerivative));
						}
						domainValuePlusDeltaValues.add(componentValue);
					}
					domainValuePlusDelta = new NumberSetVector<T>(domainValuePlusDeltaValues);
				}
				
				NumberSetVector<T> image_domainplusdelta_i = this.evaluate(domainValuePlusDelta);
				NumberSetVector<T> image_delta_domain_i = image_domainplusdelta_i.sub(imageDomainValue);
				
				for ( int j=0;j<n_image; j++ ) {
					elementsMatrix[i][j] = factory.divide(image_delta_domain_i.components().get(j), xDelta);
				}
			}
			
			return new NumberSetMatrix<T>(factory, elementsMatrix);
		}
		else {
			throw new NullPointerException();
		}
 	}
	
	/**
	 * @post Devuelve el campo escalar de norma elevada al cuadrado
	 */
	public NumberSetVectorialScalarFunction squaredNormFunction() {
		return new NumberSetVectorialScalarFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				return NumberSetVectorialFunction.this.evaluate(operand).lengthSquared();
			}
			
		};
	}
}
