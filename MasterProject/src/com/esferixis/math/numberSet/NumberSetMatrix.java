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
import java.util.List;

/**
 * Matriz de conjuntos numéricos
 * 
 * @author ariel
 *
 */
public final class NumberSetMatrix<T extends NumberSet<T>> {
	private final NumberSetFactory<T> elementsFactory;
	private final T[][] elements;
	
	/**
	 * @post Crea un array con la fábrica y los elementos especificados
	 * 		 sin verificar
	 */
	NumberSetMatrix(NumberSetFactory<T> factory, T[][] elements) {
		this.elementsFactory = factory;
		this.elements = elements;
	}
	
	/**
	 * @pre La fábrica no puede ser nula
	 * @post Devuelve la matriz identidad con la fábrica y la cantidad de columnas/filas
	 * 		 especificada
	 */
	public static <T extends NumberSet<T> > NumberSetMatrix<T> identity(NumberSetFactory<T> elementsFactory, int columns_rows) {
		if ( elementsFactory != null ) {
			T[][] elements = (T[][]) Array.newInstance(elementsFactory.getNumberSetClass(), columns_rows, columns_rows);
			for ( int i=0; i<columns_rows; i++ ) {
				for ( int j=0; j<columns_rows; j++ ) {
					elements[i][j] = (i == j) ? elementsFactory.one() : elementsFactory.zero();
				}
			}
			return new NumberSetMatrix<T>(elementsFactory, elements);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea un array con la fábrica y los elementos especificados
	 */
	public static <T extends NumberSet<T> > NumberSetMatrix<T> create(T[][] elements) {
		if ( ( elements != null ) ) {
			int n_columns = elements.length, n_rows=elements[0].length;
			
			if ( ( n_columns > 0 ) && ( n_rows > 0 ) ) {
				final NumberSetFactory<T> factory = elements[0][0].factory();
				final T[][] resultElements = (T[][]) Array.newInstance(factory.getClass(), n_columns, n_rows);
				for ( int i=0; i<n_columns ; i++ ) {
					if ( elements[i].length == n_rows ) {
						for ( int j=0 ; j<n_rows ; j++ ) {
							resultElements[i][j] = elements[i][j];
						}
					}
					else {
						throw new IllegalArgumentException("Invalid elements array");
					}
				}
				return new NumberSetMatrix<T>(factory, resultElements);
			}
			else {
				throw new IllegalArgumentException("Invalid elements array");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Crea un array con la fábrica y los elementos "floats" especificados
	 */
	public static <T extends NumberSet<T> > NumberSetMatrix<T> create(NumberSetFactory<T> factory, float[][] elements) {
		if ( (factory != null ) && ( elements != null ) ) {
			int n_columns = elements.length, n_rows=elements[0].length;
			
			if ( ( n_columns > 0 ) && ( n_rows > 0 ) ) {
				final T[][] resultElements = (T[][]) Array.newInstance(factory.getNumberSetClass(), n_columns, n_rows);
				for ( int i=0; i<n_columns ; i++ ) {
					if ( elements[i].length == n_rows ) {
						for ( int j=0 ; j<n_rows ; j++ ) {
							resultElements[i][j] = factory.convert(elements[i][j]);
						}
					}
					else {
						throw new IllegalArgumentException("Invalid elements array");
					}
				}
				return new NumberSetMatrix<T>(factory, resultElements);
			}
			else {
				throw new IllegalArgumentException("Invalid elements array");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la fábrica de los elementos
	 */
	public NumberSetFactory<T> elementsFactory() {
		return this.elementsFactory;
	}
	
	/**
	 * @post Devuelve la cantidad de columnas
	 */
	public int columns() {
		return this.elements.length;
	}
	
	/**
	 * @post Devuelve la cantidad de filas
	 */
	public int rows() {
		return this.elements[0].length;
	}
	
	/**
	 * @post Devuelve si es cuadrada
	 */
	public boolean isSquare() {
		return this.columns() == this.rows();
	}
	
	/**
	 * @pre La otra matriz no puede ser nula y tiene que tener la misma
	 * 		cantidad de dimensiones
	 * @post Verifica que la cantidad de dimensiones sea igual
	 * 		 con la matriz especificada
	 */
	private void checkEqualsDimensions(NumberSetMatrix<T> other) {
		if ( other != null ) {
			if ( ( this.columns() != other.columns() ) || ( this.rows() != other.rows() ) ) {
				throw new IllegalArgumentException("Dimensions mismatch");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Las matrices tienen que ser de las mismas dimensiones
	 * @post Suma la matriz con la especificada
	 */
	public NumberSetMatrix<T> add(NumberSetMatrix<T> other) {
		this.checkEqualsDimensions(other);
		final T[][] resultElements = (T[][]) Array.newInstance(this.elementsFactory.getClass(), this.columns(), this.rows());
		for ( int i=0 ; i<this.elements.length ; i++ ) {
			for ( int j=0 ; j<this.elements[i].length ; j++ ) {
				resultElements[i][j] = this.elementsFactory.add(this.elements[i][j], other.elements[i][j]);
			}
		}
		return new NumberSetMatrix<T>(this.elementsFactory, resultElements);
	}
	
	/**
	 * @pre Las matrices tienen que ser de las mismas dimensiones
	 * @post Resta la matriz con la especificada
	 */
	public NumberSetMatrix<T> sub(NumberSetMatrix<T> other) {
		this.checkEqualsDimensions(other);
		final T[][] resultElements = (T[][]) Array.newInstance(this.elementsFactory.getNumberSetClass(), this.columns(), this.rows());
		for ( int i=0 ; i<this.elements.length ; i++ ) {
			for ( int j=0 ; j<this.elements[i].length ; j++ ) {
				resultElements[i][j] = this.elementsFactory.sub(this.elements[i][j], other.elements[i][j]);
			}
		}
		return new NumberSetMatrix<T>(this.elementsFactory, resultElements);
	}
	
	/**
	 * @post Calcula la opuesta
	 */
	public NumberSetMatrix<T> opposite() {
		final T[][] resultElements = (T[][]) Array.newInstance(this.elementsFactory.getClass(), this.columns(), this.rows());
		for ( int i=0 ; i<this.elements.length ; i++ ) {
			for ( int j=0 ; j<this.elements[i].length ; j++ ) {
				resultElements[i][j] = this.elementsFactory.opposite(this.elements[i][j]);
			}
		}
		return new NumberSetMatrix<T>(this.elementsFactory, resultElements);
	}
	
	/**
	 * @pre La matriz especificada no puede ser nula,
	 * 		la cantidad de sus filas tiene que ser igual a la cantidad de columnas
	 * @post Realiza la multiplicación con la matriz especificada
	 */
	public NumberSetMatrix<T> multiply(NumberSetMatrix<T> other) {
		final T[][] resultElements = (T[][]) Array.newInstance(this.elementsFactory.getNumberSetClass(), other.columns(), this.rows());
		if ( other.rows() == this.columns() ) {
			for ( int i=0; i<other.columns(); i++ ) {
				for ( int j=0; j<this.rows() ; j++ ) {
					T newElement = this.elementsFactory.zero();
					for ( int k=0; k<this.columns() ; k++ ) {
						newElement = this.elementsFactory.add(newElement, this.elementsFactory.multiply(this.elements[k][j], other.elements[i][k]));
					}
					resultElements[i][j] = newElement;
				}
			}
		}
		return new NumberSetMatrix<T>(this.elementsFactory, resultElements);
	}
	
	/**
	 * @pre La matriz tiene que ser cuadrada y tiene que tener inversa
	 * @post Calcula la inversa de la matriz
	 */
	public NumberSetMatrix<T> inverse() {
		if ( this.isSquare() ) {
			int n_rows = this.rows(), n_columns = this.columns() * 2; 
			final T[][] row_column_elementsMatrix = (T[][]) Array.newInstance(this.elementsFactory.getNumberSetClass(), n_rows, n_columns);
			for ( int i=0; i<n_rows; i++ ) {
				for ( int j=0; j<n_columns; j++ ) {
					final T value;
					if ( j < this.columns() ) {
						value = this.elements[j][i];
					}
					else {
						value = ( j - this.columns() == i ) ? this.elementsFactory.one() : this.elementsFactory.zero();
					}
					row_column_elementsMatrix[i][j] = value;
				}
			}
			
			// Triangular inferiormente
			for ( int i=0; (i<n_rows-1); i++ ) {
				int max_n_row=-1;
				T max_abs_value = this.elementsFactory.zero();
				
				/**
				 * Buscar una fila en donde el primer elemento no se anule
				 * y sea el máximo
				 */
				for ( int j=i; j<n_rows; j++) {
					T candidate_abs_value = this.elementsFactory.abs( row_column_elementsMatrix[j][i] );
					if ( !candidate_abs_value.equals(this.elementsFactory.zero()) ) {
						if ( this.elementsFactory.hasOnlyGreaterElements(candidate_abs_value, max_abs_value) ) {
							max_n_row = j;
							max_abs_value = candidate_abs_value;
						}
					}
				}
				
				// Si no se encontró
				if ( max_n_row == -1 ) {
					throw new IllegalArgumentException("Subdetermined system");
				}
				
				// Intercambiar con la primer fila y ponerlo en la primera posición
				{
					T[] temp = row_column_elementsMatrix[max_n_row];
					row_column_elementsMatrix[max_n_row] = row_column_elementsMatrix[i];
					row_column_elementsMatrix[i] = temp;
				}
				
				
				/**
				 * Restar cada una de las demás filas con la fila "max_n_row"
				 * multiplicada con el primer elemento de la anteriormente
				 * mencionada y dividida por el primer elemento de "max_abs_value"
				 * 
				 * Si encuentra filas linealmente dependientes, interrumpir
				 */
				final T first_i = row_column_elementsMatrix[i][i];
				for ( int j=i+1; (j<n_rows); j++ ) {
					final T first_j = row_column_elementsMatrix[j][i];
					final T factor = this.elementsFactory.divide(first_j, first_i);
					boolean coefficientsHasNotZeroes = false;
					for ( int k=i; (k<n_columns); k++ ) {
						T value = this.elementsFactory.sub(row_column_elementsMatrix[j][k], this.elementsFactory.multiply(row_column_elementsMatrix[i][k], factor) );
						row_column_elementsMatrix[j][k] = value;
						if ( !value.equals(this.elementsFactory.zero()) ) {
							coefficientsHasNotZeroes = true;
						}
					}
					
					// Si no hay ningún coeficiente que no sea cero
					if ( !coefficientsHasNotZeroes ) {
						throw new IllegalStateException("Non-invertible matrix");
					}
				}
				
			}
			
			// Formar "unos" en la pseudodiagonal y "ceros" en la parte superior
			for ( int i=n_rows-1; i>=0; i-- ) {
				final T diagonalValue = row_column_elementsMatrix[i][i];
				
				// Formar ceros en la columna asociada (Si no es cero)
				for ( int j=0; j<=i-1; j++ ) {
					final T columnValue = row_column_elementsMatrix[j][i];
					
					if ( !columnValue.equals(this.elementsFactory.zero()) ) {
						final T factor = this.elementsFactory.divide(columnValue, diagonalValue);
						
						for ( int k=0; k<n_columns; k++ ) {
							row_column_elementsMatrix[j][k] = this.elementsFactory.sub(row_column_elementsMatrix[j][k], this.elementsFactory.multiply(row_column_elementsMatrix[i][k], factor) );
						}
					}
				}
				
				// Transformar el elemento de la diagonal en un "uno"
				for ( int k=0; k<n_columns; k++ ) {
					row_column_elementsMatrix[i][k] = this.elementsFactory.divide(row_column_elementsMatrix[i][k], diagonalValue);
				}
			}
			
			// Recoger la matriz inversa de la mitad derecha de la matriz gaussiana
			T[][] inverseElements = (T[][]) Array.newInstance(this.elementsFactory.getNumberSetClass(), this.columns(), this.rows());
			
			for (int i=0; i<this.columns() ; i++) {
				for (int j=0; j<this.rows() ; j++) {
					inverseElements[i][j] = row_column_elementsMatrix[j][i+this.columns()];
				}
			}
			
			return new NumberSetMatrix<T>(this.elementsFactory, inverseElements);
		}
		else {
			throw new IllegalStateException("Attemped to invert a non-square matrix");
		}
	}
	
	/**
	 * @post Devuelve la transpuesta
	 */
	public NumberSetMatrix<T> transpose() {
		T[][] resultArray = (T[][]) Array.newInstance(this.elementsFactory().getNumberSetClass(), this.rows(), this.columns());
		for ( int i = 0 ; i < this.columns() ; i++ ) {
			for ( int j=0 ; j < this.rows() ; j++ ) {
				resultArray[j][i] = this.elements[i][j];
			}
		}
		return new NumberSetMatrix<T>(this.elementsFactory, resultArray);
	}
	
	/**
	 * @pre La matriz tiene que tener rango completo
	 * @post Calcula la pseudoinversa izquierda de la matriz
	 */
	public NumberSetMatrix<T> left_pseudoinverse() {
		NumberSetMatrix<T> thisTranspose = this.transpose();
		return thisTranspose.multiply(this).inverse().multiply(thisTranspose);
	}
	
	/**
	 * @post Devuelve el cuadrado de la norma de frobenius
	 */
	public T frobeniusNormSquared() {
		T result = this.elementsFactory.zero();
		for ( T[] eachColumn : this.elements ) {
			for ( T eachElement : eachColumn ) {
				result = this.elementsFactory.add(result, this.elementsFactory.square( eachElement ) );
			}
		}
		return result;
	}
	
	/**
	 * @post Convierte la columna especificada en un vector
	 */
	public NumberSetVector<T> columnToVector(int index) {
		if ( ( index >= 0 ) && ( index < this.columns() ) ) {
			return new NumberSetVector<T>(Arrays.asList(this.elements[index]));
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @post Convierte la fila especificada en un vector
	 */
	public NumberSetVector<T> rowToVector(int index) {
		if ( ( index >= 0 ) && ( index < this.rows() ) ) {
			List<T> components = new ArrayList<T>(this.columns());
			for ( int i=0; i<this.columns() ;i++ ) {
				components.add(this.elements[i][index]);
			}
			return new NumberSetVector<T>(components);
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @pre Los índices tienen que ser válidos
	 * @post Devuelve el elemento en la columna y en la fila especificados
	 */
	public T getElement(int n_column, int n_row) {
		if ( ( ( n_column >= 0 ) && ( n_column < this.columns() ) ) && ( ( n_row >= 0 ) && ( n_row < this.rows() ) ) ) {
			return this.elements[n_column][n_row];
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @post Convierte la matriz en un array
	 */
	public T[][] toArray() {
		T[][] resultArray = (T[][]) Array.newInstance(this.elementsFactory().getNumberSetClass(), this.columns(), this.rows());
		for ( int i = 0 ; i < this.columns() ; i++ ) {
			for ( int j=0 ; j < this.rows() ; j++ ) {
				resultArray[i][j] = this.elements[i][j];
			}
		}
		return resultArray;
	}
	
	/**
	 * @post Devuelve una representación en cadena
	 */
	@Override
	public String toString() {
		String result = "matrix[\n";
		for ( int i=0; i<this.rows() ; i++ ) {
			result += "{ ";
			for ( int j=0; j<this.columns() ; j++ ) {
				result += "'" + this.elements[j][i] + "'";
				if ( j != this.columns() -1 ) {
					result += ", ";
				}
			}
			result += " }\n";
		}
		return result + "]";
	}
}
