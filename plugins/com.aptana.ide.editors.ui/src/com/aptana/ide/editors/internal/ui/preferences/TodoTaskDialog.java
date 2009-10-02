package com.aptana.ide.editors.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.internal.ui.preferences.TodoPreferencePage.TodoTask;
import com.aptana.ide.editors.preferences.IPreferenceConstants;

public class TodoTaskDialog extends StatusDialog
{
	private Text fNameDialogField;
	private Combo fPriorityDialogField;

	private List<String> fExistingNames;
	private TodoTask fTask;

	public TodoTaskDialog(Shell parent, TodoTask task, List existingEntries)
	{
		super(parent);

		fExistingNames = new ArrayList<String>(existingEntries.size());
		for (TodoTask curr : (List<TodoTask>) existingEntries)
		{
			if (!curr.equals(task))
			{
				fExistingNames.add(curr.name);
			}
		}

		if (task == null)
		{
			setTitle("New Task Tag");
		}
		else
		{
			setTitle("Edit Task Tag");
		}
		fTask = task;
	}

	public TodoTask getResult()
	{
		return fTask;
	}

	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite inner = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		Label label = new Label(inner, SWT.NONE);
		label.setText("Tag: ");

		fNameDialogField = new Text(inner, SWT.SINGLE | SWT.BORDER);
		fNameDialogField.addKeyListener(new KeyListener()
		{
			
			public void keyReleased(KeyEvent e)
			{
				doValidation();
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
		});
		fNameDialogField.setText((fTask != null) ? fTask.name : ""); //$NON-NLS-1$
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		fNameDialogField.setLayoutData(gridData);

		String[] items = new String[] { "High", "Normal", "Low" };

		label = new Label(inner, SWT.NONE);
		label.setText("Priority: ");

		fPriorityDialogField = new Combo(inner, SWT.READ_ONLY);
		fPriorityDialogField.setItems(items);
		if (fTask != null)
		{
			if (IPreferenceConstants.COMPILER_TASK_PRIORITY_HIGH.equals(fTask.priority))
			{
				fPriorityDialogField.select(0);
			}
			else if (IPreferenceConstants.COMPILER_TASK_PRIORITY_NORMAL.equals(fTask.priority))
			{
				fPriorityDialogField.select(1);
			}
			else
			{
				fPriorityDialogField.select(2);
			}
		}
		else
		{
			fPriorityDialogField.select(1);
		}

		applyDialogFont(composite);
		return composite;
	}

	private void doValidation()
	{
		IStatus status = Status.OK_STATUS;
		String newText = fNameDialogField.getText();
		if (newText.length() == 0)
		{
			status = new Status(IStatus.ERROR, UnifiedEditorsPlugin.ID, -1, "Enter Name", null);
		}
		else
		{
			if (newText.indexOf(',') != -1)
			{
				status = new Status(IStatus.ERROR, UnifiedEditorsPlugin.ID, -1, "Comma not allowed in name", null);
			}
			else if (fExistingNames.contains(newText))
			{
				status = new Status(IStatus.ERROR, UnifiedEditorsPlugin.ID, -1,
						"An existing entry already uses this name", null);

			}
			else if (Character.isWhitespace(newText.charAt(0))
					|| Character.isWhitespace(newText.charAt(newText.length() - 1)))
			{
				status = new Status(IStatus.ERROR, UnifiedEditorsPlugin.ID, -1, "Spaces not allowed in name", null);
			}
		}
		updateStatus(status);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		// FIXME Uncomment for help context!
		// WorkbenchHelp.setHelp(newShell, IJavaHelpContextIds.TODO_TASK_INPUT_DIALOG);
	}
	
	@Override
	protected void okPressed()
	{
		String name = fNameDialogField.getText().trim();
		String priority = IPreferenceConstants.COMPILER_TASK_PRIORITY_NORMAL;
		switch (fPriorityDialogField.getSelectionIndex())
		{
			case 0:
				priority = IPreferenceConstants.COMPILER_TASK_PRIORITY_HIGH;
				break;
			case 1:
				priority = IPreferenceConstants.COMPILER_TASK_PRIORITY_NORMAL;
				break;
			default:
				priority = IPreferenceConstants.COMPILER_TASK_PRIORITY_LOW;
				break;
		}
		fTask = new TodoTask(name, priority);
		super.okPressed();
	}

}
