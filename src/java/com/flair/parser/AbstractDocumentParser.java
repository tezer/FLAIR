/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flair.parser;

import com.flair.grammar.Language;

/**
 * Represents an abstract document parser
 * @author shadeMe
 */
public abstract class AbstractDocumentParser
{
    protected final AbstractDocumentFactory	    docFactory;
    
    public AbstractDocumentParser(AbstractDocumentFactory factory)
    {
	assert factory != null;
	docFactory = factory;
    }
    
    public abstract boolean		    isLanguageSupported(Language lang);
    public abstract AbstractDocument	    parse(AbstractDocumentSource source, AbstractParsingStrategy strategy);
}
