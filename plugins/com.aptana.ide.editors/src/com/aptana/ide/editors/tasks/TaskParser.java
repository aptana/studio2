package com.aptana.ide.editors.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.builder.BuildContext;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author cwilliams
 */
class TaskParser
{

	private boolean fCaseSensitive = false;
	private String[] fTags;
	private int[] fPriorities;

	public TaskParser()
	{
		fCaseSensitive = Platform.getPreferencesService().getBoolean(UnifiedEditorsPlugin.ID,
				IPreferenceConstants.COMPILER_TASK_CASE_SENSITIVE, false, null);
		fTags = tokenize(Platform.getPreferencesService().getString(UnifiedEditorsPlugin.ID,
				IPreferenceConstants.COMPILER_TASK_TAGS, "", null), ",");
		fPriorities = convertPriorities(tokenize(Platform.getPreferencesService().getString(UnifiedEditorsPlugin.ID,
				IPreferenceConstants.COMPILER_TASK_PRIORITIES, "", null), ","));
	}

	private int[] convertPriorities(String[] stringPriorities)
	{
		int priorities[] = new int[stringPriorities.length];
		for (int i = 0; i < stringPriorities.length; i++)
		{
			String priority = stringPriorities[i];
			if (priority.equals(IPreferenceConstants.COMPILER_TASK_PRIORITY_LOW))
			{
				priorities[i] = IMarker.PRIORITY_LOW;
			}
			else if (priority.equals(IPreferenceConstants.COMPILER_TASK_PRIORITY_HIGH))
			{
				priorities[i] = IMarker.PRIORITY_HIGH;
			}
			else
			{
				priorities[i] = IMarker.PRIORITY_NORMAL;
			}
		}
		return priorities;
	}

	private String[] tokenize(String tags, String delim)
	{
		return tags.split(delim);
	}

	public List<TaskTag> parse(BuildContext buildContext)
	{
		if (fTags.length <= 0)
			return Collections.emptyList();

		List<TaskTag> tasks = new ArrayList<TaskTag>();
		try
		{
			LexemeList ll = buildContext.getLexemeList();
			if (ll == null)
				return tasks;
			for (Lexeme lexeme : ll.toArray())
			{
				if (!lexeme.getType().equalsIgnoreCase("COMMENT") && !lexeme.getLanguage().equals("text/jscomment"))
					continue;

				String[] lines = lexeme.getText().split("[\\r|\\n]");
				for (String line : lines)
				{
					tasks.addAll(processLine(buildContext, line, lexeme.offset));
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.log(UnifiedEditorsPlugin.getDefault(), IStatus.ERROR, e.getMessage(), e);
		}
		return tasks;
	}

	private String trimComment(String text)
	{
		// FIXME Ideally we'd be able to ask the lexeme for the contents that don't include the begin/end markers of the
		// comment
		if (text.endsWith("-->"))
		{
			return text.substring(0, text.length() - 3);
		}
		if (text.endsWith("*/"))
		{
			return text.substring(0, text.length() - 2);
		}
		return text;
	}

	private int getLineNumber(BuildContext buildContext, int offset)
	{
		return buildContext.getContents().substring(0, offset).split("\\r|\\n").length;
	}

	private List<TaskTag> processLine(BuildContext buildContext, String line, int offset) throws CoreException
	{
		List<TaskTag> tasks = new ArrayList<TaskTag>();
		if (!fCaseSensitive)
			line = line.toLowerCase();
		for (int i = 0; i < fTags.length; i++)
		{
			String tag = fTags[i];
			int priority = fPriorities[i];
			if (!fCaseSensitive)
				tag = tag.toLowerCase();
			int index = line.indexOf(tag);
			if (index == -1)
				continue;
			int newOffset = offset + index;
			String message = line.substring(index).trim();
			message = trimComment(message);
			tasks.add(new TaskTag(priority, message, getLineNumber(buildContext, newOffset), newOffset, newOffset
					+ message.length()));
		}
		return tasks;
	}
}
