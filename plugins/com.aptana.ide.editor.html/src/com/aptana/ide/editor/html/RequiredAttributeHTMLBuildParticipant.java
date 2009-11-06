package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.IProblem;
import com.aptana.ide.core.builder.Warning;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;

public class RequiredAttributeHTMLBuildParticipant extends HTMLBuildParticipant
{

	// TODO This varies by HTML version. I pulled this from HTML 4.01. From
	// http://www.w3.org/TR/html4/index/attributes.html
	private static Map<String, String[]> REQUIRED_ATTRIBUTES = new HashMap<String, String[]>();
	static
	{
		REQUIRED_ATTRIBUTES.put("img", new String[] { "alt", "src" });
		REQUIRED_ATTRIBUTES.put("basefont", new String[] { "size" });
		REQUIRED_ATTRIBUTES.put("applet", new String[] { "width", "height" });
		REQUIRED_ATTRIBUTES.put("style", new String[] { "type" });
		REQUIRED_ATTRIBUTES.put("script", new String[] { "type" });
		REQUIRED_ATTRIBUTES.put("textarea", new String[] { "rows", "cols" });
		REQUIRED_ATTRIBUTES.put("area", new String[] { "alt" });
		REQUIRED_ATTRIBUTES.put("param", new String[] { "name" });
		REQUIRED_ATTRIBUTES.put("map", new String[] { "name" });
		REQUIRED_ATTRIBUTES.put("bdo", new String[] { "dir" });
		REQUIRED_ATTRIBUTES.put("meta", new String[] { "content" });
		REQUIRED_ATTRIBUTES.put("optgroup", new String[] { "label" });
		REQUIRED_ATTRIBUTES.put("form", new String[] { "action" });
	}

	@Override
	public void build(BuildContext context, IProgressMonitor monitor)
	{
		if (!isHTMLFile(context))
			return;
		// ok we have an html file
		IParseNode root = context.getRootNode();
		List<IProblem> problems = walk(context, root);
		context.recordNewProblems(problems);
	}

	private List<IProblem> walk(BuildContext context, IParseNode root)
	{
		List<IProblem> problems = new ArrayList<IProblem>();
		if (root == null) {
		    return problems;
		}
		IParseNode[] children = root.getChildren();
		for (IParseNode node : children)
		{
			if (node instanceof HTMLElementNode)
			{
				HTMLElementNode elementNode = (HTMLElementNode) node;
				Collection<String> missingAttributes = getMissingAttributes(elementNode);
				for (String attributeName : missingAttributes)
				{
					problems.add(new Warning(2, context.getFile().getFullPath().toPortableString(), getLineNumber(
							context, elementNode), elementNode.getStartingOffset(), elementNode.getEndingOffset(),
							"Missing required attribute '" + attributeName + "'"));
				}
			}
			problems.addAll(walk(context, node));
		}
		return problems;
	}

	private Set<String> getMissingAttributes(HTMLElementNode elementNode)
	{
		String tagName = elementNode.getName();
		if (!REQUIRED_ATTRIBUTES.containsKey(tagName))
			return Collections.emptySet();
		Set<String> missingAttributes = new HashSet<String>();
		for (String attribute : REQUIRED_ATTRIBUTES.get(tagName))
		{
			if (!containsAttribute(elementNode, attribute))
				missingAttributes.add(attribute);
		}
		return missingAttributes;
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
