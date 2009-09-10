/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.xul;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

import com.aptana.ide.editors.unified.ContributedBrowser;
import com.aptana.ide.editors.unified.ContributedOutline;
import com.aptana.ide.xul.browser.Activator;
import com.aptana.ide.xul.browser.FirefoxBrowser;

/**
 * FirefoxOutline
 */
public class FirefoxOutline extends ContributedOutline
{
	private TreeViewer treeViewer;
	private FirefoxBrowser browser;
	private WorkbenchJob refreshJob;
	private PatternFilter filter;
	private String pattern;

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		treeViewer.getTree().setLayout(new GridLayout(1, true));
		treeViewer.setLabelProvider(new DOMLabelProvider());
		treeViewer.setAutoExpandLevel(3);
		treeViewer.setContentProvider(new DOMContentProvider());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() == 1)
				{
					nsIDOMNode node = (nsIDOMNode) selection.getFirstElement();
					browser.highlightElement(node);
				}
			}

		});
		treeViewer.addFilter(new InternalNodeFilter());
		filter = new PatternFilter()
		{

			protected boolean isLeafMatch(Viewer viewer, Object element)
			{
				if (element instanceof nsIDOMNode)
				{
					if (((nsIDOMNode) element).getNodeType() == nsIDOMNode.ELEMENT_NODE)
					{
						nsIDOMElement e = (nsIDOMElement) ((nsIDOMNode) element)
								.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
						if (Activator.INTERNAL_ID.equals(e.getAttribute("class"))) //$NON-NLS-1$
						{
							return false;
						}
					}
					DOMLabelProvider prov = (DOMLabelProvider) treeViewer.getLabelProvider();
					return this.wordMatches(prov.getText(element));
				}
				return true;
			}

		};
		treeViewer.addFilter(filter);
		refreshJob = new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (treeViewer.getControl().isDisposed())
				{
					return Status.CANCEL_STATUS;
				}

				if (pattern == null)
				{
					return Status.OK_STATUS;
				}

				filter.setPattern(pattern);

				try
				{
					// don't want the user to see updates that will be made to the tree
					treeViewer.getControl().setRedraw(false);
					treeViewer.refresh(true);

					if (pattern.length() > 0)
					{
						/*
						 * Expand elements one at a time. After each is expanded, check to see if the filter text has
						 * been modified. If it has, then cancel the refresh job so the user doesn't have to endure
						 * expansion of all the nodes.
						 */
						IStructuredContentProvider provider = (IStructuredContentProvider) treeViewer
								.getContentProvider();
						Object[] elements = provider.getElements(treeViewer.getInput());
						for (int i = 0; i < elements.length; i++)
						{
							if (monitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}
							treeViewer.expandToLevel(elements[i], AbstractTreeViewer.ALL_LEVELS);
						}

						TreeItem[] items = treeViewer.getTree().getItems();
						if (items.length > 0)
						{
							// to prevent scrolling
							treeViewer.getTree().showItem(items[0]);
						}

					}
				}
				finally
				{
					// done updating the tree - set redraw back to true
					treeViewer.getControl().setRedraw(true);
				}
				return Status.OK_STATUS;
			}

		};
		TreeItem item = new TreeItem(treeViewer.getTree(), SWT.NONE);
		item.setText(Messages.getString("FirefoxOutline.Select_Tab_To_Load_Outline")); //$NON-NLS-1$
		refreshJob.setSystem(true);
		refresh();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#setBrowser(com.aptana.ide.editors.unified.ContributedBrowser)
	 */
	public void setBrowser(ContributedBrowser browser)
	{
		this.browser = (FirefoxBrowser) browser;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#refresh()
	 */
	public void refresh()
	{
		if (treeViewer != null && browser != null && browser.getDocument() != null)
		{
			treeViewer.setInput(browser.getDocument());
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#getParent()
	 */
	public Composite getParent()
	{
		return treeViewer.getControl().getParent();
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#getViewer()
	 */
	public Viewer getViewer()
	{
		return treeViewer;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ContributedOutline#setFilterText(java.lang.String)
	 */
	public void setFilterText(String text)
	{
		pattern = text;
		if (filter != null) {
			filter.setPattern(text);
		}
		if (refreshJob != null) {
			refreshJob.cancel();
			refreshJob.schedule(200);
		}
	}
}
