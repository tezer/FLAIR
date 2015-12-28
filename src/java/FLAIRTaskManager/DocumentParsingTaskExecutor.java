/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FLAIRTaskManager;

/**
 * Task scheduler for document parsing tasks
 * @author shadeMe
 */
class DocumentParsingTaskExecutor extends AbstractTaskExecutor
{
    private final DocumentParserPool		    parserPool;
    
    public DocumentParsingTaskExecutor(DocumentParserPool parserPool)
    {
	super(Constants.PARSER_THREADPOOL_SIZE);
	this.parserPool = parserPool;
    }
    
    public void parse(DocumentParsingTask task)
    {
	task.setParserPool(parserPool);
	queue(task);
    }
}