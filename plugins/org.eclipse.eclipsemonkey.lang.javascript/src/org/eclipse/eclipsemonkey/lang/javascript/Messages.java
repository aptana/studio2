package org.eclipse.eclipsemonkey.lang.javascript;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.eclipsemonkey.lang.javascript.messages"; //$NON-NLS-1$
	public static String JavaScriptGlobal_MSG_javascript_console_started;
	public static String JavaScriptGlobal_MSG_Prompt_dialog_default;
	public static String JavaScriptGlobal_TTL_Alert_dialog;
	public static String JavaScriptGlobal_TTL_Confirm_dialog;
	public static String JavaScriptGlobal_TTL_javascript_console;
	public static String JavaScriptGlobal_TTL_Prompt_dialog;
	public static String JavaScriptRunner_ERR_MSG_function_not_defined;
	public static String JavaScriptRunner_MSG_Error_executing_script;
	public static String JavaScriptRunner_TTL_Error_executing_script;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
