package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.IProblem;
import com.aptana.ide.core.builder.Warning;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;

public class RequiredAttributeHTMLBuildParticipant extends HTMLBuildParticipant
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
				String tagName = elementNode.getName();
				if (tagName.equalsIgnoreCase("img"))
				{
					if (!containsAttribute(elementNode, "alt"))
						problems
								.add(new Warning(2, context.getFile().getFullPath().toPortableString(), -1, elementNode
										.getStartingOffset(), elementNode.getEndingOffset(),
										"Missing required attribute 'alt'"));
				}
			}
			problems.addAll(walk(context, node));
		}
		return problems;
	}

	private boolean containsAttribute(HTMLElementNode elementNode, String attrName)
	{
		if (elementNode == null)
			return false;
		IParseNodeAttribute[] attrs = elementNode.getAttributes();
		if (attrs == null)
			return false;
		for (IParseNodeAttribute attr : attrs)
		{
			String attributeName = attr.getName();
			if (attributeName.equalsIgnoreCase(attrName))
				return true;

		}
		return false;
	}

}
