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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.views.model;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.runtime.Reference;
import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;

/**
 * @author Kevin Lindsey
 */
public class ModelContributer extends ContributedOutline
{
	private TreeViewer _treeViewer;
	
	/**
	 * ModelContributer
	 * 
	 * @param editor
	 */
	public ModelContributer()
	{
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		this._treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		
		Tree tree = this._treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setLayout(new GridLayout(1, true));
		
		this._treeViewer.setLabelProvider(new ModelLabelProvider());
		this._treeViewer.setAutoExpandLevel(1);
		this._treeViewer.setContentProvider(new ModelContentProvider());
		this._treeViewer.setComparer(new IElementComparer() {
			public boolean equals(Object a, Object b)
			{
				boolean result = false;
				
				if (a instanceof Reference && b instanceof Reference)
				{
					Reference ref1 = (Reference) a;
					Reference ref2 = (Reference) b;
					
					result = ref1.getPropertyName().equals(ref2.getPropertyName());
				}
				else
				{
					result = (a == b);
				}
				
				return result;
			}

			public int hashCode(Object element)
			{
				return 0;
			}
		});

		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText(Messages.getString("ModelContributer.Select_model_tab_to_load_outline")); //$NON-NLS-1$
		
		refresh();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#getParent()
	 */
	public Composite getParent()
	{
		return _treeViewer.getControl().getParent();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#getViewer()
	 */
	public Viewer getViewer()
	{
		return _treeViewer;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#refresh()
	 */
	public void refresh()
	{
		this._treeViewer.setInput(JSLanguageEnvironment.getInstance().getRuntimeEnvironment());
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#setBrowser(com.aptana.ide.editors.unified.ContributedBrowser)
	 */
	public void setBrowser(ContributedBrowser browser)
	{
		// do nothing
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#setFilterText(java.lang.String)
	 */
	public void setFilterText(String text)
	{
		// do nothing
	}
}
