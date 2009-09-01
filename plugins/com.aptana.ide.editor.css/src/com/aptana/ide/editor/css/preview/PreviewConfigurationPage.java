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
package com.aptana.ide.editor.css.preview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.editor.css.BrowserExtensionLoader;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.MultiPageCSSEditor;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.ContributedBrowser;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewConfigurationPage
{

	private Composite displayArea;
	private Composite browserArea;
	private Composite editArea;

	private Button currentPageButton;
	private Button startUrlButton;
	private Text startUrlText;
	private Button internalServerButton;
	private Button customServerButton;
	private Text baseUrlText;
	private Button addProjectNameButton;

	private Button save;
	private Button cancel;

	private String title = Messages.PreviewConfigurationPage_Title;
	private String browserLabel = ""; //$NON-NLS-1$

	private Label nameLabel;
	private Text nameText;

	private ContributedBrowser browser;
	private MultiPageCSSEditor editor;
	private int index = -1;
	private String url;

	/**
	 * Creates a new preview configuration page
	 * 
	 * @param editor
	 */
	public PreviewConfigurationPage(MultiPageCSSEditor editor)
	{
		this.editor = editor;
		this.url = this.editor.getURL();
	}

	/**
	 * Sets the index of this preview page
	 * 
	 * @param index -
	 *            index of page
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	/**
	 * Creates the preview page control
	 * 
	 * @param parent -
	 *            parent of preview page
	 */
	public void createControl(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(1, false);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.editArea = new Composite(displayArea, SWT.NONE);
		GridLayout eaLayout = new GridLayout(1, false);
		eaLayout.marginWidth = 0;
		eaLayout.marginHeight = 0;
		editArea.setLayout(eaLayout);
		editArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.browserArea = new Composite(displayArea, SWT.NONE);
		GridData baData = new GridData(SWT.FILL, SWT.FILL, true, true);
		baData.exclude = true;
		GridLayout baLayout = new GridLayout(1, false);
		baLayout.marginHeight = 0;
		baLayout.marginWidth = 0;
		this.browserArea.setLayout(baLayout);
		this.browserArea.setLayoutData(baData);
		this.browserArea.setVisible(false);

		createButtonSection(this.editArea);
		createConfigurationSection(this.editArea);
		createBrowserSection(this.editArea);
		createStartActionSection(this.editArea);
		createServerSection(this.editArea);
	}

	/**
	 * Gets the browser label
	 * 
	 * @return - browser label
	 */
	public String getBrowserLabel()
	{
		return this.browserLabel;
	}

	/**
	 * Sets the browser object and its name albe
	 * 
	 * @param browser
	 * @param label
	 */
	public void setBrowser(ContributedBrowser browser, String label)
	{
		Button button = buttons.get(label);
		if (button != null && !button.isDisposed())
		{
			button.setSelection(true);
			Iterator<Button> iter = buttons.values().iterator();
			Button other;
			while (iter.hasNext())
			{
				other = iter.next();
				if (other != button && other != null)
				{
					other.setSelection(false);
				}
			}
		}
		if (this.browser != null)
		{
			this.browser.dispose();
		}
		this.browserLabel = label;
		this.browser = browser;
		this.browser.createControl(browserArea);
	}

	/**
	 * Sets the title of the preview page
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		if (this.nameText != null && !this.nameText.isDisposed())
		{
			this.nameText.setText(title);
		}
		else
		{
			this.title = title;
		}
	}

	/**
	 * Displays the edit area of this preview page
	 */
	public void showEditArea()
	{
		GridData eaData = (GridData) editArea.getLayoutData();
		eaData.exclude = false;
		GridData baData = (GridData) browserArea.getLayoutData();
		baData.exclude = true;
		this.browserArea.setVisible(false);
		this.editArea.setVisible(true);
		displayArea.layout(true, true);
	}

	/**
	 * Displays the browser area of this preview page
	 */
	public void showBrowserArea()
	{
		GridData eaData = (GridData) editArea.getLayoutData();
		eaData.exclude = true;
		GridData baData = (GridData) browserArea.getLayoutData();
		baData.exclude = false;
		this.browserArea.setVisible(true);
		this.editArea.setVisible(false);
		displayArea.layout(true, true);
	}

	/**
	 * Gets the URL for this page
	 * 
	 * @return - string url
	 */
	public String getURL()
	{
		return this.url;
	}

	/**
	 * Sets the url of the preview page
	 * 
	 * @param url
	 */
	public void setURL(String url)
	{
		if (this.browser != null)
		{
			if (currentPageButton.getSelection())
			{
				this.browser.setURL(url);
			}
			else
			{
				this.browser.setURL(startUrlText.getText());
			}
		}
		this.url = url;
	}

	private void save()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		boolean found = false;
		IConfigurationElement[] ce;
		String browserClass;
		String browserName;
		Object obj;
		for (int i = 0; i < extensions.length && !found; i++)
		{
			ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length && !found; j++)
			{
				browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
				browserName = BrowserExtensionLoader.getBrowserLabel(ce[j]);
				if (browserClass != null && browserName != null && browserName.equals(browserLabel))
				{
					found = true;
					try
					{
						obj = ce[j].createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
						if (obj instanceof ContributedBrowser)
						{
							this.setBrowser((ContributedBrowser) obj, browserName);
						}
					}
					catch (CoreException e)
					{
					}

				}
			}
		}
		editor.setPreviewPageText(this.index, this.title);
		setURL(this.url);
		editor.savePreviewsPages();
		showBrowserArea();
	}

	private void createButtonSection(Composite parent)
	{
		Composite buttons = new Composite(parent, SWT.NONE);
		GridLayout bLayout = new GridLayout(2, false);
		bLayout.marginHeight = 0;
		bLayout.marginWidth = 0;
		buttons.setLayout(bLayout);
		GridData bData = new GridData(SWT.FILL, SWT.FILL, true, false);
		buttons.setLayoutData(bData);

		save = new Button(buttons, SWT.PUSH);
		save.setToolTipText(Messages.PreviewConfigurationPage_SaveText);
		save.setImage(CSSPlugin.getImage("icons/save.gif")); //$NON-NLS-1$
		save.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				save();
			}
		});
		save.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		cancel = new Button(buttons, SWT.PUSH);
		cancel.setText(Messages.PreviewConfigurationPage_CancelText);
		cancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				showBrowserArea();
			}
		});
		cancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	private void createConfigurationSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.PreviewConfigurationPage_GroupTitle);

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		nameLabel = new Label(group, SWT.LEFT);
		nameLabel.setText(Messages.PreviewConfigurationPage_NameLabel);

		nameText = new Text(group, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		nameText.setText(title);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				title = nameText.getText();
			}
		});
	}

	private Map<String, Button> buttons = new HashMap<String, Button>();

	private void createBrowserSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.PreviewConfigurationPage_BrowserGroupText);

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		List<String> labels = BrowserExtensionLoader.getAllBrowserLabels();
		String label;
		for (int i = 0; i < labels.size(); i++)
		{
			label = labels.get(i);

			final Button browserButton = new Button(group, SWT.RADIO);
			browserButton.setText(label);
			if (i == 0)
			{
				browserLabel = label;
				browserButton.setSelection(true);
			}
			buttons.put(label, browserButton);
			browserButton.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if (browserButton.getSelection())
					{
						browserLabel = browserButton.getText();
					}
				}
			});
		}
	}

	/**
	 * Gets the title of the preview page
	 * 
	 * @return - string title
	 */
	public String getTitle()
	{
		return title;
	}

	private void createStartActionSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.PreviewConfigurationPage_StartActionGroupText);
		group.setFont(parent.getFont());

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		currentPageButton = new Button(group, SWT.RADIO);
		currentPageButton.setText(Messages.PreviewConfigurationPage_CurrentPageText);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		currentPageButton.setLayoutData(data);
		currentPageButton.setSelection(true);

		startUrlButton = new Button(group, SWT.RADIO);
		startUrlButton.setText(Messages.PreviewConfigurationPage_StartUrlText);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		startUrlButton.setLayoutData(data);

		startUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 1;
		startUrlText.setLayoutData(data);
	}

	private void createServerSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT);
		group.setText(Messages.PreviewConfigurationPage_ServerGroupTitle);
		group.setFont(parent.getFont());

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight *= 2;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		internalServerButton = new Button(group, SWT.RADIO);
		internalServerButton.setText(Messages.PreviewConfigurationPage_InternalServerText);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		data.horizontalSpan = 2;
		internalServerButton.setLayoutData(data);
		internalServerButton.setSelection(true);

		customServerButton = new Button(group, SWT.RADIO);
		customServerButton.setText(Messages.PreviewConfigurationPage_ExternalServerText);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		data.horizontalSpan = 2;
		customServerButton.setLayoutData(data);

		Label baseUrlLabel = new Label(group, SWT.NONE);
		baseUrlLabel.setText(Messages.PreviewConfigurationPage_BaseUrlLabel);
		baseUrlLabel.setAlignment(SWT.RIGHT);
		data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		data.horizontalIndent = 50;
		baseUrlLabel.setLayoutData(data);

		baseUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		baseUrlText.setLayoutData(data);

		Label dummyLabel = new Label(group, SWT.NONE);
		dummyLabel.setLayoutData(new GridData());

		addProjectNameButton = new Button(group, SWT.CHECK);
		addProjectNameButton.setText(Messages.PreviewConfigurationPage_AddProjectText);
		addProjectNameButton.setLayoutData(new GridData());
	}

	/**
	 * Gets the control of the preview page
	 * 
	 * @return - control
	 */
	public Control getControl()
	{
		return this.displayArea;
	}

	/**
	 * Disposes this page
	 */
	public void dispose()
	{
		if (this.browser != null)
		{
			this.browser.dispose();
		}
	}
}
