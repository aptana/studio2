package com.aptana.ide.editors.unified.contentassist;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

public class UnifiedContextType extends TemplateContextType
{

	private static final String PREFIX = "com.aptana.ide.editors.contextType."; //$NON-NLS-1$

	public UnifiedContextType()
	{
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.Year());
	}

	public static final String getFullContextTypeId(String aptanaContentType)
	{
		return PREFIX + aptanaContentType;
	}

}
