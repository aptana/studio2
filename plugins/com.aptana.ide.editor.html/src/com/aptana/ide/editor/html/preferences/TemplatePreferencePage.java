package com.aptana.ide.editor.html.preferences;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editors.preferences.UnifiedTemplatePreferencePage;

public class TemplatePreferencePage extends UnifiedTemplatePreferencePage
{

	@Override
	protected List<String> getContentTypes()
	{
		List<String> list = new ArrayList<String>();
		list.add(HTMLMimeType.MimeType);
		return list;
	}

}
