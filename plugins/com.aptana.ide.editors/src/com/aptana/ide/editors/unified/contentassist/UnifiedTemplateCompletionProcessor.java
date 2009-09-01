package com.aptana.ide.editors.unified.contentassist;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

import com.aptana.ide.editors.UnifiedEditorsPlugin;

public class UnifiedTemplateCompletionProcessor extends TemplateCompletionProcessor
{

	private String contextTypedId;
	private ContributionContextTypeRegistry fContextTypeRegistry;

	public UnifiedTemplateCompletionProcessor(String contentType)
	{
		this.contextTypedId = UnifiedContextType.getFullContextTypeId(contentType);
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region)
	{
		return getContextTypeRegistry().getContextType(contextTypedId);
	}

	private synchronized ContextTypeRegistry getContextTypeRegistry()
	{
		if (fContextTypeRegistry == null)
		{
			fContextTypeRegistry = new ContributionContextTypeRegistry();
			fContextTypeRegistry.addContextType(contextTypedId);
		}
		return fContextTypeRegistry;
	}

	@Override
	protected Image getImage(Template template)
	{
		return UnifiedEditorsPlugin.getImage("icons/template_obj.gif"); //$NON-NLS-1$
	}

	@Override
	protected Template[] getTemplates(String contextTypeId)
	{
		return getTemplateStore().getTemplates(contextTypeId);
	}

	private ContributionTemplateStore getTemplateStore()
	{
		return UnifiedEditorsPlugin.getDefault().getTemplateStore(getContextTypeRegistry());
	}

}
