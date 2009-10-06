package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.IProblem;
import com.aptana.ide.core.builder.Warning;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.nodes.IParseNode;

public class UnclosedTagHTMLBuildParticipant extends HTMLBuildParticipant
{

	@Override
	public void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor)
	{
		for (BuildContext context : contexts)
		{
			if (!isHTMLFile(context))
				continue;
			// ok we have an html file
			IParseNode root = context.getRootNode();
			List<IProblem> problems = walk(context, root);
			context.recordNewProblems(problems);
		}
	}

	private List<IProblem> walk(BuildContext context, IParseNode root)
	{
		List<IProblem> problems = new ArrayList<IProblem>();
		IParseNode[] children = root.getChildren();
		for (IParseNode node : children)
		{
			if (node instanceof HTMLElementNode)
			{
				HTMLElementNode elementNode = (HTMLElementNode) node;
				if (!isClosed(context, elementNode))
					problems.add(new Warning(3, context.getFile().getFullPath().toPortableString(), -1, elementNode
							.getStartingOffset(), elementNode.getEndingOffset(), "Unclosed tag '"
							+ elementNode.getName() + "'"));

			}
			problems.addAll(walk(context, node));
		}
		return problems;
	}

	private boolean isClosed(BuildContext context, HTMLElementNode elementNode)
	{
		if (elementNode.isClosed())
			return true;
		return HTMLUtils.isTagClosed(elementNode.getStartingLexeme(), context.getLexemeList()) != HTMLUtils.TAG_OPEN;
	}
}
