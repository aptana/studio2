package com.aptana.ide.editor.css.preferences;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editors.preferences.UnifiedTemplatePreferencePage;

public class TemplatePreferencePage extends UnifiedTemplatePreferencePage
{

	@Override
	protected List<String> getContentTypes()
	{
		List<String> list = new ArrayList<String>();
		list.add(CSSMimeType.MimeType);
		return list;
	}

}
