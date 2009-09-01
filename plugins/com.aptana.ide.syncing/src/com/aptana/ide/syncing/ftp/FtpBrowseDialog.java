package com.aptana.ide.syncing.ftp;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.dialogs.ElementTreeSelectionDialog;

public class FtpBrowseDialog extends ElementTreeSelectionDialog
{
	/**
	 * Constructs a new FtpBrowseDialog
	 * 
	 * @param parentShell
	 */
	public FtpBrowseDialog(Shell parentShell)
	{
		// We hack this and actually creates the content and the label providers in the
		// doCreateTreeViewer method
		super(parentShell, null, null);
		setHelpAvailable(false);
	}

	/**
	 * Creates the tree viewer.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the {@link SWT} style bits
	 * @return the tree viewer
	 */
	protected TreeViewer doCreateTreeViewer(Composite parent, int style)
	{
		TreeViewer viewer = new TreeViewer(new Tree(parent, style));
		fLabelProvider = new DecoratingLabelProvider(new FtpBrowserLabelProvider(), CoreUIPlugin.getDefault().getWorkbench()
				.getDecoratorManager().getLabelDecorator());
		fContentProvider = new FtpBrowserContentProvider(viewer);
		return viewer;
	}

	/*
	 * Override the super implementation to adapt to this implementation where the content provider can be null at this
	 * stage.
	 */
	protected boolean evaluateIfTreeEmpty(Object input)
	{
		if (fContentProvider == null)
		{
			return false;
		}
		return super.evaluateIfTreeEmpty(input);
	}
}
