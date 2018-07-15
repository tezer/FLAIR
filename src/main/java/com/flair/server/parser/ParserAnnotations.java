package com.flair.server.parser;

/*
 * Parser-specific metadata with some helper methods
 */
public interface ParserAnnotations {
	interface Token {
		String word();
		String lemma();  // empty string if there is no lemma
		String pos();
		boolean isStopword();
	}

	interface Sentence {
		int start();
		int end();
		String text();
		Iterable<? extends Token> tokens();
	}

	Iterable<? extends Sentence> sentences();

	DocumentParserType type();
	<T extends ParserAnnotations> T data(Class<T> typeClass);
}