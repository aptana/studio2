/*******************************************************************************

 * Copyright (c) 2007 Gunnar Wagenknecht and others.

 * All rights reserved. This program and the accompanying materials

 * are made available under the terms of the Eclipse Public License v1.0

 * which accompanies this distribution, and is available at

 * http://www.eclipse.org/legal/epl-v10.html

 *

 * Contributors:

 *     IBM Corporation - initial API and implementation

 *     Gunnar Wagenknecht - initial API and implementation
 
 *     Shalom Gibly - Aptana additions and modifications

 ******************************************************************************/

package com.aptana.ide.core.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.CloseResourceAction;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.progress.ProgressMonitorJobsDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.epl.Activator;

/**
 * Project property page for viewing and modifying the project natures.
 * 
 * @since Aptana Studio 1.2.4
 */

public class ProjectNaturesPage extends PropertyPage implements ICheckStateListener
{

	protected static final String APTANA_NATURE_PREFIX = "com.aptana.ide.";//$NON-NLS-1$;
	private static final int NATURES_LIST_MULTIPLIER = 30;
	private Image aptanaNatureImage = Activator.getImage("icons/aptana_nature.gif"); //$NON-NLS-1$;
	private IProject project;
	private boolean modified = false;
	// widgets
	private CheckboxTableViewer listViewer;
	protected boolean showAptanaOnly;
	private List<String> projectNatures;

	private HashMap<Object, TableItem> maintainChecked = new HashMap<Object, TableItem>();
	private HashMap<String, String> descriptionCache = new HashMap<String, String>();
	private String primaryNature;
	private Button makePrimaryButton;
	private Button restoreButton;
	private List<String> initialCheckedItems;

	public ProjectNaturesPage()
	{
		showAptanaOnly = true;
	}

	/**
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent)
	{
		Font font = parent.getFont();
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setFont(font);
		initialize();
		Label description = createDescriptionLabel(composite);
		description.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite tableComposite = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		tableComposite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		tableComposite.setLayoutData(data);

		listViewer = CheckboxTableViewer.newCheckList(tableComposite, SWT.TOP | SWT.BORDER);
		Table table = listViewer.getTable();
		table.setFont(font);
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setWidth(350);
		data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		if (!project.isOpen())
			listViewer.getControl().setEnabled(false);

		// Only set a height hint if it will not result in a cut off dialog

		if (DialogUtil.inRegularFontMode(parent))
		{
			data.heightHint = getDefaultFontHeight(table, NATURES_LIST_MULTIPLIER);
		}
		table.setLayoutData(data);
		table.setFont(font);
		listViewer.setLabelProvider(getLabelProvider());
		listViewer.setContentProvider(getContentProvider(project));
		listViewer.setComparator(getViewerComperator());
		listViewer.setInput(project.getWorkspace());
		listViewer.addCheckStateListener(this);
		table.setMenu(createMenu());
		initialCheckedItems = initializeCheckedNatures();
		Collections.sort(initialCheckedItems);
		listViewer.setCheckedElements(initialCheckedItems.toArray());
		listViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateButtons();
			}
		});

		// Add the buttons
		Composite buttons = new Composite(tableComposite, SWT.NONE);
		layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		makePrimaryButton = createButton(EPLMessages.ProjectNaturesPage_LBL_MakePrimary, buttons, new MakePrimarySelectionListener());
		restoreButton = createButton(EPLMessages.ProjectNaturesPage_LBL_Restore, buttons, new RestoreSelectionListener());
		updateButtons();

		return composite;
	}

	/**
	 * Returns a list that contains the items that are checked while loading and resetting this page.
	 */
	private List<String> initializeCheckedNatures()
	{
		Set<String> projectNatureIds = new HashSet<String>(projectNatures);
		IProjectNatureDescriptor[] natureDescriptors = project.getWorkspace().getNatureDescriptors();
		List<String> checked = new ArrayList<String>(projectNatureIds.size());
		for (int i = 0; i < natureDescriptors.length; i++)
		{
			if (projectNatureIds.remove(natureDescriptors[i].getNatureId()))
			{
				checked.add(fixNatureId(natureDescriptors[i].getNatureId()));
			}
		}
		for (Iterator<String> stream = projectNatureIds.iterator(); stream.hasNext();)
		{
			checked.add(stream.next());
		}
		return checked;
	}

	private Button createButton(String text, Composite parent, SelectionListener selectionListener)
	{
		Button b = new Button(parent, SWT.PUSH);
		b.setText(text);
		GridData data = new GridData(GridData.FILL);
		data.widthHint = getButtonWidthHint(b);
		b.setLayoutData(data);
		if (selectionListener != null)
		{
			b.addSelectionListener(selectionListener);
		}
		return b;
	}

	/*
	 * Updates the buttons enablement.
	 */
	private void updateButtons()
	{
		StructuredSelection selection = (StructuredSelection) listViewer.getSelection();
		makePrimaryButton.setEnabled(!selection.isEmpty() && !isPrimary(selection.getFirstElement()));
		restoreButton.setEnabled(modified || isPrimaryModified());
	}

	/*
	 * Returns a width hint for a button control.
	 */
	private static int getButtonWidthHint(Button button)
	{
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	/**
	 * Get the defualt widget height for the supplied control.
	 * 
	 * @return int
	 * @param control
	 *            - the control being queried about fonts
	 * @param lines
	 *            - the number of lines to be shown on the table.
	 */
	private static int getDefaultFontHeight(Control control, int lines)
	{
		FontData[] viewerFontData = control.getFont().getFontData();
		int fontHeight = 10;

		// If we have no font data use our guess
		if (viewerFontData.length > 0)
		{
			fontHeight = viewerFontData[0].getHeight();
		}
		return lines * fontHeight;
	}

	/**
	 * Returns a content provider for the list dialog. It will return all available natures as strings.
	 * 
	 * @return the content provider that shows the natures (as string children)
	 */
	private IStructuredContentProvider getContentProvider(final IProject project)
	{
		return new BaseWorkbenchContentProvider()
		{
			public Object[] getChildren(Object o)
			{
				if (!(o instanceof IWorkspace))
				{
					return new Object[0];
				}
				Set<String> projectNatureIds = new HashSet<String>(projectNatures);
				// collect all the natures
				IProjectNatureDescriptor[] natureDescriptors = ((IWorkspace) o).getNatureDescriptors();
				HashSet<String> elements = new HashSet<String>(natureDescriptors.length);
				for (int i = 0; i < natureDescriptors.length; i++)
				{
					String natureId = fixNatureId(natureDescriptors[i].getNatureId());
					if (natureId != null)
					{
						if (natureId.startsWith(APTANA_NATURE_PREFIX) || projectNatureIds.contains(natureId)
								|| !showAptanaOnly)
						{
							elements.add(natureId);
							descriptionCache.put(natureId, natureDescriptors[i].getLabel());
						}
					}
				}
				// Add any natures that exists in the project, but do not exist in the workbench
				// (This can happen when importing a project from a different workspace, or when the nature
				// provider uninstalled)
				for (String nature : projectNatures)
				{
					if (!elements.contains(nature))
					{
						elements.add(nature);
						descriptionCache.put(nature, EPLMessages.ProjectNaturesPage_MissingDescription);
					}
				}
				return elements.toArray();
			}
		};
	}

	private ILabelProvider getLabelProvider()
	{
		return new NaturesLabelProvider();
	}

	protected String getNatureDescriptorLabel(IProjectNatureDescriptor natureDescriptor)
	{
		String id = natureDescriptor.getNatureId();
		id = fixNatureId(id);
		String label = natureDescriptor.getLabel();
		if (label.trim().length() != 0)
			return id + " (" + label + ')'; //$NON-NLS-1$
		return id;
	}

	/**
	 * Creates the table menu.
	 * 
	 * @return A newly created menu
	 */
	protected Menu createMenu()
	{
		final Menu menu = new Menu(listViewer.getTable());
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(EPLMessages.ProjectNaturesPage_LBL_SetPrimary);
		item.addSelectionListener(new MakePrimarySelectionListener());
		return menu;
	}

	private ViewerComparator getViewerComperator()
	{
		return new ViewerComparator(new Comparator()
		{
			public int compare(Object element1, Object element2)
			{
				// set the Aptana on top and the rest on the bottom
				String firstId = element1.toString();
				String secondId = element2.toString();
				if (firstId.startsWith(APTANA_NATURE_PREFIX))
				{
					if (secondId.startsWith(APTANA_NATURE_PREFIX))
					{
						// Compare 2 aptana natures by name
						return firstId.compareTo(secondId);
					}
					else
					{
						// Put aptana as first
						return -1;
					}
				}
				else if (secondId.startsWith(APTANA_NATURE_PREFIX))
				{
					// put aptana as first
					return 1;
				}
				// The natures does not belong to Aptana, so return a simple string comparison
				return firstId.compareTo(secondId);
			}
		});
	}

	/**
	 * Handle the exception thrown when saving.
	 * 
	 * @param e
	 *            the exception
	 */
	protected void handle(InvocationTargetException e)
	{
		IdeLog.logError(Activator.getDefault(), EPLMessages.ProjectNaturesPage_ERR_NaturePage, e);
		IStatus error;
		Throwable target = e.getTargetException();
		if (target instanceof CoreException)
		{
			error = ((CoreException) target).getStatus();
		}
		else
		{
			String msg = target.getMessage();
			if (msg == null)
			{
				msg = IDEWorkbenchMessages.Internal_error;
			}
			error = new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 1, msg, target);
		}
		ErrorDialog.openError(getControl().getShell(), null, null, error);
	}

	/**
	 * Initializes a ProjectReferencePage.
	 */
	private void initialize()
	{
		project = (IProject) getElement().getAdapter(IResource.class);
		try
		{
			String[] natureIds = project.getDescription().getNatureIds();
			projectNatures = new ArrayList<String>(Arrays.asList(natureIds));
		}
		catch (CoreException e)
		{
			handle(new InvocationTargetException(e));
		}
		primaryNature = (projectNatures != null && !projectNatures.isEmpty()) ? projectNatures.get(0) : null;
		noDefaultAndApplyButton();
		String desc = NLS.bind(
                EPLMessages.ProjectNaturesPage_TXT_AdditionalNatures, project
                        .getName());
		setDescription(desc);
	}

	/**
	 * Returns true if the given element string is set as the primary nature.
	 * 
	 * @param element
	 * @return
	 */
	protected boolean isPrimary(Object element)
	{
		return primaryNature != null && primaryNature.equals(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
	 */
	public void checkStateChanged(CheckStateChangedEvent event)
	{
		if (maintainChecked.containsKey((event.getElement())))
		{
			// revert the change
			maintainChecked.get(event.getElement()).setChecked(!event.getChecked());
		}
		// Check if the current checked items are the same as the initial ones.
		Object[] checkedElements = listViewer.getCheckedElements();
		Arrays.sort(checkedElements);
		modified = !Arrays.equals(initialCheckedItems.toArray(), checkedElements);
		if (primaryNature == null)
		{
			// in case that the item was checked, set it as the primary
			if (event.getChecked())
			{
				primaryNature = event.getElement().toString();
				listViewer.refresh();
			}
		}
		else
		{
			if (!event.getChecked() && isPrimary(event.getElement()))
			{
				// reset the primary element
				primaryNature = null;
				// Find the next available item which is checked and set it to the primary
				checkedElements = listViewer.getCheckedElements();
				if (checkedElements.length > 0)
				{
					// take the first checked and set it to defualt
					primaryNature = checkedElements[0].toString();
				}
				listViewer.refresh();
			}
		}
		updateButtons();
	}

	/**
	 * @see PreferencePage#performOk
	 */
	public boolean performOk()
	{
		if (!modified && !isPrimaryModified())
		{
			return true;
		}
		// get checked natures
		Object[] checked = listViewer.getCheckedElements();
		final ArrayList<String> natureIds = new ArrayList<String>();
		for (int i = 0; i < checked.length; i++)
		{
			if (checked[i] instanceof String)
			{
				natureIds.add(checked[i].toString());
			}
			else
			{
				handle(new InvocationTargetException(new IllegalStateException(NLS.bind(
						"invalid element \"{0}\" in nature list", checked[i])))); //$NON-NLS-1$
				return false;
			}
		}
		// Locate and promote the primary item to the top of the list
		natureIds.remove(primaryNature);
		if (primaryNature != null)
		{
			natureIds.add(0, primaryNature);
		}

		// set nature ids
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				try
				{
					IProjectDescription description = project.getDescription();
					description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
					// Use IResource.AVOID_NATURE_CONFIG to avoid any warning about the natures.
					// We have to use it since not all of the Natures that are defined in the system
					// are valid and some are forced into the project in a non-standard way.
					project.setDescription(description, IResource.AVOID_NATURE_CONFIG, monitor);
				}
				catch (CoreException e)
				{
					throw new InvocationTargetException(e);
				}
			}
		};
		try
		{
			// This will block until the progress is done
			new ProgressMonitorJobsDialog(getControl().getShell()).run(true, true, runnable);
		}
		catch (InterruptedException e)
		{
			// Ignore interrupted exceptions
		}
		catch (InvocationTargetException e)
		{
			handle(e);
			return false;
		}
		resetProject();
		return true;
	}

	/*
	 * Returns true only if the primary nature was modified.
	 */
	private boolean isPrimaryModified()
	{
		// Check if just the primary was modified before exiting
		if (projectNatures != null && !projectNatures.isEmpty())
		{
			if (!projectNatures.get(0).equals(primaryNature))
			{
				return true;
			}
		}
		else if (primaryNature != null)
		{
			return true;
		}
		return false;
	}

	private String fixNatureId(String natureId)
	{
		int secondComIndex = natureId.indexOf(".com."); //$NON-NLS-1$
		if (secondComIndex > -1)
		{
			natureId = natureId.substring(secondComIndex + 1);
		}
		return natureId;
	}

	/**
	 * Ask to reset the project (e.g. Close and Open) to apply the changes.
	 */
	protected void resetProject()
	{
		boolean reset = MessageDialog
				.openQuestion(getControl().getShell(), EPLMessages.ProjectNaturesPage_ResetTitle,
						EPLMessages.ProjectNaturesPage_ResetMessage);
		if (reset)
		{
			IRunnableWithProgress close = new IRunnableWithProgress()
			{
				public void run(final IProgressMonitor monitor) throws InvocationTargetException
				{
					// Use the CloseResourceAction to provide a file saving dialog in case the project has some unsaved
					// files
					UIJob job = new UIJob(EPLMessages.ProjectNaturesPage_Job_CloseProject)
					{
						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							CloseResourceAction closeAction = new CloseResourceAction(Display.getDefault()
									.getActiveShell());
							closeAction.selectionChanged(new StructuredSelection(new Object[] { project }));
							closeAction.run();
							monitor.done();
							return Status.OK_STATUS;
						}
					};
					job.schedule();
					try
					{
						job.join();
					}
					catch (InterruptedException e)
					{
						IdeLog.logError(Activator.getDefault(), EPLMessages.ProjectNaturesPage_ERR_CloseProject, e);
					}
					monitor.done();
				}
			};
			try
			{
				// This will block until the progress is done
				new ProgressMonitorJobsDialog(getControl().getShell()).run(true, true, close);
			}
			catch (InterruptedException e)
			{
				// Ignore interrupted exceptions
			}
			catch (InvocationTargetException e)
			{
				handle(e);
			}

			IRunnableWithProgress open = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException
				{
					try
					{
						project.open(monitor);
					}
					catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}
				}
			};
			try
			{
				// This will block until the progress is done
				new ProgressMonitorJobsDialog(getControl().getShell()).run(true, true, open);
			}
			catch (InterruptedException e)
			{
				// Ignore interrupted exceptions
			}
			catch (InvocationTargetException e)
			{
				handle(e);
			}
		}
	}

	/**
	 * Natures label provider
	 */
	private class NaturesLabelProvider extends LabelProvider implements IFontProvider
	{
		public String getText(Object element)
		{
			if (element instanceof String)
			{
				if (descriptionCache.containsKey(element))
				{
					String desc = descriptionCache.get(element);
					if (isPrimary(element))
					{
						desc += EPLMessages.ProjectNaturesPage_TXT_Primary;
					}
					return desc;
				}
				return element.toString();
			}
			if (element instanceof IWorkspace)
			{
				return "Unknown"; //$NON-NLS-1$
			}
			return super.getText(element);
		}

		public Image getImage(Object element)
		{
			String natureId = element.toString();
			if (natureId != null && natureId.startsWith(APTANA_NATURE_PREFIX))
				return aptanaNatureImage;
			return super.getImage(element);
		}

		public Font getFont(Object element)
		{
			updateEnablement(element);
			if (isPrimary(element))
			{
				return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
			}
			return null;
		}

		private void updateEnablement(Object element)
		{
			if (maintainChecked.containsKey(element))
			{
				return;
			}
			TableItem[] items = listViewer.getTable().getItems();
			for (TableItem item : items)
			{
				if (item.getData() == element)
				{
					if (!item.getData().toString().startsWith(APTANA_NATURE_PREFIX))
					{
						item.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
						maintainChecked.put(element, item);
					}
					break;
				}
			}
		}
	}

	/*
	 * A selection adapter that handles the 'Make Primary' clicks.
	 */
	private class MakePrimarySelectionListener extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			ISelection selection = listViewer.getSelection();
			if (!selection.isEmpty() && selection instanceof StructuredSelection)
			{
				Object firstElement = ((StructuredSelection) selection).getFirstElement();
				// Select the item
				listViewer.setChecked(firstElement, true);
				// Make it as primary
				primaryNature = firstElement.toString();
				listViewer.refresh();
				updateButtons();
			}
		}
	}

	/*
	 * A selection adapter that handles the 'Restore' clicks.
	 */
	private class RestoreSelectionListener extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			modified = false;
			initialize();
			listViewer.setCheckedElements(initialCheckedItems.toArray());
			listViewer.refresh();
			updateButtons();
		}
	}
}