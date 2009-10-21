package com.aptana.ide.editor.html;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.core.builder.BuildParticipant;
import com.aptana.ide.editor.css.CSSColors;
import com.aptana.ide.editor.css.IIndexConstants;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editor.css.parsing.nodes.CSSSimpleSelectorNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSTermNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.index.core.Index;
import com.aptana.ide.index.core.IndexManager;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.nodes.IParseNode;

public class CSSClassesAndIdsIndexer extends BuildParticipant
{

	private Index fIndex;
	private Set<Index> indices;

	public CSSClassesAndIdsIndexer()
	{
		super();
	}

	@Override
	public void buildStarting(List<BuildContext> contexts, boolean isBatch, IProgressMonitor monitor)
	{
		indices = new HashSet<Index>();
	}

	@Override
	public void build(BuildContext context, IProgressMonitor monitor)
	{
		String extension = context.getFile().getFileExtension();
		if (extension != null && extension.equalsIgnoreCase("css"))
		{
			indexCSS(context);
		}
		else if (extension != null
				&& (extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("htm")
						|| extension.equalsIgnoreCase("shtml") || extension.equalsIgnoreCase("xhtml")))
		{
			indexHTML(context);
		}
	}

	@Override
	public void buildFinishing(IProgressMonitor monitor)
	{
		// Save the indexes now (so it gets saved to disk!)
		saveModifiedIndices();
		indices.clear();
	}

	private void saveModifiedIndices()
	{
		for (Index index : indices)
		{
			try
			{
				if (index != null)
					index.save();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		indices.clear();
		indices = null;
	}

	private void indexHTML(BuildContext context)
	{
		walkNode(context.getRootNode(), context);
	}

	private void walkNode(IParseNode parent, BuildContext context)
	{
		if (parent == null)
			return;
		if (parent instanceof HTMLElementNode)
		{
			String cssClass = ((HTMLElementNode) parent).getCSSClass();
			if (cssClass != null && cssClass.trim().length() > 0)
			{
				StringTokenizer tokenizer = new StringTokenizer(cssClass);
				while (tokenizer.hasMoreTokens())
					addIndex(context, IIndexConstants.CSS_CLASS, tokenizer.nextToken());
			}
			String id = ((HTMLElementNode) parent).getID();
			if (id != null && id.trim().length() > 0)
			{
				addIndex(context, IIndexConstants.CSS_IDENTIFIER, id);
			}
		}
		else
		{
			if (parent instanceof CSSSimpleSelectorNode)
			{
				CSSSimpleSelectorNode node = (CSSSimpleSelectorNode) parent;
				IParseNode[] children = node.getChildren();
				if (children != null && children.length >= 1)
				{
					IParseNode listNode = children[0];
					children = listNode.getChildren();
					for (IParseNode textChild : children)
					{
						String text = textChild.getText();
						if (text != null && text.startsWith("."))
						{
							addIndex(context, IIndexConstants.CSS_CLASS, text.substring(1));
						}
						else if (text != null && text.startsWith("#"))
						{
							addIndex(context, IIndexConstants.CSS_IDENTIFIER, text.substring(1));
						}
					}
				}
			}
			if (parent instanceof CSSTermNode)
			{
				CSSTermNode term = (CSSTermNode) parent;
				String value = term.getAttribute("value");
				if (isColor(value))
				{
					addIndex(context, IIndexConstants.CSS_COLOR, CSSColors.to6CharHexWithLeadingHash(value.trim()));
				}
			}
		}
		for (IParseNode child : parent.getChildren())
		{
			walkNode(child, context);
		}

	}

	private boolean isColor(String value)
	{
		if (value == null || value.trim().length() == 0)
			return false;
		if (CSSColors.namedColorExists(value))
			return true;
		if (value.startsWith("#") && (value.length() == 4 || value.length() == 7))
			return true; // FIXME Check to make sure it's hex values!
		return false;
	}

	private void addIndex(BuildContext context, String category, String word)
	{
		Index index = getIndex(context);
		indices.add(index);
		index.addEntry(category, word, context.getFile().getProjectRelativePath().toPortableString());
	}

	private void indexCSS(BuildContext context)
	{
		LexemeList ll = context.getLexemeList();
		for (Lexeme l : ll.toArray())
		{
			if (l == null || !l.getLanguage().equals(CSSMimeType.MimeType))
				continue;

			if (l.typeIndex == CSSTokenTypes.HASH)
			{
				String cssId = l.getText();
				if (cssId.startsWith("#"))
					cssId = cssId.substring(1);
				addIndex(context, IIndexConstants.CSS_IDENTIFIER, cssId);
			}
			else if (l.typeIndex == CSSTokenTypes.CLASS)
			{
				String cssClass = l.getText();
				if (cssClass.startsWith("."))
					cssClass = cssClass.substring(1);
				addIndex(context, IIndexConstants.CSS_CLASS, cssClass);
			}
			else if (l.typeIndex == CSSTokenTypes.COLOR
					|| (l.typeIndex == CSSTokenTypes.IDENTIFIER && CSSColors
							.namedColorExists(l.getText().toLowerCase())))
			{
				addIndex(context, IIndexConstants.CSS_COLOR, CSSColors.to6CharHexWithLeadingHash(l.getText()));
			}
		}
	}

	private Index getIndex(BuildContext context)
	{
		if (fIndex == null)
		{
			IProject project = context.getFile().getProject();
			fIndex = IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
		}
		return fIndex;
	}

	@Override
	public void cleanStarting(IProject project)
	{
		Index index = IndexManager.getInstance().getIndex(project.getFullPath().toPortableString());
		index.removeCategories(IIndexConstants.CSS_CLASS, IIndexConstants.CSS_IDENTIFIER);
	}

	@Override
	public boolean isActive(IProject project)
	{
		return true;
	}

}
