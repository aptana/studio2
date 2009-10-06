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
package com.aptana.ide.editors.junit.unified.contentassist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.editor.css.contentassist.CSSCompletionProposal;
import com.aptana.ide.editor.html.contentassist.HTMLCompletionProposal;
import com.aptana.ide.editor.js.contentassist.JSCompletionProposal;
import com.aptana.ide.editors.junit.ContentAssistTestCase;
import com.aptana.ide.editors.junit.ContentAssistTestSuite;
import com.aptana.ide.editors.junit.EditorsJunitPlugin;
import com.aptana.ide.editors.junit.ProfileFile;
import com.aptana.ide.editors.junit.TestTextViewer;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.profiles.Profile;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.LongIdleFileChangedNotifier;
import com.aptana.ide.editors.unified.contentassist.IContentAssistProcessorFactory;
import com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal;
import com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor;
import com.thoughtworks.xstream.XStream;

/**
 * AbstractContentAssistProcessorTest
 * @author Ingo Muschenetz
 *
 */
public abstract class AbstractContentAssistProcessorTest extends TestCase
{
	/**
	 * AbstractContentAssistProcessorTest
	 */
	public AbstractContentAssistProcessorTest()
	{
		TestUtils.loadEnvironment();
		LongIdleFileChangedNotifier.LONG_IDLE_DELAY = 100;
		ProfileManager.APPLY_PROFILE_DELAY = 100;
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.html.contentassist.HTMLContentAssistProcessor.computeCompletionProposals(ITextViewer,
	 * int)'
	 * @param testResource 
	 * @param factory 
	 * @param cpFactory 
	 */
	public void computeCompletionProposals(String testResource, IFileServiceFactory factory,
			IContentAssistProcessorFactory cpFactory)
	{

		XStream xstream = new XStream();
		xstream.alias("testSuite", ContentAssistTestSuite.class); //$NON-NLS-1$
		xstream.alias("testCase", ContentAssistTestCase.class); //$NON-NLS-1$
		xstream.alias("HTMLCompletionProposal", HTMLCompletionProposal.class); //$NON-NLS-1$
		xstream.alias("JSCompletionProposal", JSCompletionProposal.class); //$NON-NLS-1$
		xstream.alias("CSSCompletionProposal", CSSCompletionProposal.class); //$NON-NLS-1$
		xstream.alias("ProfileFile", ProfileFile.class); //$NON-NLS-1$

		InputStream input = this.getClass().getResourceAsStream(testResource);
		ContentAssistTestSuite suite = (ContentAssistTestSuite) xstream.fromXML(input);
		try
		{
			input.close();
		}
		catch (IOException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}

		for (int i = 0; i < suite.testCases.size(); i++)
		{
			ContentAssistTestCase tc = (ContentAssistTestCase) suite.testCases.get(i);
			performTestCase(tc, factory, cpFactory);
		}

	}

	private void performTestCase(ContentAssistTestCase tc, IFileServiceFactory factory,
			IContentAssistProcessorFactory cpFactory)
	{

		String documentSource = tc.documentSource;
		int activationOffset = tc.offset;

		if (tc.ignoreTest)
		{
			Trace.info("Skipping " + tc.description); //$NON-NLS-1$
			return;
		}

		Trace.info("Running " + tc.description); //$NON-NLS-1$

		if (documentSource.indexOf("%%") >= 0) //$NON-NLS-1$
		{
			activationOffset = documentSource.indexOf("%%"); //$NON-NLS-1$
			documentSource = documentSource.replaceAll("\\%\\%", StringUtils.EMPTY); //$NON-NLS-1$ 
		}

		File file = TestUtils.createFileFromString("test", "." + tc.fileExtension, documentSource); //$NON-NLS-1$ //$NON-NLS-2$

		ProfileManager pm = TestUtils.createProfileManager();

		FileSourceProvider fsp = new FileSourceProvider(file);
		FileService fileService = factory.createFileService(fsp);
		FileContextManager.add(fsp.getSourceURI(), fileService);

		IUnifiedContentAssistProcessor cp = cpFactory.getContentAssistProcessor(new EditorFileContext(fileService));
		TestTextViewer viewer = new TestTextViewer(documentSource);

		Profile p = TestUtils.createProfile("test", file, new File[] { file }); //$NON-NLS-1$
		pm.setCurrentProfile(p);

		fileService.forceContentChangedEvent();

		try
		{
			// FIXME This is awful. This makes the tests run much slower. can we force this stuff to happen, or get notified via an event, or skip it altogether for some languages?
			Thread.sleep(2 * (ProfileManager.APPLY_PROFILE_DELAY + LongIdleFileChangedNotifier.LONG_IDLE_DELAY));
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}

		fileService.fireContentChangedEvent(StringUtils.EMPTY, activationOffset, 0); 

		try
		{
			Thread.sleep(2 * (ProfileManager.APPLY_PROFILE_DELAY + LongIdleFileChangedNotifier.LONG_IDLE_DELAY));
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}

		// cp.getOffsetMapper().calculateCurrentLexeme(activationOffset);
		if (tc.completionProposals == null || tc.invalidCompletionProposals == null)
		{
			fail("You must define both the completion proposals and invalid completion proposal nodes in XML, even if they are empty"); //$NON-NLS-1$
		}

		ICompletionProposal[] proposals = cp.computeCompletionProposals(viewer, activationOffset,
				tc.activationCharacter);
		if ((proposals == null || proposals.length == 0) && tc.completionProposals.size() > 0)
		{
			fail(tc.description);
		}
		else if (proposals.length > 0 && tc.completionProposals.size() == 0)
		{
			fail(tc.description);
		}

		for (int j = 0; j < tc.completionProposals.size(); j++)
		{
			IUnifiedCompletionProposal foundProposal = null;
			IUnifiedCompletionProposal prop = (IUnifiedCompletionProposal) tc.completionProposals.get(j);
			for (int k = 0; k < proposals.length; k++)
			{
				IUnifiedCompletionProposal testProposal = (IUnifiedCompletionProposal) proposals[k];
				if (testProposal.getDisplayString().equals(prop.getDisplayString()))
				{
					foundProposal = testProposal;
				}
			}

			if (foundProposal == null)
			{
				fail(tc.description + ": Unable to find proposal " + prop.getDisplayString()); //$NON-NLS-1$
			}

			assertEquals(tc.description + ": display string " + prop.getDisplayString(), prop.getDisplayString(), //$NON-NLS-1$
					foundProposal.getDisplayString());
			assertEquals(tc.description + ": replacement string " + prop.getDisplayString(), prop.getReplaceString(), //$NON-NLS-1$
					foundProposal.getReplaceString());
			assertEquals(tc.description + ": replacement length " + prop.getDisplayString(), prop //$NON-NLS-1$
					.getReplacementLength(), foundProposal.getReplacementLength());
			assertEquals(tc.description + ": default selection " + prop.getDisplayString(), prop.isDefaultSelection(), //$NON-NLS-1$
					foundProposal.isDefaultSelection());
		}

		for (int j = 0; j < tc.invalidCompletionProposals.size(); j++)
		{
			IUnifiedCompletionProposal prop = (IUnifiedCompletionProposal) tc.invalidCompletionProposals.get(j);
			for (int k = 0; k < proposals.length; k++)
			{
				IUnifiedCompletionProposal testProposal = (IUnifiedCompletionProposal) proposals[k];
				if (testProposal.getReplaceString().equals(prop.getReplaceString()))
				{
					fail("Found proposal " + testProposal.getDisplayString() + " that should not exist"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
}
