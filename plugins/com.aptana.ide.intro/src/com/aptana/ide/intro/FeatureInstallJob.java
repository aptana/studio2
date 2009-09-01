/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.intro;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.intro.preferences.FeatureDescriptor;
import com.aptana.ide.update.Activator;

/**
 * FeatureInstallJob
 */
public class FeatureInstallJob extends UIJob
{
	List<FeatureDescriptor> features = new ArrayList<FeatureDescriptor>();

	/**
	 * FeatureInstallJob
	 * @param features 
	 */
	public FeatureInstallJob(List<FeatureDescriptor> features)
	{
		super(Messages.FeatureInstallJob_Title);
		this.features = features;
	}

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        try
        {
            if (features == null || features.size() == 0)
            {
                return Status.OK_STATUS;
            }
            
            Activator.getDefault().getPluginManager().install(features.toArray(new FeatureDescriptor[features.size()]), new NullProgressMonitor());
        }
        catch (Exception e)
        {
            IdeLog.logError(IntroPlugin.getDefault(), Messages.FeatureInstallJob_ERR_SearchSite, e);
        }
        return Status.OK_STATUS;
    }
}
