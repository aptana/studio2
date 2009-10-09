package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.IProblem;
import com.aptana.ide.core.builder.Warning;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.parsing.nodes.IParseNode;

public class MissingTitleTagBuildParticipant extends HTMLBuildParticipant
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
			if (!containsTitleTag(context, root))
			{
				List<IProblem> problems = new ArrayList<IProblem>();
				problems.add(new Warning(3, context.getFile().getFullPath().toPortableString(), -1, 0, 0,
						"Missing 'title' tag"));
				context.recordNewProblems(problems);
			}
		}

	}

	private boolean containsTitleTag(BuildContext context, IParseNode root)
	{
		if (root == null)
			return false;
		IParseNode[] children = root.getChildren();
		if (children == null)
			return false;
		for (IParseNode node : children)
		{
			if (node instanceof HTMLElementNode)
			{
				HTMLElementNode elementNode = (HTMLElementNode) node;
				String name = elementNode.getName();
				if (name.equalsIgnoreCase("title"))
					return true;
				if (name.equalsIgnoreCase("body"))
					return false;
			}
			boolean containsTitleTag = containsTitleTag(context, node);
			if (containsTitleTag)
				return true;
		}
		return false;
	}

}
