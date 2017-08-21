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

import java.util.List;

import com.esferixis.misc.strings.parser.ConstantFunctionParser;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.ParametrizedFunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * Matriz de transformación proporcional
 * 
 * @author ariel
 *
 */
public class ProportionalMatrix3f extends Matrix3f {
	private static final long serialVersionUID = -6747037748255322311L;

	private static final ExpressionParser<ProportionalMatrix3f> PARSER = new ExpressionParser<ProportionalMatrix3f>(
		new ConstantFunctionParser<ProportionalMatrix3f>("IDENTITY", Matrix3f.IDENTITY),
		new ConstantFunctionParser<ProportionalMatrix3f>("ZERO", Matrix3f.ZERO),
		new ParametrizedFunctionParser<Matrix3f>("mul") {

			@Override
			public ProportionalMatrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return ProportionalMatrix3f.parse(parameters.get(0)).mul(ProportionalMatrix3f.parse(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<ProportionalMatrix3f>("translate") {

			@Override
			public ProportionalMatrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return ProportionalMatrix3f.parse(parameters.get(0)).translate(Vector2f.parse(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<ProportionalMatrix3f>("rotate") {

			@Override
			public ProportionalMatrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return ProportionalMatrix3f.parse(parameters.get(0)).rotate(Float.parseFloat(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<ProportionalMatrix3f>("scale") {

			@Override
			public ProportionalMatrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 2);
				return ProportionalMatrix3f.parse(parameters.get(0)).scale(Float.parseFloat(parameters.get(1)));
			}
			
		},
		new ParametrizedFunctionParser<ProportionalMatrix3f>("invert") {

			@Override
			public ProportionalMatrix3f parse(List<String> parameters) throws ParseException {
				ExpressionParser.checkParametersQuantity(parameters, 1);
				return ProportionalMatrix3f.parse(parameters.get(0)).invert();
			}
			
		}
	);
	
	public static ProportionalMatrix3f parse(String string) {
		return PARSER.parse(string, ProportionalMatrix3f.class);
	}
	
	/**
	 * @param source
	 */
	ProportionalMatrix3f(float[][] source) {
		super(source);
	}
	
	/**
	 * @param source
	 */
	ProportionalMatrix3f(Matrix3f source) {
		this(source.m);
	}

	/**
	 * @post Devuelve la transformación del escalar especificado
	 */
	public float transformScalar(float scalar) {
		return (float) Math.sqrt( this.m[0][0] * this.m[0][0] + this.m[0][1] * this.m[0][1] ) * scalar;
	}
	
	/**
	 * @post Devuelve el resultado de la multiplicación de la matriz con otra
	 */
	public ProportionalMatrix3f mul(ProportionalMatrix3f other) {
		return new ProportionalMatrix3f(this.mul( (Matrix3f) other));
	}
	
	/**
	 * @post Devuelve el resultado de la translación con el vector especificado
	 */
	@Override
	public ProportionalMatrix3f translate(Vector2f vector) {
		return new ProportionalMatrix3f(super.translate(vector));
	}
	
	/**
	 * @post Devuelve la matriz rotada con el ángulo especificado
	 */
	@Override
	public ProportionalMatrix3f rotate(float angle) {
		return new ProportionalMatrix3f(super.rotate(angle));
	}
	
	/**
	 * @post Devuelve la matriz multiplicada con el escalar especificado
	 */
	@Override
	public ProportionalMatrix3f scale(float scalar) {
		return new ProportionalMatrix3f(super.scale(scalar));
	}
	
	/**
	 * @post Devuelve el desplazamiento angular
	 */
	public float getAngleDisplacement() {
		return this.transformDirection(new Vector2f(1.0f, 0.0f)).getAngle();
	}
	
	/**
	 * @post Devuelve la inversa de la matriz
	 */
	@Override
	public ProportionalMatrix3f invert() {
		return new ProportionalMatrix3f(super.invert());
	}
}
