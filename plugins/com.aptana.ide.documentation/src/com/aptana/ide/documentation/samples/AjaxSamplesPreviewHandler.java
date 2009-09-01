/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.documentation.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.documentation.DocumentationPlugin;
import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.intro.browser.CoreBrowserEditor;
import com.aptana.ide.samples.handlers.IPreviewHandler;
import com.aptana.ide.samples.model.SamplesEntry;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class AjaxSamplesPreviewHandler implements IPreviewHandler
{

	private void copyChildren(File parent, File[] children, List<File> createdFiles)
	{
		for (int i = 0; i < children.length; i++)
		{
			File file = children[i];
			File target = new File(parent, file.getName());
			try
			{
				if (file.isDirectory())
				{
					if (!target.exists())
					{
						if (target.mkdir())
						{
							createdFiles.add(target);
							copyChildren(target, file.listFiles(), createdFiles);
						}
					}
					else
					{
						copyChildren(target, file.listFiles(), createdFiles);
					}
				}
				else
				{
					if (!target.exists())
					{
						OutputStream os = new FileOutputStream(target);
						InputStream is = new FileInputStream(file);
						FileUtils.pipe(is, os, false);
						createdFiles.add(target);
						is.close();
						os.close();
					}
				}
			}
			catch (IOException ex)
			{
			}
		}
	}

	/**
	 * @see com.aptana.ide.samples.handlers.IPreviewHandler#previewRequested(com.aptana.ide.samples.model.SamplesEntry)
	 */
	public void previewRequested(final SamplesEntry entry)
	{
		if (entry.isRoot())
		{
			final List<File> migratedIncludes = new ArrayList<File>();
			List<SamplesEntry> entries = entry.getSubEntries();
			File index = null;
			for (int i = 0; i < entries.size(); i++)
			{
				SamplesEntry sub = (SamplesEntry) entries.get(i);
				String name = sub.getFile().getName();
				if (name.equals("index.html")) //$NON-NLS-1$
				{
					index = sub.getFile();
					break;
				}
				else if (index == null
						&& (name.endsWith(".html") || name.endsWith(".HTML") || name.endsWith(".htm") || name //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								.endsWith(".HTM"))) //$NON-NLS-1$
				{
					index = sub.getFile();
				}
			}

			if (index != null)
			{
				try
				{
					final URL url = index.toURI().toURL();
					if (url != null)
					{
						Bundle b = Platform.getBundle(DocumentationPlugin.PLUGIN_ID);

						// Ensure the entire folder is extracted so loading page will be complete
						URL content = b.getEntry("/content"); //$NON-NLS-1$
						if (content != null)
						{
							FileLocator.toFileURL(content);
						}

						URL loading = b.getEntry("/content/loading_sample.html"); //$NON-NLS-1$
						loading = FileLocator.toFileURL(loading);
						AjaxSampleBrowserInput input = new AjaxSampleBrowserInput(entry, loading);
						IWorkbenchWindow window = IntroPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
						if (window != null)
						{
							IWorkbenchPage page = window.getActivePage();
							final IEditorPart editor = IDE.openEditor(page, input, CoreBrowserEditor.ID);

							final Job buildSample = new Job(Messages.AjaxSamplesPreviewHandler_JobGeneratePreview)
							{

								protected IStatus run(IProgressMonitor monitor)
								{
									List<String> includes = entry.getParent().getIncludePaths();
									for (int i = 0; i < includes.size(); i++)
									{
										File file = new File((String) includes.get(i));
										File target = new File(entry.getFile(), file.getName());
										try
										{
											if (file.isDirectory())
											{
												if (!target.exists())
												{
													if (target.mkdir())
													{
														migratedIncludes.add(target);
														copyChildren(target, file.listFiles(), migratedIncludes);
													}
												}
												else
												{
													copyChildren(target, file.listFiles(), migratedIncludes);
												}
											}
											else
											{
												if (!target.exists())
												{
													OutputStream os = new FileOutputStream(target);
													InputStream is = new FileInputStream(file);
													FileUtils.pipe(is, os, false);
													migratedIncludes.add(target);
													is.close();
													os.close();
												}
											}
										}
										catch (IOException ex)
										{
										}
									}
									UIJob urlUpdate = new UIJob(Messages.AjaxSamplesPreviewHandler_JobUpdatePreview)
									{

										public IStatus runInUIThread(IProgressMonitor monitor)
										{
											if (editor != null && !((CoreBrowserEditor) editor).isDisposed())
											{
												((CoreBrowserEditor) editor).setURL(StringUtils.urlDecodeFilename(url
														.toExternalForm().toCharArray()));
											}
											return Status.OK_STATUS;
										}

									};
									urlUpdate.schedule();
									return Status.OK_STATUS;
								}

							};
							((CoreBrowserEditor) editor).addDisposeListener(new DisposeListener()
							{

								public void widgetDisposed(DisposeEvent e)
								{
									Job cleanupJob = new Job(Messages.AjaxSamplesPreviewHandler_JobCleanPreview)
									{

										protected IStatus run(IProgressMonitor monitor)
										{
											try
											{
												buildSample.join();
											}
											catch (InterruptedException e)
											{
											}
											synchronized (migratedIncludes)
											{
												for (int i = migratedIncludes.size() - 1; i >= 0; i--)
												{
													((File) migratedIncludes.get(i)).delete();
												}
											}
											return Status.OK_STATUS;
										}

									};
									cleanupJob.setSystem(true);
									cleanupJob.setPriority(Job.LONG);
									cleanupJob.schedule();
								}

							});
							buildSample.setPriority(Job.BUILD);
							buildSample.setSystem(true);
							buildSample.schedule();
						}
					}

				}
				catch (final Exception e)
				{
					final Job buildSample = new Job(Messages.AjaxSamplesPreviewHandler_JobShowError)
					{
						protected IStatus run(IProgressMonitor monitor)
						{
							CoreUIUtils
									.logAndDialogError(
											CoreUIUtils.getActiveShell(),
											DocumentationPlugin.getDefault(),
											Messages.AjaxSamplesPreviewHandler_ERR_Title_CreatePreview,
											Messages.AjaxSamplesPreviewHandler_ERR_MSG_CreatePreview,
											e);
							return Status.OK_STATUS;
						}
					};
					buildSample.setPriority(Job.INTERACTIVE);
					buildSample.setSystem(true);
					buildSample.schedule();
				}
			}
		}
	}

}
