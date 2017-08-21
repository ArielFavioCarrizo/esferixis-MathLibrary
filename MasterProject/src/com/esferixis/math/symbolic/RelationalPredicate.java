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

package com.esferixis.math.symbolic;

/**
 * @author ariel
 * 
 * Predicado relacional
 */
public class RelationalPredicate {
	public static enum Operator {
		EQUAL,
		UNEQUAL,
		GREATER,
		LESS,
		GREATEREQUAL,
		LESSEQUAL,
	}

	private final Expression operand1, operand2;
	
	/**
	 * @post Crea el predicado relacional con el operador especificado
	 */
	public RelationalPredicate(Expression operand1, Expression operand2) {
		if ( ( operand1 != null ) && ( operand2 != null ) ) {
			this.operand1 = operand1;
			this.operand2 = operand2;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el operando 1
	 */
	public Expression getOperand1() {
		return this.operand1;
	}
	
	/**
	 * @post Devuelve el operando 2
	 */
	public Expression getOperand2() {
		return this.operand2;
	}
}
