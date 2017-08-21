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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.esferixis.math.numberSet.NumberSet;
import com.esferixis.math.numberSet.NumberSetFactory;
import com.esferixis.math.numberSet.NumberSetFunction;
import com.esferixis.math.numberSet.NumberSetVector;
import com.esferixis.math.numberSet.NumberSetVectorialFunction;
import com.esferixis.math.pointarithmetic.FloatNumberPoint;
import com.esferixis.misc.iterator.AppendIterator;

/**
 * @author ariel
 *
 */
public final class FloatClosedIntervalSet extends NumberSet<FloatClosedIntervalSet> implements Serializable {
	private static final long serialVersionUID = 1311351289533606318L;
	
	public static final FloatClosedIntervalSet EMPTY = new FloatClosedIntervalSet( new ArrayList<FloatClosedInterval>() );
	public static final FloatClosedIntervalSet ALLRANGE = new FloatClosedIntervalSet( FloatClosedInterval.ALLRANGE );
	
	private ArrayList<FloatClosedInterval> intervals;
	
	public static final class NumericMethods {
		private NumericMethods() {};
		
		public static final class Result {
			private final FloatClosedInterval domainValue;
			private final FloatClosedIntervalSet imageValue;
			private final int iterations;
			
			public Result(FloatClosedInterval domainValue, FloatClosedIntervalSet imageValue, int iterations) {
				this.domainValue = domainValue;
				this.imageValue = imageValue;
				this.iterations = iterations;
			}
			
			/**
			 * @post Devuelve el valor de dominio
			 */
			public FloatClosedInterval getDomainValue() {
				return this.domainValue;
			}
			
			/**
			 * @post Devuelve el valor de imagen
			 */
			public FloatClosedIntervalSet getImageValue() {
				return this.imageValue;
			}
			
			/**
			 * @post Devuelve las iteraciones
			 */
			public int getIterations() {
				return this.iterations;
			}
			
			/**
			 * @post Devuelve una representación en cadena de texto
			 */
			public String toString() {
				return "{ f(" + this.domainValue + ") = " + this.imageValue + ", n = " + this.iterations + "}";
			}
		}
		
		private static abstract class BisectionStrategy {
			/**
			 * @post Devuelve el punto intermedio de bisección
			 */
			public abstract float midPoint(NumberSetFunction function, FloatClosedInterval domainInterval);
		}
		
		/**
		 * @pre La función ni el dominio pueden ser nulos, y la función tiene que converger
		 * 		a valores puntuales
		 * @post Devuelve el intervalo de raíces más pequeño en el intervalo
		 * 		 especificado con el error de imagen especificado y la estrategia
		 * 		 de bisección especificada,
		 * 		 si no encuentra un intervalo devuelve null
		 */
		private static Result bisection_minnearest(NumberSetFunction function, FloatClosedInterval domain, float imageError, BisectionStrategy bisectionStrategy) {
			if ( ( function != null ) && ( domain != null ) && ( bisectionStrategy != null ) ) {
				Stack<FloatClosedInterval> pendingIntervals = new Stack<FloatClosedInterval>();
				int n=0;
				
				FloatClosedInterval rootInterval = null;
				FloatClosedIntervalSet rootIntervalImage = null;
				
				pendingIntervals.push(domain);
					
				while ( !pendingIntervals.isEmpty() && ( rootInterval == null ) ) {
					n++;
					final FloatClosedInterval eachInterval = pendingIntervals.pop();
					final FloatClosedIntervalSet eachImage = function.evaluate(new FloatClosedIntervalSet(eachInterval));
					
					if ( eachImage.contains(0.0f) ) {
						if ( ( eachImage.length() > imageError ) && ( !eachInterval.isPoint() ) ) {
							float minAfter = Math.nextUp(eachInterval.getMin());
							FloatClosedInterval smallest, greatest;
							if ( eachInterval.getMax() != minAfter ) {
								final float midPoint = bisectionStrategy.midPoint(function, eachInterval);
							
								smallest = new FloatClosedInterval(eachInterval.getMin(), midPoint);
								greatest = new FloatClosedInterval(midPoint, eachInterval.getMax());
							}
							else {
								smallest = new FloatClosedInterval(eachInterval.getMin());
								greatest = new FloatClosedInterval(eachInterval.getMax());
							}
								
							pendingIntervals.push(greatest);
							pendingIntervals.push(smallest);
								
							//n += result.getIterations()-1;
						}
						else {
							rootInterval = eachInterval;
							rootIntervalImage = eachImage;
						}
					}
				}
				
				return new Result( rootInterval, rootIntervalImage, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función ni el dominio pueden ser nulos, y la función tiene que converger
		 * 		a valores puntuales
		 * @post Devuelve el intervalo de raíces más pequeño en el intervalo
		 * 		 especificado con el error de imagen especificado,
		 * 		 si no encuentra un intervalo devuelve null
		 */
		public static Result bisection_minnearest(NumberSetFunction function, FloatClosedInterval domain, float imageError) {
			return bisection_minnearest(function, domain, imageError, new BisectionStrategy() {

				@Override
				public float midPoint(NumberSetFunction function,
						FloatClosedInterval domainInterval) {
					return ( domainInterval.getMin() + domainInterval.getMax() ) / 2.0f;
				}
				
			} );
		}
		
		/**
		 * @pre La función ni el dominio no pueden ser nulos, y la función
		 * 		tiene que converger a valores puntuales
		 * @post Devuelve el intervalo de raíces más pequeño en el intervalo
		 * 		 especificado, si no encuentra un intervalo devuelve null
		 */
		public static Result bisection_minnearest_P(final NumberSetFunction function, final FloatClosedInterval domain, final float imageError) {
			return bisection_minnearest(function, domain, imageError, new BisectionStrategy() {

				@Override
				public float midPoint(NumberSetFunction function,
						FloatClosedInterval domainInterval) {
					final float midPoint;
					FloatNumberPoint.NumericalMethods.Result result = FloatNumberPoint.NumericalMethods.bisection_minnearest_N_R(function, domainInterval, domain.length() / 7.0f, imageError);
					
					if ( ( result.getPoint() != null ) && ( result.getPoint().getValue() > domainInterval.getMin() ) && ( result.getPoint().getValue() < domainInterval.getMax() ) ) {
						midPoint = result.getPoint().getValue();
					}
					else {
						midPoint = (domainInterval.getMin()+domainInterval.getMax())/2.0f;
					}
					return midPoint;
				}
				
			} );
		}
		
		/**
		 * @pre La función ni el dominio no pueden ser nulos, y la función
		 * 		tiene que converger a valores puntutales
		 * @post Devuelve una estimación del mínimo global en el intervalo de dominio
		 * 		 especificado que tenga un error de imagen menor al especificado.
		 * 		 Usa la estrategia de bisección especificada
		 */
		private static Result bisection_globalMinima(final NumberSetFunction function, final FloatClosedInterval domain, final float imageError, BisectionStrategy bisectionStrategy ) {
			if ( ( function != null ) && ( domain != null ) && ( bisectionStrategy != null ) ) {
				int n=0;
				float toleratedImageMaxValue = Float.POSITIVE_INFINITY;
				final FloatClosedIntervalFunctionEvaluation.Factory evaluationsFactory = new FloatClosedIntervalFunctionEvaluation.Factory(function);
				
				Stack<FloatClosedIntervalFunctionEvaluation> pendingEvaluations = new Stack<FloatClosedIntervalFunctionEvaluation>();
				
				FloatClosedIntervalFunctionEvaluation globalMinimaCandidate = null;
				
				pendingEvaluations.push(evaluationsFactory.make(domain));
				
				while ( !pendingEvaluations.isEmpty() ) {
					FloatClosedIntervalFunctionEvaluation eachEvaluation = pendingEvaluations.pop();

					if ( eachEvaluation.getImageSet().getMin() < toleratedImageMaxValue ) {
						if ( eachEvaluation.getImageSet().getMax() < toleratedImageMaxValue ) {
							toleratedImageMaxValue = eachEvaluation.getImageSet().getMax();
						}
						
						if ( ( eachEvaluation.getImageSet().length() > imageError ) && ( !eachEvaluation.getDomainInterval().isPoint() ) ) {
							float minAfter = Math.nextUp(eachEvaluation.getDomainInterval().getMin());
							FloatClosedIntervalFunctionEvaluation evaluation1, evaluation2;
							if ( eachEvaluation.getDomainInterval().getMax() != minAfter ) {
								final float midPoint = bisectionStrategy.midPoint(function, eachEvaluation.getDomainInterval());
								
								evaluation1 = evaluationsFactory.make( new FloatClosedInterval(eachEvaluation.getDomainInterval().getMin(), midPoint) );
								evaluation2 = evaluationsFactory.make( new FloatClosedInterval(Math.nextUp(midPoint), eachEvaluation.getDomainInterval().getMax()) );
							}
							else {
								evaluation1 = evaluationsFactory.make( new FloatClosedInterval(eachEvaluation.getDomainInterval().getMin()) );
								evaluation2 = evaluationsFactory.make( new FloatClosedInterval(eachEvaluation.getDomainInterval().getMax()) );
							}
							
							if ( evaluation1.getImageSet().getMax() < evaluation2.getImageSet().getMax() ) {
								pendingEvaluations.push(evaluation2);
								pendingEvaluations.push(evaluation1);
							}
							else {
								pendingEvaluations.push(evaluation1);
								pendingEvaluations.push(evaluation2);
							}
						}
						else {
							globalMinimaCandidate = eachEvaluation;
						}
					}
					n++;
				}
				
				return new Result(globalMinimaCandidate.getDomainInterval(), globalMinimaCandidate.getImageSet(), n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función ni el dominio no pueden ser nulos, y la función
		 * 		tiene que converger a valores puntutales
		 * @post Devuelve una estimación del mínimo global en el intervalo de dominio
		 * 		 especificado que tenga un error de imagen menor al especificado.
		 */
		public static Result bisection_globalMinima(final NumberSetFunction function, final FloatClosedInterval domain, final float imageError ) {
			return bisection_globalMinima(function, domain, imageError, new BisectionStrategy() {

				@Override
				public float midPoint(NumberSetFunction function,
						FloatClosedInterval domainInterval) {
					return ( domainInterval.getMin() + domainInterval.getMax() ) / 2.0f;
				}
				
			} );
		}

		public static final class MultiResult {
			private final List<FloatClosedInterval> values;
			private final int iterations;
			
			public MultiResult(List<FloatClosedInterval> values, int iterations) {
				this.values = values;
				this.iterations = iterations;
			}
			
			/**
			 * @post Devuelve los valores
			 */
			public List<FloatClosedInterval> getValues() {
				return this.values;
			}
			
			/**
			 * @post Devuelve las iteraciones
			 */
			public int getIterations() {
				return this.iterations;
			}
			
			/**
			 * @post Devuelve una representación en cadena de texto
			 */
			public String toString() {
				return "{ " + this.values + ", n = " + this.iterations + "}";
			}
		}
		
		private static abstract class BisectionMultiStrategy {
			/**
			 * @post Devuelve el punto intermedio de bisección
			 */
			public abstract List<Float> multiMidPoint(NumberSetVectorialFunction function, List<FloatClosedInterval> domainInterval);
		}
		
		/**
		 * @pre La función ni el dominio pueden ser nulos, y la función tiene que
		 * 		converger a valores puntuales
		 * @post Devuelve el intervalo de raíces más pequeño en el intervalo
		 * 		 especificado con la estrategia especificada, si no encuentra
		 * 		 un intervalo devuelve null.
		 * 
		 * 		 Prioriza aquellos multiintervalos que tengan el menor índice de componente, pequeño, frente
		 * 		 a otros que lo tienen grande
		 */
		private static MultiResult bisection_minnearest_multi(NumberSetVectorialFunction function, List<FloatClosedInterval> multiDomain, float imageError, BisectionMultiStrategy bisectionMultiStrategy) {
			if ( ( function != null ) && ( multiDomain != null ) && ( bisectionMultiStrategy != null ) ) {
				final Stack< List<FloatClosedInterval> > pendingMultiIntervals = new Stack< List<FloatClosedInterval> >();
				boolean squaredImageError = false;
				int n=0;
				
				List<FloatClosedInterval> multiRoot = null;
				
				pendingMultiIntervals.push(multiDomain);
				
				while ( !pendingMultiIntervals.isEmpty() && (multiRoot == null) ) {
					n++;
					List<FloatClosedInterval> eachMultiInterval = pendingMultiIntervals.pop();
					FloatClosedIntervalSet eachImage;
					{
						NumberSetVector<FloatClosedIntervalSet> eachMultiImage = function.evaluate( createVector(eachMultiInterval) );
						if ( eachMultiImage.components().size() == 1 ) {
							eachImage = eachMultiImage.components().get(0);
						}
						else {
							eachImage = eachMultiImage.lengthSquared();
							if ( !squaredImageError ) {
								imageError *= imageError;
								squaredImageError = true;
							}
						}
					}
					
					if ( eachImage.contains(0.0f) ) {
						if ( ( eachImage.length() > imageError ) && ( !isPoint(eachMultiInterval) ) ) {
							FloatClosedInterval[][] newIntervals = new FloatClosedInterval[multiDomain.size()][2];
							List<Float> multiMidPoint = bisectionMultiStrategy.multiMidPoint(function, eachMultiInterval);
							
							// Generar intervalos pequeños y grandes por cada componente
							for ( int i=0; i<multiDomain.size() ; i++) {
								final FloatClosedInterval eachIntervalComponent = eachMultiInterval.get(i);
								final float eachMidComponent = multiMidPoint.get(i);
								
								final float minAfter = Math.nextUp(eachIntervalComponent.getMin());
								
								if ( eachIntervalComponent.getMax() > minAfter ) {
									newIntervals[i][0] = new FloatClosedInterval(eachIntervalComponent.getMin(), eachMidComponent);
									newIntervals[i][1] = new FloatClosedInterval(eachMidComponent, eachIntervalComponent.getMax());
								} else {
									newIntervals[i][0] = new FloatClosedInterval(eachIntervalComponent.getMin());
									newIntervals[i][1] = new FloatClosedInterval(eachIntervalComponent.getMax());
								}
							}
							
							// Empujar a las pila las combinaciones de componentes de intervalo
							int[] currentElections = new int[multiDomain.size()];
							Arrays.fill(currentElections, 1);
							
							boolean moreCombinations;
							
							do {
								List<FloatClosedInterval> eachSubMultiInterval = new ArrayList<FloatClosedInterval>();
								for ( int i=0; i<currentElections.length; i++ ) {
									eachSubMultiInterval.add( newIntervals[i][ currentElections[i] ] );
								}
								
								pendingMultiIntervals.push( eachSubMultiInterval );
								
								moreCombinations = false;
								for ( int i=currentElections.length-1; (i>=0) && (!moreCombinations);i--) {
									if ( currentElections[i] == 1 ) {
										currentElections[i] = 0;
										moreCombinations = true;
									}
									else {
										currentElections[i] = 1;
									}
								}
							} while ( moreCombinations );
						}
						else {
							multiRoot = eachMultiInterval;
						}
					}
				}
				
				return new MultiResult(multiRoot, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función ni el dominio pueden ser nulos, y la función tiene que
		 * 		converger a valores puntuales
		 * @post Devuelve el intervalo de raíces más pequeño en el intervalo
		 * 		 especificado con la estrategia de punto medio, si no encuentra
		 * 		 un intervalo devuelve null.
		 * 
		 * 		 Prioriza aquellos multiintervalos que tengan el menor índice de componente, pequeño, frente
		 * 		 a otros que lo tienen grande
		 */
		public static MultiResult bisection_minnearest_multi(NumberSetVectorialFunction function, List<FloatClosedInterval> multiDomain, float imageError) {
			return bisection_minnearest_multi(function, multiDomain, imageError, new BisectionMultiStrategy(){

				@Override
				public List<Float> multiMidPoint(
						NumberSetVectorialFunction function,
						List<FloatClosedInterval> domainInterval) {
					List<Float> multiMidPoint = new ArrayList<Float>(domainInterval.size());
					
					for ( FloatClosedInterval eachInterval : domainInterval ) {
						multiMidPoint.add( (eachInterval.getMin()+eachInterval.getMax()) / 2.0f );
					}
					
					return multiMidPoint;
				}
				
			} );
		}
	}
	
	
	
	private static abstract class BinaryOperator {
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los intervalos especificados
		 */
		protected abstract FloatClosedIntervalSet process(FloatClosedInterval operand1, FloatClosedInterval operand2);
		
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los conjuntos especificados
		 */
		public final FloatClosedIntervalSet process(FloatClosedIntervalSet operand1, FloatClosedIntervalSet operand2) {
			if ( ( operand1.intervals().size() == 1 ) && ( operand2.intervals().size() == 1 ) ) {
				return this.process(operand1.intervals().get(0), operand2.intervals().get(0));
			}
			else {
				return this.process_multiple(operand1, operand2);
			}
		}
		
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los conjuntos especificados para conjuntos con múltiples
		 * 		 intervalos
		 */
		protected FloatClosedIntervalSet process_multiple(FloatClosedIntervalSet operand1, FloatClosedIntervalSet operand2) {
			FloatClosedIntervalSet result = EMPTY;
			for ( FloatClosedInterval eachInterval1 : operand1.intervals() ) {
				for ( FloatClosedInterval eachInterval2 : operand2.intervals() ) {
					result = result.union( this.process(eachInterval1, eachInterval2) );
				}
			}
			return result;
		}
	}
	
	private static abstract class UnaryOperator {
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los intervalos especificados
		 */
		protected abstract FloatClosedIntervalSet process(FloatClosedInterval operand);
		
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los conjuntos especificados
		 */
		public final FloatClosedIntervalSet process(FloatClosedIntervalSet operand) {
			if ( operand.intervals().size() == 1 ) {
				return this.process(operand.intervals().get(0));
			}
			else {
				return this.process_multiple(operand);
			}
		}
		
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los conjuntos especificados para conjuntos con múltiples
		 * 		 intervalos
		 */
		protected FloatClosedIntervalSet process_multiple(FloatClosedIntervalSet operand) {
			FloatClosedIntervalSet result = EMPTY;
			for ( FloatClosedInterval eachInterval : operand.intervals() ) {
				result = result.union( this.process(eachInterval) );
			}
			return result;
		}
	}
	
	private static abstract class InyectiveUnaryOperator extends UnaryOperator {
		/**
		 * @post Devuelve el resultado de efectuar la operación
		 * 		 con los conjuntos especificados para conjuntos con múltiples
		 * 		 intervalos
		 */
		protected FloatClosedIntervalSet process_multiple(FloatClosedIntervalSet operand) {
			List<FloatClosedInterval> resultIntervals = new ArrayList<FloatClosedInterval>(operand.intervals().size());
			for ( FloatClosedInterval eachInterval : operand.intervals() ) {
				resultIntervals.addAll( this.process(eachInterval).intervals() );
			}
			return new FloatClosedIntervalSet(resultIntervals);
		}
	}
	
	public static final NumberSetFactory<FloatClosedIntervalSet> FACTORY = new NumberSetFactory<FloatClosedIntervalSet>() {

		@Override
		public FloatClosedIntervalSet convert(float value) {
			return new FloatClosedIntervalSet(value);
		}

		@Override
		public FloatClosedIntervalSet convert(double value) {
			return convert( (float) value);
		}

		@Override
		public FloatClosedIntervalSet add(FloatClosedIntervalSet operand1,
				FloatClosedIntervalSet operand2) {
			return new BinaryOperator(){

				@Override
				protected FloatClosedIntervalSet process(
						FloatClosedInterval operand1,
						FloatClosedInterval operand2) {
					return new FloatClosedIntervalSet( new FloatClosedInterval( operand1.getMin() + operand2.getMin(), operand1.getMax() + operand2.getMax() ) );
				}
				
			}.process(operand1, operand2);
		}

		@Override
		public FloatClosedIntervalSet multiply(FloatClosedIntervalSet operand1,
				FloatClosedIntervalSet operand2) {
			return new BinaryOperator(){

				@Override
				protected FloatClosedIntervalSet process(
					FloatClosedInterval operand1,
					FloatClosedInterval operand2) {
					Collection<Float> candidates = Arrays.asList( new Float[]{ operand1.getMin() * operand2.getMin(), operand1.getMin() * operand2.getMax(), operand1.getMax() * operand2.getMin(), operand1.getMax() * operand2.getMax() } );
					return new FloatClosedIntervalSet( new FloatClosedInterval( Collections.min(candidates), Collections.max(candidates) ) );
				}
					
			}.process(operand1, operand2);
		}
		
		@Override
		public FloatClosedIntervalSet divide(FloatClosedIntervalSet dividend, FloatClosedIntervalSet divisor) {
			return new BinaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(
						FloatClosedInterval operand1, FloatClosedInterval operand2) {
					if (!operand2.contains(0.0f) ) {
						Collection<Float> candidates = Arrays.asList( new Float[]{ operand1.getMin() / operand2.getMin(), operand1.getMin() / operand2.getMax(), operand1.getMax() / operand2.getMin(), operand1.getMax() / operand2.getMax() } );
						return new FloatClosedIntervalSet( new FloatClosedInterval( Collections.min(candidates), Collections.max(candidates) ) );
					}
					else {
						/** 
						 * 1 / [operand2.getMin(), operand2.getMax] = [ -Inf, 1.0f / operand2.getMin() ] U [ operand2.getMax(), +Inf ]
						 * [operand1.getMin(), operand1.getMax()] / [operand2.getMin(), operand2.getMax] =
						 * [operand1.getMin(), operand1.getMax()] * ( 1 / [operand2.getMin(), operand2.getMax] ) =
						 * [operand1.getMin(), operand1.getMax()] * ( [ -Inf, 1.0f / operand2.getMin() ] U [ operand2.getMax(), +Inf ] ) =
						 * [ [operand1.getMin(), operand1.getMax()] * [ -Inf, 1.0f / operand2.getMin() ] ) U ( [operand1.getMin(), operand1.getMax()] * [ operand2.getMax(), +Inf ] ]
						 * 
						 * A = { operand1.getMin() / operand2.getMin(), operand1.getMax() / operand2.getMin() }
						 * B = { operand1.getMin() / operand2.getMax(), operand1.getMax() / operand2.getMax() }
						 */
						
						final float climit_min1_min2 = operand1.getMin() / operand2.getMin();
						final float climit_max1_min2 = operand1.getMax() / operand2.getMin();
						
						final float climit_min1_max2 = operand1.getMin() / operand2.getMax();
						final float climit_max1_max2 = operand1.getMax() / operand2.getMax();
						
						final float r_interval1_max, r_interval2_min;
						
						if ( ( operand1.getMin() >= 0.0f ) && ( operand1.getMax() >= 0.0f ) ) {
							r_interval1_max = climit_min1_min2;
							r_interval2_min = climit_min1_max2;
						} else if ( ( operand1.getMin() <= 0.0f ) && ( operand1.getMax() <= 0.0f ) ) {
							r_interval1_max = climit_max1_min2;
							r_interval2_min = climit_max1_max2;
						}
						else {
							if ( operand1.getMin() < 0.0f ) {
								// Implica que operand1.getMax() > 0.0f
								r_interval1_max = climit_max1_min2;
								r_interval2_min = climit_min1_max2;
							}
							else {
								// Implica que operand1.getMax() < 0.0f
								r_interval1_max = climit_min1_min2;
								r_interval2_min = climit_max1_max2;
							}
						}
						
						return new FloatClosedIntervalSet( new FloatClosedInterval(-Float.NEGATIVE_INFINITY, r_interval1_max), new FloatClosedInterval(r_interval2_min, Float.POSITIVE_INFINITY) );
					}
				}
				
			}.process(dividend, divisor);
		}

		@Override
		public FloatClosedIntervalSet opposite(FloatClosedIntervalSet operand) {
			return new InyectiveUnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					return new FloatClosedIntervalSet( new FloatClosedInterval( -operand.getMax(), -operand.getMin() ) );
				}
				
			}.process(operand);
		}
		
		@Override
		public FloatClosedIntervalSet abs(FloatClosedIntervalSet operand) {
			return new UnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					FloatClosedInterval interval;
					
					float abs_min = Math.abs(operand.getMin());
					float abs_max = Math.abs(operand.getMax());
					if ( !operand.contains(0.0f) ) {
						if ( abs_min < abs_max ) {
							interval = new FloatClosedInterval(abs_min, abs_max);
						}
						else {
							interval = new FloatClosedInterval(abs_max, abs_min);
						}
					}
					else {
						interval = new FloatClosedInterval(0.0f, Math.max(abs_min, abs_max));
					}
					return new FloatClosedIntervalSet( interval );
				}
				
			}.process(operand);
		}

		@Override
		public FloatClosedIntervalSet inverse(FloatClosedIntervalSet operand) {
			return new UnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					if ( !operand.contains(0.0f) ) { // Si no contiene el cero
						return new FloatClosedIntervalSet( new FloatClosedInterval( 1.0f / operand.getMax(), 1.0f / operand.getMin() ) );
					}
					else { // Caso contrario
						return new FloatClosedIntervalSet( new FloatClosedInterval( Float.NEGATIVE_INFINITY, 1.0f / operand.getMin() ), new FloatClosedInterval( 1.0f / operand.getMax(), Float.POSITIVE_INFINITY ) );
					}
				}
				
			}.process(operand);
		}

		@Override
		public FloatClosedIntervalSet exp(FloatClosedIntervalSet exponent) {
			return new InyectiveUnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					return new FloatClosedIntervalSet( new FloatClosedInterval( (float) Math.exp(operand.getMin()), (float) Math.exp(operand.getMax()) ) );
				}
				
			}.process(exponent);
		}

		@Override
		public FloatClosedIntervalSet log(FloatClosedIntervalSet antilogarithm) {
			return new InyectiveUnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					return new FloatClosedIntervalSet( new FloatClosedInterval( (float) Math.log(operand.getMin()), (float) Math.log(operand.getMax()) ) );
				}
				
			}.process(antilogarithm);
		}

		/**
		 * @post Devuelve la base elevada a la potencia especificada
		 */
		private FloatClosedIntervalSet pow(FloatClosedIntervalSet base, final float exponent) {
			if ( exponent > 0 ) {
				return new UnaryOperator() {

					@Override
					protected FloatClosedIntervalSet process(
							FloatClosedInterval base) {
						FloatClosedInterval result;
						float minPow = (float) Math.pow(base.getMin(), exponent);
						float maxPow = (float) Math.pow(base.getMax(), exponent);
						
						if ( exponent == Math.floor(exponent) ) { // Si es entero
							if ( (exponent % 2 == 1) || ( base.getMin() >= 0.0f ) ) {
								result = new FloatClosedInterval(minPow, maxPow);
							}
							else if ( base.getMax() < 0 ) {
								result = new FloatClosedInterval(maxPow, minPow);
							}
							else {
								result = new FloatClosedInterval(0.0f, Math.max(minPow, maxPow));
							}
							return new FloatClosedIntervalSet(result);
						}
						else { // Si no es entero
							if ( base.getMax() >= 0.0f ) {
								if ( base.getMin() <= 0.0f ) {
									minPow = 0.0f;
								}
									
								result = new FloatClosedInterval(minPow, maxPow);
									
								return new FloatClosedIntervalSet(result);
							}
							else {
								return FloatClosedIntervalSet.EMPTY;
							}
						}
					}
					
				}.process(base);
			} else if ( exponent == 0 ) {
				if ( !base.equals(0.0f) ) {
					return new FloatClosedIntervalSet(1.0f);
				}
				else {
					return FloatClosedIntervalSet.EMPTY;
				}
			}
			else {
				return pow(FACTORY.inverse(base), -exponent);
			}
		}
		
		@Override
		public FloatClosedIntervalSet pow(FloatClosedIntervalSet base,
				FloatClosedIntervalSet exponent) {
			if ( exponent.getValue() != null ) {
				return pow(base, exponent.getValue().floatValue());
			}
			else {
				if ( base.getMin() > 0.0f ) {
					return super.pow(base, exponent);
				}
				else {
					FloatClosedIntervalSet negativePart = base.intersection(new FloatClosedIntervalSet(new FloatClosedInterval(Float.NEGATIVE_INFINITY, -Float.MIN_VALUE) ));
					
					FloatClosedIntervalSet result = FloatClosedIntervalSet.EMPTY;
					
					{
						Iterator<Float> integersIterator = base.integers();
						while ( integersIterator.hasNext() ) {
							result.union( pow(negativePart, this.convert(integersIterator.next().longValue())) );
						}
					}
					
					if ( base.contains(0.0f) && (!exponent.equals(0.0f)) ) {
						// Incluir el cero
						result.union(new FloatClosedIntervalSet(0.0f));
					}
					
					// Incluir la parte positiva
					result.union(base.intersection(new FloatClosedIntervalSet(new FloatClosedInterval(Float.MIN_VALUE, Float.POSITIVE_INFINITY))));
					
					return result;
				}
			}
		}
		
		@Override
		public FloatClosedIntervalSet square(FloatClosedIntervalSet operand) {
			return new UnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					FloatClosedInterval interval;
					
					float square_min = operand.getMin() * operand.getMin();
					float square_max = operand.getMax() * operand.getMax();
					if ( !operand.contains(0.0f) ) {
						if ( square_min < square_max ) {
							interval = new FloatClosedInterval(square_min, square_max);
						}
						else {
							interval = new FloatClosedInterval(square_max, square_min);
						}
					}
					else {
						interval = new FloatClosedInterval(0.0f, Math.max(square_min, square_max));
					}
					return new FloatClosedIntervalSet( interval );
				}
				
			}.process(operand);
		}

		@Override
		public FloatClosedIntervalSet sin(FloatClosedIntervalSet angle) {
			return new UnaryOperator() {

				@Override
				protected FloatClosedIntervalSet process(FloatClosedInterval operand) {
					FloatClosedInterval result;
					if ( operand.getMax() - operand.getMin() < 2.0f * Math.PI ) {
						float minSin = (float) Math.sin(operand.getMin());
						float maxSin = (float) Math.sin(operand.getMax());
						
						float baseAngle = (float) Math.floor(operand.getMin() / 2.0f / Math.PI) * 2.0f * (float) Math.PI;
						
						boolean hasCritical_1 = operand.contains( baseAngle + (float) Math.PI * 0.5f ) || operand.contains( baseAngle + (float) Math.PI * 2.5f );
						boolean hasCritical_3 = operand.contains( baseAngle + (float) Math.PI * 1.5f ) || operand.contains( baseAngle + (float) Math.PI * 3.5f );
						
						float minResult, maxResult;
						
						if ( hasCritical_1 ) {
							maxResult = 1.0f;
						}
						else {
							maxResult = Math.max(minSin, maxSin);
						}
						
						if ( hasCritical_3 ) {
							minResult = -1.0f;
						}
						else {
							minResult = Math.min(minSin, maxSin);
						}
						
						return new FloatClosedIntervalSet( new FloatClosedInterval(minResult, maxResult) );
					}
					else {
						result = new FloatClosedInterval(-1.0f, 1.0f);
					}
					return new FloatClosedIntervalSet(result);
				}
				
			}.process(angle);
		}

		@Override
		public Class<FloatClosedIntervalSet> getNumberSetClass() {
			return FloatClosedIntervalSet.class;
		}

		@Override
		public boolean hasOnlyGreaterElements(FloatClosedIntervalSet operand1,
				FloatClosedIntervalSet operand2) {
			return (operand1.getMin() > operand2.getMin());
		}

		@Override
		public boolean contains(FloatClosedIntervalSet container,
				FloatClosedIntervalSet contained) {
			return !container.union(contained).intersection(contained).isEmpty();
		}

		@Override
		public FloatClosedIntervalSet conditionalEvaluation(
				FloatClosedIntervalSet x,
				com.esferixis.math.numberSet.NumberSetFactory.ConditionType conditionType,
				FloatClosedIntervalSet a, NumberSetFunction trueFunction,
				NumberSetFunction falseFunction) {
			if ( ( x != null ) && ( conditionType != null ) && ( a != null ) && ( trueFunction != null ) && ( falseFunction != null ) ) {
				FloatClosedIntervalSet trueDomain = null;
				FloatClosedIntervalSet falseDomain = null;
				switch ( conditionType ) {
				case GREATER:
					trueDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(Math.nextUp(a.getMin()), Float.POSITIVE_INFINITY)) );
					falseDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(Float.NEGATIVE_INFINITY, a.getMax()) ) );
					break;
				case GREATEROREQUALS:
					trueDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(a.getMin(), Float.POSITIVE_INFINITY)) );
					falseDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(Float.NEGATIVE_INFINITY, Math.nextAfter(a.getMax(), -1.0f)) ) );
					break;
				case EQUALS:
					trueDomain = x.intersection(a);
					if ( a.getValue() != null ) {
						falseDomain = x.substract(a);
					}
					else {
						falseDomain = x;
					}
					break;
				case SMALLEROREQUALS:
					trueDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(Float.NEGATIVE_INFINITY, a.getMax()) ) );
					falseDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(Math.nextUp(a.getMin()), Float.POSITIVE_INFINITY)) );
					break;
				case SMALLER:
					trueDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(Float.NEGATIVE_INFINITY, Math.nextAfter(a.getMax(), -1.0f)) ) );
					falseDomain = x.intersection(new FloatClosedIntervalSet( new FloatClosedInterval(a.getMin(), Float.POSITIVE_INFINITY)) );
					break;
				case NOTEQUALS:
					if ( a.getValue() != null ) {
						trueDomain = x.substract(a);
					}
					else {
						trueDomain = x;
					}
					falseDomain = x.intersection(a);
					break;
				}
				
				return trueFunction.evaluate(trueDomain).union(falseFunction.evaluate(falseDomain));
			}
			else {
				throw new NullPointerException();
			}
		}
		
	};
	
	/**
	 * @pre La lista de intervalos no puede ser nula ni vacía
	 * @post Crea un vector a partir de la lista de intervalos de coordenada
	 * 		 especificados
	 */
	public static NumberSetVector<FloatClosedIntervalSet> createVector(List<FloatClosedInterval> intervals) {
		if ( intervals != null ) {
			List<FloatClosedIntervalSet> coordinates = new ArrayList<FloatClosedIntervalSet>(intervals.size());
			for ( FloatClosedInterval eachInterval : intervals ) {
				coordinates.add( new FloatClosedIntervalSet(eachInterval) );
			}
			
			return new NumberSetVector<FloatClosedIntervalSet>(coordinates);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public static boolean isPoint(List<FloatClosedInterval> multiInterval) {
		if ( multiInterval != null ) {
			boolean hasNoPoint = false;
			Iterator<FloatClosedInterval> intervalsIterator = multiInterval.iterator();
			while ( intervalsIterator.hasNext() && (!hasNoPoint) ) {
				hasNoPoint = !intervalsIterator.next().isPoint();
			}
			
			return !hasNoPoint;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea el conjunto con los elementos especificados
	 */
	private FloatClosedIntervalSet(List<FloatClosedInterval> intervals) {
		if ( intervals instanceof ArrayList ) {
			this.intervals = (ArrayList<FloatClosedInterval>) intervals;
		}
		else {
			this.intervals = new ArrayList<FloatClosedInterval>(intervals);
		}
	}
	
	/**
	 * @post Crea el conjunto con los elementos especificados
	 */
	private FloatClosedIntervalSet(FloatClosedInterval... intervals) {
		this( Arrays.asList(intervals) );
	}
	
	
	/**
	 * @pre El intervalo no puede ser nulo
	 * @post Crea un conjunto con el intervalo especificado
	 */
	public FloatClosedIntervalSet(FloatClosedInterval interval) {
		if ( interval != null ) {
			this.intervals = new ArrayList<FloatClosedInterval>( Collections.singletonList( interval ) );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea un conjunto con el valor especificado
	 */
	public FloatClosedIntervalSet(float value) {
		this( new FloatClosedInterval(value) );
	}
	
	/**
	 * @post Devuelve los intervalos ordenados de menor a mayor
	 */
	public List<FloatClosedInterval> intervals() {
		return this.intervals;
	}
	
	/**
	 * @post Devuelve si está vacío
	 */
	public boolean isEmpty() {
		return this.intervals.isEmpty();
	}
	
	/**
	 * @pre El conjunto no puede estar vacío
	 * @post Devuelve el valor mínimo
	 * @author ariel
	 * @throws NoSuchElementException
	 */
	public float getMin() {
		if ( !this.isEmpty() ) {
			return this.intervals.get(0).getMin();
		}
		else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * @pre El conjunto no puede estar vacío
	 * @post Devuelve el valor máximo
	 * @author ariel
	 * @throws NoSuchElementException
	 */
	public float getMax() {
		if ( !this.isEmpty() ) {
			return this.intervals.get(this.intervals.size() - 1).getMax();
		}
		else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * @post Devuelve el intervalo que encierra el conjunto
	 */
	public FloatClosedInterval getContainingInterval() {
		return new FloatClosedInterval(this.getMin(), this.getMax());
	}
	
	/**
	 * @post Devuelve un valor si se trata de uno, caso contrario
	 * 		 devuelve null
	 */
	public Float getValue() {
		if ( this.intervals.size() == 1 ) {
			return this.intervals.get(0).getValue();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @post Devuelve si contiene el intervalo especificado
	 */
	public boolean contains(float value) {
		if ( new FloatClosedInterval(this.getMin(), this.getMax()).contains(value) ) {
			return ( Collections.binarySearch(this.intervals, new FloatClosedInterval(value)) == 0);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @post Devuelve la longitud
	 */
	public float length() {
		return this.getMax() - this.getMin();
	}
	
	/**
	 * @post Devuelve el iterador de los enteros contenidos
	 * @author ariel
	 *
	 */
	public Iterator<Float> integers() {
		return new AppendIterator<Float>( new Iterator< Iterator<Float> >() {
			private final Iterator<FloatClosedInterval> intervals = FloatClosedIntervalSet.this.intervals.iterator();
			
			@Override
			public boolean hasNext() {
				return this.intervals.hasNext();
			}

			@Override
			public Iterator<Float> next() {
				return new Iterator<Float>() {
					private final FloatClosedInterval interval = intervals.next();
					private float minFloor = (float) Math.floor( interval.getMin() );
					private float maxCeil = (float) Math.ceil( interval.getMax() );
					
					private float nextValue = minFloor;

					@Override
					public boolean hasNext() {
						return ( nextValue <= maxCeil);
					}

					@Override
					public Float next() {
						float oldNextValue = nextValue;
						
						nextValue = (float) Math.ceil( nextValue + Math.ulp(nextValue) );
						
						return oldNextValue;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
					
				};
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		});
	}
	
	private static abstract class ConmutativeIntersectionProcessor {
		/**
		 * @post Procesa los intervalos que están
		 * 		 fuera de la intersección
		 */
		protected abstract void processDisjunction(List<FloatClosedInterval> intervals);
		
		/**
		 * @post Procesa los intervalos que están
		 * 		 dentro de la intersección
		 */
		protected abstract void processIntersection(FloatClosedInterval intersectingInterval1, List<FloatClosedInterval> intersectingIntervals2);
		
		/**
		 * @post Procesa los conjuntos de intervalos
		 */
		public void process(List<FloatClosedInterval> intervals1, List<FloatClosedInterval> intervals2) {
			while ( !intervals1.isEmpty() || !intervals2.isEmpty() ) {
				if ( !intervals1.isEmpty() && !intervals2.isEmpty() ) {
					if ( intervals1.get(0).getMin() > intervals2.get(intervals2.size()-1).getMin() ) {
						List<FloatClosedInterval> intervalTemp = intervals1;
						intervals1 = intervals2;
						intervals2 = intervalTemp;
					}
					
					
					{
						/**
						 * Buscar el índice que le sigue al intervalo del primero más próximo al
						 * menor intervalo del segundo
						 */
						int indexAfter = -(Collections.binarySearch(intervals1, intervals2.get(0), new Comparator<FloatClosedInterval>() {
							@Override
							public int compare(FloatClosedInterval interval1,
									FloatClosedInterval interval2) {
								final int result;
								if ( interval1.getMax() < interval2.getMin() ) {
									result = -1;
								}
								else {
									result = 1;
								}
								return result;
							}
						} ) + 1);
						
						if ( indexAfter > 0 )
							this.processDisjunction(intervals1.subList(0, indexAfter));
						
						// Truncar la lista de los intervalos del primero desde el índice mencionado
						intervals1 = intervals1.subList(indexAfter, intervals1.size());
					}
					
					if ( !intervals1.isEmpty() ) {
						/**
						 * Si el primer intervalo del segundo solapa con el primer intervalo
						 * del primero
						 */
						if ( intervals1.get(0).getMin() <= intervals2.get(0).getMin() ) {
							/**
							 * Buscar el índice que le sigue al intervalo del segundo más grande
							 * cuyo menor es menor o igual que el máximo del primero
							 */
							int indexAfter = -(Collections.binarySearch(intervals2, intervals1.get(0), new Comparator<FloatClosedInterval>() {
								@Override
								public int compare(FloatClosedInterval interval1,
										FloatClosedInterval interval2) {
									final int result;
									if ( interval1.getMin() <= interval2.getMax() ) {
										result = -1;
									}
									else {
										result = 1;
									}
									return result;
								}
							} ) + 1);
							
							this.processIntersection(intervals1.get(0), intervals2.subList(0, indexAfter));
							
							// Recortar el primer intervalo de la primera
							intervals1 = intervals1.subList(1, intervals1.size());
							
							// Recortar los intervalos que están en la intersección
							intervals2 = intervals2.subList(indexAfter, intervals2.size());
						}
					}
				}
				else {
					this.processDisjunction( !intervals1.isEmpty() ? intervals1 : intervals2);
					intervals1.clear();
					intervals2.clear();
				}
			}
		}
	}
	
	/**
	 * @post Calcula la unión con el conjunto especificado
	 */
	public FloatClosedIntervalSet union(FloatClosedIntervalSet other) {
		return new BinaryOperator() {

			@Override
			public FloatClosedIntervalSet process(FloatClosedInterval operand1,
					FloatClosedInterval operand2) {
				FloatClosedInterval[] resultIntervals;
				if ( operand1.getMin() >= operand2.getMin() ) {
					FloatClosedInterval temp = operand1;
					operand1 = operand2;
					operand2 = temp;
				}
				
				if ( operand1.getMax() >= operand2.getMin() ) {
					resultIntervals = new FloatClosedInterval[]{ new FloatClosedInterval(operand1.getMin(), Math.max(operand1.getMax(), operand2.getMax())) };
				}
				else {
					resultIntervals = new FloatClosedInterval[]{ operand1, operand2 };
				}
				
				return new FloatClosedIntervalSet( new ArrayList<FloatClosedInterval>( Arrays.asList( resultIntervals ) ) );
			}
			
			/**
			 * @post Devuelve el resultado de efectuar la operación
			 * 		 con los conjuntos especificados
			 */
			@Override
			public FloatClosedIntervalSet process_multiple(FloatClosedIntervalSet operand1, FloatClosedIntervalSet operand2) {
				final List<FloatClosedInterval> resultIntervals = new ArrayList<FloatClosedInterval>(operand1.intervals().size() + operand2.intervals().size());
				
				new ConmutativeIntersectionProcessor() {

					@Override
					public void processDisjunction(
							List<FloatClosedInterval> intervals) {
						resultIntervals.addAll(intervals);
					}

					@Override
					public void processIntersection(
							FloatClosedInterval intersectingInterval1,
							List<FloatClosedInterval> intersectingIntervals2) {
						resultIntervals.add( new FloatClosedInterval( Math.min(intersectingInterval1.getMin(), intersectingIntervals2.get(0).getMin() ), Math.max(intersectingInterval1.getMax(), intersectingIntervals2.get(intersectingIntervals2.size()-1).getMax() ) ) );
					}
					
				}.process(operand1.intervals, operand2.intervals);
				
				return new FloatClosedIntervalSet(resultIntervals);
			}
			
		}.process(this, other);
	}
	
	/**
	 * @post Efectúa la unión entre la lista de conjuntos especificados
	 */
	public static FloatClosedIntervalSet union(List<FloatClosedIntervalSet> operands) {
		FloatClosedIntervalSet result = FloatClosedIntervalSet.EMPTY;
		for ( FloatClosedIntervalSet eachOperand : operands ) {
			result = result.union(eachOperand);
		}
		return result;
	}
	
	/**
	 * @post Calcula la intersección con el conjunto especificado
	 */
	public FloatClosedIntervalSet intersection(FloatClosedIntervalSet other) {
		class Operator extends BinaryOperator {
			@Override
			public FloatClosedIntervalSet process(FloatClosedInterval operand1,
					FloatClosedInterval operand2) {
				FloatClosedInterval[] resultIntervals;
				if ( operand1.getMin() >= operand2.getMin() ) {
					FloatClosedInterval temp = operand1;
					operand1 = operand2;
					operand2 = temp;
				}
				
				if ( operand1.getMax() >= operand2.getMin() ) {
					resultIntervals = new FloatClosedInterval[]{ new FloatClosedInterval(operand2.getMin(), Math.min(operand1.getMax(), operand2.getMax() ) ) };
				}
				else {
					resultIntervals = new FloatClosedInterval[]{};
				}
				
				return new FloatClosedIntervalSet( Arrays.asList( resultIntervals ) );
			}
			
			/**
			 * @post Devuelve el resultado de efectuar la operación
			 * 		 con los conjuntos especificados
			 */
			@Override
			public FloatClosedIntervalSet process_multiple(FloatClosedIntervalSet operand1, FloatClosedIntervalSet operand2) {
				final List<FloatClosedInterval> resultIntervals = new ArrayList<FloatClosedInterval>(operand1.intervals().size() + operand2.intervals().size());
				
				new ConmutativeIntersectionProcessor() {

					@Override
					public void processDisjunction(
							List<FloatClosedInterval> intervals) {
						
					}

					@Override
					public void processIntersection(
							FloatClosedInterval intersectingInterval1,
							List<FloatClosedInterval> intersectingIntervals2) {
						
						resultIntervals.addAll( Operator.this.process(intersectingInterval1, intersectingIntervals2.get(0)).intervals() );
						if ( intersectingIntervals2.size() > 2 ) {
							resultIntervals.addAll( intersectingIntervals2.subList(1, intersectingIntervals2.size()-1) );
						}
						
						if ( intersectingIntervals2.size() > 1 ) {
							resultIntervals.addAll( Operator.this.process(intersectingInterval1, intersectingIntervals2.get(intersectingIntervals2.size()-1)).intervals() );
						}
					}
					
				}.process(operand1.intervals, operand2.intervals);
				
				return new FloatClosedIntervalSet(resultIntervals);
			}
		}
		
		return new Operator().process(this, other);
	}
	
	/**
	 * @post Efectúa la intersección entre la lista de conjuntos especificados
	 */
	public static FloatClosedIntervalSet intersection(List<FloatClosedIntervalSet> operands) {
		FloatClosedIntervalSet result = FloatClosedIntervalSet.EMPTY;
		for ( FloatClosedIntervalSet eachOperand : operands ) {
			result = result.intersection(eachOperand);
		}
		return result;
	}
	
	/**
	 * @post Devuelve el complemento
	 */
	public FloatClosedIntervalSet complement() {
		if ( this.equals(EMPTY) ) {
			return ALLRANGE;
		}
		else if ( this.equals(ALLRANGE) ) {
			return EMPTY;
		}
		else {
			List<FloatClosedInterval> resultIntervals = new ArrayList<FloatClosedInterval>(this.intervals.size()+1);
			boolean first = true;
			float nextMin = Float.NaN;
			
			for ( FloatClosedInterval eachInterval : this.intervals ) {
				if ( first ) {
					if ( eachInterval.getMin() == Float.NEGATIVE_INFINITY ) {
						nextMin = Math.nextUp(eachInterval.getMax());
					}
					else {
						nextMin = Float.NEGATIVE_INFINITY;
					}
					first = false;
				}
				else {
					resultIntervals.add(new FloatClosedInterval(nextMin, Math.nextAfter(eachInterval.getMin(), -1.0f)));
					nextMin = Math.nextUp(eachInterval.getMax());
				}
			}
			
			if ( nextMin < Float.POSITIVE_INFINITY ) {
				resultIntervals.add(new FloatClosedInterval(nextMin, Float.POSITIVE_INFINITY));
			}
			
			return new FloatClosedIntervalSet(resultIntervals);
		}
	}
	
	/**
	 * @pre El sustrahendo no puede ser nulo
	 * @post Efectúa la sustracción con el sustrahendo especificado
	 */
	public FloatClosedIntervalSet substract(FloatClosedIntervalSet other) {
		if ( other != null ) {
			return this.intersection(other.complement());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve si es i
	 * gual al entero especificado
	 */
	public boolean equals(float value) {
		return ( this.intervals.size() == 1 ) && this.intervals.get(0).equals(value);
	}
	
	/**
	 * @post Devuelve una representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		boolean first = true;
		String result = "{ ";
		for ( FloatClosedInterval eachInterval : this.intervals ) {
			if ( !first ) {
				result = result + " U ";
			}
			else {
				first = false;
			}
			
			result = result + eachInterval;
		}
		return result + " }";
	}

	/* (non-Javadoc)
	 * @see com.esferixis.math.numberSet.NumberSet#factory()
	 */
	@Override
	public NumberSetFactory<FloatClosedIntervalSet> factory() {
		return FACTORY;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.intervals.hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof FloatClosedIntervalSet ) ) {
			return ( other == this) || ((FloatClosedIntervalSet) other).intervals().equals(this.intervals());
		}
		else {
			return false;
		}
	}
}
