package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.IProblem;
import com.aptana.ide.core.builder.Warning;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLDocumentType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;

public class LowerCaseTagAndAttributeNamesChecker extends HTMLBuildParticipant
{

	@Override
	public void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor)
	{
		for (BuildContext context : contexts)
		{
			if (!isHTMLFile(context))
				continue;
			// ok we have an html file
			IParseState parseState = context.getParseState();
			if (!(parseState instanceof HTMLParseState))
				continue;
			// Make sure it's XHTML
			HTMLParseState htmlParseState = (HTMLParseState) parseState;
			if (htmlParseState.getDocumentType() < HTMLDocumentType.XHTML_1_0_STRICT)
				continue;
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

				String name = elementNode.getName();
				if (!name.equals(name.toLowerCase()))
				{
					// FIXME Grab the closing lexeme for this open tag and calculate proper end offset using it
					problems.add(new Warning(5, context.getFile().getFullPath().toPortableString(), getLineNumber(
							context, elementNode), elementNode.getStartingOffset(), elementNode.getStartingOffset()
							+ name.length() + 2, "Tagnames must be lowercase in XHTML: '" + name + "'"));
				}
				IParseNodeAttribute[] attributes = elementNode.getAttributes();
				if (attributes != null && attributes.length > 0)
				{
					int index = context.getLexemeList().getLexemeIndex(elementNode.getStartingLexeme());
					Lexeme closeLex = HTMLUtils.getTagCloseLexeme(elementNode.getStartingLexeme(), context
							.getLexemeList());
					int endIndex = context.getLexemeList().getLexemeIndex(closeLex);
					Lexeme[] lexemes = context.getLexemeList().copyRange(index, endIndex);
					for (IParseNodeAttribute attribute : attributes)
					{
						String attributeName = attribute.getName();
						Lexeme match = findMatching(lexemes, attributeName);
						if (match != null && match.getText() != null && !match.getText().equals(attributeName))
						{
							problems.add(new Warning(6, context.getFile().getFullPath().toPortableString(),
									getLineNumber(context, match), match.getStartingOffset(), match.getEndingOffset(),
									"Attribute names must be lowercase in XHTML: '" + match.getText() + "'"));
						}
					}
				}
			}
			problems.addAll(walk(context, node));
		}
		return problems;
	}

	private Lexeme findMatching(Lexeme[] lexemes, String attributeName)
	{
		for (Lexeme lexeme : lexemes)
		{
			if (lexeme != null && lexeme.typeIndex == HTMLTokenTypes.NAME && lexeme.getText() != null
					&& lexeme.getText().equalsIgnoreCase(attributeName))
				return lexeme;
		}
		return null;
	}

}
