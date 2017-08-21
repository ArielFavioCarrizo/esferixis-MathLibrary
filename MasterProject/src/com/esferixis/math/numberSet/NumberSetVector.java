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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.math.Vector3f;

/**
 * Vector de conjuntos numéricos
 * 
 * @author ariel
 *
 */
public final class NumberSetVector<T extends NumberSet<T>> {
	private final NumberSetFactory<T> elementsFactory;
	private final List<T> components;
	
	/**
	 * @pre La lista de componentes, la cantidad de componentes y los componentes no pueden ser nulos
	 * @post Crea un vector de conjuntos numéricos con los conjuntos
	 * 		 especificados
	 */
	public NumberSetVector(List<T> components) {
		if ( ( components != null ) ) {
			if ( !components.isEmpty() ) {
				this.components = Collections.unmodifiableList(new ArrayList<T>(components));
				this.elementsFactory = components.get(0).factory();
				for ( T eachComponent : this.components ) {
					if ( eachComponent == null ) {
						throw new NullPointerException();
					}
					else if ( !this.elementsFactory.getNumberSetClass().isAssignableFrom( eachComponent.getClass() ) ) {
						throw new ClassCastException();
					}
				}
			}
			else {
				throw new IllegalArgumentException("Expected non-empty components list");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La lista de componentes y la cantidad de componentes no pueden ser nulas
	 * @post Crea un vector de conjuntos numéricos con los conjuntos
	 * 		 especificados
	 */
	@SafeVarargs
	public NumberSetVector(T... components) {
		this(Arrays.asList(components));
	}
	
	/**
	 * @post Crea un vector con la fábrica y los componentes especificados
	 */
	public NumberSetVector(NumberSetFactory<T> elementsFactory, float... componentsValue) {
		if ( elementsFactory != null ) {
			this.elementsFactory = elementsFactory;
			
			this.components = new ArrayList<T>(componentsValue.length);
			for ( float eachComponentValue : componentsValue ) {
				this.components.add( this.elementsFactory.convert(eachComponentValue) );
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea el vector a partir del vector Vector3f convencional
	 */
	public NumberSetVector(NumberSetFactory<T> elementsFactory, Vector3f vector) {
		this( elementsFactory, vector.getX(), vector.getY(), vector.getZ());
	}
	
	/**
	 * @post Devuelve la fábrica de los componentes
	 */
	public NumberSetFactory<T> elementsFactory() {
		return this.elementsFactory;
	}
	
	/**
	 * @post Devuelve la lista de componentes (Sólo lectura)
	 */
	public List<T> components() {
		return this.components;
	}
	
	/**
	 * @post Devuelve una representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		boolean first=true;
		String result = "( ";
		for ( T eachComponent : this.components ) {
			if ( first ) {
				first = false;
			}
			else {
				result += ", ";
			}
			result += eachComponent.toString();
		}
		return result + " )";
	}
	
	/**
	 * @post Valida la compatibilidad con el vector especificado
	 */
	private void validate(NumberSetVector<T> other) {
		if ( other != null ) {
			if ( other.components().size() != this.components().size() ) {
				throw new IllegalArgumentException("Vector size mismatch");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private abstract class ComponentByComponentBinaryOperator {
		/**
		 * @post Procesa dos elementos de igual nombre
		 */
		protected abstract T process(T element1, T element2);
		
		/**
		 * @post Procesa el otro vector especificado y devuelve
		 * 		 el resultado
		 */
		public NumberSetVector<T> process(NumberSetVector<T> other) {
			NumberSetVector.this.validate(other);
			
			List<T> components = new ArrayList<T>(NumberSetVector.this.components.size());
			
			for ( int i=0 ; i < NumberSetVector.this.components.size() ; i++ ) {
				components.add( this.process(NumberSetVector.this.components().get(i), other.components.get(i)) );
			}
			
			return new NumberSetVector<T>( components );
		}
	}
	
	private abstract class ComponentByComponentUnaryOperator {
		/**
		 * @post Procesa el elemento
		 */
		protected abstract T process(T element);
		
		/**
		 * @post Procesa el vector y devuelve
		 * 		 el resultado
		 */
		public NumberSetVector<T> process() {
			List<T> components = new ArrayList<T>(NumberSetVector.this.components.size());
			
			for ( int i=0 ; i < NumberSetVector.this.components.size() ; i++ ) {
				components.add( this.process(NumberSetVector.this.components().get(i)) );
			}
			
			return new NumberSetVector<T>( components );
		}
	}
	
	/**
	 * @pre El otro vector tiene que tener la misma cantidad de dimensiones
	 * @post Devuelve la suma con el vector especificado
	 */
	public NumberSetVector<T> add(NumberSetVector<T> other) {
		return new ComponentByComponentBinaryOperator() {

			@Override
			protected T process(T element1, T element2) {
				return elementsFactory.add(element1, element2);
			}
			
		}.process(other);
	}
	
	/**
	 * @pre El otro vector tiene que tener la misma cantidad de dimensiones
	 * @post Devuelve la resta con el vector especificado
	 */
	public NumberSetVector<T> sub(NumberSetVector<T> other) {
		return new ComponentByComponentBinaryOperator() {

			@Override
			protected T process(T element1, T element2) {
				return elementsFactory.add(element1, elementsFactory.opposite( element2 ) );
			}
			
		}.process(other);
	}
	
	/**
	 * @post Devuelve el opuesto
	 */
	public NumberSetVector<T> opposite() {
		return new ComponentByComponentUnaryOperator() {

			@Override
			protected T process(T element) {
				return NumberSetVector.this.elementsFactory.opposite(element);
			}
			
		}.process();
	}
	
	/**
	 * @pre El otro vector tiene que tener la misma cantidad de dimensiones
	 * @post Devuelve el producto escalar con el vector especificado
	 */
	public T dot(NumberSetVector<T> other) {
		this.validate(other);
		
		T result = this.elementsFactory.zero();

		for ( int i=0 ; i < NumberSetVector.this.components.size() ; i++ ) {
			 result = this.elementsFactory.add(result, this.elementsFactory.multiply( this.components().get(i), other.components().get(i)));
		}
		
		return result;
	}
	
	/**
	 * @pre Ambos vectores tienen que ser de dimensión 3
	 * @post Devuelve el producto cruz con el vector especificado
	 */
	public NumberSetVector<T> cross(NumberSetVector<T> other) {
		if ( this.components().size() == 3 ) {
			// return new Vector3f(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x);
			int[][] componentsReference_bycomponent = {
					{ 1, 2 },
					{ 2, 0 },
					{ 0, 1 }
			};
			
			List<T> resultComponents = new ArrayList<T>(3);
			
			for ( int i = 0 ; i <= 2 ; i++ ) {
				int[] eachComponentsReference = componentsReference_bycomponent[i];
				resultComponents.add( this.elementsFactory.sub( this.elementsFactory.multiply(this.components.get(eachComponentsReference[0]), other.components.get(eachComponentsReference[1])), this.elementsFactory.multiply(this.components.get(eachComponentsReference[1]), other.components.get(eachComponentsReference[0]))));
			}
			
			return new NumberSetVector<T>(resultComponents);
		}
		else {
			throw new IllegalStateException("Expected 3d vector");
		}
	}
	
	/**
	 * @post Devuelve el producto con el escalar especificado
	 */
	public NumberSetVector<T> scale(final T scalar) {
		return new ComponentByComponentUnaryOperator() {

			@Override
			protected T process(T element) {
				return NumberSetVector.this.elementsFactory.multiply(element, scalar);
			}
			
		}.process();
	}
	
	/**
	 * @post Devuelve la longitud elevada al cuadrado
	 */
	public T lengthSquared() {
		T result = this.elementsFactory.zero();
		
		for ( T eachElement : this.components ) {
			result = this.elementsFactory.add(result, this.elementsFactory.square(eachElement));
		}
		
		return result;
	}
	
	/**
	 * @post Proyecta el vector especificado sobre éste y devuelve el resultado
	 */
	public NumberSetVector<T> vectorProjection(NumberSetVector<T> other) {
		return other.scale( this.elementsFactory.divide(this.dot(this), other.dot(other) ) );
	}
	
	/**
	 * @post Devuelve la normalización sobre el vector especificado
	 */
	public NumberSetVector<T> normalise() {
		return this.scale( this.elementsFactory.inverse( this.elementsFactory.square(this.lengthSquared()) ) );
	}
	
	/**
	 * @post Devuelve la matriz columna asociada
	 */
	public NumberSetMatrix<T> columnMatrix() {
		T[][] elementsArray = (T[][]) Array.newInstance(this.elementsFactory.getClass(), this.components.size(), 0);
		for ( int i=0; i<this.components.size() ; i++ ) {
			elementsArray[i][0] = this.components.get(i);
		}
		return new NumberSetMatrix<T>(this.elementsFactory, elementsArray);
	}
	
	/**
	 * @post Devuelve la matriz fila asociada
	 */
	public NumberSetMatrix<T> rowMatrix() {
		T[][] elementsArray = (T[][]) Array.newInstance(Array.class, 0, 0);
		elementsArray[0] = this.components.toArray(elementsArray[0]);
		return new NumberSetMatrix<T>(this.elementsFactory, elementsArray);
	}
	
	/**
	 * @post Devuelve el hash
	 */
	public int hashCode() {
		return this.components.hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al vector especificado
	 */
	public boolean equals(NumberSetVector<T> other) {
		if ( ( other != null ) && ( other instanceof NumberSetVector ) && ( other.elementsFactory == this.elementsFactory ) ) {
			return other.components.equals(this.components);
		}
		else {
			return false;
		}
	}
}
