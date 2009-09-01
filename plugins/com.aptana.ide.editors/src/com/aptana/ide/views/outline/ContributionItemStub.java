package com.aptana.ide.views.outline;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

public abstract class ContributionItemStub implements IContributionItem {

	public void dispose() {
	}

	public void fill(Composite parent) {
	}

	public void fill(Menu parent, int index) {
	}

	public void fill(ToolBar parent, int index) {
	}

	public void fill(CoolBar parent, int index) {
	}	

	public boolean isDirty() {
		return false;
	}

	public boolean isDynamic() {
		return false;
	}

	public boolean isEnabled() {
		return true;
	}
	
	public boolean isGroupMarker() {
		return false;
	}
	
	public boolean isSeparator() {
		return false;
	}
	
	public boolean isVisible() {
		return true;
	}
	
	public void saveWidgetState() {
	}
	
	public void setParent(IContributionManager parent) {
	}
	
	public void setVisible(boolean visible) {
	}
	
	public void update() {
	}
	
	public void update(String id) {
	}
}
