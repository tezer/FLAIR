/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flair.taskmanager;

import com.flair.grammar.Language;
import com.flair.parser.AbstractDocumentSource;
import com.flair.parser.AbstractParsingStrategyFactory;
import com.flair.parser.DocumentCollection;
import java.util.List;

/**
 * Takes a collection of document sources and parses them
 * @author shadeMe
 */
class BasicDocumentParseJobInput
{
    public final Language			    sourceLanguage;
    public final List<AbstractDocumentSource>	    docSources;
    public final DocumentParseTaskExecutor	    docParsingExecutor;
    public final DocumentParserPool		    docParserPool;
    public final AbstractParsingStrategyFactory	    docParsingStrategy;
    
    public BasicDocumentParseJobInput(Language sourceLanguage,
				     List<AbstractDocumentSource> docSources,
				     DocumentParseTaskExecutor docParser,
				     DocumentParserPool parserPool,
				     AbstractParsingStrategyFactory strategy)
    {
	this.sourceLanguage = sourceLanguage;
	this.docSources = docSources;
	this.docParsingExecutor = docParser;
	this.docParserPool = parserPool;
	this.docParsingStrategy = strategy;
    }
}

class BasicDocumentParseJobOutput
{
    public final DocumentCollection	parsedDocs;
    
    public BasicDocumentParseJobOutput() {
	this.parsedDocs = new DocumentCollection();
    }
}

class BasicDocumentParseJob extends AbstractTaskLinkingJob
{
    private final BasicDocumentParseJobInput	    input;
    private final BasicDocumentParseJobOutput	    output;
    
    public BasicDocumentParseJob(BasicDocumentParseJobInput in)
    {
	super();
	this.input = in;
	this.output = new BasicDocumentParseJobOutput();
    }
    
    @Override
    public Object getOutput()
    {
	waitForCompletion();
	return output.parsedDocs;
    }
    
    @Override
    public void begin() 
    {
	if (isStarted())
	    throw new IllegalStateException("Job has already begun");
	
	for (AbstractDocumentSource itr : input.docSources)
	{
	    DocumentParseTask newTask = new DocumentParseTask(this,
								  new BasicTaskChain(this),
								  itr,
								  input.docParsingStrategy.create(),
								  input.docParserPool);

	    registerTask(newTask);
	    input.docParsingExecutor.queue(newTask);
	}
	
	flagStarted();
    }

    @Override
    public void performLinking(TaskType previousType, AbstractTaskResult previousResult)
    {
	switch (previousType)
	{
	    case PARSE_DOCUMENT:
	    {
		// add the result to the doc collection
		DocumentParseTaskResult result = (DocumentParseTaskResult)previousResult;
		output.parsedDocs.add(result.getOutput(), true);
		output.parsedDocs.sort();
		break;
	    }
	}
    }

    @Override
    public String toString()
    {
	if (isCompleted() == false)
	    return "BasicDocumentParseJob is still running";
	else if (isCancelled())
	    return "BasicDocumentParseJob was cancelled";
	else
	    return "BasicDocumentParseJob Output:\nInput:\n\tLanguage: " + input.sourceLanguage + "\n\tDocument Sources: " + input.docSources.size() + "\nOutput:\n\tParsed Docs: " + output.parsedDocs.size();
    }
}
