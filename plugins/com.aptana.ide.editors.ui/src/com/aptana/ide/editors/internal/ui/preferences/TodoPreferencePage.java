package com.aptana.ide.editors.internal.ui.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;

public class TodoPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	static class TodoTask
	{
		public TodoTask(String name, String priority)
		{
			this.name = name;
			this.priority = priority;
		}

		String name;
		String priority;
	}

	public TodoPreferencePage()
	{
		super();
		setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
		setDescription("Description");
		setTitle("title");
	}

	private List<String> extract(String raw)
	{
		StringTokenizer tokenizer = new StringTokenizer(raw, ",");
		List<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens())
		{
			tokens.add(tokenizer.nextToken());
		}
		return tokens;
	}

	@Override
	protected void createFieldEditors()
	{
		TableViewerEditor taskEditor = new TableViewerEditor("Todo Tasks", getFieldEditorParent())
		{

			@Override
			protected Object createObject()
			{
				TodoTaskDialog dialog = new TodoTaskDialog(getShell(), null, fElements);
				if (dialog.open() == Window.OK)
					return dialog.getResult();
				return null;
			}
			
			@Override
			protected Object editObject(Object toEdit)
			{
				TodoTaskDialog dialog = new TodoTaskDialog(getShell(), (TodoTask) toEdit, fElements);
				if (dialog.open() == Window.OK)
					return dialog.getResult();
				return toEdit;
			}

			@Override
			protected ColumnsDescription createTableColumns()
			{
				return new ColumnsDescription(new String[] { "Tag", "Priority" }, true);
			}

			@Override
			protected void doLoadDefault()
			{
				if (fTableControl != null)
				{
					List<String> tags = extract(getPreferenceStore().getDefaultString(
							IPreferenceConstants.COMPILER_TASK_TAGS));
					List<String> priorities = extract(getPreferenceStore().getDefaultString(
							IPreferenceConstants.COMPILER_TASK_PRIORITIES));
					for (int i = 0; i < tags.size(); i++)
					{
						addElement(new TodoTask(tags.get(i), priorities.get(i)));
					}
				}
			}

			@Override
			protected void doLoad()
			{
				if (fTableControl != null)
				{
					List<String> tags = extract(getPreferenceStore().getString(IPreferenceConstants.COMPILER_TASK_TAGS));
					List<String> priorities = extract(getPreferenceStore().getString(
							IPreferenceConstants.COMPILER_TASK_PRIORITIES));
					for (int i = 0; i < tags.size(); i++)
					{
						addElement(new TodoTask(tags.get(i), priorities.get(i)));
					}
				}
			}

			@Override
			protected Map<String, String> createPrefMap(List<Object> elements)
			{
				Map<String, String> prefMap = new HashMap<String, String>();
				StringBuilder tagBuilder = new StringBuilder();
				StringBuilder priorityBuilder = new StringBuilder();
				for (Object element : elements)
				{
					TodoTask task = (TodoTask) element;
					tagBuilder.append(task.name).append(",");
					priorityBuilder.append(task.priority).append(",");
				}
				if (!elements.isEmpty())
				{
					tagBuilder.deleteCharAt(tagBuilder.length() - 1);
					priorityBuilder.deleteCharAt(priorityBuilder.length() - 1);
				}
				prefMap.put(IPreferenceConstants.COMPILER_TASK_TAGS, tagBuilder.toString());
				prefMap.put(IPreferenceConstants.COMPILER_TASK_PRIORITIES, priorityBuilder.toString());
				return prefMap;
			}

			@Override
			protected ITableLabelProvider createLabelProvider()
			{
				return new TodoTaskLabelProvider();
			}
		};
		taskEditor.setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
		addField(taskEditor);

		BooleanFieldEditor caseSensitiveEditor = new BooleanFieldEditor(
				IPreferenceConstants.COMPILER_TASK_CASE_SENSITIVE, "Case-sensitive", getFieldEditorParent());
		caseSensitiveEditor.setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
		addField(caseSensitiveEditor);

	}

	public void init(IWorkbench workbench)
	{
		// TODO Auto-generated method stub

	}

	private class TodoTaskLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public TodoTaskLabelProvider()
		{
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element)
		{
			return null; // RubyPluginImages.get(RubyPluginImages.IMG_OBJS_REFACTORING_INFO);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			return getColumnText(element, 0);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			TodoTask task = (TodoTask) element;
			if (columnIndex == 0)
			{
				return task.name;
			}
			else
			{
				// if (PRIORITY_HIGH.equals(task.priority)) {
				// return PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_high_priority;
				// } else if (PRIORITY_NORMAL.equals(task.priority)) {
				// return PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_normal_priority;
				// } else if (PRIORITY_LOW.equals(task.priority)) {
				// return PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_low_priority;
				// }
				return task.priority;
			}
		}
	}

}
