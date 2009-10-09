package com.aptana.ide.core.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;

public abstract class BuildParticipant
{
	public abstract boolean isActive(IProject project);

	public abstract void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor);

	public abstract void cleanStarting(IProject project);

	// public abstract void reconcile(ReconcileContext context);

	protected int getLineNumber(BuildContext context, IParseNode node)
	{
		return getLineNumber(context, node.getStartingLexeme());
	}

	protected int getLineNumber(BuildContext context, Lexeme lex)
	{
		if (context == null || lex == null)
			return -1;
		String contents = context.getContents();
		if (contents == null)
			return -1;
		try
		{
			int line = contents.substring(0, lex.getStartingOffset()).split("\r\n|\r|\n").length;
			return line;
		}
		catch (Exception e)
		{
			// ignore
		}
		return -1;
	}
}
