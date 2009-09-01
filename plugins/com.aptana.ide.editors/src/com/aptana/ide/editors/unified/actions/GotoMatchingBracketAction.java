package com.aptana.ide.editors.unified.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;

import com.aptana.ide.editors.unified.UnifiedEditor;

public class GotoMatchingBracketAction extends Action
{

	public final static String GOTO_MATCHING_BRACKET = "GotoMatchingBracket"; //$NON-NLS-1$

	private final UnifiedEditor fEditor;

	public GotoMatchingBracketAction(UnifiedEditor editor)
	{
		super(Messages.GotoMatchingBracket_label);
		Assert.isNotNull(editor);
		fEditor = editor;
		setEnabled(true);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IHelpContextIds.GOTO_MATCHING_BRACKET_ACTION);
	}

	public void run()
	{
		fEditor.gotoMatchingBracket();
	}

}
