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

import java.util.ArrayList;

import org.eclipse.core.runtime.Preferences;

import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * Profile
 * 
 * @author Ingo Muschenetz
 */
public class Profile
{
	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$
	private static final String SDOC_EXTENSION = ".sdoc"; //$NON-NLS-1$
	private static String PROFILE_NAME_PREF_PREFIX = "profile."; //$NON-NLS-1$
	private static String PROFILE_LIST_KEY = "list"; //$NON-NLS-1$
	private static String PROFILE_FILES_KEY = "files"; //$NON-NLS-1$

	private ArrayList _listeners = new ArrayList();
	private ArrayList _fileList = new ArrayList();

	private String _profileName = null;
	private String _profileURI = null;
	private boolean _dynamic = false;

	/**
	 * Creates a new profile
	 * 
	 * @param name
	 *            The name of the profile. Needs clarification.
	 * @param uri
	 *            The "path" of the profile. This appears to be unused
	 */
	public Profile(String name, String uri)
	{
		this(name, uri, false);
	}

	/**
	 * Creates a new profile
	 * 
	 * @param name
	 *            The name of the profile
	 * @param uri
	 *            The "path" of the profile. Needs clarification.
	 * @param dynamic
	 *            Is this profile created manually or dynamically
	 */
	public Profile(String name, String uri, boolean dynamic)
	{
		this._profileName = name;
		this._profileURI = uri;
		this._dynamic = dynamic;
		load();
	}

	/**
	 * getName
	 * 
	 * @return Returns the profileName.
	 */
	public String getName()
	{
		return _profileName;
	}

	/**
	 * getPath
	 * 
	 * @return Returns the data.
	 */
	public String getURI()
	{
		return _profileURI;
	}

	/**
	 * addFiles
	 * 
	 * @param uris
	 */
	public void addURIs(String[] uris)
	{
		ArrayList ipaths = new ArrayList();

		for (int i = 0; i < uris.length; i++)
		{
			if (uris[i].endsWith(SDOC_EXTENSION) == false)
			{
				ipaths.add(new ProfileURI(uris[i], this));
			}
		}

		addURIsNoSave((ProfileURI[]) ipaths.toArray(new ProfileURI[0]));

		ipaths.clear();

		for (int i = 0; i < uris.length; i++)
		{
			if (uris[i].endsWith(SDOC_EXTENSION))
			{
				String jsFile = uris[i].substring(0, uris[i].lastIndexOf(SDOC_EXTENSION)) + JS_EXTENSION;
				int index = containsURI(jsFile);
				if (index != -1)
				{
					ProfileURI path = (ProfileURI) _fileList.get(index);
					ProfileURI[] currentChildren = path.getChildren();
					boolean contains = false;
					for (int j = 0; j < currentChildren.length; j++)
					{
						if (currentChildren[j].getURI().equals(uris[i]))
						{
							contains = true;
							break;
						}
					}
					if (!contains)
					{
						path.addChild(new ProfileURI(uris[i], this));
					}
				}
				else
				{
					ipaths.add(new ProfileURI(uris[i], this));
				}
			}
		}

		addURIs((ProfileURI[]) ipaths.toArray(new ProfileURI[0]));
	}

	/**
	 * @param uris
	 */
	public void addTransientURIs(String[] uris)
	{
		ArrayList ipaths = new ArrayList();

		for (int i = 0; i < uris.length; i++)
		{
			if (uris[i].endsWith(SDOC_EXTENSION) == false)
			{
				if (containsURI(uris[i]) == -1)
				{
					ipaths.add(new TransientProfileURI(uris[i], this));
				}
			}
		}

		addURIsNoSave((ProfileURI[]) ipaths.toArray(new ProfileURI[0]));

		ipaths.clear();

		for (int i = 0; i < uris.length; i++)
		{
			if (uris[i].endsWith(SDOC_EXTENSION))
			{
				String jsFile = uris[i].substring(0, uris[i].lastIndexOf(SDOC_EXTENSION)) + JS_EXTENSION;
				int index = containsURI(jsFile);
				if (index != -1)
				{
					ProfileURI path = (ProfileURI) _fileList.get(index);
					path.addChild(new TransientProfileURI(uris[i], this));
				}
				else
				{
					ipaths.add(new TransientProfileURI(uris[i], this));
				}

			}
		}

		addURIs((ProfileURI[]) ipaths.toArray(new ProfileURI[0]));
	}

	/**
	 * addFiles(IPath[])
	 * 
	 * @param paths
	 */
	private void addURIs(ProfileURI[] paths)
	{
		addURIsNoSave(paths);
		save();
	}

	/**
	 * addFilesNoSave
	 * 
	 * @param paths
	 */
	private void addURIsNoSave(ProfileURI[] paths)
	{
		for (int i = 0; i < paths.length; i++)
		{
			if (_fileList.contains(paths[i]) == false)
			{
				_fileList.add(paths[i]);
			}
		}
	}

	/**
	 * removeFiles
	 * 
	 * @param paths
	 */
	public void removeURIs(ProfileURI[] paths)
	{
		for (int i = 0; i < paths.length; i++)
		{
			if (_fileList.contains(paths[i]) != false)
			{
				_fileList.remove(paths[i]);
			}
		}
		save();
	}

	/**
	 * replaceAllURIs
	 * 
	 * @param oldURIs
	 * @param newURIs
	 */
	public void replaceAllURIs(String[] oldURIs, String[] newURIs)
	{
		removeURIsAsStringsNoSave(oldURIs);
		addTransientURIs(newURIs);
	}

	/**
	 * removeURIsAsStringsNoSave
	 * 
	 * @param uris
	 */
	public void removeURIsAsStrings(String[] uris)
	{
		// _isDirty = true;
		removeURIsAsStringsNoSave(uris);
		save();
	}

	/**
	 * removeURIsAsStringsNoSave
	 * 
	 * @param uris
	 */
	private void removeURIsAsStringsNoSave(String[] uris)
	{
		for (int i = 0; i < uris.length; i++)
		{
			ProfileURI path = new ProfileURI(uris[i]);

			if (_fileList.contains(path) != false)
			{
				_fileList.remove(path);
			}
		}
	}

	/**
	 * @param uris
	 */
	public void removeTransientURIs(String[] uris)
	{
		for (int j = 0; j < uris.length; j++)
		{
			int index = containsURI(uris[j]);

			if (index != -1)
			{
				_fileList.remove(index);
			}
		}

		if (uris.length > 0)
		{
			save();
		}
	}

	/**
	 * containsFile
	 * 
	 * @param uri
	 * @return int
	 */
	public int containsURI(String uri)
	{
		for (int i = 0; i < _fileList.size(); i++)
		{
			ProfileURI path = (ProfileURI) _fileList.get(i);

			if (path.getURI().equals(uri))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * @return ProfilePath[]
	 */
	public ProfileURI[] getURIs()
	{
		ProfileURI[] files = (ProfileURI[]) _fileList.toArray(new ProfileURI[] {});
		return files;
	}

	/**
	 * getURIsIncludingChildren
	 * 
	 * @return ProfileURI[]
	 */
	public ProfileURI[] getURIsIncludingChildren()
	{
		ProfileURI[] files = (ProfileURI[]) _fileList.toArray(new ProfileURI[] {});

		ArrayList list = new ArrayList();

		for (int i = 0; i < files.length; i++)
		{
			list.add(files[i]);

			ProfileURI[] children = files[i].getChildren();

			for (int j = 0; j < children.length; j++)
			{
				list.add(children[j]);
			}
		}

		return (ProfileURI[]) list.toArray(new ProfileURI[] {});
	}

	/**
	 * getURIsIncludingChildrenAsStrings
	 * 
	 * @return String[]
	 */
	public String[] getURIsIncludingChildrenAsStrings()
	{
		ProfileURI[] files = this.getURIsIncludingChildren();
		String[] result = new String[files.length];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = files[i].getURI();
		}

		return result;
	}

	/**
	 * getFilesAsStrings
	 * 
	 * @return String[]
	 */
	public String[] getURIsAsStrings()
	{
		return getURIsAsStrings(_fileList);
	}

	/**
	 * getFilesAsStrings
	 * 
	 * @param fileList
	 * @return String[]
	 */
	public String[] getURIsAsStrings(ArrayList fileList)
	{
		String[] files = new String[fileList.size()];

		for (int i = 0; i < fileList.size(); i++)
		{
			files[i] = ((ProfileURI) fileList.get(i)).getURI();
		}

		return files;
	}

	/**
	 * moveURIsUp
	 * 
	 * @param paths
	 */
	public void moveURIsUp(ProfileURI[] paths)
	{
		for (int i = 0; i < paths.length; i++)
		{
			if (_fileList.contains(paths[i]) != false)
			{
				ProfileURI path = paths[i];

				int index = this._fileList.indexOf(path);

				if (index <= 0)
				{
					return;
				}

				_fileList.remove(path);
				_fileList.add(index - 1, path);
			}
		}
		save();
	}

	/**
	 * moveURIsDown
	 * 
	 * @param paths
	 */
	public void moveURIsDown(ProfileURI[] paths)
	{
		for (int i = 0; i < paths.length; i++)
		{
			if (_fileList.contains(paths[i]) != false)
			{
				ProfileURI path = paths[i];

				int index = this._fileList.indexOf(path);

				if (index >= (_fileList.size() - 1))
				{
					return;
				}

				_fileList.remove(path);
				_fileList.add(index + 1, path);
			}
		}
		save();
	}

	/**
	 * clear
	 */
	public void clear()
	{
		_fileList.clear();
		save();
	}

	/**
	 * load
	 */
	public void load()
	{
		if (!_dynamic)
		{
			// try/catch check for use in UnitTests (which may not have the plugin loaded)
			try
			{
				Preferences prefs = UnifiedEditorsPlugin.getDefault().getPluginPreferences();
				String pathsString = prefs.getString(getProfileFilesKey(PROFILE_FILES_KEY));
				if (pathsString != null && !pathsString.trim().equals(StringUtils.EMPTY))
				{
					load(pathsString.split(",")); //$NON-NLS-1$
				}
			}
			catch (Exception ex)
			{
			}
		}
	}

	/**
	 * load
	 * 
	 * @param paths
	 */
	public void load(String[] paths)
	{
		_fileList.clear();
		addURIs(paths);
	}

	/**
	 * save
	 */
	public void save()
	{
		if (!_dynamic)
		{
			if (PluginUtils.isPluginLoaded(UnifiedEditorsPlugin.getDefault()))
			{
				Preferences prefs = UnifiedEditorsPlugin.getDefault().getPluginPreferences();
				String pathsString = profileURIsToString(this.getURIsIncludingChildren());

				prefs.setValue(getProfileFilesKey(PROFILE_FILES_KEY), pathsString);

				if (pathsString.length() > 0)
				{
					addProfileToMasterList(prefs);
				}
				else
				{
					removeProfileFromMasterList(prefs);
				}

				UnifiedEditorsPlugin.getDefault().savePluginPreferences();
			}
		}

		fireProfileChangeEvent(this);
	}

	/**
	 * addProfileToMasterList
	 * 
	 * @param prefs
	 */
	private void addProfileToMasterList(Preferences prefs)
	{
		boolean found = false;

		String profilesList = prefs.getString(getProfileListKey());

		if (profilesList != null && profilesList.length() > 0)
		{
			String[] names = profilesList.split(","); //$NON-NLS-1$

			for (int i = 0; i < names.length; i++)
			{
				if (names[i].equals(constructProfileKey()))
				{
					found = true;
					break;
				}
			}
		}

		if (!found)
		{
			if (profilesList == null || profilesList.length() == 0)
			{
				prefs.setValue(getProfileListKey(), constructProfileKey());
			}
			else
			{
				prefs.setValue(getProfileListKey(), profilesList + "," + constructProfileKey()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Constructs the "key" for this profile
	 * 
	 * @return String
	 */
	private String constructProfileKey()
	{
		return this.getName() + "=" + this.getURI(); //$NON-NLS-1$
	}

	/**
	 * removeProfileFromMasterList
	 * 
	 * @param prefs
	 */
	private void removeProfileFromMasterList(Preferences prefs)
	{
		String newList = StringUtils.EMPTY;
		String profilesList = prefs.getString(getProfileListKey());

		if (profilesList != null && profilesList.length() > 0)
		{
			String[] names = profilesList.split(","); //$NON-NLS-1$
			for (int i = 0; i < names.length; i++)
			{
				if (names[i].equals(constructProfileKey()) == false)
				{
					if (newList.length() > 0)
					{
						newList += ","; //$NON-NLS-1$
					}
					newList += names[i];
				}
			}
		}

		prefs.setValue(getProfileListKey(), newList);
	}

	/**
	 * fireProfileChangeEvent
	 * 
	 * @param p
	 */
	public void fireProfileChangeEvent(Profile p)
	{
		for (int i = 0; i < _listeners.size(); i++)
		{
			IProfileChangeListener listener = (IProfileChangeListener) _listeners.get(i);
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
		_listeners.add(l);
	}

	/**
	 * removeProfileChangeListener
	 * 
	 * @param l
	 */
	public void removeProfileChangeListener(IProfileChangeListener l)
	{
		_listeners.remove(l);
	}

	/**
	 * pathsToString
	 * 
	 * @param paths
	 * @return String
	 */
	public String profileURIsToString(ProfileURI[] paths)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < paths.length; i++)
		{
			if (sb.length() != 0)
			{
				sb.append(","); //$NON-NLS-1$
			}
			sb.append(paths[i].getURI());
		}
		String str = sb.toString();
		return str;
	}

	/**
	 * getProfileFilesKey
	 * 
	 * @param keyName
	 * @return String
	 */
	private String getProfileFilesKey(String keyName)
	{
		return PROFILE_NAME_PREF_PREFIX + _profileURI + "." + keyName; //$NON-NLS-1$
	}

	/**
	 * getProfileListKey
	 * 
	 * @return String
	 */
	public static String getProfileListKey()
	{
		return PROFILE_NAME_PREF_PREFIX + "." + PROFILE_LIST_KEY; //$NON-NLS-1$
	}

	/**
	 * setDynamic
	 * 
	 * @param dynamic
	 */
	public void setDynamic(boolean dynamic)
	{
		this._dynamic = dynamic;
	}

	/**
	 * isDynamic
	 * 
	 * @return boolean
	 */
	public boolean isDynamic()
	{
		return this._dynamic;
	}

	/**
	 * Test to see if two profiles are equivalent
	 * 
	 * @param p
	 * @return How we test equivalency: case 1: New profile is null. false case 2: Old profile was null, new profile is
	 *         empty. false case 3. Both profiles do not have same base URI. false case 4: Old has 5 files, new has
	 *         none. false case 5: Old has no files, new has 5. false case 6: Old has 5 files, new has 5. Files are
	 *         different names. false case 7: Old has 5 files, new has 5. Files are identical names, but different
	 *         order. false case 8: Old has 5 files, new has 5. All names are identical, and identical order. true.
	 */
	public boolean isEquivalent(Profile p)
	{
		if (p == null)
		{
			return false;
		}

		if (!this.getURI().equals(p.getURI()))
		{
			return false;
		}

		ProfileURI[] thisUris = this.getURIsIncludingChildren();
		ProfileURI[] compareUris = p.getURIsIncludingChildren();
		if (thisUris.length != compareUris.length || thisUris.length == 0)
		{
			return false;
		}

		for (int i = 0; i < thisUris.length; i++)
		{
			ProfileURI profileURI = thisUris[i];
			if (profileURI.equals(compareUris))
			{
				return false;
			}
		}

		return true;
	}
}
