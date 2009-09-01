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
package com.aptana.ide.editors.unified.hover;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.UnifiedInformationControl;
import com.aptana.ide.editors.unified.UnifiedInformationProvider;
import com.aptana.ide.editors.unified.utils.HTMLTextPresenter;

/**
 * Computes the information to be shown in a hover popup which appears on top of the text viewer's text widget when a
 * hover event occurs. This is extended from TextHover to understand the concept of IFileLanguageService and Lexemes
 * 
 * @author Ingo Muschenetz
 */
public class LexemeTextHover extends UnifiedInformationProvider implements ITextHoverExtension, ITextHover
{
	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of LexemeTextHover
	 * 
	 * @param fileService
	 */
	public LexemeTextHover(EditorFileContext fileService)
	{
		super(null, fileService);
	}

	/**
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new UnifiedInformationControl(parent, SWT.TOOL, SWT.NONE,
				// TODO: read from prefs
						new HTMLTextPresenter(false), Messages.LexemeTextHover_PressHelpAndFocus);
			}
		};
	}

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		return super.getSubject(textViewer, offset);
	}

	/**
	 * Get the associated hover information
	 * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		String info = getContributedHoverInfo(textViewer, hoverRegion);

		if (info != null && info.length() > 0)
		{
			return info;
		}

		return super.getInformation(textViewer, hoverRegion);
	}

	/** XXX: Debug hover temporary solution ? */
	private String getContributedHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		ITextHover hover = (ITextHover) getContributedAdapter(ITextHover.class);

		if (hover != null)
		{
			return hover.getHoverInfo(textViewer, hoverRegion);
		}

		return null;
	}

	private Object getContributedAdapter(Class clazz)
	{
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName()))
		{
			adapter = manager.getAdapter(this, clazz.getName());
			if (adapter == null)
			{
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}

}
