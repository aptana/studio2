package com.aptana.ide.views.outline;

import org.eclipse.jface.resource.ImageDescriptor;

import com.aptana.ide.editors.unified.InstanceCreator;

/**
 * @author Kevin Lindsey
 */
public class FilterActionInfo
{
	private String _name;
	private String _tooltip;
	private ImageDescriptor _imageDescriptor;
	private InstanceCreator _filterCreator;
	
	/**
	 * Filter
	 * 
	 * @param filter
	 * @param imageDescriptor
	 */
	public FilterActionInfo(String name, String tooltip, InstanceCreator filterCreator, ImageDescriptor imageDescriptor)
	{
		this._name = (name == null) ? Messages.FilterActionInfo_Filter : name;
		this._tooltip = (tooltip == null) ? this._name : tooltip;
		this._filterCreator = filterCreator;
		this._imageDescriptor = imageDescriptor;
	}
	
	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
	
	/**
	 * getImageDescriptor
	 * 
	 * @return
	 */
	public ImageDescriptor getImageDescriptor()
	{
		return this._imageDescriptor;
	}
	
	/**
	 * getFilter
	 * 
	 * @return
	 */
	public BaseFilter getFilter()
	{
		return (BaseFilter) this._filterCreator.createInstance();
	}
	
	/**
	 * getToolTip
	 * 
	 * @return
	 */
	public String getToolTip()
	{
		return this._tooltip;
	}
}