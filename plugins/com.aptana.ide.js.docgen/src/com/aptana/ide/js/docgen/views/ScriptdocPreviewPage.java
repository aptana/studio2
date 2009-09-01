/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.js.docgen.views;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSEditor;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.js.docgen.DocgenPlugin;
import com.aptana.ide.js.docgen.GenerateDocs;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.views.outline.Messages;

/**
 * Specific class for previewing a scriptdoc'd file
 * @author Ingo Muschenetz
 *
 */
public class ScriptdocPreviewPage extends Page {

	private Job _delayedRefreshJob;
	private IUnifiedEditor _editor;
	private IDocumentListener _documentListener;
	private Composite composite;
	private BrowserViewer browser;
	private static final int REFRESH_DELAY = 500;
	private boolean exportedResources = false;

	public ScriptdocPreviewPage(IUnifiedEditor editor) {
		_editor = editor;
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		// create main container
		this.composite = createComposite(parent);

		// create delayed update job
		this._delayedRefreshJob = this.createDelayedRefreshJob();
		this._delayedRefreshJob.setSystem(true);
		this._delayedRefreshJob.schedule();

		// create document change listener and add to editor
		this.createDocumentListener();
		this._editor.getDocumentProvider().getDocument(this._editor.getEditorInput()).addDocumentListener(
				this._documentListener);

		// refresh tree
		browser.refresh();
	}

	/**
	 * createDelayedRefreshJob
	 * 
	 * @return workbench job
	 */
	private Job createDelayedRefreshJob()
	{
		return new Job("Refresh Content") { //$NON-NLS-1$

			/**
			 * 
			 */
			protected IStatus run(IProgressMonitor monitor) {
				try
				{
					if (browser.isDisposed())
					{
						return Status.CANCEL_STATUS;
					}
						
					if(!(_editor instanceof JSEditor))
					{
						return Status.CANCEL_STATUS;
					}
					
					final JSEditor editor = (JSEditor)_editor;

					IParseState pstate = editor.getFileContext().getParseState();
					IEditorInput input = editor.getEditorInput();
					
					if(pstate instanceof JSParseState)
					{
						String xml = GenerateDocs.generateXML((JSParseState)pstate, input.getName());
						InputStream schemaStream = DocgenPlugin.class.getResourceAsStream("/com/aptana/ide/js/docgen/resources/docs_vjq_all.xsl"); //$NON-NLS-1$
						Calendar cal = new GregorianCalendar();
						if(!exportedResources)
						{
							String filePath = FileUtils.systemTempDir;
							String folderPath = filePath + "/images/"; //$NON-NLS-1$ //$NON-NLS-2$
							GenerateDocs.exportImage(folderPath, "arrow-back_16.gif"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "arrow-back_16.png"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "arrow-forward_16.gif"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "arrow-forward_16.gif"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "blue-button.png"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "grey-button.png"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "red-button.png"); //$NON-NLS-1$
							GenerateDocs.exportImage(folderPath, "header.png"); //$NON-NLS-1$
							GenerateDocs.exportResource(filePath, "jquery.pack.js"); //$NON-NLS-1$
							GenerateDocs.exportResource(filePath, "new_vjq.js"); //$NON-NLS-1$
							GenerateDocs.exportResource(filePath, "dimensions.js"); //$NON-NLS-1$						
							exportedResources = true;
						}
						
						final String index = GenerateDocs.generateHTMLFromXML(xml, FileUtils.systemTempDir, input.getName(), schemaStream) + "?id=" + cal.getTimeInMillis(); //$NON-NLS-1$
						WorkbenchJob wb = new WorkbenchJob(Messages.ScriptdocPreviewPage_Job_RefreshingBrowser) {
							/**
							 * 
							 */
							public IStatus runInUIThread(IProgressMonitor monitor) {
								
								if(browser.isDisposed())
								{
									return Status.CANCEL_STATUS;
								}
								else
								{
									browser.setURL(index);
									return Status.OK_STATUS;
								}
							}
						};
						wb.setSystem(true);
						wb.schedule();
					}

				}
				// SWT errors may be thrown here and will show as an error box since this is done on the UI thread
				// Catch everything and log it so that the dialog doesn't annoy the user since they may be typing into
				// the editor when this code throws errors and will impact them severly
				catch (Exception e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
							Messages.UnifiedOutlinePage_ErrorRefreshingOutline, e);
				}
				catch (Error e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
							Messages.UnifiedOutlinePage_ErrorRefreshingOutline, e);
				}

				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * createDocumentListener
	 */
	private void createDocumentListener()
	{
		this._documentListener = new IDocumentListener()
		{
			/**
			 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
			 */
			public void documentAboutToBeChanged(DocumentEvent event)
			{
			}

			/**
			 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
			 */
			public void documentChanged(DocumentEvent event)
			{
				// cancel currently running job first, to prevent unnecessary redraw
				if (_delayedRefreshJob != null)
				{
					_delayedRefreshJob.cancel();
					_delayedRefreshJob.schedule(REFRESH_DELAY);
				}
			}
		};
	}
	
	private Composite createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(data);
		composite.setLayout(new GridLayout());

		browser = new BrowserViewer(composite, SWT.NULL);
		browser.setURL(""); //$NON-NLS-1$
		return composite;

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		browser.setFocus();
	}
	
	public Control getControl() {
		// TODO Auto-generated method stub
		return composite;
	}


}
