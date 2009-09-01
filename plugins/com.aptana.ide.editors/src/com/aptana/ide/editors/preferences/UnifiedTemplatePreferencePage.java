package com.aptana.ide.editors.preferences;

import java.util.List;

import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.contentassist.UnifiedContextType;

public abstract class UnifiedTemplatePreferencePage extends TemplatePreferencePage
{

	public UnifiedTemplatePreferencePage()
	{
		super();
		ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
		List<String> contentTypes = getContentTypes();
		for (String contentType : contentTypes)
		{
			registry.addContextType(UnifiedContextType.getFullContextTypeId(contentType));
		}
		setTemplateStore(UnifiedEditorsPlugin.getDefault().getTemplateStore(registry));
		setContextTypeRegistry(registry);
		setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Returns a list of the content types whose templates we want to manage. i.e. text/css, text/html
	 * 
	 * @return
	 */
	protected abstract List<String> getContentTypes();

	@Override
	protected boolean isShowFormatterSetting()
	{
		return false;
	}

}
