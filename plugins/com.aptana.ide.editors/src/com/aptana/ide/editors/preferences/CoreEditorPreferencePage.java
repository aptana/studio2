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
package com.aptana.ide.editors.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.UnifiedColorManager;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class CoreEditorPreferencePage extends EditorPreferencePage
{
	/**
	 * Main editor area
	 */
	protected Composite editorArea;

	private Cursor hand;

	private Cursor arrow;

	private Composite associations;

	private Composite parentVisible;

	private PreferenceMastHead header;

	/**
	 * CoreEditorsPreferencePage
	 * 
	 * @param style
	 */
	protected CoreEditorPreferencePage(int style)
	{
		super(style);
	}

	/**
	 * Returns an image for this editor (assumed 48x48)
	 * 
	 * @return - editor image
	 */
	protected ImageDescriptor getEditorImage()
	{
		return UnifiedEditorsPlugin.getImageDescriptor("images/blank_file.png"); //$NON-NLS-1$
	}

	/**
	 * Returned the id for this editor (used to lookup file associations)
	 * 
	 * @return - string editor id
	 */
	protected abstract String getEditorId();

	/**
	 * Gets editor description text to display near the image
	 * 
	 * @return - description of editor
	 */
	protected abstract String getEditorDescription();

	/**
	 * Creates the editor descriptor area
	 */
	public void createEditorDescriptorArea()
	{
		editorArea = new Composite(getFieldEditorParent(), SWT.NONE);
		hand = new Cursor(getShell().getDisplay(), SWT.CURSOR_HAND);
		arrow = new Cursor(getShell().getDisplay(), SWT.CURSOR_ARROW);
		GridLayout rootLayout = new GridLayout(1, true);
		rootLayout.marginHeight = 0;
		rootLayout.marginWidth = 0;
		editorArea.setLayout(rootLayout);
		editorArea.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
		GridData gdArea = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdArea.horizontalSpan = 2;
		editorArea.setLayoutData(gdArea);
		final ImageDescriptor desc = getEditorImage();
		String editorDesc = getEditorDescription();
		header = new PreferenceMastHead(editorArea, editorDesc, 3, desc);
		final Composite top = header.getControl();
		Composite parent = top.getParent();
		int parents = 0;
		while (parent != null && parents <= 3)
		{
			parent = parent.getParent();
			parents++;
		}
		if (parent != null)
		{
			parentVisible = parent;
		}
		top.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (top.isVisible())
				{
					if (parentVisible != null)
					{
						GC gc = new GC(parentVisible);
						gc.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
						gc.fillRectangle(associations.getLocation().x, associations.getLocation().y, parentVisible
								.getSize().x, associations.getSize().y + 5);
						gc.dispose();
					}
				}
			}

		});

		associations = new Composite(editorArea, SWT.NONE);
		associations.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
		GridLayout assLayout = new GridLayout(2, false);
		assLayout.marginBottom = 5;
		assLayout.marginHeight = 0;
		associations.setLayout(assLayout);
		associations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		String id = getEditorId();
		StyledText text = new StyledText(associations, SWT.SINGLE);
		String base = Messages.CoreEditorPreferencePage_LBL_FileAssociations;
		String fileString = ""; //$NON-NLS-1$
		text.setEditable(false);
		text.setText(fileString);
		text.setCursor(arrow);
		text.setForeground(PreferenceMastHead.FOOTER_FG_COLOR);
		text.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
		final Composite pencil = new Composite(associations, SWT.NONE);
		pencil.addMouseListener(new MouseAdapter()
		{

			public void mouseDown(MouseEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage("org.eclipse.ui.preferencePages.FileEditors", //$NON-NLS-1$
						null);
			}
		});
		pencil.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
		GridLayout pencilLayout = new GridLayout(1, true);
		pencilLayout.marginWidth = 0;
		pencilLayout.marginHeight = 0;
		pencil.setLayout(pencilLayout);
		pencil.setCursor(hand);
		GridData gdp = new GridData(SWT.FILL, SWT.FILL, false, false);
		pencil.setLayoutData(gdp);
		StyledText link = new StyledText(pencil, SWT.NONE);
		link.setCursor(hand);
		link.setBackground(PreferenceMastHead.FOOTER_BG_COLOR);
		link.setText(Messages.CoreEditorPreferencePage_LBL_edit);
		link.setEditable(false);
		Color linkBlue = UnifiedColorManager.getInstance().getColor(new RGB(200, 200, 255));
		StyleRange lbraceRange = new StyleRange(0, 1, linkBlue, null);
		StyleRange editRange = new StyleRange(1, 4, linkBlue, null);
		editRange.underline = true;
		StyleRange rbraceRange = new StyleRange(5, 1, linkBlue, null);
		link.setStyleRange(lbraceRange);
		link.setStyleRange(editRange);
		link.setStyleRange(rbraceRange);
		link.setToolTipText(Messages.CoreEditorPreferencePage_TTP_EditFileAssociations);
		GridData editData = new GridData(SWT.FILL, SWT.FILL, false, false);
		link.setLayoutData(editData);
		link.addMouseListener(new MouseAdapter()
		{

			public void mouseDown(MouseEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage("org.eclipse.ui.preferencePages.FileEditors", //$NON-NLS-1$
						null);
			}
		});
		if (id != null)
		{
			String[] fileAssociations = this.getFileAssociations(id);
			
			fileString = StringUtils.join(", ", fileAssociations); //$NON-NLS-1$
			text.setText(base + " " + fileString); //$NON-NLS-1$
			
			StyleRange range = new StyleRange(0, base.length(), PreferenceMastHead.FOOTER_FG_COLOR, null);
			range.fontStyle = SWT.BOLD;
			text.setStyleRange(range);
			
			range = new StyleRange(base.length(), fileString.length(), PreferenceMastHead.FOOTER_FG_COLOR, null);
			text.setStyleRange(range);
		}
	}

	/**
	 * getFileAssociations
	 * 
	 * @param id
	 * @return
	 */
	protected String[] getFileAssociations(String id)
	{
		IEditorRegistry registry = (IEditorRegistry) WorkbenchPlugin.getDefault().getEditorRegistry();
		IFileEditorMapping[] array = registry.getFileEditorMappings();
		List associations = new ArrayList();
		
		for (int i = 0; i < array.length; i++)
		{
			IFileEditorMapping mapping = (IFileEditorMapping) array[i];
			
			if (mapping.getDefaultEditor() != null && mapping.getDefaultEditor().getId().equals(id))
			{
				associations.add(mapping.getLabel());
			}
		}
		
		return (String[]) associations.toArray(new String[associations.size()]);
	}
	
	/**
	 * @see com.aptana.ide.editors.preferences.EditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors()
	{
		createEditorDescriptorArea();
		super.createFieldEditors();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		if (hand != null && !hand.isDisposed())
		{
			hand.dispose();
		}
		if (arrow != null && !arrow.isDisposed())
		{
			arrow.dispose();
		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		if (header != null)
		{
			header.setVisible(visible);
		}
		super.setVisible(visible);
	}

}
