/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flair.testing;

import com.flair.grammar.GrammaticalConstruction;
import com.flair.grammar.Language;
import com.flair.parser.AbstractDocument;
import com.flair.parser.AbstractDocumentSource;
import com.flair.parser.DocumentCollection;
import com.flair.parser.DocumentConstructionData;
import com.flair.parser.SimpleDocumentSource;
import com.flair.taskmanager.AbstractPipelineOperation;
import com.flair.taskmanager.MasterJobPipeline;
import com.flair.utilities.FLAIRLogger;
import com.flair.utilities.JSONWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executable test for the local document parsing framework. Takes a single param - absolute path to a directory with the text documents
 * @author shadeMe
 */
public class LocalFileParsingTest
{
    public static void main(String[] args)
    {
	String rootInPath = System.getProperty("user.home") + "/FLAIRLocalTest";
	String rootOutPath = rootInPath + "/FLAIROutput";
	if (args.length != 0)
	{
	   rootInPath = args[0];
	   rootOutPath = rootInPath + "/FLAIROutput";
	}
	
	JSONWriter serializer = new JSONWriter();
	FLAIRLogger.get().trace("Root Input Path: " + rootInPath+ "\nRoot Output Path: " + rootOutPath);
	File rootDir = new File(rootInPath);
	if (rootDir.isDirectory() == false)
	{
	    FLAIRLogger.get().error("Root input path is not a directory");
	    System.exit(0);
	}
	
	File[] inputFiles = rootDir.listFiles();
	List<AbstractDocumentSource> inputSource = new ArrayList<>();
	for (File itr : inputFiles)
	{
	   try (BufferedReader br = new BufferedReader(new FileReader(itr))) 
	   {
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
		    sb.append(line);
		    sb.append(System.lineSeparator());
		    line = br.readLine();
		}
		String everything = sb.toString();
		if (everything.isEmpty() == false)
		    inputSource.add(new SimpleDocumentSource(everything, Language.ENGLISH));
	   } catch (IOException ex) {
	   }
	   
	   long startTime = System.currentTimeMillis();
	   AbstractPipelineOperation op = MasterJobPipeline.get().parseDocumentSources(Language.ENGLISH, inputSource);
	   DocumentCollection docCol = op.getOutput();
	   long endTime = System.currentTimeMillis();
	   
	   File outFile = new File(rootOutPath);
	   outFile.mkdirs();
	   docCol.serialize(serializer, outFile.getAbsolutePath());
	   
	   try (BufferedWriter bw = new BufferedWriter(new FileWriter(rootOutPath + "/constructions.csv"))) 
	   {
		String header = "document,";
		for (GrammaticalConstruction itr1 : GrammaticalConstruction.values())
		    header += itr1.name() + ",";
		
		header += "# of sentences,# of dependencies,readability score";
		bw.write(header);
		bw.newLine();
		
		int i = 1;
		for (AbstractDocument itr1 : docCol)
		{
		    String outString = "" + i++ + ",";
		    for (GrammaticalConstruction itr2 : GrammaticalConstruction.values())
		    {
			DocumentConstructionData data = itr1.getConstructionData(itr2);
			outString += data.getRelativeFrequency() + ",";
		    }
		    
		    outString += itr1.getNumSentences() + "," + itr1.getNumDependencies() + "," + itr1.getReadabilityScore();
		    bw.write(outString);
		    bw.newLine();
		}
	   } catch (IOException ex) {
	   }
	   
	   FLAIRLogger.get().trace(op.toString());
	   FLAIRLogger.get().trace("LocalFileParsingTest parsed " + docCol.size() + " documents in " + (endTime - startTime) + " ms");
	   System.exit(0);
	}
    }
}
