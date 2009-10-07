package com.aptana.ide.core.builder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.eclipse.ParsingRegistry;
import com.aptana.ide.parsing.nodes.IParseNode;

public class BuildContext
{

	private IFile fFile;
	private IParseState fParseState;
	private List<IProblem> problems;

	public BuildContext(IFile file)
	{
		this.fFile = file;
		this.problems = new ArrayList<IProblem>();
	}

	public IFile getFile()
	{
		return fFile;
	}

	public String getContents()
	{
		// TODO Cache the contents?
		try
		{
			return StreamUtils.readContent(fFile.getContents(), fFile.getCharset());
		}
		catch (IOException e)
		{
			IdeLog.log(AptanaCorePlugin.getDefault(), IStatus.ERROR, e.getMessage(), e);
		}
		catch (CoreException e)
		{
			IdeLog.log(AptanaCorePlugin.getDefault(), IStatus.ERROR, e.getMessage(), e);
		}
		return null;
	}

	public LexemeList getLexemeList()
	{
		IParseState parseState = getParseState();
		if (parseState == null)
			return null;
		return parseState.getLexemeList();
	}

	public IParseNode getRootNode()
	{
		IParseState parseState = getParseState();
		if (parseState == null)
			return null;
		return parseState.getParseResults();
	}

	public IParseState getParseState()
	{
		if (fParseState != null)
			return fParseState;
		String language = getLanguage();
		if (language == null)
			return null;
		IParser parser = ParsingRegistry.getParser(language);
		if (parser == null)
			parser = ParsingRegistry.createParser(language);
		if (parser == null)
			return null;
		IParseState parseState = parser.createParseState(null);
		// FIXME May want some check to make sure file isn't binary or way too big before we read it all into memory!
		String contents = getContents();
		if (contents == null)
			return null;
		parseState.setEditState(contents, contents, 0, 0);
		try
		{
			parser.parse(parseState);
		}
		catch (ParseException e)
		{
			IdeLog.log(AptanaCorePlugin.getDefault(), IStatus.ERROR, e.getMessage(), e);
		}
		catch (LexerException e)
		{
			IdeLog.log(AptanaCorePlugin.getDefault(), IStatus.ERROR, e.getMessage(), e);
		}
		fParseState = parseState;
		return parseState;
	}

	private String getLanguage()
	{
		if (fFile == null || fFile.getFileExtension() == null)
			return null;
		String fileExtension = fFile.getFileExtension().toLowerCase();
		if (fileExtension.equals("htm") || fileExtension.equals("html") || fileExtension.equals("shtml") || fileExtension.equals("xhtml"))
			return "text/html";
		if (fileExtension.equals("css"))
			return "text/css";
		if (fileExtension.equals("js"))
			return "text/javascript";
		if (fFile.getName().endsWith(".html.erb"))
			return "text/html+rb";
		return null;
	}

	public void recordNewProblems(List<IProblem> problems)
	{
		this.problems.addAll(problems);
	}
	
	List<IProblem> getRecordedProblems()
	{
		return this.problems;
	}
}
