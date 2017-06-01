/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
package br.unicamp.cst.representation.owrl.grammar;

// Generated from Owrl.g4 by ANTLR 4.5.3
// Generated from Owrl.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link OwrlParser}.
 */
public interface OwrlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link OwrlParser#conf}.
	 * @param ctx the parse tree
	 */
	void enterConf(OwrlParser.ConfContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#conf}.
	 * @param ctx the parse tree
	 */
	void exitConf(OwrlParser.ConfContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(OwrlParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(OwrlParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(OwrlParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(OwrlParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#atrib}.
	 * @param ctx the parse tree
	 */
	void enterAtrib(OwrlParser.AtribContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#atrib}.
	 * @param ctx the parse tree
	 */
	void exitAtrib(OwrlParser.AtribContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#part}.
	 * @param ctx the parse tree
	 */
	void enterPart(OwrlParser.PartContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#part}.
	 * @param ctx the parse tree
	 */
	void exitPart(OwrlParser.PartContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(OwrlParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(OwrlParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(OwrlParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(OwrlParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(OwrlParser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(OwrlParser.PropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#qualitydimension}.
	 * @param ctx the parse tree
	 */
	void enterQualitydimension(OwrlParser.QualitydimensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#qualitydimension}.
	 * @param ctx the parse tree
	 */
	void exitQualitydimension(OwrlParser.QualitydimensionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(OwrlParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(OwrlParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(OwrlParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(OwrlParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link OwrlParser#cod}.
	 * @param ctx the parse tree
	 */
	void enterCod(OwrlParser.CodContext ctx);
	/**
	 * Exit a parse tree produced by {@link OwrlParser#cod}.
	 * @param ctx the parse tree
	 */
	void exitCod(OwrlParser.CodContext ctx);
}