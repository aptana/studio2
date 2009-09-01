package com.aptana.ide.core.ui.widgets;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;

/**
 * @author Pavel Petrochenko
 */
public class ToolbarTooltip extends ToolTip
{
	private ToolBar tb;
	private String id;

	/**
	 * @param toolbar
	 * @param id
	 */
	public ToolbarTooltip(ToolBar toolbar, String id)
	{
		super(toolbar);
		toolbar.addListener(SWT.MouseEnter, new Listener()
		{

			public void handleEvent(Event event)
			{
				for (ToolItem item : tb.getItems())
				{
					Object data2 = item.getData();
					ContributionItem citem = (ContributionItem) data2;
					if (citem instanceof ActionContributionItem)
					{
						ActionContributionItem cm = (ActionContributionItem) citem;
						IAction action = cm.getAction();
						String id = action.getId();
						if (id != null)
						{
							if (id.equals(ToolbarTooltip.this.id))
							{								
								item.setToolTipText(null);
								return;
							}
						}
					}
				}
			}

		});
		this.tb = toolbar;
		this.id = id;
	}

	/**
	 * @see com.aptana.ide.core.ui.widgets.ToolTip#shouldCreateToolTip(org.eclipse.swt.widgets.Event)
	 */
	protected boolean shouldCreateToolTip(Event event)
	{
		boolean shouldCreateToolTip = super.shouldCreateToolTip(event);
		if (shouldCreateToolTip)
		{
			ToolItem item = tb.getItem(new Point(event.x, event.y));
			if (item == null)
			{
				return false;
			}
			Object data2 = item.getData();
			ContributionItem citem = (ContributionItem) data2;
			if (citem instanceof ActionContributionItem)
			{
				ActionContributionItem cm = (ActionContributionItem) citem;
				IAction action = cm.getAction();
				String id = action.getId();
				if (id != null)
				{
					if (id.equals(this.id))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.core.ui.widgets.ToolTip#createToolTipContentArea(org.eclipse.swt.widgets.Event,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent)
	{
		return null;
	}

	/**
	 * @param site
	 * @param id
	 * @return tooltip
	 */
	public static ToolbarTooltip install(IViewSite site, String id)
	{
		ToolBarManager toolBarManager = (ToolBarManager) site.getActionBars().getToolBarManager();
		final ToolBar control = toolBarManager.getControl();
		return new ToolbarTooltip(control, id);
	}
}
