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
package com.aptana.ide.editors.profiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.Preferences;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.DocumentSourceProvider;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.IFileServiceFactory;
import com.aptana.ide.editors.unified.ILanguageEnvironment;
import com.aptana.ide.editors.unified.utils.IUpdaterThreadUpdateable;
import com.aptana.ide.editors.unified.utils.UpdaterThread;
import com.aptana.ide.parsing.IParseState;

/**
 * ProfileManager
 */
public class ProfileManager implements IUpdaterThreadUpdateable
{
	/**
	 * The delay before we trigger any apply profile delays
	 */
	public static int APPLY_PROFILE_DELAY = 1000;

	/**
	 * DEFAULT_PROFILE_NAME
	 */
	public static final String DEFAULT_PROFILE_NAME = "Default Profile"; //$NON-NLS-1$

	/**
	 * staticProtocol
	 */
	static final String staticProtocol = "static://"; //$NON-NLS-1$

	/**
	 * titleLabel
	 */
	static final String titleLabel = " (Auto-created)"; //$NON-NLS-1$

	/**
	 * DEFAULT_PROFILE_PATH
	 */
	public static final String DEFAULT_PROFILE_PATH = staticProtocol + DEFAULT_PROFILE_NAME;

	Profile currentProfile = null;

	ArrayList listeners = new ArrayList();

	ArrayList appliedListeners = new ArrayList();

	HashMap profiles = new HashMap();

	private HashMap languageEnvironments = new HashMap();

	private HashMap languageFactories = new HashMap();

	// private String _activeFilePath = StringUtils.EMPTY;
	// private HashMap indexToPath = new HashMap();
	// private HashMap indexToContext = new HashMap();

	private UpdaterThread _applyProfilesThread = null;

	private HashMap changeListenerHash = new HashMap();

	/**
	 * ProfileManager
	 */
	public ProfileManager()
	{
		this(true);
	}

	/**
	 * ProfileManager
	 * 
	 * @param threaded
	 */
	public ProfileManager(boolean threaded)
	{
		if (threaded)
		{
			startProfileThread();
		}

		loadStaticProfiles();
	}

	private void startProfileThread()
	{
		_applyProfilesThread = new UpdaterThread(this, APPLY_PROFILE_DELAY, Messages.ProfileManager_ApplyProfiles);
		_applyProfilesThread.start();
	}

	private void loadStaticProfiles()
	{
		boolean defaultCreated = false;

		try
		{
			Preferences prefs = UnifiedEditorsPlugin.getDefault().getPluginPreferences();
			String profilesList = prefs.getString(Profile.getProfileListKey());

			if (profilesList.length() == 0)
			{
				return;
			}

			String[] list = profilesList.split(","); //$NON-NLS-1$
			for (int i = 0; i < list.length; i++)
			{
				if (list[i].length() > 0)
				{
					Trace.info(Messages.ProfileManager_LoadingProfile + list[i]);
					String[] parts = list[i].split("="); //$NON-NLS-1$
					String name = parts[0];
					String path = parts[1];

					createProfile(name, path);

					if (list[i].equals(DEFAULT_PROFILE_NAME))
					{
						defaultCreated = true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			// try/catch check for use in UnitTests (which may not have the
			// plugin loaded)
		}
		finally
		{
			if (!defaultCreated)
			{
				createProfile(DEFAULT_PROFILE_NAME, DEFAULT_PROFILE_PATH);
			}

			this.setCurrentProfile(DEFAULT_PROFILE_PATH);
		}

	}

	/**
	 * createProfile
	 * 
	 * @param name
	 * @param path
	 * @return Profile
	 */
	public Profile createProfile(String name, String path)
	{
		return createProfile(name, path, false);
	}

	/**
	 * createProfile
	 * 
	 * @param name
	 * @param path
	 * @param dynamic
	 * @return Profile
	 */
	public Profile createProfile(String name, String path, boolean dynamic)
	{
		Profile profile = new Profile(name, path, dynamic);
		addProfile(profile);
		return profile;
	}

	/**
	 * Adds a profile to the list of profiles
	 * 
	 * @param profile
	 */
	public void addProfile(Profile profile)
	{
		IProfileChangeListener pcl = new IProfileChangeListener()
		{
			public void onProfileChanged(Profile p)
			{
				fireProfileChangeEvent(p);
				applyProfiles();
			}
		};
		changeListenerHash.put(profile.getURI(), pcl);
		profile.addProfileChangeListener(pcl);

		profiles.put(profile.getURI(), profile);

		fireProfileChangeEvent(profile);
	}

	/**
	 * Removes a profile from the list of profiles
	 * 
	 * @param path
	 *            The path of the profile to remove
	 */
	public void removeProfile(String path)
	{
		IProfileChangeListener pcl = null;
		if (changeListenerHash.containsKey(path))
		{
			pcl = (IProfileChangeListener) changeListenerHash.get(path);
			changeListenerHash.remove(path);
		}
		if (profiles.containsKey(path))
		{
			Profile p = getProfile(path);
			p.clear();

			if (pcl != null)
			{
				p.removeProfileChangeListener(pcl);
			}

			profiles.remove(path);
			fireProfileChangeEvent(p);
			applyProfiles();
		}
	}

	/**
	 * setCurrentProfile
	 * 
	 * @param path
	 */
	public void setCurrentProfile(String path)
	{
		Profile p = getProfile(path);
		if (p == null)
		{
			p = getDefaultProfile();
		}
		setCurrentProfile(p);
	}

	/**
	 * Returns the default profile path
	 * 
	 * @return - default profile
	 */
	public Profile getDefaultProfile()
	{
		return (Profile) profiles.get(DEFAULT_PROFILE_PATH);
	}

	/**
	 * setCurrentProfile
	 * 
	 * @param profile
	 */
	public void setCurrentProfile(Profile profile)
	{

		this.currentProfile = profile;
		fireProfileChangeEvent(this.currentProfile);
		applyProfiles();
	}

	/**
	 * getCurrentProfile
	 * 
	 * @return Profile
	 */
	public Profile getCurrentProfile()
	{
		return this.currentProfile;
	}

	/**
	 * isCurrentProfile
	 * 
	 * @param profile
	 * @return boolean
	 */
	public boolean isCurrentProfile(Profile profile)
	{
		return this.currentProfile == profile;
	}

	/**
	 * addLanguageSupport
	 * 
	 * @param mimeType
	 * @param lang
	 * @param factory
	 */
	public void addLanguageSupport(String mimeType, ILanguageEnvironment lang, IFileServiceFactory factory)
	{
		languageEnvironments.put(mimeType, lang);
		if (!languageFactories.containsKey(mimeType))
		{
			languageFactories.put(mimeType, factory);
		}
	}

	/**
	 * Returns an array of the current Profiles
	 * 
	 * @return Profile[]
	 */
	public Profile[] getProfiles()
	{
		Profile[] array = (Profile[]) profiles.values().toArray(new Profile[0]);
		return array;
	}

	/**
	 * Return the names of all the current profiles
	 * 
	 * @return ProfileNames
	 */
	public String[] getProfilePaths()
	{
		return (String[]) profiles.keySet().toArray(new String[0]);
	}

	/**
	 * Returns the named Profile
	 * 
	 * @param path
	 * @return Profile
	 */
	public Profile getProfile(String path)
	{
		return (Profile) profiles.get(path);
	}

	/**
	 * Return the total number of files across all profiles.
	 * 
	 * @return TotalFileCount
	 */
	public int getTotalFileCount()
	{
		Profile[] profiles = getProfiles();

		int count = 0;

		for (int i = 0; i < profiles.length; i++)
		{
			count += profiles[i].getURIs().length;
		}

		return count;
	}

	/**
	 * fireProfileChangeEvent
	 * 
	 * @param p
	 */
	public void fireProfileChangeEvent(Profile p)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			IProfileChangeListener listener = (IProfileChangeListener) listeners.get(i);
			listener.onProfileChanged(p);
		}
	}

	/**
	 * addProfileChangeListener
	 * 
	 * @param l
	 */
	public void addProfileChangeListener(IProfileChangeListener l)
	{
		listeners.add(l);
	}

	/**
	 * removeProfileChangeListener
	 * 
	 * @param l
	 */
	public void removeProfileChangeListener(IProfileChangeListener l)
	{
		listeners.remove(l);
	}

	/**
	 * fireProfileChangeEvent
	 * 
	 * @param p
	 * @param state
	 */
	public void fireProfileAppliedEvent(ProfileURI p, boolean state)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			IProfileAppliedListener listener = (IProfileAppliedListener) appliedListeners.get(i);
			listener.onProfileApplied(p, state);
		}
	}

	/**
	 * addProfileChangeListener
	 * 
	 * @param l
	 */
	public void addProfileAppliedListener(IProfileAppliedListener l)
	{
		appliedListeners.add(l);
	}

	/**
	 * removeProfileChangeListener
	 * 
	 * @param l
	 */
	public void removeProfileChangeListener(IProfileAppliedListener l)
	{
		appliedListeners.remove(l);
	}

	/**
	 * Resets and applies the current profile (was applyProfileInternal)
	 */
	public void onUpdaterThreadUpdate()
	{
		resetIconStatus();
		resetEnvironment();
		resetAndApply();
	}

	/**
	 * resetIconStatus
	 */
	private void resetIconStatus()
	{
		if (currentProfile != null)
		{
			ProfileURI[] paths = currentProfile.getURIsIncludingChildren();

			for (int i = 0; i < paths.length; i++)
			{
				fireProfileAppliedEvent(paths[i], false);
			}
		}
	}

	/**
	 * resetEnvironment
	 */
	public void resetEnvironment()
	{
		Collection langs = languageEnvironments.values();
		
		for (Iterator iter = langs.iterator(); iter.hasNext();)
		{
			ILanguageEnvironment lang = (ILanguageEnvironment) iter.next();
			lang.cleanEnvironment();
		}
		
		// let special file handlers reset their environments
		for (ProfileFileTypeInfo info : ProfileFileTypeManager.getInstance().getAllInfos())
		{
			// NOTE: we get a null, for example, when the AIR license has expired
			if (info.processor != null)
			{
				info.processor.cleanEnvironment();
			}
		}
	}

	/**
	 * resetAndApply
	 */
	private void resetAndApply()
	{

		if (currentProfile == null)
		{
			return;
		}

		// build hash maps
		HashSet allURIs = new HashSet(Arrays.asList(FileContextManager.getKeySet()));
		String activeEditorURI = CoreUIUtils.getActiveEditorURI();
		HashSet currentProfileURIs = new HashSet(Arrays.asList(currentProfile.getURIsIncludingChildrenAsStrings()));

		// remove active editor and all current profiles
		allURIs.remove(activeEditorURI);
		allURIs.remove(currentProfileURIs);

		// Set file index of remaining uri's to -1
		deactivateFileContexts(allURIs);

		reindexCurrentProfile();

		// make sure active editor is on top of all other files
		reindexActiveEditor(activeEditorURI, currentProfileURIs);
	}

	/**
	 * deactivateFileContexts
	 * 
	 * @param allURIs
	 */
	private void deactivateFileContexts(HashSet allURIs)
	{
		for (Iterator iter = allURIs.iterator(); iter.hasNext();)
		{
			String uri = (String) iter.next();
			FileService fileContext = FileContextManager.get(uri);

			if (fileContext != null)
			{
				IParseState parseState = fileContext.getParseState();

				if (parseState != null)
				{
					parseState.setFileIndex(FileContextManager.DEFAULT_FILE_INDEX);
				}
			}
		}
	}

	/**
	 * reindexActiveEditor
	 * 
	 * @param activeEditorURI
	 * @param currentProfileURIs
	 */
	private void reindexActiveEditor(String activeEditorURI, HashSet currentProfileURIs)
	{
		FileService activeFileContext = FileContextManager.get(activeEditorURI);

		if (activeFileContext != null && currentProfileURIs.contains(activeEditorURI) == false)
		{
			IParseState parseState = activeFileContext.getParseState();

			if (parseState != null)
			{
				parseState.setFileIndex(FileContextManager.CURRENT_FILE_INDEX);
			}

			// This adds the contents to the environment
			activeFileContext.forceContentChangedEvent();
		}
	}

	/**
	 * reindexCurrentProfile
	 * 
	 * @param currentProfileURIs
	 */
	private void reindexCurrentProfile()
	{
		int fileIndex = 0;

		String[] currentProfileURIs = currentProfile.getURIsIncludingChildrenAsStrings();
		String[] openEditorsArray = CoreUIUtils.getOpenEditorPaths();
		Set<String> openEditors = new HashSet<String>(Arrays.asList(openEditorsArray));

		for (String uri : currentProfileURIs)
		{
			// Only do this if there are any open editors, otherwise, there are
			// no language factories
			// so will throw exception
			if (openEditorsArray.length > 0)
			{
				// See if we have a profile file processor for this file
				// extension. Ultimately, all file types should be processed
				// this way, but this was added to allow AIR to support
				// script elements pointing to SWF files
				String extension = FileUtils.getExtension(uri);
				ProfileFileTypeInfo info = ProfileFileTypeManager.getInstance().getInfo(extension);
				
				if (info != null && info.processor != null)
				{
					if (info.processor.processFile(uri, fileIndex++))
					{
						fireProfileAppliedEvent(new ProfileURI(uri, currentProfile), true);
						continue;
					}
				}
				
				// Effectively will only download items listed as *.js files
				String mimeType = this.computeMIMEType(uri);

				if (mimeType == null)
				{
					IdeLog.logInfo(
						UnifiedEditorsPlugin.getDefault(),
						StringUtils.format(Messages.ProfileManager_MimeTypeError, uri)
					);
				}
				else
				{
					FileService fileContext = FileContextManager.get(uri);
	
					if (fileContext == null ||
					// !bug: this causes a full parse, but we haven't set
							// the file index yet
							(fileContext.getSourceProvider() instanceof DocumentSourceProvider && openEditors.contains(uri) == false))
					{
						IFileServiceFactory factory = (IFileServiceFactory) this.languageFactories.get(mimeType);
						
						if (factory == null)
						{
							IdeLog.logError(
								UnifiedEditorsPlugin.getDefault(),
								StringUtils.format(Messages.ProfileManager_ServiceFactoryError, mimeType)
							);
							continue;
						}
	
						String path = CoreUIUtils.getPathFromURI(uri);
						File file = new File(path);
						
						if (file.exists() == false)
						{
							continue;
						}
	
						FileSourceProvider fsp = new FileSourceProvider(file);
						fileContext = factory.createFileService(fsp);
						FileContextManager.add(uri, fileContext);
					}
	
					// Set the file index
					fileContext.getParseState().setFileIndex(fileIndex++);
	
					// **NOTE:** we need to do a full parse here to get the lexemes
					// etc refreshed if we don't do this, then @id files won't see each other on
					// F3 as they are on the lexeme parse. Also they need to be profile wide.
					// we can fix this soon....
					fileContext.doFullParse();
	
					// This adds the contents to the environment
					fileContext.forceContentChangedEvent();
				}
			}

			fireProfileAppliedEvent(new ProfileURI(uri, currentProfile), true);
		}
	}

	/**
	 * computeMIMEType
	 * 
	 * @param uri
	 * @return String
	 */
	private String computeMIMEType(String uri)
	{
		String mimeType = null; // TODO: we need to make this generic based on
		// file registration

		if (uri.toLowerCase().endsWith(".js") //$NON-NLS-1$
				|| uri.toLowerCase().endsWith(".sdoc")) { //$NON-NLS-1$
			mimeType = "text/javascript"; //$NON-NLS-1$
		}
		// else if (uri.endsWith(".htm") || uri.endsWith(".html"))
		// {
		// mimeType = "text/html";
		// }
		// else if (uri.endsWith(".css"))
		// {
		// mimeType = "text/css";
		// }

		return mimeType;
	}

	/**
	 * applyProfiles Sets the apply profile thread to dirty so that the internal method will be called
	 */
	public void applyProfiles()
	{
		if (_applyProfilesThread != null)
		{
			_applyProfilesThread.setDirty();
		}
		else
		{
			onUpdaterThreadUpdate();
		}
	}

	/**
	 * refreshEnvironment
	 */
	public void refreshEnvironment()
	{
		Profile[] profiles = getProfiles();

		for (int i = 0; i < profiles.length; i++)
		{
			fireProfileChangeEvent(profiles[i]);
		}

		applyProfiles();
	}

	/**
	 * @param profile
	 * @return - static profile
	 */
	public Profile makeProfileStatic(Profile profile)
	{
		String profileName = profile.getName();
		String[] fileListArray = profile.getURIsAsStrings();
		String path = profile.getURI();
		boolean wasSelected = false;

		if (path == this.getCurrentProfile().getURI())
		{
			wasSelected = true;
		}

		String newPath = staticProtocol + path;

		removeProfile(path);

		if (profileName.indexOf(titleLabel) != -1)
		{
			profileName = profileName.substring(0, profileName.length() - titleLabel.length());
		}

		Profile newProfile = createProfile(profileName, newPath);

		newProfile.addURIs(fileListArray);

		if (wasSelected)
		{
			setCurrentProfile(newProfile.getURI());
		}

		return newProfile;
	}

}
