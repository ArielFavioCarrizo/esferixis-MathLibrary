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
 * Fábrica de conjuntos numéricos
 * 
 * @author ariel
 *
 */
public abstract class NumberSetFactory<T extends NumberSet<T>> {
	/**
	 * @post Devuelve la clase del conjunto numérico
	 */
	public abstract Class<T> getNumberSetClass();
	
	/**
	 * @post Convierte el número flotante "float" en el conjunto T
	 */
	public abstract T convert(float value);
	
	/**
	 * @post Convierte el número flotante "double" en el conjunto T
	 */
	public abstract T convert(double value);
	
	/**
	 * @post Devuelve el cero
	 */
	public T zero() {
		return this.convert(0.0f);
	}
	
	/**
	 * @post Devuelve el uno
	 */
	public T one() {
		return this.convert(1.0f);
	}
	
	/**
	 * @post Convierte el vector "float" en el vector del conjunto "T"
	 */
	public NumberSetVector<T> convert(float... components) {
		List<T> componentsResult = new ArrayList<T>(components.length);
		for ( float eachComponent : components ) {
			componentsResult.add(this.convert(eachComponent));
		}
		return new NumberSetVector<T>(componentsResult);
	}
	
	/**
	 * @post Suma los dos conjuntos
	 */
	public abstract T add(T operand1, T operand2);
	
	/**
	 * @post Suma los conjuntos especificados
	 * 
	 * 		 Ésta implementación efectúa una sumatoria
	 */
	public T add(T... operands) {
		T result = this.convert(0.0f);
		
		for ( T eachOperand : operands ) {
			result = this.add(result, eachOperand);
		}
		
		return result;
	}
	
	/**
	 * @post Resta los dos conjuntos
	 * 
	 * 		 Ésta implementación devuelve operand1+(-operand2)
	 */
	public T sub(T operand1, T operand2) {
		return this.add(operand1, this.opposite( operand2 ) );
	}
	
	/**
	 * @post Multiplica los dos conjuntos
	 */
	public abstract T multiply(T operand1, T operand2);
	
	/**
	 * @post Calcula el valor absoluto
	 */
	public abstract T abs(T operand);
	
	/**
	 * @post Calcula el valor opuesto
	 */
	public abstract T opposite(T operand);
	
	/**
	 * @post Calcula la inversa
	 */
	public abstract T inverse(T operand);
	
	/**
	 * @post Calcula la división entre los dos conjuntos
	 * 
	 * 		 Ésta implementación calcula la inversa del divisor,
	 * 		 y luego la multiplica con el dividendo
	 */
	public T divide(T dividend, T divisor) {
		return this.multiply(dividend, this.inverse(divisor));
	}
	
	/**
	 * @post Calcula la exponencial
	 */
	public abstract T exp(T exponent);
	
	/**
	 * @post Devuelve el logaritmo natural
	 */
	public abstract T log(T antilogarithm);
	
	/**
	 * @post Devuelve la base a la potencia especificada
	 * 
	 * 		 Ésta implementación devuelve exp( log(base) * exponent)
	 */
	public T pow(T base, T exponent) {
		return this.exp( this.multiply( this.log(base), exponent) );
	}
	
	/**
	 * @post Devuelve el cuadrado
	 * 
	 * 		 Ésta implementación devuelve pow( operand, 2.0f )
	 */
	public T square(T operand) {
		return this.pow(operand, this.convert(2.0f));
	}
	
	/**
	 * @post Devuelve la raíz cuadrada
	 * 
	 * 		 Ésta implementación devuelve pow( operand, 0.5f )
	 */
	public T sqrt(T operand) {
		return this.pow(operand, this.convert(0.5f));
	}
	
	
	/**
	 * @post Devuelve el seno del ángulo
	 */
	public abstract T sin(T angle);
	
	/**
	 * @post Devuelve el coseno del ángulo
	 * 	
	 * 		 Ésta implementación devuelve sin(Math.PI/2-angle)
	 */
	public T cos(T angle) {
		return this.sin(this.sub(this.convert(Math.PI/2.0d), angle));
	}
	
	/**
	 * @post Devuelve la tangente del ángulo
	 * 
	 * 		 Ésta implementación devuelve sin(angle)/cos(angle)
	 */
	public T tan(T angle) {
		return this.divide(this.sin(angle), this.cos(angle));
	}
	
	/**
	 * @post Devuelve si el primero sólo tiene elementos mayores al segundo
	 */
	public abstract boolean hasOnlyGreaterElements(T operand1, T operand2);
	
	/**
	 * @post Devuelve si contiene los elementos del operando 2
	 */
	public abstract boolean contains(T container, T contained);
	
	public enum ConditionType {
		GREATER,
		SMALLER,
		GREATEROREQUALS,
		SMALLEROREQUALS,
		EQUALS,
		NOTEQUALS
	}
	
	/**
	 * @post Devuelve el resultado de:
	 * 		 trueFunction(x) y falseFunction(x) según x
	 * 		 cumpla o no cumpla la condición especificada con a
	 */
	public abstract T conditionalEvaluation(T x, ConditionType conditionType, T a, NumberSetFunction trueFunction, NumberSetFunction falseFunction);
}
