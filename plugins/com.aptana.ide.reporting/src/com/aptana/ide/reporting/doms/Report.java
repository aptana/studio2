package com.aptana.ide.reporting.doms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.ui.AptanaNavigator;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.intro.browser.CoreBrowserEditor;
import com.aptana.ide.intro.browser.CoreBrowserEditorInput;
import com.aptana.ide.reporting.Activator;

public class Report
{
	public Report()
	{
	}

	public String getSelectedProject()
	{
		IWorkbenchPart[] views = CoreUIUtils.getViewsInternal(AptanaNavigator.ID);
		if (views != null && views.length == 1)
		{
			if (views[0] instanceof AptanaNavigator)
			{
				Tree projectTree = ((AptanaNavigator) views[0]).getTreeViewer().getTree();
				TreeItem[] selection = projectTree.getSelection();
				if (selection != null && selection.length > 0)
				{
					Object data = selection[0].getData();
					String projectName = null;
					if (data instanceof IProject)
					{
						projectName = ((IProject) data).getName();
					}
					else if (data instanceof IResource)
					{
						projectName = ((IResource) data).getProject().getName();
					}

					return projectName;
				}
			}
		}

		return null;
	}

	public String getSelectedProjectPath()
	{
		IWorkbenchPart[] views = CoreUIUtils.getViewsInternal(AptanaNavigator.ID);
		if (views != null && views.length == 1)
		{
			if (views[0] instanceof AptanaNavigator)
			{
				Tree projectTree = ((AptanaNavigator) views[0]).getTreeViewer().getTree();
				TreeItem[] selection = projectTree.getSelection();
				if (selection != null && selection.length > 0)
				{
					Object data = selection[0].getData();
					String projectPath = null;
					if (data instanceof IProject)
					{
						projectPath = ((IProject) data).getLocation().toPortableString();
					}
					else if (data instanceof IResource)
					{
						projectPath = ((IResource) data).getProject().getLocation().toPortableString();
					}

					return projectPath;
				}
			}
		}

		return null;
	}

	public String getTemplate(String name)
	{
		File file = null;
		URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(name);
		
		try
		{
			if(url != null)
			{
				URL fileURL = FileLocator.toFileURL(url);
				file = new File(fileURL.getPath());
			}
			else
			{
				file = new File(name);
				if(file.exists() == false)
				{
					return null;
				}
			}
			
			String s = FileUtils.readContent(file);
			return s;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void createReportView(String contents, String reportType, String projectName)
	{
		String newFileName = FileUtils.getRandomFileName("_report", "html"); //$NON-NLS-1$ //$NON-NLS-2$
		File newFile = new File(FileUtils.systemTempDir + File.separator + newFileName);
		String editorTitle = projectName + " " + reportType + " Report"; //$NON-NLS-1$ //$NON-NLS-2$
		String saveAsFile = projectName + "_" + reportType + "_" + "Report.html"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		try
		{
			FileWriter fw = new FileWriter(newFile);
			fw.write(contents);
			fw.close();

			URL url = newFile.toURI().toURL();

			if (url != null)
			{
				CoreBrowserEditorInput input = new CoreBrowserEditorInput(url);
				input.setImage(Activator.getImageDescriptor("icons/view_detailed.png")); //$NON-NLS-1$
//				input.setName(editorTitle);
				input.setSaveAsAllowed(true);
				input.setSaveAsFile(newFile);
				input.setSaveAsFileName(saveAsFile);
				IWorkbenchWindow window = IntroPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
				if (window != null)
				{
					IWorkbenchPage page = window.getActivePage();
					IDE.openEditor(page, (IEditorInput) input, CoreBrowserEditor.ID);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
