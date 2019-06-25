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
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class OwrlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, ID=9, 
		INT_VALUE=10, REAL_VALUE=11, NEWLINE=12, WS=13;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "ID", 
		"INT_VALUE", "REAL_VALUE", "NEWLINE", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'['", "']'", "'<'", "'>'", "'create'", "'modify'", "'destroy'", 
		"'\"'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "ID", "INT_VALUE", 
		"REAL_VALUE", "NEWLINE", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public OwrlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Owrl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\17`\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\n\3\n\7\n@\n\n\f\n\16\nC\13\n\3\13\6\13F\n\13\r\13"+
		"\16\13G\3\f\6\fK\n\f\r\f\16\fL\3\f\3\f\6\fQ\n\f\r\f\16\fR\3\r\5\rV\n\r"+
		"\3\r\3\r\3\16\6\16[\n\16\r\16\16\16\\\3\16\3\16\2\2\17\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\3\2\6\4\2C\\c|\5\2\62"+
		";C\\c|\3\2\62;\3\2\13\13e\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\3\35\3\2\2\2\5\37\3"+
		"\2\2\2\7!\3\2\2\2\t#\3\2\2\2\13%\3\2\2\2\r,\3\2\2\2\17\63\3\2\2\2\21;"+
		"\3\2\2\2\23=\3\2\2\2\25E\3\2\2\2\27J\3\2\2\2\31U\3\2\2\2\33Z\3\2\2\2\35"+
		"\36\7]\2\2\36\4\3\2\2\2\37 \7_\2\2 \6\3\2\2\2!\"\7>\2\2\"\b\3\2\2\2#$"+
		"\7@\2\2$\n\3\2\2\2%&\7e\2\2&\'\7t\2\2\'(\7g\2\2()\7c\2\2)*\7v\2\2*+\7"+
		"g\2\2+\f\3\2\2\2,-\7o\2\2-.\7q\2\2./\7f\2\2/\60\7k\2\2\60\61\7h\2\2\61"+
		"\62\7{\2\2\62\16\3\2\2\2\63\64\7f\2\2\64\65\7g\2\2\65\66\7u\2\2\66\67"+
		"\7v\2\2\678\7t\2\289\7q\2\29:\7{\2\2:\20\3\2\2\2;<\7$\2\2<\22\3\2\2\2"+
		"=A\t\2\2\2>@\t\3\2\2?>\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2B\24\3\2\2"+
		"\2CA\3\2\2\2DF\t\4\2\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2H\26\3\2"+
		"\2\2IK\t\4\2\2JI\3\2\2\2KL\3\2\2\2LJ\3\2\2\2LM\3\2\2\2MN\3\2\2\2NP\13"+
		"\2\2\2OQ\t\4\2\2PO\3\2\2\2QR\3\2\2\2RP\3\2\2\2RS\3\2\2\2S\30\3\2\2\2T"+
		"V\7\17\2\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\7\f\2\2X\32\3\2\2\2Y[\t\5\2"+
		"\2ZY\3\2\2\2[\\\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]^\3\2\2\2^_\b\16\2\2_\34"+
		"\3\2\2\2\t\2AGLRU\\\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}