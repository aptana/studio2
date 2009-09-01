package com.aptana.ide.editor.js.preferences;

import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.preferences.UnifiedTemplatePreferencePage;

public class TemplatePreferencePage extends UnifiedTemplatePreferencePage
{

	@Override
	protected List<String> getContentTypes()
	{
		List<String> list = new ArrayList<String>();
		list.add(JSMimeType.MimeType);
		list.add(JSCommentMimeType.MimeType);
		list.add(ScriptDocMimeType.MimeType);
		return list;
	}

}
