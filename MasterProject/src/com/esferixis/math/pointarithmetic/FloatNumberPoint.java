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
package com.esferixis.math.pointarithmetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.math.numberSet.NumberSet;
import com.esferixis.math.numberSet.NumberSetFactory;
import com.esferixis.math.numberSet.NumberSetFunction;
import com.esferixis.math.numberSet.NumberSetVector;
import com.esferixis.math.numberSet.NumberSetVectorialFunction;
import com.esferixis.math.numberSet.linearsystem.LinearSystem;
import com.esferixis.misc.infinitesimalSortedSet.Interval;

/**
 * @author ariel
 *
 */
public final class FloatNumberPoint extends NumberPoint<FloatNumberPoint> {
	private final float value;
	
	public FloatNumberPoint(float value) {
		this.value = value;
	}
	
	public static class NumericalMethods {
		private NumericalMethods() {};
		
		private interface RootAcceptanceCriterium {
			/**
			 * @post Devuelve si tiene una precisión aceptable
			 */
			public boolean hasSufficientPrecision(float x, float image);
		}
		
		private static final class ImageErrorRootAcceptanceCriterium implements RootAcceptanceCriterium {
			private final float imageError;
			
			public ImageErrorRootAcceptanceCriterium(float imageError) {
				this.imageError = imageError;
			}

			/* (non-Javadoc)
			 * @see com.esferixis.math.pointarithmetic.FloatNumberPoint.NumericalMethods.PointAcceptanceCriterium#hasSufficientPrecision(float, float)
			 */
			@Override
			public boolean hasSufficientPrecision(float x, float image) {
				return ( Math.abs(image) < this.imageError );
			}
			
		}
		
		public static class Result {
			private final Float value;
			private final FloatClosedInterval enclosingInterval;
			private final int iterations;
			
			private Result(Float value, FloatClosedInterval enclosingInterval, int iterations) {
				this.value = value;
				this.enclosingInterval = enclosingInterval;
				this.iterations = iterations;
			}
			
			/**
			 * @post Devuelve el punto
			 */
			public FloatNumberPoint getPoint() {
				if ( this.value != null ) {
					return new FloatNumberPoint(this.value);
				}
				else {
					return null;
				}
			}
			
			/**
			 * @post Devuelve el intervalo (Si fue hallado)
			 */
			public FloatClosedInterval getEnclosingInterval() {
				return this.enclosingInterval;
			}
			
			/**
			 * @post Devuelve la cantidad de iteraciones
			 */
			public int getIterations() {
				return this.iterations;
			}
			
			/**
			 * @post Devuelve una representación en String
			 */
			@Override
			public String toString() {
				return "{ " + this.value + ", n=" + this.iterations + " }";
			}
		}
		
		public static class MultiResult {
			private final NumberSetVector<FloatNumberPoint> value;
			private final int iterations;
			
			private MultiResult(NumberSetVector<FloatNumberPoint> value, int iterations) {
				this.value = value;
				this.iterations = iterations;
			}
			
			/**
			 * @post Devuelve el punto
			 */
			public NumberSetVector<FloatNumberPoint> getPoint() {
				return this.value;
			}
			
			/**
			 * @post Devuelve la cantidad de iteraciones
			 */
			public int getIterations() {
				return this.iterations;
			}
			
			/**
			 * @post Devuelve una representación en String
			 */
			@Override
			public String toString() {
				return "{ " + this.value + ", n=" + this.iterations + " }";
			}
		}
		
		/**
		 * @pre La función y el dominio no pueden ser nulos.
		 * @post Devuelve la aproximación de la raíz con el error de dominio y/o de imagen especificados,
		 * 		 efectuando el método de bisección.
		 * 		 Si no fuera posible hallar la solución devuelve null
		 */
		public static Result bisection_minnearest(NumberSetFunction function, FloatClosedInterval domain, float imageError) {
			if ( ( function != null ) && ( domain != null ) ) {
				
				int n=0;
				Float result = null;
				FloatClosedInterval enclosingInterval=null;
				
				Stack<FloatClosedInterval> pendingIntervals = new Stack<FloatClosedInterval>();
				pendingIntervals.push(domain);
				while ( !pendingIntervals.isEmpty() && ( result == null ) ) {
					n++;
					final FloatClosedInterval eachInterval = pendingIntervals.pop();
					
					float y1 = function.evaluate(new FloatNumberPoint(eachInterval.getMin())).getValue();
					float y2 = function.evaluate(new FloatNumberPoint(eachInterval.getMax())).getValue();
					
					if ( ((y1 >= 0.0f) && (y2 <= 0.0f)) || ( (y1 <= 0.0f) && (y2 >= 0.0f) ) ) {
						float midPoint = (eachInterval.getMin()+eachInterval.getMax())/2.0f;
						
						if ( ( Math.abs( function.evaluate(new FloatNumberPoint(midPoint)).getValue() ) > imageError ) && ( eachInterval.getMin() + Math.ulp(eachInterval.getMin()) < eachInterval.getMax() ) ) {
							pendingIntervals.push(new FloatClosedInterval(midPoint, eachInterval.getMax()));
							pendingIntervals.push(new FloatClosedInterval(eachInterval.getMin(), midPoint));
						}
						else {
							result = midPoint;
							enclosingInterval = eachInterval;
						}
					}
				}
				return new Result(result, enclosingInterval, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función no puede ser nula,
		 * 		y tiene que especificarse el punto inicial o un intervalo de dominio.
		 * 		Tiene que especificarse el criterio de aceptación de raíz
		 * @post Devuelve la aproximación de la raíz empezando a partir del punto inicial especificado,
		 * 		 con el delta de dominio de aproximación de derivada, el criterio de aceptación 
		 * 		 especificado, efectuando el método de Newton-Rawson.
		 * 		 Si se especifica intervalo de dominio se descartará el resultado si no pertenece
		 * 		 a dicho intervalo.
		 * 		 Si no fuera posible hallar la solución devuelve null
		 */
		private static Result newton_raphson(NumberSetFunction function, float x0, float xDeltaDerivative, FloatClosedInterval domainInterval, RootAcceptanceCriterium rootAcceptanceCriterium) {
			
			final int maxIterations=51;
			if ( ( function != null ) && ( rootAcceptanceCriterium != null ) ) {
				float x = x0;
				Float result = null;
				
				Float x_neg=null, x_pos=null;
				
				int n=0;
				
				boolean noEnd=false;
				
				do {
					n++;
					final float y = function.evaluate(new FloatNumberPoint(x)).getValue();
					
					if ( y < 0.0f ) {
						x_neg = x;
					} else if ( y > 0.0f ) {
						x_pos = x;
					}
					
					noEnd = (n < maxIterations) && ( (domainInterval == null) || domainInterval.contains(x) );
					
					if ( noEnd ) {
						if ( !rootAcceptanceCriterium.hasSufficientPrecision(x, y) ) {
							x = x - y / function.derivateApproximation( new FloatNumberPoint(x), xDeltaDerivative).getValue();
						}
						else {
							result = x;
							noEnd = false;
						}
					}
				} while ( noEnd );
				
				FloatClosedInterval enclosingInterval;
				if ( ( x_neg != null ) && ( x_pos != null ) ) {
					enclosingInterval = new FloatClosedInterval(Math.min(x_neg, x_pos), Math.max(x_neg, x_pos));
				}
				else {
					enclosingInterval = null;
				}
				
				return new Result(result, enclosingInterval, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función no puede ser nula,
		 * 		y tiene que especificarse el punto inicial o un intervalo de dominio.
		 * 		Tiene que especificarse por lo menos error de dominio o de imagen.
		 * @post Devuelve la aproximación de la raíz empezando a partir del punto inicial especificado,
		 * 		 con el delta de dominio de aproximación de derivada, el error de imagen
		 * 		 especificado, efectuando el método de Newton-Rawson.
		 * 		 Si se especifica intervalo de dominio se descartará el resultado si no pertenece
		 * 		 a dicho intervalo.
		 * 		 Si no fuera posible hallar la solución devuelve null
		 */
		public static Result newton_raphson(NumberSetFunction function, float x0, float xDeltaDerivative, FloatClosedInterval domainInterval, float imageError) {
			return newton_raphson(function, x0, xDeltaDerivative, domainInterval, new ImageErrorRootAcceptanceCriterium(imageError));
		}
		
		/**
		 * @pre La función y el dominio no pueden ser nulos.
		 * @post Devuelve la aproximación de la raíz con el delta de aproximación de derivada,
		 * 		 el error de dominio y/o de imagen especificados,
		 * 		 usando el método de bisección y el de Newton-Rawson.
		 * 		 Si no fuera posible hallar la solución devuelve null
		 */
		public static Result bisection_minnearest_N_R(NumberSetFunction function, FloatClosedInterval domain, float xDeltaDerivative, float imageError) {
			if ( ( function != null ) && ( domain != null ) ) {
				FloatClosedInterval enclosingInterval=null;
				boolean first=true;
				
				int n=0;
				Float result = null;
				
				Stack<FloatClosedInterval> pendingIntervals = new Stack<FloatClosedInterval>();
				pendingIntervals.push(domain);
				while ( !pendingIntervals.isEmpty() && ( result == null ) ) {
					n++;
					final FloatClosedInterval eachInterval = pendingIntervals.pop();
					
					float y1 = function.evaluate(new FloatNumberPoint(eachInterval.getMin())).getValue();
					float y2 = function.evaluate(new FloatNumberPoint(eachInterval.getMax())).getValue();
					
					float midPoint = (eachInterval.getMin()+eachInterval.getMax())/2.0f;
					
					if ( ((y1 >= 0.0f) && (y2 <= 0.0f)) || ( (y1 <= 0.0f) && (y2 >= 0.0f) ) ) {
						
						Result midPointN_R = newton_raphson(function, midPoint, xDeltaDerivative, eachInterval, imageError);
						if ( midPointN_R.getPoint() != null) {
							midPoint = midPointN_R.getPoint().getValue();
						}
						
						if ( ( Math.abs( function.evaluate(new FloatNumberPoint(midPoint)).getValue() ) > imageError ) && ( eachInterval.getMin() + Math.ulp(eachInterval.getMin()) < eachInterval.getMax() ) ) {
							pendingIntervals.push(new FloatClosedInterval(midPoint, eachInterval.getMax()));
							pendingIntervals.push(new FloatClosedInterval(eachInterval.getMin(), midPoint));
						}
						else {
							result = midPoint;
							enclosingInterval = eachInterval;
						}
					}
					else if ( first ) {
						Result midPointN_R = newton_raphson(function, midPoint, xDeltaDerivative, eachInterval, imageError);
						if ( midPointN_R.getPoint() != null) {
							if ( midPointN_R.getEnclosingInterval() == null ) {
								result = midPointN_R.getPoint().getValue();
							}
							else {
								pendingIntervals.push(midPointN_R.getEnclosingInterval());
							}
						}
					}
					
					first = false;
				}
				return new Result(result, enclosingInterval, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve una estimación de la raíz más cercana usando
		 * 		 intentos de bisecciones sucesivos empezando desde el menor intervalo
		 */
		public static Result bisection_minnearest_N_R_segmented(NumberSetFunction function, FloatClosedInterval domain, float xDeltaDerivative, float imageError, float domainSegmentLength) {
			Result result = null;
			int segments = (int) ( Math.floor( domain.length() / domainSegmentLength ) );
			for ( int i=0 ; (i<segments) && ( ( result == null ) || ( result.getPoint() == null ) ) ;i++ ) {
				FloatClosedInterval domainSegment = new FloatClosedInterval(domain.getMin() + domain.length() * (float) i / (float) segments, domain.getMin() + domain.length() * (float) (i+1) / (float) segments);
				result = bisection_minnearest_N_R(function, domainSegment, xDeltaDerivative, imageError);
			}
			return result;
		}
		
		/**
		 * @pre La función no puede ser nula, y si hay intervalos de dominio tienen
		 * 		que tener las mismas dimensiones que las dimensiones del punto
		 * 		inicial y ninguna de ellas puede ser nula
		 * @post Devuelve una raíz con el método de newton rawson multivariable
		 */
		public static MultiResult newton_raphson_multivariate(final NumberSetVectorialFunction function, final NumberSetVector<FloatNumberPoint> x0, final float xDeltaDerivative, final List<FloatClosedInterval> domainIntervals, final float imageError) {
			if ( ( function != null ) && (x0 != null)) {
				final float squaredImageError = imageError * imageError;
				
				// Si se especificaron intervalos de dominio
				if ( domainIntervals != null ) {
					if ( domainIntervals.size() == x0.components().size() ) {
						for ( FloatClosedInterval eachInterval : domainIntervals ) {
							if ( eachInterval == null ) {
								throw new NullPointerException();
							}
						}
					}
					else {
						throw new IllegalArgumentException("Domain intervals mismatch");
					}
				}
				
				boolean end = false;
				NumberSetVector<FloatNumberPoint> x = x0;
				NumberSetVector<FloatNumberPoint> result = null;	
				int n=0;
				
				do {
					if ( n++ < 51 ) {
						/** 
						 * Si se especificaron intervalos de dominio verificar que
						 * "x" no esté fuera de los intervalos
						 */
						if ( domainIntervals != null ) {
							for ( int i=0 ; (i<x.components().size()) && (!end); i++) {
								if ( !domainIntervals.get(i).contains(x.components().get(i).getValue()) ) {
									end = true;
								}
							}
						}
						
						NumberSetVector<FloatNumberPoint> imageValue = function.evaluate(x);
						// Si la imagen es mayor al error esperado
						if ( imageValue.lengthSquared().getValue() > squaredImageError ) {
							// Calcular nuevo "x"
							//x = x.sub( function.jacobianApproximation(x, xDeltaDerivative).inverse().multiply(imageValue.columnMatrix()).columnToVector(0) );
							x = LinearSystem.gaussSolve(function.jacobianApproximation(x, xDeltaDerivative), imageValue.opposite()).add(x);
						}
						else { // Caso contrario
							result = x;
							end = true;
						}
					}
					else {
						end = true;
					}
					
				} while (!end);
				
				return new MultiResult(result, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función y los intervalos no pueden ser nulos
		 * @post Busca una raíz en el intervalo especificado con el método de Montecarlo,
		 * 		 especificando el error de imagen y la cantidad máxima de iteraciones especificada
		 */
		public static MultiResult montecarlo_rootfinding(NumberSetVectorialFunction function, FloatClosedInterval[] domainIntervals, float imageError, Integer maxIterations) {
			if ( ( function != null ) && ( domainIntervals != null ) ) {
				imageError *= imageError;
				int n;
				float[] domainValue = new float[domainIntervals.length];
				
				NumberSetVector<FloatNumberPoint> solution = null;
				float minImageSquaredLength = Float.POSITIVE_INFINITY;
				
				for (n = 0 ; ( (maxIterations == null ) || (n < maxIterations) ) && ( solution == null ) ; n++ ) {
					for ( int i = 0 ; i < domainIntervals.length ; i++) {
						FloatClosedInterval eachInterval = domainIntervals[i];
						if ( eachInterval != null ) {
							domainValue[i] = (float) ( eachInterval.getMin() + eachInterval.length() * Math.random() );
						}
						else {
							throw new NullPointerException();
						}
					}
					
					final NumberSetVector<FloatNumberPoint> pointDomain = new NumberSetVector<FloatNumberPoint>(FACTORY, domainValue);
					final float pointImageLengthSquared = function.evaluate(pointDomain).lengthSquared().getValue();
					
					if ( pointImageLengthSquared < imageError ) {
						solution = pointDomain;
					}
					
					if ( pointImageLengthSquared < minImageSquaredLength ) {
						minImageSquaredLength = pointImageLengthSquared;
						System.out.print("{ " + pointDomain + "}, { " + pointImageLengthSquared + " }\n");
					}
				}
				
				return new MultiResult(solution, n);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La función y los intervalos no pueden ser nulos
		 * @post Busca la raíz más pequeña en el intervalo especificado en forma probabilística,
		 * 		 con el método de newton-rawson especificando el error de imagen
		 * 		 y la cantidad máxima de iteraciones especificada en una búsqueda de una raíz
		 */
		public static Result probabilistic_N_R_smallest_rootfinding(final NumberSetFunction function, FloatClosedInterval domainInterval, final float xDeltaDerivative, final float imageError, final int browsingMaxIterations) {
			if ( ( function != null ) && ( domainInterval != null ) ) {
				RootAcceptanceCriterium rootAcceptanceCriterium = new RootAcceptanceCriterium() {
					private final RootAcceptanceCriterium targetAcceptanceCriterium = new ImageErrorRootAcceptanceCriterium(imageError);
					
					@Override
					public boolean hasSufficientPrecision(float x, float image) {
						return targetAcceptanceCriterium.hasSufficientPrecision(x, function.evaluate(new FloatNumberPoint(x)).getValue());
					}
					
				};
				
				int n=0;
				
				final List<Float> foundedRoots = new ArrayList<Float>();
				Float smallestFoundedRoot = null;
				
				NumberSetFunction remainingFunction = new NumberSetFunction() {

					@Override
					public <V extends NumberSet<V>> V evaluate(V operand) {
						final NumberSetFactory<V> factory = operand.factory();
						V value = function.evaluate(operand);
						V denominator = factory.one();
						for ( Float eachRoot : foundedRoots ) {
							denominator = factory.multiply(denominator, factory.sub(operand, factory.convert(eachRoot)));
						}
						return factory.divide(value, denominator);
					}
					
				};
				
				boolean foundedRoot;
				do {
					foundedRoot = false;
					if (domainInterval.length() != 0.0f) {
						for ( int i=0; (i<browsingMaxIterations) && (!foundedRoot); i++ ) {
							final float x0 = (float) (domainInterval.getMin() + Math.random() * domainInterval.length());
							final Result rootResult = newton_raphson(remainingFunction, x0, xDeltaDerivative, null, rootAcceptanceCriterium);
							if ( rootResult.getPoint() != null ) {
								final float candidateRoot=rootResult.getPoint().getValue();
								if ( domainInterval.contains(candidateRoot) ) {
									smallestFoundedRoot = candidateRoot;
									domainInterval = new FloatClosedInterval(domainInterval.getMin(), smallestFoundedRoot);
								}
									
								foundedRoots.add(candidateRoot);
								foundedRoot = true;
							}
							n++;
						}
					}
				} while ( foundedRoot );
				
				return new Result( smallestFoundedRoot, null, n);
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	public static final NumberSetFactory<FloatNumberPoint> FACTORY = new NumberSetFactory<FloatNumberPoint>() {

		@Override
		public FloatNumberPoint convert(float value) {
			return new FloatNumberPoint(value);
		}

		@Override
		public FloatNumberPoint convert(double value) {
			return this.convert( (float) value);
		}

		@Override
		public FloatNumberPoint add(FloatNumberPoint operand1,
				FloatNumberPoint operand2) {
			return new FloatNumberPoint(operand1.value+operand2.value);
		}
		
		/**
		 * @post Resta los dos conjuntos
		 */
		@Override
		public FloatNumberPoint sub(FloatNumberPoint operand1, FloatNumberPoint operand2) {
			return new FloatNumberPoint(operand1.value-operand2.value);
		}

		@Override
		public FloatNumberPoint multiply(FloatNumberPoint operand1,
				FloatNumberPoint operand2) {
			return new FloatNumberPoint( operand1.value * operand2.value );
		}

		@Override
		public FloatNumberPoint abs(FloatNumberPoint operand) {
			return new FloatNumberPoint(Math.abs(operand.value));
		}

		@Override
		public FloatNumberPoint opposite(FloatNumberPoint operand) {
			return new FloatNumberPoint(-operand.value);
		}

		@Override
		public FloatNumberPoint inverse(FloatNumberPoint operand) {
			return new FloatNumberPoint(1.0f / operand.value);
		}
		
		/**
		 * @post Calcula la división entre los dos conjuntos
		 * 
		 * 		 Ésta implementación calcula la inversa del divisor,
		 * 		 y luego la multiplica con el dividendo
		 */
		@Override
		public FloatNumberPoint divide(FloatNumberPoint dividend, FloatNumberPoint divisor) {
			return new FloatNumberPoint(dividend.value / divisor.value);
		}

		@Override
		public FloatNumberPoint exp(FloatNumberPoint exponent) {
			return new FloatNumberPoint( (float) Math.exp(exponent.value));
		}

		@Override
		public FloatNumberPoint log(FloatNumberPoint antilogarithm) {
			return new FloatNumberPoint( (float) Math.log(antilogarithm.value));
		}
		
		/**
		 * @post Devuelve la base a la potencia especificada
		 */
		@Override
		public FloatNumberPoint pow(FloatNumberPoint base, FloatNumberPoint exponent) {
			return new FloatNumberPoint( (float) Math.pow( base.value, exponent.value) );
		}

		@Override
		public FloatNumberPoint sin(FloatNumberPoint angle) {
			return new FloatNumberPoint( (float) Math.sin(angle.value) );
		}
		
		/**
		 * @post Devuelve el coseno del ángulo
		 * 	
		 * 		 Ésta implementación devuelve sin(Math.PI/2-angle)
		 */
		@Override
		public FloatNumberPoint cos(FloatNumberPoint angle) {
			return new FloatNumberPoint( (float) Math.cos(angle.value) );
		}
		
		/**
		 * @post Devuelve la tangente del ángulo
		 * 
		 * 		 Ésta implementación devuelve sin(angle)/cos(angle)
		 */
		@Override
		public FloatNumberPoint tan(FloatNumberPoint angle) {
			return new FloatNumberPoint( (float) Math.tan(angle.value) );
		}

		@Override
		public Class<FloatNumberPoint> getNumberSetClass() {
			return FloatNumberPoint.class;
		}

		@Override
		public boolean hasOnlyGreaterElements(FloatNumberPoint operand1,
				FloatNumberPoint operand2) {
			return operand1.getValue() > operand2.getValue();
		}

		@Override
		public boolean contains(FloatNumberPoint container,
				FloatNumberPoint contained) {
			return container.getValue() == contained.getValue();
		}

		@Override
		public FloatNumberPoint conditionalEvaluation(
				FloatNumberPoint x,
				com.esferixis.math.numberSet.NumberSetFactory.ConditionType conditionType,
				FloatNumberPoint a, NumberSetFunction trueFunction,
				NumberSetFunction falseFunction) {
			if ( (x != null ) && ( conditionType != null ) && ( a != null ) && ( trueFunction != null ) && ( falseFunction != null ) ) {
				boolean trueCondition = false;
				switch ( conditionType ) {
				case GREATER:
					trueCondition = ( x.getValue() > a.getValue() );
					break;
				case GREATEROREQUALS:
					trueCondition = ( x.getValue() >= a.getValue() );
					break;
				case EQUALS:
					trueCondition = ( x.getValue() == a.getValue() );
					break;
				case SMALLEROREQUALS:
					trueCondition = ( x.getValue() <= a.getValue() );
					break;
				case SMALLER:
					trueCondition = ( x.getValue() < a.getValue() );
					break;
				case NOTEQUALS:
					trueCondition = ( x.getValue() != a.getValue() );
					break;
				}
				return ( trueCondition ? trueFunction : falseFunction ).evaluate(x);
			}
			else {
				throw new NullPointerException();
			}
		}
		
	};
	
	/**
	 * @post Devuelve el valor
	 */
	public float getValue() {
		return this.value;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return new Float(this.value).hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof FloatNumberPoint ) ) {
			return ((FloatNumberPoint) other).value == this.value;
		}
		else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.math.numberSet.NumberSet#factory()
	 */
	@Override
	public NumberSetFactory<FloatNumberPoint> factory() {
		return FACTORY;
	}
	
	/**
	 * @post Devuelve una representación en String
	 */
	@Override
	public String toString() {
		return Float.toString(this.value);
	}
}
