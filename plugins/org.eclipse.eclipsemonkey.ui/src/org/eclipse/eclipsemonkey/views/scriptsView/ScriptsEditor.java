/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.views.scriptsView;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.eclipsemonkey.ScriptMetadata;
import org.eclipse.eclipsemonkey.ui.EclipseMonkeyUIPlugin;
import org.eclipse.eclipsemonkey.utils.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.js.JSEditor;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ScriptsEditor extends SelectionStatusDialog
{

	private Composite displayArea;
	private SashForm mainSash;
	private Composite left;
	private TreeViewer scriptsViewer;
	private Composite editorComp;
	private JSEditor editor;
	private Composite right;
	private Composite controls;
	private Label menuPathLabel;
	private Text menuPathText;
	private Label toolbarPathLabel;
	private Text toolbarPathText;
	private Label imagePathLabel;
	private Composite imageDisplay;
	private Image image;
	private Button imagePathBrowse;
	private Button addScript;
	private Button copyScript;
	private Button saveScript;
	private Button restoreScript;
	private Button alwaysRunScript;
	private boolean firstSelection = true;

	/**
	 * Creates a new scripts editor
	 * 
	 * @param parent
	 */
	public ScriptsEditor(Shell parent)
	{
		super(parent);
		setTitle(Messages.ScriptsEditor_TTL_Scripts_editor);
		setShellStyle(SWT.DIALOG_TRIM | getDefaultOrientation() | SWT.MIN | SWT.RESIZE | SWT.MAX);
	}

	private int traverseTime = -1;

	/**
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#create()
	 */
	public void create()
	{
		super.create();
		getShell().addTraverseListener(new TraverseListener()
		{

			public void keyTraversed(TraverseEvent e)
			{
				if (editorComp.isVisible())
				{
					traverseTime = e.time;
					Event e1 = new Event();
					e1.widget = e.widget;
					e1.data = e.data;
					e1.doit = e.doit;
					e1.keyCode = e.keyCode;
					e1.stateMask = e.stateMask;
					e1.character = e.character;
					editor.getViewer().getTextWidget().notifyListeners(SWT.KeyDown, e1);
				}
				else
				{
					traverseTime = -1;
				}
			}

		});
	}

	/**
	 * @see org.eclipse.jface.window.Window#getShellListener()
	 */
	protected ShellListener getShellListener()
	{
		return new ShellAdapter()
		{
			public void shellClosed(ShellEvent event)
			{
				event.doit = false; // don't close now
				if (canHandleShellCloseEvent() && event.time != traverseTime)
				{
					handleShellCloseEvent();
				}
			}
		};
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		displayArea = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		displayArea.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumHeight = 600;
		data.minimumWidth = 800;
		displayArea.setLayoutData(data);
		mainSash = new SashForm(displayArea, SWT.HORIZONTAL);
		mainSash.setLayout(layout);
		mainSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		left = new Composite(mainSash, SWT.BORDER);
		left.setLayout(layout);
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		Composite buttons = new Composite(left, SWT.NONE);
		buttons.setLayout(new GridLayout(2, true));
		addScript = new Button(buttons, SWT.PUSH);
		addScript.setImage(EclipseMonkeyUIPlugin.getImage("icons/add_script.gif")); //$NON-NLS-1$
		addScript.setToolTipText(Messages.ScriptsEditor_TTP_Add_script);
		copyScript = new Button(buttons, SWT.PUSH);
		copyScript.setImage(EclipseMonkeyUIPlugin.getImage("icons/copy_script.gif")); //$NON-NLS-1$
		copyScript.setToolTipText(Messages.ScriptsEditor_TTP_Copy_script);
		scriptsViewer = new TreeViewer(left, SWT.SINGLE | SWT.BORDER);
		scriptsViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scriptsViewer.setContentProvider(new ScriptsContentProvider());
		scriptsViewer.setLabelProvider(new ScriptsLabelProvider());
		scriptsViewer.setInput(scriptsViewer);
		scriptsViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				ITreeSelection selection = ((ITreeSelection) scriptsViewer.getSelection());
				if (selection != null && !selection.isEmpty())
				{
					Object obj = selection.getFirstElement();
					if (obj instanceof ScriptAction)
					{
						ScriptAction script = ((ScriptAction) obj);
						String source = script.getStoredScript().metadata.getSource();
						File f = queryFile();
						IEditorInput input = CoreUIUtils.createNonExistingFileEditorInput(f, "temp.js"); //$NON-NLS-1$
						try
						{
							if (firstSelection)
							{
								WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getActivePage();
								IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
								IEditorDescriptor descriptor = editorRegistry.getDefaultEditor("test.js"); //$NON-NLS-1$

								EditorReference ref = new EditorReference(page.getEditorManager(), input,
										(EditorDescriptor) descriptor);
								EditorSite site = new EditorSite(ref, editor, page, (EditorDescriptor) descriptor);
								editor.init(site, input);
								editor.createPartControl(editorComp);
								firstSelection = false;
							}
							else
							{
								editor.setInput(input);
							}
							IDocumentProvider dp = editor.getDocumentProvider();
							IDocument doc = dp.getDocument(editor.getEditorInput());
							try
							{
								if (source != null)
								{
									doc.replace(0, 0, source);
								}
							}
							catch (BadLocationException e)
							{
								e.printStackTrace();
							}
							editorComp.setVisible(true);
							saveScript.setEnabled(true);
							restoreScript.setEnabled(true);
							editor.setFocus();
							editorComp.layout(true, true);
							right.layout(true, true);
						}
						catch (PartInitException e1)
						{
							e1.printStackTrace();
						}
						ScriptMetadata metadata = script.getStoredScript().metadata;
						menuPathText.setText(metadata.getMenuName());
						String toolbarPath = metadata.getToolbarName();
						if (toolbarPath == null)
						{
							toolbarPath = ""; //$NON-NLS-1$
						}
						toolbarPathText.setText(toolbarPath);
						if (metadata.getImage() != null)
						{
							String imagePath = metadata.getPath().removeLastSegments(1).append(metadata.getImage())
									.toString();
							image = new Image(getShell().getDisplay(), imagePath);
						}
						else
						{
							if (image != null && !image.isDisposed())
							{
								image.dispose();
							}
							image = null;
						}
						imageDisplay.redraw();
						imagePathBrowse.setEnabled(true);
					}
					else
					{
						saveScript.setEnabled(false);
						restoreScript.setEnabled(false);
						menuPathText.setText(""); //$NON-NLS-1$
						toolbarPathText.setText(""); //$NON-NLS-1$
						if (image != null && !image.isDisposed())
						{
							image.dispose();
						}
						image = null;
						imageDisplay.redraw();
						editorComp.setVisible(false);
						imagePathBrowse.setEnabled(false);
					}
				}
			}

		});
		right = new Composite(mainSash, SWT.BORDER);
		right.setLayout(layout);
		GridData rightData = new GridData(SWT.FILL, SWT.FILL, true, true);
		rightData.minimumHeight = 500;
		right.setLayoutData(rightData);
		Composite buttonComp = new Composite(right, SWT.NONE);
		GridLayout bcLayout = new GridLayout(2, false);
		bcLayout.marginHeight = 0;
		bcLayout.marginWidth = 0;
		buttonComp.setLayout(bcLayout);
		GridData bcData = new GridData(SWT.FILL, SWT.FILL, true, false);
		buttonComp.setLayoutData(bcData);
		saveScript = new Button(buttonComp, SWT.PUSH);
		saveScript.setImage(EclipseMonkeyUIPlugin.getImage("icons/save.gif")); //$NON-NLS-1$
		saveScript.setEnabled(false);
		GridData ssData = new GridData(SWT.END, SWT.FILL, true, false);
		restoreScript = new Button(buttonComp, SWT.PUSH);
		restoreScript.setImage(EclipseMonkeyUIPlugin.getImage("icons/restore_defaults.gif")); //$NON-NLS-1$
		restoreScript.setEnabled(false);
		restoreScript.setLayoutData(ssData);
		editorComp = new Composite(right, SWT.BORDER);
		editorComp.setLayout(new FillLayout());
		editorComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editorComp.setVisible(false);
		editor = new JSEditor();
		controls = new Composite(right, SWT.NONE);
		GridLayout cLayout = new GridLayout(2, false);
		controls.setLayout(cLayout);
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		menuPathLabel = new Label(controls, SWT.LEFT);
		menuPathLabel.setText(Messages.ScriptsEditor_LBL_Menu_path);
		menuPathText = new Text(controls, SWT.SINGLE | SWT.BORDER);
		menuPathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		toolbarPathLabel = new Label(controls, SWT.LEFT);
		toolbarPathLabel.setText(Messages.ScriptsEditor_LBL_Toolbar_path);
		toolbarPathText = new Text(controls, SWT.SINGLE | SWT.BORDER);
		toolbarPathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		imagePathLabel = new Label(controls, SWT.LEFT);
		imagePathLabel.setText(Messages.ScriptsEditor_LBL_Image_path);
		Composite imageComp = new Composite(controls, SWT.NONE);
		GridLayout icLayout = new GridLayout(2, false);
		icLayout.marginWidth = 0;
		icLayout.marginHeight = 0;
		imageComp.setLayout(icLayout);
		imageDisplay = new Composite(imageComp, SWT.BORDER);
		GridData data2 = new GridData(16, 16);
		imageDisplay.setLayoutData(data2);
		imageDisplay.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (image != null && !image.isDisposed())
				{
					GC gc = new GC(imageDisplay);
					gc.drawImage(image, 0, 0);
					gc.dispose();
				}
			}

		});
		imagePathBrowse = new Button(imageComp, SWT.PUSH);
		imagePathBrowse.setText(StringUtils.ellipsify(Messages.ScriptsEditor_LBL_Browse));
		imagePathBrowse.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				String imageFile = dialog.open();
				if (imageFile != null)
				{
					if (image != null)
					{
						image.dispose();
					}
					image = new Image(getShell().getDisplay(), imageFile);
					imageDisplay.redraw();
				}
			}

		});

		alwaysRunScript = new Button(controls, SWT.CHECK);
		alwaysRunScript.setText(Messages.ScriptsEditor_LBL_Always_run_script);
		GridData arsData = new GridData(SWT.FILL, SWT.FILL, true, false);
		arsData.horizontalSpan = 2;
		alwaysRunScript.setLayoutData(arsData);

		mainSash.setWeights(new int[] { 20, 80 });
		return composite;
	}

	private File queryFile()
	{
		IPath stateLocation = EclipseMonkeyUIPlugin.getDefault().getStateLocation();
		IPath path = stateLocation.append("/_" + this.hashCode() + ".js"); //$NON-NLS-1$ //$NON-NLS-2$ 
		return new File(path.toOSString());
	}

	/**
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
	 */
	protected void computeResult()
	{
		// Does nothing for now
	}

}
