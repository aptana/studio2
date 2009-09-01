package com.aptana.ide.update.test;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class SuiteDialog extends Dialog {

	protected Object result;
	protected Shell shlSitesEditor;
	private Text text;
	private Text remoteLocalFilesystemText;
	private Button browseBtn2;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SuiteDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlSitesEditor.open();
		shlSitesEditor.layout();
		Display display = getParent().getDisplay();
		while (!shlSitesEditor.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	private void createContents() {
		shlSitesEditor = new Shell(getParent(), getStyle());
		shlSitesEditor.setSize(531, 367);
		shlSitesEditor.setText("Sites Editor");
		{
			Composite composite = new Composite(shlSitesEditor, SWT.NONE);
			composite.setBounds(0, 0, 531, 316);
			{
				Group groupSites = new Group(composite, SWT.BORDER);
				groupSites.setText("Sites");
				groupSites.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
				groupSites.setBounds(10, 10, 131, 263);
				{
					List list = new List(groupSites, SWT.BORDER);
					list.setBounds(10, 10, 97, 202);
					list.add("Site A");
					list.add("Site B");
					list.add("New Site");
					list.setSelection(2);
				}
				{
					Button button = new Button(groupSites, SWT.NONE);
					button.setBounds(7, 214, 34, 30);
					button.setText("+");
				}
				{
					Button button = new Button(groupSites, SWT.NONE);
					button.setBounds(36, 214, 34, 30);
					button.setText("-");
				}
			}
			{
				Group groupLocal = new Group(composite, SWT.NONE);
				groupLocal.setBounds(147, 10, 374, 102);
				groupLocal.setText("Source");
				{
					Combo localProjectsCombo = new Combo(groupLocal, SWT.NONE);
					localProjectsCombo.setBounds(98, 30, 255, 22);
					localProjectsCombo.add("ProjectA");
					localProjectsCombo.add("ProjectB");
					localProjectsCombo.add("ProjectC");
					localProjectsCombo.select(0);
				}
				{
					Button btnProject = new Button(groupLocal, SWT.RADIO);
					btnProject.setSelection(true);
					btnProject.setBounds(10, 31, 91, 18);
					btnProject.setText("Project");
				}
				{
					Button btnFilesystem = new Button(groupLocal, SWT.RADIO);
					btnFilesystem.setBounds(10, 58, 91, 18);
					btnFilesystem.setText("Filesystem");
				}
				{
					Button browseBtn1 = new Button(groupLocal, SWT.NONE);
					browseBtn1.setBounds(283, 53, 70, 30);
					browseBtn1.setText("Browse");
				}
				{
					text = new Text(groupLocal, SWT.BORDER);
					text.setBounds(98, 58, 179, 19);
				}
				{
					Label lblSelectTheContents = new Label(groupLocal, SWT.NONE);
					lblSelectTheContents.setBounds(10, 10, 243, 14);
					lblSelectTheContents.setText("Select the source location of your site.");
				}
			}
			{
				Group groupRemote = new Group(composite, SWT.NONE);
				groupRemote.setBounds(147, 135, 374, 138);
				groupRemote.setText("Destination");
				{
					Label lblSelectYourRemote = new Label(groupRemote, SWT.NONE);
					lblSelectYourRemote.setBounds(10, 10, 301, 14);
					lblSelectYourRemote.setText("Select the destination target of your site.");
				}
				{
					Button btnRemoteFilesystemeg = new Button(groupRemote, SWT.RADIO);
					btnRemoteFilesystemeg.setSelection(true);
					btnRemoteFilesystemeg.setBounds(10, 30, 80, 18);
					btnRemoteFilesystemeg.setText("Remote");
				}
				{
					Button btnProject_1 = new Button(groupRemote, SWT.RADIO);
					btnProject_1.setBounds(10, 54, 91, 18);
					btnProject_1.setText("Project");
				}
				{
					Button btnFilesystem_1 = new Button(groupRemote, SWT.RADIO);
					btnFilesystem_1.setBounds(10, 78, 91, 18);
					btnFilesystem_1.setText("Filesystem");
				}
				{
					Combo remoteFilesystemCombo = new Combo(groupRemote, SWT.NONE);
					remoteFilesystemCombo.setBounds(96, 29, 179, 22);
					remoteFilesystemCombo.add("content.aptana.com");
					remoteFilesystemCombo.add("ide.aptana.com");
					remoteFilesystemCombo.add("cloud-living-13432");
					remoteFilesystemCombo.select(0);
					
				}
				{
					Button btnNew = new Button(groupRemote, SWT.NONE);
					btnNew.setBounds(281, 25, 72, 30);
					btnNew.setText("New");
				}
				{
					Combo remoteProjectCombo = new Combo(groupRemote, SWT.NONE);
					remoteProjectCombo.setBounds(96, 53, 257, 22);
					remoteProjectCombo.add("ProjectA");
					remoteProjectCombo.add("ProjectB");
					remoteProjectCombo.add("ProjectC");
					remoteProjectCombo.select(1);
				}
				{
					browseBtn2 = new Button(groupRemote, SWT.NONE);
					browseBtn2.setBounds(281, 73, 72, 30);
					browseBtn2.setText("Browse");
				}
				{
					remoteLocalFilesystemText = new Text(groupRemote, SWT.BORDER);
					remoteLocalFilesystemText.setBounds(96, 78, 179, 19);
				}
			}
			{
				Button applyBtn = new Button(composite, SWT.NONE);
				applyBtn.setBounds(430, 279, 94, 30);
				applyBtn.setText("Apply");
			}
		}
		{
			Composite composite = new Composite(shlSitesEditor, SWT.NONE);
			composite.setBounds(0, 316, 531, 30);
			{
				Button btnCancel = new Button(composite, SWT.NONE);
				btnCancel.setBounds(330, 0, 94, 30);
				btnCancel.setText("Cancel");
			}
			{
				Button btnOk = new Button(composite, SWT.NONE);
				btnOk.setBounds(430, 0, 94, 30);
				btnOk.setText("OK");
			}
		}

	}
}
