/**
 * 
 */
package com.aptana.ide.core.internal.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;

/**
 * A property tester that will test the existence of the nature ID in the given {@link IProject} receiver.
 * 
 * @author Shalom Gibly
 */
public class NaturePropertyTester extends PropertyTester
{

	private static final String NATURE = Messages.NaturePropertyTester_NATURE;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 * java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (NATURE.equals(property) && receiver instanceof IAdaptable)
		{
			IProject project = (IProject) ((IAdaptable) receiver).getAdapter(IProject.class);
			if (project == null || !project.isAccessible()) {
				return false;
			}
			try
			{
				if (args != null && args.length > 0 )
				{
					return project.hasNature(args[0].toString());
				} else {
					// we have not args, so check if the project does not contain any nature
					return project.getDescription().getNatureIds().length == 0;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(),
						StringUtils.format(Messages.NaturePropertyTester_ERR_ExceptionWhileTestingNature, project.getName()),
						e);
			}
		}
		return false;
	}

}
