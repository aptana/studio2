package com.aptana.ide.security.internal.linux;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
	
	public static String messageEmptyPassword;
	public static String messageNoMatch;
	public static String buttonLogin;
	public static String buttonExit;
	public static String generalDialogTitle;
	public static String passwordChangeTitle;
	public static String messageLoginChange;
	public static String dialogTitle;
	public static String labelPassword;
	public static String labelConfirm;
	public static String showPassword;

	public static String PasswordProvider_ERR_UnableToStoreKey;
	
	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
