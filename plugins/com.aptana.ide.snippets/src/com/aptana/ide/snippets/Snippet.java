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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.editors.unified.IUnifiedEditor;

/**
 * @author Kevin Lindsey
 */
public class Snippet
{
	private static final Pattern MULTILINE_COMMENT_PATTERN = Pattern.compile("^/\\*(.*?)\\*/\\s*", Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern XML_COMMENT_PATTERN = Pattern.compile("^<!--(.*?)-->\\s*", Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("((?:\\w|[-()])+)\\s*:\\s*(.*)$", //$NON-NLS-1$
			Pattern.MULTILINE);
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{((?:\\w|-)+)\\}"); //$NON-NLS-1$

	private static final String CATEGORY_KEY = "category"; //$NON-NLS-1$
	private static final String ICON_KEY = "icon"; //$NON-NLS-1$
	private static final String TOOLBAR_KEY = "toolbar"; //$NON-NLS-1$
	private static final String TOOLTIP_KEY = "tooltip"; //$NON-NLS-1$
	private static final String LANGUAGE_KEY = "language"; //$NON-NLS-1$
	private static final String NAME_KEY = "name"; //$NON-NLS-1$

	private static final String PROMPT_START = "prompt("; //$NON-NLS-1$
	private static final String PROMPT_END = ")"; //$NON-NLS-1$

	private Map<String, String> _metadata;
	private List<SnippetVariable> _prompts;
	private File _file;
	private String _content;

	/**
	 * @return icon path;
	 */
	public String getIcon()
	{
		return this._metadata.get(ICON_KEY);
	}

	/**
	 * @return icon path;
	 */
	public String getLanguage()
	{
		return this._metadata.get(LANGUAGE_KEY);
	}

	/**
	 * @return should this item be contributed to toolbar
	 */
	public boolean isToolbar()
	{
		String string = this._metadata.get(TOOLBAR_KEY);

		if (string != null)
		{
			return Boolean.parseBoolean(string);
		}

		return false;
	}

	/**
	 * Snippet
	 * 
	 * @param category
	 * @param name
	 * @param content
	 */
	public Snippet(String category, String name, String content)
	{
		this();

		this.setValue(CATEGORY_KEY, category);
		this.setValue(NAME_KEY, name);
		this._content = content;
	}

	/**
	 * setValue
	 * 
	 * @param key
	 * @param value
	 */
	private void setValue(String key, String value)
	{
		if (key.startsWith(PROMPT_START) && key.endsWith(PROMPT_END))
		{
			String name = key.substring(PROMPT_START.length(), key.length() - PROMPT_END.length());

			if (this._prompts == null)
			{
				this._prompts = new ArrayList<SnippetVariable>();
			}

			this._prompts.add(new SnippetVariable(name, "", value)); //$NON-NLS-1$
		}
		else
		{
			this._metadata.put(key, value);
		}
	}

	/**
	 * Snippet
	 */
	private Snippet()
	{
		this._metadata = new HashMap<String, String>();
	}

	/**
	 * fromString
	 * 
	 * @param text
	 * @return Snippet
	 */
	public static Snippet fromString(String text)
	{
		Snippet result = null;

		if (text != null)
		{
			Matcher m = MULTILINE_COMMENT_PATTERN.matcher(text);
			String metadata = null;
			String content = null;

			if (m.find())
			{
				metadata = m.group(1);
				content = text.substring(m.end());
			}
			else
			{
				m = XML_COMMENT_PATTERN.matcher(text);

				if (m.find())
				{
					metadata = m.group(1);
					content = text.substring(m.end());
				}
			}

			if (metadata != null)
			{
				Snippet candidate = new Snippet();
				m = KEY_VALUE_PATTERN.matcher(metadata);

				while (m.find())
				{
					candidate.setValue(m.group(1).toLowerCase(Locale.getDefault()), m.group(2));
				}

				candidate._content = content;

				if (candidate.isValid())
				{
					result = candidate;
				}
			}
		}

		return result;
	}

	/**
	 * fromFile
	 * 
	 * @param file
	 * @return Snippet
	 */
	public static Snippet fromFile(File file)
	{
		String text = null;

		try
		{
			// get file contents as text
			text = FileUtils.readContent(file);
		}
		catch (IOException e)
		{
		}

		// get resulting snippet after processing the file content
		Snippet result = fromString(text);

		if (result != null)
		{
			// associate this file with this snippet
			result._file = file;
		}

		// return result;
		return result;
	}

	/**
	 * getCategory
	 * 
	 * @return String
	 */
	public String getCategory()
	{
		return this._metadata.get(CATEGORY_KEY);
	}

	/**
	 * getContent
	 * 
	 * @return String
	 */
	public String getRawContent()
	{
		return this._content;
	}

	/**
	 * getExpandedContent
	 *
	 * @param selectedText
	 * @return
	 */
	public String getExpandedContent(String selectedText)
	{
		return this.getExpandedContent(selectedText, new IntegerHolder());
	}
	
	/**
	 * getExpandedContent
	 * 
	 * @param selectedText
	 * @param cursorPosition
	 * @return String
	 */
	public String getExpandedContent(String selectedText, IntegerHolder cursorPosition)
	{
		boolean expand = true;
		String result = null;

		// build lookup table
		Map<String, String> valuesByName = new HashMap<String, String>();

		// add selection text
		valuesByName.put("selection", selectedText); //$NON-NLS-1$
		valuesByName.put("cursor", "" + (char) 2); //$NON-NLS-1$ //$NON-NLS-2$
		// prompt for variable values, if needed
		if (this._prompts != null && this._prompts.size() > 0)
		{
			// ask user for values
			SnippetDialog dialog = new SnippetDialog(Display.getCurrent().getActiveShell(), this);
			dialog.open();
			if (dialog.OK)
			{
				// move variable values into lookup table
				for (int i = 0; i < this._prompts.size(); i++)
				{
					SnippetVariable variable = this._prompts.get(i);

					valuesByName.put(variable.getName(), variable.getValue());
				}
			}
			else
			{
				// let later code know that user canceled this action
				expand = false;
			}
		}

		if (expand)
		{
			// replace all variable instances
			StringBuffer buffer = new StringBuffer();
			Matcher m = VARIABLE_PATTERN.matcher(this._content);

			while (m.find())
			{
				String key = m.group(1);
				String replacement = ""; //$NON-NLS-1$

				if (valuesByName.containsKey(key))
				{
					replacement = valuesByName.get(key);
				}

				// Fix for #5050. We were selecting items with their own regular expressions inside [IM]
				String quotedReplacement = Matcher.quoteReplacement(replacement);
				m.appendReplacement(buffer, quotedReplacement);
			}

			m.appendTail(buffer);

			result = buffer.toString();
			int indexOf = result.indexOf(2);
			if (indexOf != -1)
			{
				cursorPosition.cursorPosition = indexOf;
				result = result.substring(0, indexOf) + result.substring(indexOf + 1);
			}
		}

		return result;
	}

	/**
	 * Return the file with which this snippet is associated
	 * 
	 * @return File or null
	 */
	public File getFile()
	{
		return this._file;
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this._metadata.get(NAME_KEY);
	}

	/**
	 * getVariables
	 * 
	 * @return an array of variables
	 */
	public SnippetVariable[] getVariables()
	{
		SnippetVariable[] result;

		if (this._prompts != null)
		{
			result = this._prompts.toArray(new SnippetVariable[this._prompts.size()]);
		}
		else
		{
			result = new SnippetVariable[0];
		}

		return result;
	}

	/**
	 * isValid
	 * 
	 * @return boolean
	 */
	private boolean isValid()
	{
		return this.hasKey(CATEGORY_KEY) && this.hasKey(NAME_KEY) && this._content != null;
	}

	/**
	 * hasKey
	 * 
	 * @param key
	 * @return boolean
	 */
	private boolean hasKey(String key)
	{
		return this._metadata.containsKey(key);
	}

	static class IntegerHolder
	{
		int cursorPosition = -1;
	}

	void apply(final ITextEditor editor)
	{
		// get current selection
		ITextSelection ts = (ITextSelection) editor.getSelectionProvider().getSelection();
		final int selectionOffset = ts.getOffset();
		final int selectionLength = ts.getLength();

		// get document
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());

		// get selected text
		String selectedText = ""; //$NON-NLS-1$

		try
		{
			selectedText = doc.get(selectionOffset, selectionLength);
		}
		catch (BadLocationException e1)
		{
		}

		// get content after all variables have been expanded
		final IntegerHolder cursorPosition = new IntegerHolder();
		String content = getExpandedContent(selectedText, cursorPosition);

		// NOTE: null is returned as the content when a user cancels a
		// snippet that uses a dialog to fill in values
		if (content != null)
		{
			final int contentLength = content.length();

			try
			{
				// replace the current selection
				doc.replace(selectionOffset, selectionLength, content);

				final IWorkbench workbench = PlatformUI.getWorkbench();
				Display display = workbench.getDisplay();

				display.asyncExec(new Runnable()
				{
					public void run()
					{
						if (cursorPosition.cursorPosition != -1)
						{
							((IUnifiedEditor) editor).getViewer().getTextWidget().setCaretOffset(
									selectionOffset + cursorPosition.cursorPosition);
						}
						else
						{
							editor.selectAndReveal(selectionOffset, contentLength);
						}
					}
				});

			}
			catch (BadLocationException e)
			{
				// e.printStackTrace();
			}
		}
	}

	/**
	 * @return tooltip
	 */
	public String getTooltip()
	{
		String string = this._metadata.get(TOOLTIP_KEY);

		return string != null ? string : getName();
	}

}
