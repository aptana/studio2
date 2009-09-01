/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.snippets;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.editors.toolbar.IToolBarMember;
import com.aptana.ide.editors.toolbar.ToolBarContribution;
import com.aptana.ide.editors.toolbar.ToolBarContributionRegistry;
import com.aptana.ide.editors.unified.IUnifiedEditor;

/**
 * @author Kevin Lindsey
 * @author Pavel Petrochenko
 */
public final class SnippetsManager
{
	/**
	 * @author Pavel Petrochenko
	 */
	public class SnippetNode
	{
		private String categoryName;
		private Map<String,SnippetNode> categoryMap = new HashMap<String,SnippetNode>();
		private List<Snippet> childNodes = new ArrayList<Snippet>();

		/**
		 * @param name
		 */
		public SnippetNode(String name)
		{
			this.categoryName = name;
		}

		/**
		 * @return category name
		 */
		public String getCategoryName()
		{
			return categoryName;
		}

		/**
		 * @return true
		 */
		public boolean hasChilds()
		{
			return !(categoryMap.isEmpty() && childNodes.isEmpty());
		}

		/**
		 * @return sub categories
		 */
		public SnippetNode[] getSubCategories()
		{
			Collection<SnippetNode> values = categoryMap.values();
			
			return values.toArray(new SnippetNode[values.size()]);
		}

		/**
		 * @return All children, categories first
		 */
		public Object[] getAllChildren()
		{
			List<SnippetNode> lst = new ArrayList<SnippetNode>(categoryMap.values());
			
			Collections.sort(lst, new Comparator<SnippetNode>()
			{
				public int compare(SnippetNode n0, SnippetNode n1)
				{
					return n0.getCategoryName().compareTo(n1.getCategoryName());
				}

			});
			
			List<Snippet> arrayList = new ArrayList<Snippet>(childNodes);
			
			Collections.sort(arrayList, new Comparator<Snippet>()
			{

				public int compare(Snippet n0, Snippet n1)
				{
					return n0.getName().compareTo(n1.getName());
				}

			});
			
			List<Object> result = new ArrayList<Object>();
			
			result.addAll(lst);
			result.addAll(arrayList);
			
			return result.toArray();
		}

		/**
		 * @return Snippets
		 */
		public Snippet[] getChildren()
		{
			Snippet[] children = new Snippet[childNodes.size()];
			childNodes.toArray(children);
			return children;
		}

		/**
		 * @param path
		 * @param position
		 * @param snippet
		 */
		public void add(String[] path, int position, Snippet snippet)
		{
			if (position == path.length)
			{
				childNodes.add(snippet);
				return;
			}
			
			String string = path[position].trim();
			SnippetNode object = categoryMap.get(string);
			
			if (object == null)
			{
				object = new SnippetNode(string);
				categoryMap.put(string, object);
			}
			
			object.add(path, position + 1, snippet);
		}

		/**
		 * @param path
		 * @param position
		 * @param snippet
		 */
		public void remove(String[] path, int position, Snippet snippet)
		{
			if (position == path.length)
			{
				if (childNodes.remove(snippet))
				{
					fireChangeListeners();
				}
			}
			else
			{
				String string = path[position].trim();
				SnippetNode object = categoryMap.get(string);
				
				if (object != null)
				{
					object.remove(path, position + 1, snippet);
					
					if (object.childNodes.isEmpty())
					{
						categoryMap.remove(string);
						fireChangeListeners();
					}
				}
			}
		}
	}

	private static final Snippet[] NO_SNIPPETS = null;
	private static SnippetsManager instance;

	private Map<Snippet,ToolBarContribution> _tContributions = new HashMap<Snippet,ToolBarContribution>();
	private SnippetNode _rootNode = new SnippetNode(""); //$NON-NLS-1$
	private List<SnippetListChangeListener> _snippetListChangeListeners;
	private Map<File,Snippet> _snippetsByFile;
	private Map<String,List<Snippet>> _snippetsByCategory;
	private String _tempDirectory;

	/**
	 * SnippetsList
	 */
	private SnippetsManager()
	{
		this._snippetsByFile = new HashMap<File,Snippet>();
		this._snippetsByCategory = new HashMap<String, List<Snippet>>();

		Path p = new Path(FileUtils.systemTempDir);
		this._tempDirectory = p.append("aptana/snippets").toOSString(); //$NON-NLS-1$
		File f = new File(this._tempDirectory);
		f.mkdir();
	}

	/**
	 * getInstance
	 * 
	 * @return SnippetsList instance
	 */
	public static SnippetsManager getInstance()
	{
		if (instance == null)
		{
			instance = new SnippetsManager();
		}

		return instance;
	}

	/**
	 * addChangeListener
	 * 
	 * @param listener
	 */
	public void addChangeListener(SnippetListChangeListener listener)
	{
		if (listener != null)
		{
			if (this._snippetListChangeListeners == null)
			{
				this._snippetListChangeListeners = new ArrayList<SnippetListChangeListener>();
			}

			this._snippetListChangeListeners.add(listener);
		}
	}

	/**
	 * fireChangeListeners
	 */
	public void fireChangeListeners()
	{
		if (this._snippetListChangeListeners != null)
		{
			for (int i = 0; i < this._snippetListChangeListeners.size(); i++)
			{
				this._snippetListChangeListeners.get(i).listChanged(this);
			}
		}
	}

	/**
	 * removeChangeListener
	 * 
	 * @param listener
	 */
	public void removeChangeListener(SnippetListChangeListener listener)
	{
		if (listener != null)
		{
			if (this._snippetListChangeListeners != null)
			{
				this._snippetListChangeListeners.remove(listener);
			}
		}
	}

	/**
	 * addSnippet
	 * 
	 * @param category
	 * @param name
	 * @param content
	 */
	public void addSnippet(String category, String name, String content)
	{
		this.addSnippet(new Snippet(category, name, content));
	}

	/**
	 * addSnippet
	 * 
	 * @param snippet
	 */
	public void addSnippet(final Snippet snippet)
	{
		if (snippet == null)
		{
			throw new IllegalArgumentException(Messages.SnippetsManager_Snippet_Undefined);
		}

		// get snippet category
		String category = snippet.getCategory();
		
		// add to category list
		if (this._snippetsByCategory.containsKey(category) == false)
		{
			this._snippetsByCategory.put(category, new ArrayList<Snippet>());
		}
		
		this._snippetsByCategory.get(category).add(snippet);
		
		// add to model
		String[] path = getCategoryPath(category);
		this._rootNode.add(path, 0, snippet);

		// add a snippet lookup by file, if a file associate exists
		File file = snippet.getFile();

		if (file != null)
		{
			this._snippetsByFile.put(file, snippet);
		}
		
		if (snippet.isToolbar())
		{
			String icon = snippet.getIcon();
			ImageDescriptor imageDescriptor = null;
			
			if (icon!= null && icon.length() != 0)
			{
				int indexOf = icon.indexOf('/');
				String bundle = icon.substring(0, indexOf);
				String imagePath = icon.substring(indexOf);
				
				imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(bundle, imagePath);
			}
			else
			{
				imageDescriptor = SnippetsPlugin.getImageDescriptor("/icons/snippet.png");//$NON-NLS-1$
			}
			
			ToolBarContribution toolBarContribution = new ToolBarContribution(
				snippet.getName(),
				snippet.getTooltip(),
				imageDescriptor, new IToolBarMember()
				{
					public void execute(IUnifiedEditor editor, String string)
					{
						if (editor instanceof ITextEditor)
						{
							snippet.apply((ITextEditor) editor);
						}
					}
				});

			ToolBarContributionRegistry.getInstance().addContribution(snippet.getLanguage(), toolBarContribution);
			
			this._tContributions.put(snippet, toolBarContribution);
		}
		
		// fire change event
		this.fireChangeListeners();
	}

	/**
	 * getCategoryPath
	 * 
	 * @param key
	 * @return
	 */
	private String[] getCategoryPath(String key)
	{
		return key.split("->"); //$NON-NLS-1$
	}

	/**
	 * getSnippetsByCategory
	 * 
	 * @return Snippet[]
	 */
	public Snippet[] getSnippetsByCategory(String category)
	{
		Snippet[] result = NO_SNIPPETS;
		
		if (this._snippetsByCategory.containsKey(category))
		{
			List<Snippet> snippets = this._snippetsByCategory.get(category);
			
			result = snippets.toArray(new Snippet[snippets.size()]);
		}
		
		return result;
	}
	
	/**
	 * getSnippetByFile
	 * 
	 * @param file
	 * @return Snippet or null
	 */
	public Snippet getSnippetByFile(File file)
	{
		Snippet result = null;

		if (this._snippetsByFile.containsKey(file))
		{
			result = this._snippetsByFile.get(file);
		}

		return result;
	}

	/**
	 * loadSnippetDirectory
	 * 
	 * @param snippetsDirectory
	 */
	public void loadSnippetDirectory(File snippetsDirectory)
	{
		// get all files in snippets directory
		File[] files = snippetsDirectory.listFiles();

		if (files == null)
		{
			return;
		}

		// process each snippet file
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];

			if (file.isFile())
			{
				Snippet snippet = Snippet.fromFile(file);

				if (snippet != null)
				{
					this.addSnippet(snippet);
				}
			}
		}
	}

	/**
	 * removeSnippet
	 * 
	 * @param snippet
	 */
	public void removeSnippet(Snippet snippet)
	{
		if (snippet != null)
		{
			// get snippet category
			String category = snippet.getCategory();
			
			// remove from category list
			if (this._snippetsByCategory.containsKey(category))
			{
				List<Snippet> snippets = this._snippetsByCategory.get(category);
				
				snippets.remove(snippet);
				
				if (snippets.size() == 0)
				{
					this._snippetsByCategory.remove(category);
				}
			}

			// remove from model
			String[] path = getCategoryPath(category);
			this._rootNode.remove(path, 0, snippet);
			
			// remove file association
			File file = snippet.getFile();
			
			if (file != null)
			{
				this._snippetsByFile.remove(file);
			}
			
			ToolBarContribution contribution = this._tContributions.get(snippet);
			
			if (contribution != null)
			{
				this._tContributions.remove(snippet);
				ToolBarContributionRegistry.getInstance().removeContribution(snippet.getLanguage(),contribution);
			}
		}
	}

	/**
	 * @return root node
	 */
	public SnippetNode getRootNode()
	{
		return this._rootNode;
	}

	/**
	 * The temporary directory for storing snippets
	 * 
	 * @return - temp snippet directory
	 */
	public String getSnippetTempDirectory()
	{
		return this._tempDirectory;
	}
}
