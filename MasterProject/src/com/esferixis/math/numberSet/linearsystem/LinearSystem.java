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
package com.esferixis.math.numberSet.linearsystem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferixis.math.numberSet.NumberSet;
import com.esferixis.math.numberSet.NumberSetFactory;
import com.esferixis.math.numberSet.NumberSetMatrix;
import com.esferixis.math.numberSet.NumberSetVector;

/**
 * @author ariel
 *
 */
public final class LinearSystem {
	private LinearSystem() {};
	
	/**
	 * @pre La matriz A tiene que ser cuadradada, la cantidad de filas de la matriz A
	 * 		tiene que ser igual a la cantidad de componentes de b
	 * @post Resuelve el sistema especificado usando eliminación
	 * 		 gaussiana en columnas, si el sistema es inconsistente devuelve null y
	 * 		 si es subdeterminado devuelve una solución particular
	 */
	public static <T extends NumberSet<T>> NumberSetVector<T> gaussSolve(NumberSetMatrix<T> A, NumberSetVector<T> b) {
		if ( ( A != null ) && ( b != null ) ) {
			if ( A.isSquare() ) {
				boolean inconsistent = false;
				if ( A.rows() == b.components().size() ) {
					final NumberSetFactory<T> factory = A.elementsFactory();
					T[][] row_column_elementsMatrix = (T[][]) Array.newInstance(A.elementsFactory().getNumberSetClass(), A.rows(), A.columns()+1);
					
					for ( int i = 0 ; i < A.rows() ; i++ ) {
						for ( int j = 0 ; j < A.columns() ; j++ ) {
							row_column_elementsMatrix[i][j] = A.getElement(j, i);
						}
						row_column_elementsMatrix[i][A.columns()] = b.components().get(i);
					}
					
					int maxRows = row_column_elementsMatrix.length;
					
					
					// Triangular inferiormente
					for ( int i=0; (i<maxRows-1) && (!inconsistent); i++ ) {
						int max_n_row=-1;
						T max_abs_value = factory.zero();
						
						/**
						 * Buscar una fila en donde el primer elemento no se anule
						 * y sea el máximo
						 */
						for ( int j=i; j<maxRows; j++) {
							T candidate_abs_value = factory.abs( row_column_elementsMatrix[j][i] );
							if ( !candidate_abs_value.equals(factory.zero()) ) {
								if ( factory.hasOnlyGreaterElements(candidate_abs_value, max_abs_value) ) {
									max_n_row = j;
									max_abs_value = candidate_abs_value;
								}
							}
						}
						
						// Si no se encontró
						if ( max_n_row == -1 ) {
							break;
							//throw new IllegalArgumentException("Subdetermined system");
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
						 * Si encuentra filas linealmente dependientes, suprimirlas,
						 * y si encuentra una inconsistencia, interrumpir
						 */
						final T first_i = row_column_elementsMatrix[i][i];
						for ( int j=i+1; (j<maxRows) && (!inconsistent); j++ ) {
							final T first_j = row_column_elementsMatrix[j][i];
							final T factor = factory.divide(first_j, first_i);
							boolean coefficientsHasNotZeroes = false;
							for ( int k=i; (k<row_column_elementsMatrix[j].length) && (!inconsistent) ; k++ ) {
								T value = factory.sub(row_column_elementsMatrix[j][k], factory.multiply(row_column_elementsMatrix[i][k], factor) );
								row_column_elementsMatrix[j][k] = value;
								
								if ( k < A.columns()) {
									if ( !value.equals(factory.zero()) ) {
										coefficientsHasNotZeroes = true;
									}
								}
							}
							
							// Si no hay ningún coeficiente que no sea cero
							if ( !coefficientsHasNotZeroes ) {
								// Si el valor independiente es cero
								if ( row_column_elementsMatrix[j][A.columns()].equals(factory.zero()) ) {
									//throw new IllegalArgumentException("Subdetermined system");
								}
								else {
									inconsistent = true;
								}
							}
						}
						
					}
					
					if ( !inconsistent ) {
						// Calcular el resultado con la matriz triangulada
						T[] resultElements = (T[]) Array.newInstance(factory.getNumberSetClass(), A.columns());
						for ( int i=A.columns()-1; i>=0; i-- ) {
							T variableCoefficient = row_column_elementsMatrix[i][i];
							if ( !variableCoefficient.equals(factory.zero()) ) {
								T independient = row_column_elementsMatrix[i][A.columns()];
								for (int j=i+1;j<=A.columns()-1;j++) {
									independient = factory.sub(independient, factory.multiply(resultElements[j], row_column_elementsMatrix[i][j]));
								}
								resultElements[i] = factory.divide(independient, variableCoefficient);
							}
							else {
								resultElements[i] = factory.zero();
							}
						}
						
						return new NumberSetVector<T>(Arrays.asList(resultElements));
					}
					else {
						return null;
					}
				}
				else {
					throw new IllegalArgumentException("Rows A matrix and vector b length mismatch");
				}
			}
			else {
				throw new IllegalArgumentException("Non square A matrix");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
}
