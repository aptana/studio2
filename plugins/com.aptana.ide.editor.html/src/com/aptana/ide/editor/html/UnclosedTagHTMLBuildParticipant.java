package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.IProblem;
import com.aptana.ide.core.builder.Warning;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.nodes.IParseNode;

public class UnclosedTagHTMLBuildParticipant extends HTMLBuildParticipant
{

	@Override
	public void build(BuildContext context, IProgressMonitor monitor)
	{
		if (!isHTMLFile(context))
			return;
		// ok we have an html file
		IParseNode root = context.getRootNode();
		List<IProblem> problems = walk(context, root);
		problems.addAll(findUnopenedLexemes(context));
		context.recordNewProblems(problems);
	}

	private Collection<? extends IProblem> findUnopenedLexemes(BuildContext context)
	{
		List<IProblem> problems = new ArrayList<IProblem>();
		LexemeList list = context.getLexemeList();
		if (list == null) {
		    return problems;
		}
		Lexeme[] lexes = list.toArray();
		for (Lexeme lex : lexes)
		{
			if (lex == null || !lex.getLanguage().equals(HTMLMimeType.MimeType))
				continue;
			if (HTMLUtils.isEndTag(lex))
			{
				if (isUnopened(context, lex))
				{

					problems.add(new Warning(3, context.getFile().getFullPath().toPortableString(), getLineNumber(
							context, lex), lex.getStartingOffset(), lex.getEndingOffset(), "Unopened tag '"
							+ lex.getText() + "'"));
				}
			}
		}
		return problems;
	}

	private List<IProblem> walk(BuildContext context, IParseNode root)
	{
		List<IProblem> problems = new ArrayList<IProblem>();
		if (root == null)
			return problems;
		IParseNode[] children = root.getChildren();
		if (children == null)
			return problems;
		for (IParseNode node : children)
		{
			if (node instanceof HTMLElementNode)
			{
				HTMLElementNode elementNode = (HTMLElementNode) node;
				if (HTMLUtils.isStartTag(elementNode.getStartingLexeme()))
				{
					if (!isClosed(context, elementNode))
					{
						problems.add(new Warning(3, context.getFile().getFullPath().toPortableString(), getLineNumber(
								context, elementNode), elementNode.getStartingOffset(), elementNode.getEndingOffset(),
								"Unclosed tag '" + elementNode.getName() + "'"));
					}
				}
			}
			problems.addAll(walk(context, node));
		}
		return problems;
	}

	private boolean isUnopened(BuildContext context, Lexeme lexeme)
	{
		return !HTMLUtils.isEndTagBalanced(lexeme, context.getLexemeList(), (HTMLParseState) context.getParseState());
	}

	private boolean isClosed(BuildContext context, HTMLElementNode elementNode)
	{
		if (elementNode.isClosed())
			return true;
		return HTMLUtils.isStartTagBalanced(elementNode.getStartingLexeme(), context.getLexemeList(),
				(HTMLParseState) context.getParseState());
	}
}
