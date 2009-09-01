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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editors.toolbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.editors.unified.UnifiedConfiguration;

/**
 * Toolbar widget for editors
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ToolbarWidget
{

	private CTabFolder types;
	private ToolItem linkItem;
	private Composite buttons;
	private Font toolbarFont;
	private List<String> mimeTypes;
	private List<String> mimeTypeLabels;
	private IPreferenceStore store;
	private IUnifiedEditor editor;
	private String linkPreference;
	private List<Image> images = new ArrayList<Image>();

	/**
	 * TAB_TEXT
	 */
	public static final Color TAB_TEXT = UnifiedColorManager.getInstance().getColor(new RGB(75, 75, 75));
	/**
	 * TOOLBAR_BG
	 */
	public static final Color TOOLBAR_BG = UnifiedColorManager.getInstance().getColor(new RGB(200, 200, 200));
	/**
	 * TAB_SELECTED_COLOR
	 */
	public static final Color TAB_SELECTED_COLOR = UnifiedColorManager.getInstance().getColor(new RGB(175, 175, 175));

	/**
	 * Creates a new toolbar widget for certain languages
	 * 
	 * @param mimeTypes
	 * @param mimeTypeLabels
	 * @param store
	 * @param linkPreference
	 * @param editor
	 */
	public ToolbarWidget(String[] mimeTypes, String[] mimeTypeLabels, IPreferenceStore store, String linkPreference,
			IUnifiedEditor editor)
	{
		this.mimeTypes = new ArrayList<String>();
		this.mimeTypeLabels = new ArrayList<String>();
		this.store = store;
		this.linkPreference = linkPreference;
		this.editor = editor;
		if (mimeTypes != null && mimeTypes.length > 0 && mimeTypeLabels != null && mimeTypeLabels.length > 0
				&& mimeTypes.length == mimeTypeLabels.length)
		{
			for (int i = 0; i < mimeTypes.length; i++)
			{
				this.mimeTypes.add(mimeTypes[i]);
				this.mimeTypeLabels.add(mimeTypeLabels[i]);
			}
		}
	}

	/**
	 * Disposes of resources used by this toolbar widget
	 */
	public void dispose()
	{
		for (int i = 0; i < images.size(); i++)
		{
			((Image) images.get(i)).dispose();
		}
		if (toolbarFont != null)
		{
			toolbarFont.dispose();
		}
	}

	/**
	 * Set visible
	 * 
	 * @param visible -
	 *            true to show
	 */
	public void setVisible(boolean visible)
	{
		if (buttons != null && !buttons.isDisposed())
		{
			buttons.setVisible(visible);
			GridData data = (GridData) buttons.getLayoutData();
			data.exclude = !visible;
		}
	}

	/**
	 * Is the toolbar visible
	 * 
	 * @return - true if visible
	 */
	public boolean isVisible()
	{
		if (buttons != null && !buttons.isDisposed())
		{
			return buttons.isVisible();
		}
		return false;
	}

	/**
	 * Creates the toolbar widget
	 * 
	 * @param parent
	 * @return - toolbar composite
	 */
	public Composite createControl(Composite parent)
	{
		buttons = new Composite(parent, SWT.NONE);
		GridLayout bLayout = new GridLayout(1, true);
		bLayout.marginHeight = 0;
		bLayout.marginWidth = 0;
		buttons.setLayout(bLayout);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		int toolbarsFound = 0;
		
		if (mimeTypes.size() > 1)
		{
			types = new CTabFolder(buttons, SWT.TOP | SWT.FLAT);
			types.setSimple(true);
			types.addPaintListener(new PaintListener()
			{

				public void paintControl(PaintEvent e)
				{
					e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
					e.gc.fillRectangle(0, 0, 1, 2);
					e.gc.fillRectangle(0, 0, 2, 1);
					if (e.width - 2 > -1)
					{
						e.gc.fillRectangle(e.width - 1, 0, 1, 2);
						e.gc.fillRectangle(e.width - 2, 0, 2, 1);
					}
				}

			});
			Composite gradient = new Composite(types, SWT.NONE);
			GridLayout gLayout = new GridLayout(1, true);
			gLayout.marginHeight = 0;
			gLayout.marginWidth = 0;
			gradient.setLayout(gLayout);
			gradient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			gradient.setBackground(TOOLBAR_BG);
			ToolBar tb = new ToolBar(gradient, SWT.FLAT | SWT.WRAP);
			tb.setBackground(TOOLBAR_BG);
			GridData tbData = new GridData(SWT.END, SWT.FILL, true, false);
			tb.setLayoutData(tbData);
			linkItem = new ToolItem(tb, SWT.CHECK);
			linkItem.setImage(UnifiedEditorsPlugin.getImage("icons/link.gif")); //$NON-NLS-1$
			linkItem.setToolTipText(Messages.getString("ToolbarWidget.TTP_LinkWithCursor")); //$NON-NLS-1$
			linkItem.setSelection(store.getBoolean(linkPreference));
			linkItem.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					store.setValue(linkPreference, linkItem.getSelection());
				}

			});
			types.setTopRight(gradient, SWT.FILL);
			types.setBackground(TOOLBAR_BG);
			types.setForeground(TAB_TEXT);
			GridLayout tLayout = new GridLayout(1, true);
			tLayout.marginHeight = 0;
			tLayout.marginWidth = 0;
			types.setLayout(tLayout);
			types.setBorderVisible(true);
			types.setSelectionBackground(TAB_SELECTED_COLOR);
			types.setSelectionForeground(TAB_TEXT);
			types.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			// Font size
			int fontsize = 8;

			// On Mac OS X 8 pt Arial is hard to read in this widget, bump up to 10
			if (Platform.OS_MACOSX.equals(Platform.getOS()))
			{
				fontsize = 10;
			}

			toolbarFont = new Font(types.getDisplay(), "Arial", fontsize, SWT.NONE); //$NON-NLS-1$
			types.setFont(toolbarFont);
			String[] languages = (String[]) this.mimeTypes.toArray(new String[0]);
			String[] labels = (String[]) this.mimeTypeLabels.toArray(new String[0]);
			types.setTabHeight(14);
			for (int i = 0; i < languages.length; i++)
			{
				String mimeType = languages[i];
				List items = ToolBarContributionRegistry.getInstance().getContributions(mimeType);
				toolbarsFound += items.size();
				//If there are no items, don't even create this toolbar.
				if (items.size() > 0) {
					tb = new ToolBar(types, SWT.FLAT | SWT.WRAP);
					tb.setBackground(TOOLBAR_BG);
					tbData = new GridData(SWT.FILL, SWT.FILL, true, false);
					tb.setLayoutData(tbData);
					CTabItem tab = new CTabItem(types, SWT.NONE);
					tab.setData(mimeType);
					tab.setControl(tb);
					tab.setText(labels[i]);

					Iterator iter = items.iterator();
					while (iter.hasNext()) {
						final ToolBarContribution item = (ToolBarContribution) iter
								.next();
						if (item != null) {
							ToolItem tItem = new ToolItem(tb, SWT.PUSH);
							tItem.addSelectionListener(new SelectionAdapter() {

								public void widgetSelected(SelectionEvent e) {
									item.getInstance().execute(editor, ""); //$NON-NLS-1$
								}

							});
							if (item.getIcon() != null) {
								try {
									Image image = item.getIcon().createImage();
									images.add(image);
									tItem.setImage(image);
								} catch (SWTException ex) {
									IdeLog
											.logError(
													UnifiedEditorsPlugin
															.getDefault(),
													StringUtils
															.format(
																	Messages.getString("ToolbarWidget.ERR_UnableToLoadImageForSnippet"), //$NON-NLS-1$
																	item
																			.getText()),
													ex);
								}
							} else {
								tItem.setText(item.getText());
							}
							tItem.setToolTipText(item.getTooltipText());
						}
					}
				}
			}
			

			
			types.setSelection(0);
		}
		else if (mimeTypes.size() == 1)
		{	
			String mimeType = (String) this.mimeTypes.get(0);
			List items = ToolBarContributionRegistry.getInstance().getContributions(mimeType);
			toolbarsFound += items.size();
			//if there are no items, don't even create this toolbar.
			if (items.size() > 0) {
				ToolBar tb = new ToolBar(buttons, SWT.FLAT | SWT.WRAP);
				tb.setBackground(TOOLBAR_BG);
				GridData tbData = new GridData(SWT.FILL, SWT.FILL, true, false);
				tb.setLayoutData(tbData);
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
					final ToolBarContribution item = (ToolBarContribution) iter
							.next();
					if (item != null) {
						ToolItem tItem = new ToolItem(tb, SWT.PUSH);
						tItem.addSelectionListener(new SelectionAdapter() {

							public void widgetSelected(SelectionEvent e) {
								item.getInstance().execute(editor, ""); //$NON-NLS-1$
							}

						});
						if (item.getIcon() != null) {
							try {
								Image image = item.getIcon().createImage();
								images.add(image);
								tItem.setImage(image);
							} catch (SWTException ex) {
								IdeLog.logError(UnifiedEditorsPlugin
										.getDefault(), StringUtils.format(
										Messages.getString("ToolbarWidget.ERR_UnableToLoadImageForSnippet"), //$NON-NLS-1$
										item.getText()), ex);
							}
						} else {
							tItem.setText(item.getText());
						}
						tItem.setToolTipText(item.getTooltipText());
					}
				}
			} 
		}
		if (toolbarsFound == 0) {
			IdeLog.logWarning(UnifiedEditorsPlugin.getDefault(),
					Messages.getString("ToolbarWidget.WRN_NoToolbarContributorsInRegistryForMimeTypes") //$NON-NLS-1$
							+ mimeTypes.toString());
			setVisible(false);
		}
		return buttons;
	}

	/**
	 * Hooks the selection listener to viewer obtained from the editor passed in during instantiation
	 */
	public void hookCursorListener()
	{
		final ISourceViewer viewer = editor.getViewer();
		if (viewer instanceof TextViewer)
		{
			((TextViewer) viewer).addPostSelectionChangedListener(new ISelectionChangedListener()
			{

				public void selectionChanged(SelectionChangedEvent event)
				{
					ITextSelection selection = (ITextSelection) event.getSelection();
					int offset = selection.getOffset();
					if (linkItem != null && linkItem.getSelection())
					{
						if (types != null)
						{
							try
							{
								String type = TextUtilities.getContentType(viewer.getDocument(),
										UnifiedConfiguration.UNIFIED_PARTITIONING, offset, true);
								CTabItem[] items = types.getItems();
								for (int i = 0; i < items.length; i++)
								{
									if (items[i].getData().equals(type))
									{
										types.setSelection(items[i]);
										break;
									}
								}
							}
							catch (BadLocationException e)
							{
								// Do nothing
							}
						}
					}
				}

			});
		}
	}

}
