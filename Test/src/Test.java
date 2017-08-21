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

import java.util.Arrays;
import java.util.List;

import com.esferixis.math.Vector3f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.math.intervalarithmetic.FloatClosedIntervalSet;
import com.esferixis.math.numberSet.NumberSet;
import com.esferixis.math.numberSet.NumberSetFactory;
import com.esferixis.math.numberSet.NumberSetFunction;
import com.esferixis.math.numberSet.NumberSetVector;
import com.esferixis.math.numberSet.NumberSetVectorialFunction;
import com.esferixis.math.numberSet.NumberSetVectorialScalarFunction;
import com.esferixis.math.pointarithmetic.FloatNumberPoint;
import com.esferixis.misc.relation.UnitypeFunction;

/**
 * @author ariel
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final NumberSetFunction function = new NumberSetFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate(V operand) {
				final NumberSetFactory<V> factory = operand.factory();
				return factory.pow(operand, factory.convert(2.0f));
			}
			
		};
		
		final NumberSetFunction function2 = new NumberSetFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate(V operand) {
				final NumberSetFactory<V> factory = operand.factory();
				//return value.multiply(new FloatClosedIntervalSet(2.0f)).sub(new FloatClosedIntervalSet(2.0f));
				return factory.sub( function.derivateApproximation(operand, 0.001f), factory.convert(2.0f));
			}
			
		};
		
		final NumberSetFunction functionDerivate = new NumberSetFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate(V operand) {
				final NumberSetFactory<V> factory = operand.factory();
				return factory.pow(factory.sub(operand, factory.convert(1.0f)), factory.convert(2.0f));
			}
			
		};
		
		final NumberSetFunction polynomialFunction = new NumberSetFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate(V operand) {
				final NumberSetFactory<V> factory = operand.factory();
				float roots[] = new float[]{-3.0f, 1.0f, 3.2423f, 7.0f, 15.0f, -6.44324f};
				V result = factory.convert(1.0f);
				
				for ( float eachRoot : roots ) {
					result = factory.multiply(result, factory.sub(operand, factory.convert(eachRoot)));
				}
				
				return result;
			}
			
		};
		
		final NumberSetFunction function5 = new NumberSetFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate(V operand) {
				final NumberSetFactory<V> factory = operand.factory();
				return factory.add( factory.sin(factory.pow(operand, factory.convert(2.0f))), factory.sin(factory.multiply(operand, factory.convert(3.0f)) ));
			}
			
		};
		
		final NumberSetVectorialFunction multiFunction1 = new NumberSetVectorialFunction() {

			@Override
			public <V extends NumberSet<V>> NumberSetVector<V> evaluate(
					NumberSetVector<V> operand) {
				return operand.sub( new NumberSetVector<V>(operand.elementsFactory(), 2.0f, 1.0f) );
			}
			
		};
		
		final NumberSetVectorialScalarFunction multiFunctionUnitOne = new NumberSetVectorialScalarFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				final NumberSetFactory<V> factory = operand.elementsFactory();
				return factory.sub(operand.lengthSquared(), factory.convert(1.0f));
			}
			
		};
		
		class ImplicitSurfaceTemporalStatic extends NumberSetVectorialScalarFunction {
			private final NumberSetVectorialScalarFunction implicitSurface;
			
			/**
			 * @post Crea la función de la superficie implícita estática
			 * 		 con la función de superficie implícita especificada
			 */
			public ImplicitSurfaceTemporalStatic(NumberSetVectorialScalarFunction implicitSurface) {
				this.implicitSurface = implicitSurface;
			}

			/* (non-Javadoc)
			 * @see com.esferixis.math.numberSet.NumberSetVectorialScalarFunction#evaluate_scalar(com.esferixis.math.numberSet.NumberSetVector)
			 */
			@Override
			public <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				return implicitSurface.evaluate_scalar( new NumberSetVector<V>( Arrays.asList( operand.components().get(1), operand.components().get(2), operand.components().get(3)) ) );
			}
			
		}
		
		NumberSetVectorialScalarFunction sphere = new ImplicitSurfaceTemporalStatic(multiFunctionUnitOne);
		
		abstract class ImplicitSurfaceTransformation extends NumberSetVectorialScalarFunction {
			private final NumberSetVectorialScalarFunction temporalImplicitSurface;
			
			/**
			 * @post Crea la función de la superficie implícita estática
			 * 		 con la función de superficie implícita especificada
			 */
			public ImplicitSurfaceTransformation(NumberSetVectorialScalarFunction temporalImplicitSurface) {
				this.temporalImplicitSurface = temporalImplicitSurface;
			}

			/**
			 * @post Devuelve la transformación del punto especificado
			 * 		 con el tiempo especificado
			 */
			public abstract <V extends NumberSet<V>> NumberSetVector<V> transform(V time, NumberSetVector<V> point);
			
			/* (non-Javadoc)
			 * @see com.esferixis.math.numberSet.NumberSetVectorialScalarFunction#evaluate_scalar(com.esferixis.math.numberSet.NumberSetVector)
			 */
			@Override
			public final <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				final V time = operand.components().get(1);
				NumberSetVector<V> point = new NumberSetVector<V>( Arrays.asList( operand.components().get(1), operand.components().get(2), operand.components().get(3) ));
				point = this.transform(time, point);
				
				return this.temporalImplicitSurface.evaluate_scalar( new NumberSetVector<V>( Arrays.asList(time, point.components().get(0), point.components().get(1), point.components().get(2)) ) );
			}
		}
		
		class ImplicitSurfaceTranslation extends NumberSetVectorialScalarFunction {
			private NumberSetVectorialScalarFunction temporalImplicitSurface;
			private final Vector3f startPosition, speed;
			private final float referenceTime;
			
			/**
			 * @param temporalImplicitSurface
			 */
			public ImplicitSurfaceTranslation(
					NumberSetVectorialScalarFunction temporalImplicitSurface, Vector3f startPosition, Vector3f speed, float referenceTime) {
				this.temporalImplicitSurface = temporalImplicitSurface;
				this.startPosition = startPosition;
				this.speed = speed;
				this.referenceTime = referenceTime;
			}

			/* (non-Javadoc)
			 * @see #transform(com.esferixis.math.numberSet.NumberSet, com.esferixis.math.numberSet.NumberSetVector)
			 */
			public <V extends NumberSet<V>> NumberSetVector<V> transform(
					V time, NumberSetVector<V> point) {
				final NumberSetFactory<V> factory = time.factory();
				NumberSetVector<V> translation = new NumberSetVector<V>(factory, this.startPosition).add(new NumberSetVector<V>(factory, this.speed).scale(factory.sub(time, factory.convert(this.referenceTime))));
				return point.sub(translation);
			}
			
			@Override
			public final <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				final V time = operand.components().get(0);
				NumberSetVector<V> point = new NumberSetVector<V>( Arrays.asList( operand.components().get(1), operand.components().get(2), operand.components().get(3) ));
				point = this.transform(time, point);
				
				return this.temporalImplicitSurface.evaluate_scalar( new NumberSetVector<V>( Arrays.asList(time, point.components().get(0), point.components().get(1), point.components().get(2)) ) );
			}
		
		}
		
		class CollisionFunction extends NumberSetVectorialScalarFunction {
			private final NumberSetVectorialScalarFunction temporalImplicitSurface1, temporalImplicitSurface2;
			
			/**
			 * @post Crea la función de colisión con las funciones de superficie temporales implícitas especificadas
			 */
			public CollisionFunction(NumberSetVectorialScalarFunction temporalImplicitSurface1, NumberSetVectorialScalarFunction temporalImplicitSurface2) {
				this.temporalImplicitSurface1 = temporalImplicitSurface1;
				this.temporalImplicitSurface2 = temporalImplicitSurface2;
			}

			/* (non-Javadoc)
			 * @see com.esferixis.math.numberSet.NumberSetVectorialScalarFunction#evaluate_scalar(com.esferixis.math.numberSet.NumberSetVector)
			 */
			@Override
			public <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				final NumberSetFactory<V> factory = operand.elementsFactory();
				return factory.add( factory.abs( this.temporalImplicitSurface1.evaluate_scalar(operand) ), factory.abs( this.temporalImplicitSurface2.evaluate_scalar(operand) ) );
			}
			
		}
		
		/*
		FloatClosedIntervalSet.NumericMethods.Result intervalResult;
		FloatClosedInterval interval = new FloatClosedInterval(2.0f, 1000.0f);
		
		System.out.println( intervalResult = FloatClosedIntervalSet.NumericMethods.bisection_minnearest(function5, interval, 0.001f) );
		System.out.println( FloatClosedIntervalSet.NumericMethods.bisection_minnearest_P(function5, interval, 0.001f) );
		System.out.println( FloatNumberPoint.NumericalMethods.bisection_minnearest_N_R_segmented(function5, interval, 0.00001f, 0.0001f, 0.4f) );
		
		float x = intervalResult.getValue().midPoint();
		int i;
		int errors=0;
		
		int n_sum = 0;
		
		for ( i = 0 ; i < 1000 ; i++) {
			FloatNumberPoint.NumericalMethods.Result result = FloatNumberPoint.NumericalMethods.probabilistic_N_R_smallest_rootfinding(function5, interval, 0.00001f, 0.0001f, 10);
			n_sum += result.getIterations();
			
			if ( ( result.getPoint() == null ) || ( Math.abs(result.getPoint().getValue() - x) > 0.001f ) ) {
				errors++;
			}
		}
		
		System.out.println("Error probability: " + (float) errors / (float) i * 100.0f + " %" );
		System.out.println("n average: " + (float) n_sum / (float) i);
		*/
		
		//System.out.print( FloatClosedIntervalSet.NumericMethods.bisection_minnearest_multi(multiFunction1, Arrays.asList(new FloatClosedInterval(-300.0f, 300.0f), new FloatClosedInterval(-300.0f, 300.0f)), 0.0001f) );
		//System.out.print( FloatClosedIntervalSet.NumericMethods.bisection_minnearest_multi(multiFunctionUnitOne, Arrays.asList(new FloatClosedInterval(-300.0f, 300.0f), new FloatClosedInterval(-300.0f, 300.0f), new FloatClosedInterval(-300.0f, 300.0f)), 0.0001f) );
		
		//System.out.print( FloatClosedIntervalSet.NumericMethods.bisection_minnearest_multi( new ImplicitSurfaceTranslation(sphere, new Vector3f(3.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), 0.0f), Arrays.asList(new FloatClosedInterval(0.0f, 300.0f), new FloatClosedInterval(-300.0f, 300.0f), new FloatClosedInterval(-300.0f, 300.0f), new FloatClosedInterval(-300.0f, 300.0f)), 0.0001f) );
		//float length=3928.0f;
		//NumberSetVectorialScalarFunction multiFunction = new CollisionFunction( new ImplicitSurfaceTranslation(sphere, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), 0.0f), new ImplicitSurfaceTranslation(sphere, new Vector3f(10.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), 0.0f) );
		//FloatClosedInterval[] multiDomainInterval = new FloatClosedInterval[]{ new FloatClosedInterval(0.0f, 10.0f), new FloatClosedInterval(-length, length), new FloatClosedInterval(-length, length), new FloatClosedInterval(-length, length) };
		//System.out.print( FloatClosedIntervalSet.NumericMethods.bisection_minnearest_multi( multiFunction, Arrays.asList(new FloatClosedInterval(0.0f, 10.0f), new FloatClosedInterval(-length, length), new FloatClosedInterval(-length, length), new FloatClosedInterval(-length, length)), 0.001f) );
		//System.out.print( FloatNumberPoint.NumericalMethods.newton_raphson_multivariate(multiFunction.gradientApproximation(0.00000001f), new NumberSetVector<FloatNumberPoint>(FloatNumberPoint.FACTORY, 1.2f, 0.0f, 0.0f, 0.0f), 0.000001f, null, 0.001f));
		//System.out.print( FloatNumberPoint.NumericalMethods.montecarlo_rootfinding(multiFunction, multiDomainInterval, 0.001f, null) );
		//System.out.print( FloatNumberPoint.NumericalMethods.probabilistic_bisection(multiFunction, multiDomainInterval, 0.001f, null) );
		/**
		System.out.print( FloatNumberPoint.NumericalMethods.newton_raphson_multivariate(new NumberSetVectorialScalarFunction() {

			@Override
			public <V extends NumberSet<V>> V evaluate_scalar(
					NumberSetVector<V> operand) {
				final NumberSetFactory<V> factory = operand.elementsFactory();
				return factory.square( factory.sub( operand.lengthSquared(), factory.convert(2.0f)) );
			}
			
		}.gradientApproximation(0.001f), new NumberSetVector<FloatNumberPoint>(FloatNumberPoint.FACTORY, 1.2f, 0.0f), 0.001f, null, 0.001f));
		*/
		
		System.out.println( FloatClosedIntervalSet.NumericMethods.bisection_globalMinima(polynomialFunction, new FloatClosedInterval(-100.0f, 20.0f), 0.001f) );
	}

}
