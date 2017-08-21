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

import java.math.BigInteger;

/**
 * @author ariel
 *
 */
public final class NumberExtra {
	private static NumberExtra INSTANCE = new NumberExtra();
	
	private NumberExtra() {};
	
	// Operador
	private abstract class Operator<R> {
		
		public Operator() {
			
		}
		
		/**
		 * @pre Ninguno de los operandos puede ser nulo
		 * @post Procesa los operandos convirtiéndolos al tipo más conveniente
		 * @param number1
		 * @param number2
		 */
		public final R process(Number number1, Number number2) {
			R result;
			if ( ( number1 instanceof Double ) || ( number2 instanceof Double) ) {
				result = this.process(number1.doubleValue(), number2.doubleValue());
			}
			else if ( ( number1 instanceof Float ) || ( number2 instanceof Float ) ) {
				result = this.process(number1.floatValue(), number2.floatValue());
			}
			else {
				BigInteger integer1;
				BigInteger integer2;
				if ( number1.getClass() != BigInteger.class ) {
					integer1 = BigInteger.valueOf(number1.longValue());
					integer2 = (BigInteger) number2;
				}
				else if ( number2.getClass() != BigInteger.class ) {
					integer1 = (BigInteger) number1;
					integer2 = BigInteger.valueOf(number2.longValue());
				}
				else {
					integer1 = (BigInteger) number1;
					integer2 = (BigInteger) number2;
				}
				result = this.process(integer1, integer2);
			}
			
			return result;
		}
		
		protected abstract R process(Double number1, Double number2);
		protected abstract R process(Float number1, Float number2);
		protected abstract R process(BigInteger number1, BigInteger number2);
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Suma los dos números especificados
	 */
	public static Number add(Number number1, Number number2) {
		return INSTANCE.new Operator<Number>() {
			@Override
			protected Double process(Double number1, Double number2) {
				return number1 + number2;
			}
			
			@Override
			protected Float process(Float number1, Float number2) {
				return number1 + number2;
			}

			@Override
			protected BigInteger process(BigInteger number1, BigInteger number2) {
				return number1.add(number2);
			}
		}.process(number1, number2);
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Resta los dos números especificados
	 */
	public static Number subtract(Number number1, Number number2) {
		return INSTANCE.new Operator<Number>() {
			@Override
			protected Double process(Double number1, Double number2) {
				return number1 - number2;
			}
			
			@Override
			protected Float process(Float number1, Float number2) {
				return number1 - number2;
			}

			@Override
			protected BigInteger process(BigInteger number1, BigInteger number2) {
				return number1.subtract(number2);
			}
		}.process(number1, number2);
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Multiplica los dos números especificados
	 */
	public static Number multiply(Number number1, Number number2) {
		return INSTANCE.new Operator<Number>() {
			@Override
			protected Double process(Double number1, Double number2) {
				return number1 * number2;
			}
			
			@Override
			protected Float process(Float number1, Float number2) {
				return number1 * number2;
			}

			@Override
			protected BigInteger process(BigInteger number1, BigInteger number2) {
				return number1.multiply(number2);
			}
		}.process(number1, number2);
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Divide los dos números especificados
	 */
	public static Number divide(Number number1, Number number2) {
		return INSTANCE.new Operator<Number>() {
			@Override
			protected Double process(Double number1, Double number2) {
				return number1 / number2;
			}
			
			@Override
			protected Float process(Float number1, Float number2) {
				return number1 / number2;
			}

			@Override
			protected Number process(BigInteger number1, BigInteger number2) {
				Double number1_d = number1.doubleValue();
				Double number2_d = number2.doubleValue();
				if ( Double.isInfinite(number1_d) || Double.isInfinite(number2_d) ) {
					return number1.divide(number2);
				}
				else {
					return number1_d / number2_d;
				}
			}
		}.process(number1, number2);
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Divide los dos números especificados
	 */
	public static Number abs(Number number1, Number number2) {
		return INSTANCE.new Operator<Number>() {
			@Override
			protected Double process(Double number1, Double number2) {
				return Math.pow(number1, number2);
			}
			
			@Override
			protected Float process(Float number1, Float number2) {
				return (float) Math.pow(number1, number2);
			}

			@Override
			protected BigInteger process(BigInteger number1, BigInteger number2) {
				BigInteger result = BigInteger.ONE;
				while ( !number2.equals(BigInteger.ZERO) ) {
					result.multiply(number1);
					number2.subtract( BigInteger.valueOf( 1L ) );
				}
				return result;
			}
		}.process(number1, number2);
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Efectúa un elevado a la potencia especificada
	 */
	public static Number pow(Number number1, Number number2) {
		return INSTANCE.new Operator<Number>() {
			@Override
			protected Double process(Double number1, Double number2) {
				return Math.pow(number1, number2);
			}
			
			@Override
			protected Float process(Float number1, Float number2) {
				return (float) Math.pow(number1, number2);
			}

			@Override
			protected BigInteger process(BigInteger number1, BigInteger number2) {
				BigInteger result;
				if ( number2.signum() >= 0 ) {
					result = BigInteger.ONE;
					while ( !number2.equals(BigInteger.ZERO) ) {
						result.multiply(number1);
						number2.subtract( BigInteger.valueOf( 1L ) );
					}
				}
				else {
					result = BigInteger.ZERO;
				}
				return result;
			}
		}.process(number1, number2);
	}
 }
