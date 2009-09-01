package com.aptana.ide.core.ui.widgets;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A generic TableViewer Sorter. 
 * 
 * Mostly taken from https://bugs.eclipse.org/bugs/show_bug.cgi?id=158112
 * 
 * @author brad
 */
public class TreeViewerSorter extends ViewerSorter {
	private int columnIndex = 0;

	public TreeViewerSorter(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	/**
	 * Compares the two objects provided.
	 * 
	 * If numbers are in the relevant string then the objects are returned in
	 * number order (rather than string order).
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		int order = 0;
		if (viewer instanceof TreeViewer) {
			TreeViewer tv = (TreeViewer) viewer;
			Tree table = tv.getTree();
			table.setSortColumn(table.getColumn(columnIndex));
			int idx1 = -1, idx2 = -1;
			TreeItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++) {
				Object obj = items[i].getData();
				if (obj.equals(e1)) {
					idx1 = i;
				}
				else if (obj.equals(e2)) {
					idx2 = i;
				}
				if (idx1 > 0 && idx2 > 0) {
					break;
				}
			}

			if (idx1 > -1 && idx2 > -1) {
				String str1 = table.getItems()[idx1].getText(this.columnIndex);
				String str2 = table.getItems()[idx2].getText(this.columnIndex);
				order = str1.compareTo(str2);

				try {
					Double d1 = Double.valueOf(str1);
					Double d2 = Double.valueOf(str2);
					order = d1.compareTo(d2);
				}
				catch (NumberFormatException e) {
					// do nothing
				}

				if (table.getSortDirection() != SWT.UP) {
					order *= -1;
				}
			}
		}

		return order;
	}

	/**
	 * The TableViewer passed in will be set up to use this sorter when a column
	 * is clicked.
	 */
	public static void bind(final TreeViewer tableViewer) {
		final Tree table = tableViewer.getTree();
		for (int i = 0; i < table.getColumnCount(); i++) {
			final int columnNum = i;
			TreeColumn column = table.getColumn(i);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent e) {
					TreeViewerSorter sorter = new TreeViewerSorter(columnNum);
					if (table.getSortDirection() == SWT.UP) {
						table.setSortDirection(SWT.DOWN);
					}
					else if (table.getSortDirection() == SWT.DOWN) {
						table.setSortDirection(SWT.UP);
					}
					else {
						table.setSortDirection(SWT.UP);
					}
					tableViewer.setSorter(sorter);
				}
			});
		}
	}
}
