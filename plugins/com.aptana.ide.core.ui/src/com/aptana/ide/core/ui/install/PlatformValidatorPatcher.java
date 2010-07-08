/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.ui.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.update.configuration.IInstallConfiguration;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.internal.operations.OperationValidator;
import org.eclipse.update.operations.IInstallFeatureOperation;
import org.eclipse.update.operations.IOperationValidator;
import org.eclipse.update.operations.OperationsManager;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * @author Pavel Petrochenko
 */
public class PlatformValidatorPatcher
{

	private static final String PREF_BACKUP = "prefBackup"; //$NON-NLS-1$

	public static void start()
	{
		final IOperationValidator validator = OperationsManager.getValidator();
		OperationsManager.setValidator(new OperationValidator()
		{

			public IStatus validateCurrentState()
			{
				return validator.validateCurrentState();
			}

			public IStatus validatePendingChanges(IInstallFeatureOperation[] jobs)
			{
				IStatus validatePendingChanges = validator.validatePendingChanges(jobs);				
				if (validatePendingChanges==null||validatePendingChanges.getCode() != IStatus.ERROR)
				{
					Exception e = new Exception();
					StackTraceElement[] stackTrace = e.getStackTrace();
					boolean isFromUI = false;
					for (int a = 0; a < stackTrace.length; a++)
					{
						String className = stackTrace[a].getClassName();
						if (className.equals("org.eclipse.update.internal.ui.wizards.ReviewPage")) { //$NON-NLS-1$
							isFromUI = true;
							break;
						}
					}
					if (!isFromUI)
					{
						exportPreferences();
					}
				}
				return validatePendingChanges;
			}

			public IStatus validatePendingConfig(IFeature feature)
			{
				return validator.validatePendingConfig(feature);
			}

			public IStatus validatePendingInstall(IFeature oldFeature, IFeature newFeature)
			{
				return validator.validatePendingInstall(oldFeature, newFeature);
			}

			public IStatus validatePendingReplaceVersion(IFeature feature, IFeature anotherFeature)
			{
				return validator.validatePendingReplaceVersion(feature, anotherFeature);
			}

			public IStatus validatePendingRevert(IInstallConfiguration config)
			{
				return validator.validatePendingRevert(config);
			}

			public IStatus validatePendingUnconfig(IFeature feature)
			{
				return validator.validatePendingUnconfig(feature);
			}

			public IStatus validatePlatformConfigValid()
			{
				return validator.validatePlatformConfigValid();
			}

		});
	}

	/**
	 * 
	 */
	private static void exportPreferences()
	{
		IPreferenceStore preferenceStore = CoreUIPlugin.getDefault().getPreferenceStore();
		if (preferenceStore.getBoolean(IPreferenceConstants.PREF_AUTO_BACKUP_ENABLED))
		{
			String string = preferenceStore.getString(IPreferenceConstants.PREF_AUTO_BACKUP_PATH);
			File folder=new File(string);
			folder.mkdirs();
			doExport(folder);
		}
	}

	/**
	 * @param folder 
	 * 
	 */
	private static void doExport(File folder)
	{
		long currentTimeMillis = System.currentTimeMillis();
		Date dt = new Date(currentTimeMillis);
		DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.SHORT);
		String fDate = dateInstance.format(dt);
		fDate = StringUtils.replace(fDate, "/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		fDate = StringUtils.replace(fDate, "\\", "_");		 //$NON-NLS-1$ //$NON-NLS-2$
		String fName = FileUtils.ensureValidFilename(PREF_BACKUP + fDate + ".epr"); //$NON-NLS-1$
		File fl = new File(folder,fName);
		int a = 1;
		while (fl.exists())
		{
			fName = FileUtils.ensureValidFilename(PREF_BACKUP + fDate + "v" + a + ".epr"); //$NON-NLS-1$ //$NON-NLS-2$
			a++;
			fl = new File(folder,fName);
		}
		exportPreferences(fl);
		IPreferenceStore preferenceStore = CoreUIPlugin.getDefault().getPreferenceStore();
		preferenceStore.putValue(IPreferenceConstants.PREF_AUTO_BACKUP_LASTNAME, fl.getAbsolutePath());
	}

	/**
	 * @param file
	 * @return
	 */
	public static boolean exportPreferences(File file)
	{
		IPreferencesService service = Platform.getPreferencesService();
		try
		{
			FileOutputStream transfers = new FileOutputStream(file);
			String[] fos = new String[0];
			service.exportPreferences(service.getRootNode(), transfers, fos);
			transfers.flush();
			transfers.close();
			return true;
		}
		catch (final IOException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.getString("PlatformValidatorPatcher.ERR_ErrorExportingPreferences"), e); //$NON-NLS-1$
			UIJob job = new UIJob("Export Preferences") { //$NON-NLS-1$
			
				/**
				 * 
				 */
				public IStatus runInUIThread(IProgressMonitor monitor) {
					MessageDialog.openError(getDisplay().getActiveShell(), new String(), e.getLocalizedMessage());
					return Status.OK_STATUS;
				}
			};
			
			job.setSystem(true);
			job.schedule();
			
			return false;
		}
		catch (final CoreException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.getString("PlatformValidatorPatcher.ERR_ErrorExportingPreferences"), e); //$NON-NLS-1$
			UIJob job = new UIJob("Export Preferences") { //$NON-NLS-1$
				
				/**
				 * 
				 */
				public IStatus runInUIThread(IProgressMonitor monitor) {
					MessageDialog.openError(getDisplay().getActiveShell(), new String(), e.getLocalizedMessage());
					return Status.OK_STATUS;
				}
			};
			
			job.setSystem(true);
			job.schedule();
			return false;
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public static boolean importPreferences(File file)
	{
		IPreferencesService service = Platform.getPreferencesService();
		try
		{
			IStatus importPreferences = service.importPreferences(new FileInputStream(file));			
			return importPreferences.getCode()==IStatus.OK;
		}
		catch (final CoreException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.getString("PlatformValidatorPatcher.ERR_ErrorImportingPreferences"), e); //$NON-NLS-1$
			UIJob job = new UIJob("Import Preferences") { //$NON-NLS-1$
				
				/**
				 * 
				 */
				public IStatus runInUIThread(IProgressMonitor monitor) {
					MessageDialog.openError(getDisplay().getActiveShell(), new String(), e.getLocalizedMessage());
					return Status.OK_STATUS;
				}
			};
			
			job.setSystem(true);
			job.schedule();
			return false;
		}
		catch (final FileNotFoundException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.getString("PlatformValidatorPatcher.ERR_ErrorImportingPreferences"), e); //$NON-NLS-1$
			UIJob job = new UIJob("Import Preferences") { //$NON-NLS-1$
				
				/**
				 * 
				 */
				public IStatus runInUIThread(IProgressMonitor monitor) {
					MessageDialog.openError(getDisplay().getActiveShell(), new String(), e.getLocalizedMessage());
					return Status.OK_STATUS;
				}
			};
			
			job.setSystem(true);
			job.schedule();
			
			return false;
		}
	}

}
