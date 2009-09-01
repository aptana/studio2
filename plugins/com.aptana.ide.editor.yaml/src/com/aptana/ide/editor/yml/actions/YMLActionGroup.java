package com.aptana.ide.editor.yml.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

public class YMLActionGroup extends ActionGroup {
	
	/**
	 * Pop-up menu: id of the source sub menu (value <code>com.aptana.ide.editor.yml.source.menu</code>).
	 * 
	 * @since 1.0.0
	 */
	public static final String MENU_ID= "com.aptana.ide.editor.yml.source.menu"; //$NON-NLS-1$

	/**
	 * Pop-up menu: id of the comment group of the source sub menu (value
	 * <code>commentGroup</code>).
	 * 
	 * TODO: Make API
	 */
	private static final String GROUP_COMMENT= "commentGroup"; //$NON-NLS-1$


	private ITextEditor fEditor;
	private String fGroupName;

	public YMLActionGroup(ITextEditor editor, String groupName) {
		fEditor= editor;
		fGroupName= groupName;
	}

	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		String menuText= "Source";//ActionMessages.SourceMenu_label; 

		IMenuManager subMenu= new MenuManager(menuText, MENU_ID); 
		int added= 0;
		if (isEditorOwner()) {
			added= fillEditorSubMenu(subMenu);
		} else {
			added= fillViewSubMenu(subMenu);
		}
		if (added > 0)
			menu.appendToGroup(fGroupName, subMenu);
	}
	
	private int fillEditorSubMenu(IMenuManager source) {
		int added= 0;
		source.add(new Separator(GROUP_COMMENT));
		added+= addEditorAction(source, "ToggleComment"); //$NON-NLS-1$
		return added;
	}

	private int fillViewSubMenu(IMenuManager source) {
		int added= 0;
		source.add(new Separator(GROUP_COMMENT));
		return added;
	}	
	
	private int addEditorAction(IMenuManager menu, String actionID) {
		if (fEditor == null)
			return 0;
		IAction action= fEditor.getAction(actionID);
		if (action == null)
			return 0;
		if (action instanceof IUpdate)
			((IUpdate)action).update();
		if (action.isEnabled()) {
			menu.add(action);
			return 1;
		}
		return 0;
	}
	
	private boolean isEditorOwner() {
		return fEditor != null;
	}	
}
