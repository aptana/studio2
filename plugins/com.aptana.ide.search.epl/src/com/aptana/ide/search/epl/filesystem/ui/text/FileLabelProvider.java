
package com.aptana.ide.search.epl.filesystem.ui.text;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

public class FileLabelProvider extends org.eclipse.search.internal.ui.text.FileLabelProvider
{

    private static final String fgSeparatorFormat= "{0} - {1}"; //$NON-NLS-1$

    private final AbstractTextSearchViewPage fPage;
	private String[] fArgs = new String[2];

	/**
	 * @param page
	 * @param orderFlag
	 */
	public FileLabelProvider(AbstractTextSearchViewPage page, int orderFlag)
	{
		super(page, orderFlag);
		fPage = page;
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		if (!(element instanceof File))
		{
			return super.getText(element);
		}

		File resource = (File) element;
		String text = null;

		if (!resource.exists())
		{
			text = SearchMessages.FileLabelProvider_removed_resource_label;
		}
		else
		{
			IPath path = new Path(resource.getAbsolutePath()).removeLastSegments(1);
			if (path.getDevice() == null)
			{
				path = path.makeRelative();
			}
			int order = getOrder();
			if ((order == FileLabelProvider.SHOW_LABEL) || (order == FileLabelProvider.SHOW_LABEL_PATH))
			{
				text = resource.getName();
				if ((path != null) && (order == FileLabelProvider.SHOW_LABEL_PATH))
				{
					this.fArgs[0] = text;
					this.fArgs[1] = path.toString();
					text = MessageFormat.format(FileLabelProvider.fgSeparatorFormat, this.fArgs);
				}
			}
			else
			{
				if (path != null)
				{
					text = path.toString();
				}
				else
				{
					text = ""; //$NON-NLS-1$
				}
				if (order == FileLabelProvider.SHOW_PATH_LABEL)
				{
					this.fArgs[0] = text;
					this.fArgs[1] = resource.getName();
					text = MessageFormat.format(FileLabelProvider.fgSeparatorFormat, this.fArgs);
				}
			}
		}

		int matchCount = 0;
		AbstractTextSearchResult result = this.fPage.getInput();
		if (result != null)
		{
			matchCount = result.getMatchCount(element);
		}
		if (matchCount <= 1)
		{
			return text;
		}
		String format = SearchMessages.FileLabelProvider_count_format;
		return MessageFormat.format(format, new Object[] { text, new Integer(matchCount) });
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		if (!(element instanceof File))
		{
			return super.getImage(element);
		}
		File fl = (File) element;
		if (fl.isDirectory())
		{
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		return WorkbenchPlugin.getDefault().getEditorRegistry().getImageDescriptor(fl.getName()).createImage();

	}
}
