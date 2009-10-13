/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.editors.unified.errors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.actions.ShowErrors;
import com.aptana.ide.editors.actions.ShowInfos;
import com.aptana.ide.editors.actions.ShowWarnings;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.FileContextContentEvent;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.validator.ValidatorManager;
import com.aptana.ide.editors.validator.ValidatorRef;

/**
 * Checks HTML and JS Syntax and reports Errors and Warnings
 * 
 * @author Paul Colton
 */
public class UnifiedErrorManager implements IErrorManager, IPropertyChangeListener
{
	ErrorDescriptor[] _errorDescriptors;

	/**
	 * fileService
	 */
	protected FileService fileService;

	String mimeType;

	ValidatorRef[] validators;

	/**
	 * The constructor.
	 * 
	 * @param fileService
	 * @param mimeType
	 */
	public UnifiedErrorManager(FileService fileService, String mimeType)
	{
		this.fileService = fileService;
		this.mimeType = mimeType;

		if (getPreferenceStore() != null)
		{
			getPreferenceStore().addPropertyChangeListener(this);
		}

		initializeValidators();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileContextListener#onContentChanged(com.aptana.ide.editors.unified.FileContextContentEvent)
	 */
	public void onContentChanged(FileContextContentEvent evt)
	{
		if (evt.getSource() != fileService)
		{
			return;
		}

		IFileSourceProvider sourceProvider = fileService.getSourceProvider();

		String path = CoreUIUtils.getPathFromURI(sourceProvider.getSourceURI());
		String sourceString = null;

		try
		{
			sourceString = sourceProvider.getSource();
		}
		catch (IOException e1)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedErrorManager_Error, e1);
			return;
		}

		if (sourceProvider instanceof DocumentSourceProvider)
		{
			IFileError[] errors = parseForErrors(path, sourceString, sourceProvider);
			fileService.setFileErrors(errors);
		}
	}

	/**
	 * @return boolean
	 */
	public boolean showInfos()
	{
		return ShowInfos.isInstanceChecked();
	}

	/**
	 * @return boolean
	 */
	public boolean showWarnings()
	{
		return ShowWarnings.isInstanceChecked();
	}

	/**
	 * @return boolean
	 */
	public boolean showErrors()
	{
		return ShowErrors.isInstanceChecked();
	}

	/**
	 * parseForErrors
	 * 
	 * @param path
	 * @param source
	 * @param sourceProvider
	 * @return IFileError[]
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider)
	{
		loadErrorDescriptors();

		UnifiedErrorReporter reporter = new UnifiedErrorReporter(sourceProvider);
		if (validators != null)
		{
			for (ValidatorRef validatorRef : validators)
			{
				try
				{
					IFileError[] errors = validatorRef.parseForErrors(path, source, sourceProvider, showErrors(),
							showWarnings(), showInfos());
					if (errors != null & errors.length > 0)
					{
						reporter.addErrors(errors);
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
							Messages.UnifiedErrorManager_ValidatorRefParseDelegationError, ex);
				}
			}
		}
		return filterMessages(reporter.getErrors());
	}

	private IFileError[] filterMessages(IFileError[] errors)
	{
		if (errors == null)
			return new IFileError[0];
		List<IFileError> newErrors = new ArrayList<IFileError>();
		for (IFileError error : errors)
		{
			if (filterMessage(error.getMessage()) != null)
			{
				newErrors.add(error);
			}
		}
		return (IFileError[]) newErrors.toArray(new IFileError[newErrors.size()]);
	}

	/**
	 * Pass in the error or warning string and this method has a chance to say whether or not to include it or just
	 * modify it
	 * 
	 * @param msg
	 * @return The filtered message or null if this message should not be displayed
	 */
	public String filterMessage(String msg)
	{
		if (isFiltered(IMarker.SEVERITY_INFO, msg))
		{
			return null;
		}
		else
		{
			return msg;
		}
	}

	/**
	 * 
	 */
	private void loadErrorDescriptors()
	{
		IPreferenceStore store = getPreferenceStore();
		if (store != null)
		{
			String editors = store.getString(IPreferenceConstants.IGNORE_PROBLEMS);
			_errorDescriptors = ErrorDescriptor.deserializeErrorDescriptors(editors);
		}
	}

	/**
	 * Is the current file error filtered?
	 * 
	 * @param severity
	 * @param message
	 * @return boolean
	 */
	protected boolean isFiltered(int severity, String message)
	{
		if (_errorDescriptors == null)
		{
			return false;
		}

		FileError fe = new FileError();
		// fe.setSeverity(severity);
		fe.setMessage(message);

		for (int i = 0; i < _errorDescriptors.length; i++)
		{
			ErrorDescriptor ed = _errorDescriptors[i];
			if (ed.matchesError(fe))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the preference store
	 * 
	 * @return IPreferenceStore
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		String property = event.getProperty();

		if (IPreferenceConstants.IGNORE_PROBLEMS.equals(property) && fileService != null)
		{
			fileService.forceContentChangedEvent();
		}
		else if (IPreferenceConstants.VALIDATORS_LIST.equals(property))
		{
			initializeValidators();
			fileService.forceContentChangedEvent();
		}
	}

	/**
	 * Retrieve registered validators, and apply selections made via preference settings
	 */
	private void initializeValidators()
	{
		ValidatorRef[] registeredValidators = ValidatorManager.getInstance().getValidators(mimeType);

		IPreferenceStore preferenceStore = getPreferenceStore();
		if (preferenceStore != null)
		{
			String property = preferenceStore.getString(IPreferenceConstants.VALIDATORS_LIST);
			if (property != null && property.length() > 0)
			{
				String[] preferredValidators = property.split(","); //$NON-NLS-1$
				ArrayList newValidators = new ArrayList();
				for (int i = 0; i < preferredValidators.length; i++)
				{
					for (int j = 0; j < registeredValidators.length; j++)
					{
						if (preferredValidators[i].equals(registeredValidators[j].getName()))
						{
							newValidators.add(registeredValidators[j]);
							continue;
						}
					}
				}
				validators = (ValidatorRef[]) newValidators.toArray(new ValidatorRef[0]);
			}
			else
			{
				// Default is to use all the registered validators
				validators = registeredValidators;
			}
		}
		else
		{
			// Why should preference store be null ?
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedErrorManager_PreferenceRetrievalError);

			validators = registeredValidators;
		}

	}

	/**
	 * @param partition
	 * @return
	 */
	public String processLanguagePartition(ITypedRegion partition, String source)
	{
		return source;
	}

	/**
	 * @param source
	 * @param start
	 * @param length
	 * @return
	 */
	protected String stripChars(String source, int start, int length)
	{
		char[] c = source.toCharArray();

		int end = start + length;

		if (end > c.length)
		{
			end = c.length;
		}

		for (int i = start; i < end; i++)
		{

			if (c[i] != '\r' && c[i] != '\n')
			{
				c[i] = ' ';
			}
		}

		return new String(c);
	}

}
