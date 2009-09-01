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
package com.aptana.ide.editor.css.contentassist;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.CSSOffsetMapper;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * 
 */
public class CSSContextInformationValidator implements IContextInformationValidator, IContextInformationPresenter
{
	// private CSSOffsetMapper offsetMapper;

	private static final String BLACK_COLOR = "black"; //$NON-NLS-1$

	private static final String COLON = ":"; //$NON-NLS-1$
	
	/** The code assist processor. */
	private CSSContentAssistProcessor fProcessor;
	/** The context information to be validated. */
	private IContextInformation fContextInformation;

	/**
	 * Validates the info window, can add styling.
	 * 
	 * @param processor
	 */
	public CSSContextInformationValidator(CSSContentAssistProcessor processor)
	{
		fProcessor = processor;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#install(org.eclipse.jface.text.contentassist.IContextInformation,
	 *      org.eclipse.jface.text.ITextViewer, int)
	 */
	public void install(IContextInformation contextInformation, ITextViewer viewer, int offset)
	{
		fContextInformation = contextInformation;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#isContextInformationValid(int)
	 */
	public boolean isContextInformationValid(int offset)
	{
		if (fProcessor.initalPopup)
		{
			fProcessor.initalPopup = false;
			return true;
		}
		else
		{
			return fProcessor.getCSSOffsetMapper().getMode(offset).equalsIgnoreCase(CSSOffsetMapper.MODE_INVOKING);
		}
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
	 *      org.eclipse.jface.text.TextPresentation)
	 */
	public boolean updatePresentation(int offset, TextPresentation presentation)
	{
		try
		{
			String s = this.fContextInformation.getInformationDisplayString();
			if (s.indexOf(COLON) == -1)
			{
				return false;
			}

			int colonPos = s.indexOf(COLON);

			RGB blackColor = new RGB(0, 0, 0);
			ColorRegistry cm = JFaceResources.getColorRegistry();
			cm.put(BLACK_COLOR, blackColor);
			Color norm = cm.get(BLACK_COLOR); // make this settable

			StyleRange st = new StyleRange(0, colonPos, norm, null, SWT.BOLD);

			presentation.clear();
			presentation.addStyleRange(st);

			return true;
		}
		catch (Exception e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.CSSContextInformationValidator_ErrorUpdatingContextInformation, e);
			return true;
		}
	}

}
