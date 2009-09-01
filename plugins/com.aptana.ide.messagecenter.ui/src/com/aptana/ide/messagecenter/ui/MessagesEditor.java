/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.messagecenter.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.editors.preferences.PreferenceMastHead;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.intro.messaging.Message;
import com.aptana.ide.messagecenter.MessageCenterPlugin;
import com.aptana.ide.messagecenter.core.IMessageListener;
import com.aptana.ide.messagecenter.core.MessagingManager;
import com.aptana.ide.messagecenter.preferences.IPreferenceConstants;

/**
 * Aptana Message Center Editor.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class MessagesEditor extends EditorPart
{
	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.messagecenter.ui.messageEditor"; //$NON-NLS-1$

	/**
	 * MESSAGE_MARKER
	 */
	public static final String MESSAGE_MARKER = "<!-- Put Message Here -->"; //$NON-NLS-1$

	/**
	 * TITLE_MARKER
	 */
	public static final String TITLE_MARKER = "<!-- Put Title Here -->"; //$NON-NLS-1$

	private static final IEditorInput INPUT = new IEditorInput()
	{
		public Object getAdapter(Class adapter)
		{
			return null;
		}

		public String getToolTipText()
		{
			return Messages.MessagesEditor_Tooltip;
		}

		public IPersistableElement getPersistable()
		{
			return null;
		}

		public String getName()
		{
			return Messages.MessagesEditor_Name;
		}

		public ImageDescriptor getImageDescriptor()
		{
			return MessageUIPlugin.getImageDescriptor("icons/aptana_envelope.png"); //$NON-NLS-1$
		}

		public boolean exists()
		{
			return false;
		}

	};

	/**
	 * Opens the message center
	 */
	public static void openMessageCenter()
	{
		try
		{
			IWorkbench wb = MessageUIPlugin.getDefault().getWorkbench();
			if (wb != null)
			{
				IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
				if (ww != null && ww.getActivePage() != null)
				{
					IDE.openEditor(ww.getActivePage(), INPUT, MessagesEditor.ID);
				}
			}
		}
		catch (PartInitException e)
		{
			IdeLog.logError(MessageUIPlugin.getDefault(), Messages.MessagesEditor_Error_Opening, e);
		}
	}

	private Color MAIN_BG = UnifiedColorManager.getInstance().getColor(new RGB(225, 225, 225));
	private Color HEADER_BG = UnifiedColorManager.getInstance().getColor(new RGB(110, 110, 110));
	private Color HEADER_FG = UnifiedColorManager.getInstance().getColor(new RGB(196, 196, 196));

	private Composite header;
	private Composite displayArea;
	private SashForm sides;
	private ToolBar toolbar;
	private TableViewer messages;
	private Composite messagePreview;
	private Browser preview;
	private Font standard;
	private Font bold;
	private File previewFile;
	private ToolItem markAsNew;
	private ToolItem markAsOld;
	private ToolItem delete;
	private ToolItem showDeleted;
	private ToolItem vertical;
	private ToolItem horizontal;
	private ToolItem refresh;
	private ToolItem configure;
	private String loadingPage;
	private String messageTemplate;
	private Label inbox;
	private Font localFont;

	private IStructuredContentProvider contentProvider = new IStructuredContentProvider()
	{
		private List<Message> messages = null;

		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (newInput instanceof List)
			{
				messages = (List<Message>) newInput;
			}
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			return messages.toArray();
		}

	};

	private class MessagesEditorLabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
		 *      java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Message)
			{
				Message message = (Message) element;
				switch (columnIndex)
				{
					case 0:
						if (message.getTitle() != null)
						{
							return message.getTitle();
						}
						break;
					case 1:
						if (message.getChannelTitle() != null)
						{
							return message.getChannelTitle();
						}
						break;
					case 2:
						if (message.getDate() != null)
						{
							DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
							return df.format(message.getDate());
						}
						break;
				}
			}
			return ""; //$NON-NLS-1$
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (element instanceof Message && columnIndex == 0)
			{
				Message message = (Message) element;
				if (message.isDeleted())
				{
					return MessageUIPlugin.getImage("icons/delete.gif"); //$NON-NLS-1$
				}
				else if (message.isUrgent())
				{
					return MessageUIPlugin.getImage("icons/urgent.gif"); //$NON-NLS-1$
				}
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object,
		 *      int)
		 */
		public Color getBackground(Object element, int columnIndex)
		{
			return MAIN_BG;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object,
		 *      int)
		 */
		public Color getForeground(Object element, int columnIndex)
		{
			if (element instanceof Message && columnIndex == 0)
			{
				Message message = (Message) element;
				if (message.isDeleted())
				{
					if (message.isUrgent())
					{
						return UnifiedColorManager.getInstance().getColor(new RGB(200, 25, 42));
					}
				}
				else if (message.isUrgent())
				{
					return UnifiedColorManager.getInstance().getColor(new RGB(200, 25, 42));
				}
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object,
		 *      int)
		 */
		public Font getFont(Object element, int columnIndex)
		{
			if (element instanceof Message)
			{
				Message message = (Message) element;
				if (message.isRead())
				{
					return standard;
				}
				return bold;
			}
			return null;
		}
	}

	private ITableLabelProvider labelProvider = new MessagesEditorLabelProvider();

	private class MessageTableSorter extends ViewerSorter
	{
		private TableViewer fTableViewer;

		public MessageTableSorter()
		{
		}

		/**
		 * The TableViewer passed in will be set up to use this sorter when a
		 * column is clicked.
		 * 
		 * @param tableViewer
		 */
		public void bind(final TableViewer tableViewer)
		{
			fTableViewer = tableViewer;
			final Table table = fTableViewer.getTable();
			for (int i = 0; i < table.getColumnCount(); i++)
			{
				final TableColumn column = table.getColumn(i);
				column.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(final SelectionEvent e)
					{
						TableColumn sortedColumn = table.getSortColumn();
						int direction = table.getSortDirection();
						if (sortedColumn == column)
						{
							// if the column currently sorted is clicked,
							// reverse the direction
							direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
						}
						else
						{
							// moves the sorted column, but keep the direction
							sortedColumn = column;
						}
						table.setSortColumn(column);
						table.setSortDirection(direction);
						fTableViewer.refresh();
					}
				});
			}
			fTableViewer.setSorter(this);
		}

		public int compare(Viewer viewer, Object e1, Object e2)
		{
			if (e1 instanceof Message && e2 instanceof Message)
			{
				Message msg1 = (Message) e1;
				Message msg2 = (Message) e2;

				String sortedColumn = fTableViewer.getTable().getSortColumn().getText();
				int direction = messages.getTable().getSortDirection();
				if (sortedColumn.equals(Messages.MessagesEditor_Column_Date))
				{
					// for date column, sort using their values instead of
					// displayed timestamps
					if (direction == SWT.DOWN)
					{
						return msg2.getDate().compareTo(msg1.getDate());
					}
					return msg1.getDate().compareTo(msg2.getDate());
				}
			}
			return super.compare(viewer, e1, e2);
		}
	}

	private MessageTableSorter sorter = new MessageTableSorter();

	private IMessageListener listener = new IMessageListener()
	{

		public void messageChanged(final Message message, final int eventType)
		{
			UIJob job = new UIJob("Updating message editor") //$NON-NLS-1$
			{

				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (messages == null || messages.getTable().isDisposed())
					{
						MessagingManager.removeListener(listener);
						return Status.CANCEL_STATUS;
					}
					messages.update(message, null);
					TableItem[] items = messages.getTable().getItems();
					boolean exists = false;
					for (int i = 0; i < items.length; i++)
					{
						if (message.equals(items[i].getData()))
						{
							exists = true;
							break;
						}
					}
					if (!exists && (showDeleted.getSelection() || !message.isDeleted()) || message.isPurged())
					{
						populateMessageTable();
					}
					return Status.OK_STATUS;
				}

			};
			job.setSystem(true);
			job.schedule();

		}
	};

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		// No saving for this editor
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs()
	{
		// No saving as for this editor
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		if (localFont != null && !localFont.isDisposed())
		{
			localFont.dispose();
		}
		if (listener != null)
		{
			MessagingManager.removeListener(listener);
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		Bundle bundle = Platform.getBundle(MessageUIPlugin.PLUGIN_ID);
		URL contentUrl = bundle.getEntry("/content"); //$NON-NLS-1$
		try
		{
			FileLocator.toFileURL(contentUrl);
		}
		catch (IOException e1)
		{
			// Do nothing
		}
		URL loadingUrl = bundle.getEntry("/content/loading_message.html"); //$NON-NLS-1$
		try
		{
			loadingUrl = FileLocator.toFileURL(loadingUrl);
			loadingPage = loadingUrl.toExternalForm();
		}
		catch (IOException e1)
		{
			loadingPage = null;
		}

		// Create file to save message HTML to before setting browser URL
		String newFileName = FileUtils.getRandomFileName("message", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
		previewFile = new File(FileUtils.systemTempDir + File.separator + newFileName);
		previewFile.deleteOnExit();

		URL messageUrl = bundle.getEntry("/content/message.html"); //$NON-NLS-1$
		try
		{
			messageUrl = FileLocator.toFileURL(messageUrl);
			messageTemplate = FileUtils.readContent(new File(messageUrl.getFile()));
		}
		catch (IOException e1)
		{
			messageTemplate = ""; //$NON-NLS-1$
		}

		// Fonts used in table
		standard = new Font(parent.getDisplay(), "Arial", 10, SWT.NONE); //$NON-NLS-1$
		bold = new Font(parent.getDisplay(), "Arial", 10, SWT.BOLD); //$NON-NLS-1$

		displayArea = new Composite(parent, SWT.NONE);
		GridLayout daLayout = new GridLayout(2, false);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		displayArea.setBackground(HEADER_BG);

		header = new Composite(displayArea, SWT.NONE);
		header.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.drawImage(MessageUIPlugin.getImage("icons/message_center_top.png"), 33, 15); //$NON-NLS-1$
			}

		});
		header.setBackground(PreferenceMastHead.HEADER_BG_COLOR);
		GridData pmhData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pmhData.heightHint = MessageUIPlugin.getImage("icons/message_center_top.png").getImageData().height + 20; //$NON-NLS-1$
		pmhData.horizontalSpan = 2;
		header.setLayoutData(pmhData);

		Composite topBar = new Composite(displayArea, SWT.NONE);
		topBar.setBackground(HEADER_BG);
		GridLayout tbLayout = new GridLayout(3, false);
		tbLayout.marginHeight = 0;
		tbLayout.marginLeft = 33;
		tbLayout.marginWidth = 0;
		topBar.setLayout(tbLayout);
		GridData tbData = new GridData(SWT.FILL, SWT.FILL, true, false);
		tbData.horizontalSpan = 2;
		topBar.setLayoutData(tbData);

		inbox = new Label(topBar, SWT.LEFT);
		inbox.setBackground(HEADER_BG);
		inbox.setText(Messages.MessagesEditor_Inbox_Text);
		inbox.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
		inbox.setForeground(HEADER_FG);
		localFont = new Font(Display.getDefault(), SWTUtils.resizeFont(standard, 2));
		inbox.setFont(localFont);

		Composite toolbarComp = new Composite(displayArea, SWT.NONE);
		toolbarComp.setBackground(HEADER_BG);
		GridLayout tbcLayout = new GridLayout(1, false);
		tbcLayout.marginHeight = 0;
		tbcLayout.marginLeft = 5;
		tbcLayout.marginWidth = 0;
		toolbarComp.setLayout(tbcLayout);
		toolbarComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		markAsOld = new ToolItem(toolbar, SWT.PUSH);
		markAsOld.setImage(MessageUIPlugin.getImage("icons/mail_read.gif")); //$NON-NLS-1$
		markAsOld.setToolTipText(Messages.MessagesEditor_MarkAsRead_Tooltip);
		markAsOld.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] tItems = messages.getTable().getSelection();
				Message curr;
				for (int i = 0; i < tItems.length; i++)
				{
					curr = (Message) tItems[i].getData();
					curr.setRead(true);
					MessagingManager.notifyListeners(curr, IMessageListener.MESSAGE_READ);
				}
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		markAsNew = new ToolItem(toolbar, SWT.PUSH);
		markAsNew.setImage(MessageUIPlugin.getImage("icons/mail_unread.gif")); //$NON-NLS-1$
		markAsNew.setToolTipText(Messages.MessagesEditor_MarkAsUnread_Tooltip);
		markAsNew.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] tItems = messages.getTable().getSelection();
				Message curr;
				for (int i = 0; i < tItems.length; i++)
				{
					curr = (Message) tItems[i].getData();
					curr.setRead(false);
					curr.setDeleted(false);
					MessagingManager.notifyListeners(curr, IMessageListener.MESSAGE_UNREAD);
				}
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		delete = new ToolItem(toolbar, SWT.PUSH);
		delete.setToolTipText(Messages.MessagesEditor_Delete_Tooltip);
		delete.setImage(MessageUIPlugin.getImage("icons/delete.gif")); //$NON-NLS-1$
		delete.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				deleteSelectedMessages();
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		showDeleted = new ToolItem(toolbar, SWT.CHECK);
		showDeleted.setToolTipText(Messages.MessagesEditor_ShowDeleted_Tooltip);
		showDeleted.setImage(MessageUIPlugin.getImage("icons/show_deleted.png")); //$NON-NLS-1$
		showDeleted.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				messages.refresh();
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		horizontal = new ToolItem(toolbar, SWT.PUSH);
		horizontal.setImage(MessageUIPlugin.getImage("icons/horizontal.gif")); //$NON-NLS-1$
		horizontal.setToolTipText(Messages.MessagesEditor_SplitHorizontal_Tooltip);
		horizontal.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				sides.setOrientation(SWT.VERTICAL);
				IEclipsePreferences prefs = getPreferences();
				prefs.put(
                        IPreferenceConstants.MESSAGE_CENTER_ORIENTATION,
                        IPreferenceConstants.HORIZONTAL);
				try {
                    prefs.flush();
                } catch (BackingStoreException bse) {
                }
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		vertical = new ToolItem(toolbar, SWT.NONE);
		vertical.setToolTipText(Messages.MessagesEditor_SplitVertical_Tooltip);
		vertical.setImage(MessageUIPlugin.getImage("icons/vertical.gif")); //$NON-NLS-1$
		vertical.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				sides.setOrientation(SWT.HORIZONTAL);
				IEclipsePreferences prefs = getPreferences();
				prefs.put(
                        IPreferenceConstants.MESSAGE_CENTER_ORIENTATION,
                        IPreferenceConstants.VERTICAL);
				try {
                    prefs.flush();
                } catch (BackingStoreException bse) {
                }
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		refresh = new ToolItem(toolbar, SWT.NONE);
		refresh.setToolTipText(Messages.MessagesEditor_Refresh_Tooltip);
		refresh.setImage(MessageUIPlugin.getImage("icons/refresh.gif")); //$NON-NLS-1$
		refresh.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				MessagingManager.getNewMessages(new Date(0));
			}
		});

		toolbar = new ToolBar(toolbarComp, SWT.FLAT);
		toolbar.setLayout(new GridLayout(1, false));
		toolbar.setBackground(HEADER_BG);
		configure = new ToolItem(toolbar, SWT.NONE);
		configure.setToolTipText(Messages.MessagesEditor_Configure_Tooltip);
		configure.setImage(MessageUIPlugin.getImage("icons/configure.gif")); //$NON-NLS-1$
		configure.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
						.getActiveShell(), "com.aptana.ide.messagecenter.preferences.MessageCenterPreferencePage", //$NON-NLS-1$
						new String[]
							{ "com.aptana.ide.messagecenter.preferences.MessageCenterPreferencePage" }, //$NON-NLS-1$
						null);
				dialog.open();
			}
		});

		String value = Platform.getPreferencesService().getString(
                MessageCenterPlugin.PLUGIN_ID,
                IPreferenceConstants.MESSAGE_CENTER_ORIENTATION,
                IPreferenceConstants.HORIZONTAL, null);
		int orientation = value.equals(IPreferenceConstants.HORIZONTAL) ? SWT.VERTICAL
				: SWT.HORIZONTAL;
		sides = new SashForm(displayArea, orientation);
		GridLayout sLayout = new GridLayout(2, false);
		sLayout.marginHeight = 0;
		sLayout.marginWidth = 0;
		sides.setLayout(sLayout);
		sides.setBackground(HEADER_FG);
		sides.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		messages = new TableViewer(sides, SWT.MULTI | SWT.FULL_SELECTION);
		messages.setLabelProvider(labelProvider);
		messages.setContentProvider(contentProvider);
		messages.setSorter(sorter);
		messages.getTable().setHeaderVisible(true);
		messages.getTable().setBackground(MAIN_BG);
		messages.getTable().setLinesVisible(true);
		messages.getTable().setFont(bold);
		messages.getTable().addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					if (Platform.getOS().equals(Platform.OS_WIN32))
					{
						int clientWidth = messages.getTable().getClientArea().width;
						Color oldBg = event.gc.getBackground();
						event.gc.setBackground(HEADER_FG);
						RGB converted = new RGB(0, 0, 0);
						converted.red = Math.abs(HEADER_BG.getRed() - HEADER_FG.getRed());
						converted.blue = Math.abs(HEADER_BG.getBlue() - HEADER_FG.getBlue());
						converted.green = Math.abs(HEADER_BG.getGreen() - HEADER_FG.getGreen());
						event.gc.setForeground(UnifiedColorManager.getInstance().getColor(converted));
						event.gc.fillRectangle(0, event.y, clientWidth, event.height);
						event.gc.setBackground(oldBg);
						event.detail &= ~SWT.SELECTED;
					}
				}
			}
		});
		messages.getTable().addControlListener(new ControlAdapter()
		{
			public void controlResized(ControlEvent e)
			{
				int[] weights = sides.getWeights();
				if (weights != null && weights.length == 2)
				{
					int total = weights[0] + weights[1];
					double percentage = ((double) weights[0] / total) * 100;
					if (percentage > 0 && percentage < 100)
					{
                        IEclipsePreferences prefs = getPreferences();
                        prefs
                                .putInt(
                                        IPreferenceConstants.MESSAGE_CENTER_LEFT_WEIGHT,
                                        (int) percentage);
                        try {
                            prefs.flush();
                        } catch (BackingStoreException bse) {
                        }
                    }
				}
			}
		});
		messages.getTable().addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.DEL)
				{
					deleteSelectedMessages();
				}
				else if ((Platform.getOS().equals(Platform.OS_MACOSX) && (e.stateMask == SWT.COMMAND))
						|| (e.stateMask == SWT.CTRL))
				{
					if (((char) e.keyCode) == 'a')
					{
						messages.getTable().selectAll();
					}
				}
			}
		});

		final TableColumn tc1 = new TableColumn(messages.getTable(), SWT.LEFT);
		tc1.setText(Messages.MessagesEditor_Column_Subject);
		final TableColumn tc2 = new TableColumn(messages.getTable(), SWT.LEFT);
		tc2.setText(Messages.MessagesEditor_Column_Category);
		tc2.setWidth(275);
		final TableColumn tc3 = new TableColumn(messages.getTable(), SWT.LEFT);
		tc3.setText(Messages.MessagesEditor_Column_Date);
		tc3.setWidth(175);

		messages.getTable().setSortColumn(tc3);
		messages.getTable().setSortDirection(SWT.DOWN);
		sorter.bind(messages);

		messages.getTable().addControlListener(new ControlAdapter()
		{
			boolean firstResize = true;

			public void controlResized(ControlEvent e)
			{
				if (firstResize)
				{
					firstResize = false;
					TableColumn c = messages.getTable().getColumn(0);
					TableColumn c2 = messages.getTable().getColumn(1);
					TableColumn c3 = messages.getTable().getColumn(2);

					// Linux fix since the last column of the table auto-grows
					// but the logic should be that the firt
					// column auto-grows
					if (CoreUIUtils.onLinux)
					{
						c2.setWidth(275);
						c3.setWidth(175);
					}

					Point size = messages.getTable().getSize();

					// Mac/Linux fix for always having a vertical scrollbar and
					// not calculating it affects the
					// horizontal scroll bar
					if (CoreUIUtils.onMacOSX || CoreUIUtils.onLinux)
					{
						ScrollBar vScrolls = messages.getTable().getVerticalBar();
						if (vScrolls != null)
						{
							size.x = size.x - vScrolls.getSize().x - 5;
						}
					}

					c.setWidth(size.x - c2.getWidth() - c3.getWidth() - 1);
				}
			}

		});
		messagePreview = new Composite(sides, SWT.NONE);
		messagePreview.setBackground(MAIN_BG);
		GridLayout mpLayout = new GridLayout(1, true);
		mpLayout.marginWidth = 0;
		mpLayout.marginHeight = 0;
		messagePreview.setLayout(mpLayout);
		preview = new Browser(messagePreview, SWT.NONE);
		preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Get saved weights
        int left = Platform.getPreferencesService().getInt(
                MessageCenterPlugin.PLUGIN_ID,
                IPreferenceConstants.MESSAGE_CENTER_LEFT_WEIGHT, 35, null);
		if (left < 100 && left > 0)
		{
			sides.setWeights(new int[]
				{ left, 100 - left });
		}
		MessagingManager.addListener(listener);
		// Add message entries
		populateMessageTable();

		messages.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				try
				{
					TableItem[] items = messages.getTable().getSelection();
					if (items != null && items.length == 1)
					{
						preview.setUrl(loadingPage);
						Message msg = (Message) items[0].getData();
						String html = ""; //$NON-NLS-1$
						html = StringUtils.replace(messageTemplate, TITLE_MARKER, msg.getTitle());
						html = StringUtils.replace(html, MESSAGE_MARKER, StringUtils.replace(msg.getContent(), "\n", //$NON-NLS-1$
								"<br>")); //$NON-NLS-1$

						String charset = "UTF-8"; //$NON-NLS-1$
						PrintWriter pw = null;
						FileOutputStream out = new FileOutputStream(previewFile.getAbsolutePath());

						if (charset != null)
						{
							pw = new PrintWriter(new OutputStreamWriter(out, charset), true);
						}
						else
						{
							pw = new PrintWriter(new OutputStreamWriter(out), true);
						}

						pw.write(html);
						pw.close();

						try
						{
							out.close();
						}
						catch (IOException e)
						{
						}

						preview.setUrl(previewFile.getAbsolutePath());
						if (!msg.isRead())
						{
							msg.setRead(true);
							messages.update(msg, null);
							MessagingManager.notifyListeners(msg, IMessageListener.MESSAGE_READ);
						}
					}
				}
				catch (Exception e)
				{
					// Catch all errors here since this is on the UI-thread and
					// an error dialog will be shown with no
					// details
					IdeLog.logError(MessageUIPlugin.getDefault(), Messages.MessagesEditor_Error_ShowingMessage, e);
				}
			}

		});

		// Create default content for browser
		ByteArrayInputStream stream = new ByteArrayInputStream(messageTemplate.getBytes());
		FileUtils.writeStreamToFile(stream, previewFile.getAbsolutePath());
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
		}
		preview.setUrl(previewFile.getAbsolutePath());

	}

	private void deleteSelectedMessages()
	{
		TableItem[] tItems = messages.getTable().getSelection();
		int index = messages.getTable().getSelectionIndex();
		Message curr;
		for (int i = 0; i < tItems.length; i++)
		{
			curr = (Message) tItems[i].getData();
			curr.setDeleted(true);
			curr.setRead(true);
			MessagingManager.notifyListeners(curr, IMessageListener.MESSAGE_DELETED);
		}
		messages.refresh();

		if (index != -1)
		{
			int count = messages.getTable().getItemCount();
			if (count < 1)
			{
				return;
			}
			if (index >= count)
			{
				messages.getTable().select(0);
			}
			else
			{
				messages.getTable().select(index);
			}
		}
	}

	private void populateMessageTable()
	{
		messages.setInput(getDisplayedList(MessagingManager.getMessages()));
	}

	private List<Message> getDisplayedList(List<Message> list)
	{
		List<Message> subList = new ArrayList<Message>();
		for (Message msg : list)
		{
			if (showDeleted.getSelection() || !msg.isDeleted())
			{
				subList.add(msg);
			}
		}
		return subList;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		displayArea.setFocus();
	}

	/**
	 * Static method for encoding html entities
	 * 
	 * @param s
	 * @return - encoded string
	 */
	public static String HTMLEntityEncode(String s)
	{
		StringBuilder buf = new StringBuilder();
		int len = (s == null ? -1 : s.length());
		char c;
		for (int i = 0; i < len; i++)
		{
			c = s.charAt(i);
			if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9')
			{
				buf.append(c);
			}
			else
			{
				buf.append("&#" + (int) c + ";"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return buf.toString();
	}

	private static IEclipsePreferences getPreferences()
	{
	    return (new InstanceScope()).getNode(MessageCenterPlugin.PLUGIN_ID);
	}
}
