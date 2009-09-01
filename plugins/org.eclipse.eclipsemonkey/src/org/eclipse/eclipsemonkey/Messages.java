package org.eclipse.eclipsemonkey;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.eclipsemonkey.messages"; //$NON-NLS-1$

	public static String ScriptMetadata_ERR_TTL_Unable_open_editor;
	public static String ScriptMetadata_ERR_MSG_Unable_open_editor;
	public static String ScriptMetadata_LBL_Cancel_script;
	public static String ScriptMetadata_LBL_Edit_script;
	public static String ScriptMetadata_LBL_Install_plugin;
	public static String ScriptMetadata_LBL_Install_plugins;
	public static String ScriptMetadata_LBL_Missing_DOM;
	public static String ScriptMetadata_LBL_Missing_DOMs;
	public static String ScriptMetadata_MSG_script_0_requires_DOM_1;
	public static String ScriptMetadata_MSG_script_0_requires_DOMs_1;
	public static String ScriptMetadata_TTL_Update_job;
	public static String ScriptMetadata_TTL_Update_site_plural;
	public static String ScriptMetadata_TTL_Update_site_singular;
	
	public static String Subscription_ERR_MSG_Cant_find_add_method;
	
	private Messages()
	{
	}

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
