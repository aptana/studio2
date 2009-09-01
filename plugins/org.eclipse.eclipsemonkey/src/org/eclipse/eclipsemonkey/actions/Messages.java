package org.eclipse.eclipsemonkey.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.eclipsemonkey.actions.messages"; //$NON-NLS-1$

	public static String CreateMonkeyExamplesAction_0;

	public static String CreateMonkeyExamplesAction_INF_MSG_Errors_creating_examples_project;

	public static String CreateMonkeyExamplesAction_INF_MSG_unable_create_examples_project;

	public static String CreateMonkeyExamplesAction_INF_TTL_Errors_creating_examples_project;

	public static String CreateMonkeyExamplesAction_INF_TTL_unable_create_examples_project;

	public static String PasteScriptFromClipboardAction_Monkey_project_name;
	public static String PasteScriptFromClipboardAction_INF_MSG_Cant_find_script_on_clipboard;
	public static String PasteScriptFromClipboardAction_INF_MSG_Unable_create_examples_project;
	public static String PasteScriptFromClipboardAction_INF_TTL_Cant_find_script_on_clipboard;
	public static String PasteScriptFromClipboardAction_INF_TTL_Unable_create_examples_project;

	public static String PublishScript_INF_MSG_error_copying_script_for_publication;

	public static String PublishScript_INF_TTL_error_copying_script_for_publication;

	public static String RecreateMonkeyMenuAction_LBL_Examples;
	public static String RecreateMonkeyMenuAction_LBL_Paste_new_script;
	public static String RecreateMonkeyMenuAction_LBL_Run_last_script;
	public static String RecreateMonkeyMenuAction_ERR_TTL_Keystroke_not_valid;
	public static String RecreateMonkeyMenuAction_ERR_MSG_Keystroke_not_valid;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
