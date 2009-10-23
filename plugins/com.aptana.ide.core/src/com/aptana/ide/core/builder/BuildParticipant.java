package com.aptana.ide.core.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;

public abstract class BuildParticipant
{
	public abstract boolean isActive(IProject project);

	/**
	 * Pre-build callback. DO NOT DO THE ACTUAL WORK ON THE CONTEXTS INVOLVING PARSING HERE, OR WE WILL RUN OUT OF
	 * MEMORY STORING ALL THEIR ASTS!!!
	 * 
	 * @param contexts
	 * @param isBatch
	 * @param monitor
	 */
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

	public abstract void buildFinishing(IProgressMonitor monitor);

	/**
	 * Do the grunt work here. This is where it's ok to parse and get ASTs or Lexemes.
	 * 
	 * @param context
	 * @param monitor
	 */
	public abstract void build(BuildContext context, IProgressMonitor monitor);
}
