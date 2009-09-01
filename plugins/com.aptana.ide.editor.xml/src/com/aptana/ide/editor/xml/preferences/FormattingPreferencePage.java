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
package com.aptana.ide.editor.xml.preferences;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.internal.ui.dialogs.PreferencesAccess;
import com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterConfigurationBlock;
import com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterPreferencePage;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterMessages;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileConfigurationBlock;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileManager;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileStore;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileManager.Profile;

/**
 * @author Ingo Muschenetz
 */
public class FormattingPreferencePage extends CodeFormatterPreferencePage implements IWorkbenchPreferencePage
{

	private static final String PREVIEW = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //$NON-NLS-1$
			"\r\n" //$NON-NLS-1$
			+ "<rdf:RDF\r\n" //$NON-NLS-1$
			+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n" //$NON-NLS-1$
			+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n" //$NON-NLS-1$
			+ "xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\"\r\n" //$NON-NLS-1$
			+ "xmlns:admin=\"http://webns.net/mvcb/\"\r\n" //$NON-NLS-1$
			+ "xmlns:cc=\"http://web.resource.org/cc/\"\r\n" //$NON-NLS-1$
			+ "xmlns=\"http://purl.org/rss/1.0/\">\r\n" //$NON-NLS-1$
			+ "\r\n" //$NON-NLS-1$
			+ "<channel rdf:about=\"http://weblogs.java.net/blog/editors/\">\r\n" //$NON-NLS-1$
			+ "<title>Editor&apos;s Daily Blog</title>\r\n" //$NON-NLS-1$
			+ "<link>http://weblogs.java.net/blog/editors/</link>\r\n" //$NON-NLS-1$
			+ "<description>A daily update from our java.net editor, Chris Adamson, and other items from the java.net front page.</description>\r\n" //$NON-NLS-1$
			+ "<dc:language>en-us</dc:language>\r\n" //$NON-NLS-1$
			+ "<dc:creator></dc:creator>\r\n" //$NON-NLS-1$
			+ "<dc:date>2007-10-12T10:48:50+00:00</dc:date>\r\n" //$NON-NLS-1$
			+ "<admin:generatorAgent rdf:resource=\"http://www.movabletype.org/?v=3.01D\" />\r\n" //$NON-NLS-1$
			+ "\r\n" //$NON-NLS-1$
			+ "\r\n" //$NON-NLS-1$
			+ "<items>\r\n" //$NON-NLS-1$
			+ "<rdf:Seq><rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/hammer_and_a_na.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/lay_my_head_dow.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/power_of_two.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/run_1.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/closer_to_fine.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/more_adventurou.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/does_he_love_yo.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/its_a_hit.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/close_call.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/go_ahead.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/everyday.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/the_space_betwe.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/the_best_of_wha.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/what_would_you.html\" />\r\n" //$NON-NLS-1$
			+ "<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/out_of_my_hands.html\" />\r\n" //$NON-NLS-1$
			+ "</rdf:Seq>\r\n" + //$NON-NLS-1$
			"</items>\r\n" + //$NON-NLS-1$
			"\r\n" + //$NON-NLS-1$
			"</channel>\r\n" + //$NON-NLS-1$
			"\r\n" + //$NON-NLS-1$
			"</rdf:RDF>"; //$NON-NLS-1$

	/**
	 * 
	 */
	public FormattingPreferencePage()
	{
		super(XMLMimeType.MimeType, XMLPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterPreferencePage#createConfigurationBlock(com.aptana.ide.internal.ui.dialogs.PreferencesAccess)
	 */
	protected ProfileConfigurationBlock createConfigurationBlock(PreferencesAccess access)
	{
		return new CodeFormatterConfigurationBlock(getProject(), access, XMLMimeType.MimeType, XMLPlugin.getDefault()
				.getPreferenceStore(), PREVIEW, XMLPlugin.getDefault().getBundle().getSymbolicName())
		{

			protected ModifyDialog createModifyDialog(Shell shell, Profile profile, ProfileManager profileManager,
					ProfileStore profileStore, boolean newProfile)
			{

				return new FormatterModifyDialog(shell, profile, profileManager, profileStore, newProfile,
						FORMATTER_DIALOG_PREFERENCE_KEY, DIALOGSTORE_LASTSAVELOADPATH, XMLMimeType.MimeType)
				{

					protected void addPages(Map values)
					{
						addTabPage(FormatterMessages.ModifyDialog_tabpage_control_statements_title,
								new IndentationTabPage(this, values, XMLMimeType.MimeType));

						// addTabPage(FormatterMessages.ModifyDialog_tabpage_indentation_title, new
						// IndentationTabPage(this, values,PHPMimeType.MimeType));
						// addTabPage(FormatterMessages.ModifyDialog_tabpage_braces_title, new BracesTabPage(this,
						// values,PHPMimeType.MimeType));
						// addTabPage(FormatterMessages.ModifyDialog_tabpage_whitespace_title, new
						// WhiteSpaceTabPage(this, values,PHPMimeType.MimeType));
						addTabPage(FormatterMessages.ModifyDialog_tabpage_new_lines_title, new NewLinesTabPage(this,
								values, XMLMimeType.MimeType));
						addTabPage(FormatterMessages.FormattingPreferencePage_BLANK_LINES_WHITESPACES_TITLE,
								new BlankLinesAndWhiteSpacesTabPage(this, values, XMLMimeType.MimeType));
						// addTabPage(FormatterMessages.ModifyDialog_tabpage_control_statements_title, new
						// ControlStatementsTabPage(this, values,PHPMimeType.MimeType));
						// addTabPage(FormatterMessages.ModifyDialog_tabpage_comments_title, new CommentsTabPage(this,
						// values,PHPMimeType.MimeType));
					}

				};
			};

		};
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPreferencePageID()
	 */
	protected String getPreferencePageID()
	{
		return "com.aptana.ide.editor.xml.preferences.FormattingPreferencePage"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPropertyPageID()
	 */
	protected String getPropertyPageID()
	{
		return "com.aptana.xml.ui.propertyPages.CodeFormatterPreferencePage"; //$NON-NLS-1$
	}
}
