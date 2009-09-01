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
package com.aptana.ide.scripting.views;

import org.eclipse.ui.IWorkbenchPart;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.profiles.Profile;
import com.aptana.ide.editors.views.profiles.IProfilesViewEventListener;
import com.aptana.ide.editors.views.profiles.ProfilesViewEventTypes;
import com.aptana.ide.scripting.events.Event;
import com.aptana.ide.scripting.events.ProfilesAddCurrentEvent;
import com.aptana.ide.scripting.events.ProfilesAddDropEvent;
import com.aptana.ide.scripting.events.ProfilesAddProfileEvent;
import com.aptana.ide.scripting.events.ProfilesDeleteEvent;
import com.aptana.ide.scripting.events.ProfilesDeleteProfileEvent;
import com.aptana.ide.scripting.events.ProfilesLinkStateChangedEvent;
import com.aptana.ide.scripting.events.ProfilesMakeCurrentEvent;
import com.aptana.ide.scripting.events.ProfilesMakeStaticEvent;
import com.aptana.ide.scripting.events.ProfilesMoveDownEvent;
import com.aptana.ide.scripting.events.ProfilesMoveUpEvent;
import com.aptana.ide.scripting.events.ProfilesOpenProfileEvent;

/**
 * @author Kevin Lindsey
 */
public class ProfilesView extends View implements IProfilesViewEventListener
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -8939508057673096880L;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of ActiveLibrariesView
	 * 
	 * @param scope
	 * @param view
	 */
	public ProfilesView(Scriptable scope, IWorkbenchPart view)
	{
		super(scope, view);

		this.defineProperty("currentProfile", ProfilesView.class, PERMANENT); //$NON-NLS-1$
		this.defineProperty("isLinked", ProfilesView.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("profiles", ProfilesView.class, READONLY | PERMANENT); //$NON-NLS-1$

		// define functions
		String[] names = new String[] { "addProfile", "createProfile", "createDynamicProfile", "expandAll", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"expandProfile", "getPathFromURI", "getProfile", "getProfilePaths", "getProfiles", "getURIFromPath", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				"removeProfile", }; //$NON-NLS-1$
		this.defineFunctionProperties(names, ProfilesView.class, READONLY | PERMANENT);

		if (view != null && view instanceof com.aptana.ide.editors.views.profiles.ProfilesView)
		{
			com.aptana.ide.editors.views.profiles.ProfilesView profileView = (com.aptana.ide.editors.views.profiles.ProfilesView) view;
			profileView.addProfilesViewEventListener(this);
		}
	}

	/*
	 * Methods
	 */

	/**
	 * @see com.aptana.ide.editors.views.profiles.IProfilesViewEventListener#onProfilesViewEvent(com.aptana.ide.editors.views.profiles.ProfilesViewEvent)
	 */
	public void onProfilesViewEvent(com.aptana.ide.editors.views.profiles.ProfilesViewEvent e)
	{
		Event event = null;
		int eventType = e.getEventType();

		switch (eventType)
		{
			case ProfilesViewEventTypes.ADD:
			case ProfilesViewEventTypes.DROP:
/*done*/				event = new ProfilesAddDropEvent(this.getView(), eventType, e.getProfile(), e.getURIs());
				break;

			case ProfilesViewEventTypes.ADD_PROFILE:
/*done*/				event = new ProfilesAddProfileEvent(this.getView(), eventType, e.getProfile());
				break;

			case ProfilesViewEventTypes.DELETE_PROFILE:
/*done*/				event = new ProfilesDeleteProfileEvent(this.getView(), eventType, e.getProfile());
				break;

			case ProfilesViewEventTypes.OPEN:
/*done*/				event = new ProfilesOpenProfileEvent(this.getView(), eventType, e.getProfile());
				break;

			case ProfilesViewEventTypes.ADD_CURRENT:
/*done*/				event = new ProfilesAddCurrentEvent(this.getView(), eventType, e.getProfile());
				break;

			case ProfilesViewEventTypes.MAKE_STATIC:
/*done*/				event = new ProfilesMakeStaticEvent(this.getView(), eventType, e.getProfile());
				break;

			case ProfilesViewEventTypes.DELETE:
/*done*/				event = new ProfilesDeleteEvent(this.getView(), eventType, e.getURIs());
				break;

			case ProfilesViewEventTypes.MOVE_UP:
/*done*/				event = new ProfilesMoveUpEvent(this.getView(), eventType, e.getURIs());
				break;

			case ProfilesViewEventTypes.MOVE_DOWN:
/*done*/				event = new ProfilesMoveDownEvent(this.getView(), eventType, e.getURIs());
				break;
				
			case ProfilesViewEventTypes.LINK_ON:
/*partial*/				event = new ProfilesLinkStateChangedEvent(this.getView(), eventType, true);
				break;
				
			case ProfilesViewEventTypes.LINK_OFF:
/*partial*/				event = new ProfilesLinkStateChangedEvent(this.getView(), eventType, false);
				break;
				
			case ProfilesViewEventTypes.MAKE_CURRENT:
/*done*/				event = new ProfilesMakeCurrentEvent(this.getView(), eventType, e.getProfile());
				break;
				
			default:
				break;
		}

		if (event != null)
		{
			this.fireEventListeners(event);
		}
	}

	/**
	 * getIsLinked
	 * 
	 * @return boolean
	 */
	public boolean getIsLinked()
	{
		IWorkbenchPart part = this.getView();
		if (part != null)
		{
			com.aptana.ide.editors.views.profiles.ProfilesView profileView = (com.aptana.ide.editors.views.profiles.ProfilesView) part;
			return profileView.getLinkWithEditorState();
		}
		
		// Default in this case will be true
		return true;
	}
	
	/**
	 * getURIFromPath
	 * 
	 * @param path
	 * @return String
	 */
	public String getURIFromPath(String path)
	{
		return CoreUIUtils.getURI(path);
	}

	/**
	 * getPathFromURI
	 * 
	 * @param uri
	 * @return String
	 */
	public String getPathFromURI(String uri)
	{
		return CoreUIUtils.getPathFromURI(uri);
	}

	/**
	 * expandAll
	 */
	public void expandAll()
	{
		IWorkbenchPart part = this.getView();
		if (part != null)
		{
			com.aptana.ide.editors.views.profiles.ProfilesView profileView = (com.aptana.ide.editors.views.profiles.ProfilesView) part;
			profileView.expandAll();
		}
	}

	/**
	 * expandProfile
	 * 
	 * @param profilePath
	 */
	public void expandProfile(String profilePath)
	{
		IWorkbenchPart part = this.getView();
		if (part != null)
		{
			com.aptana.ide.editors.views.profiles.ProfilesView profileView = (com.aptana.ide.editors.views.profiles.ProfilesView) part;
			profileView.expandProfile(profilePath);
		}
	}

	/**
	 * addProfile
	 * 
	 * @param profile
	 */
	public void addProfile(Object profile)
	{
		if (profile instanceof Profile)
		{
			UnifiedEditorsPlugin.getDefault().getProfileManager().addProfile((Profile) profile);
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
		return UnifiedEditorsPlugin.getDefault().getProfileManager().createProfile(name, path, false);
	}

	/**
	 * createDynamicProfile
	 * 
	 * @param name
	 * @param path
	 * @return Profile
	 */
	public Profile createDynamicProfile(String name, String path)
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().createProfile(name, path, true);
	}

	/**
	 * removeProfile
	 * 
	 * @param name
	 */
	public void removeProfile(String name)
	{
		UnifiedEditorsPlugin.getDefault().getProfileManager().removeProfile(name);
	}

	/**
	 * setCurrentProfile
	 * 
	 * @param name
	 */
	public void setCurrentProfile(String name)
	{
		UnifiedEditorsPlugin.getDefault().getProfileManager().setCurrentProfile(name);
	}

	/**
	 * getCurrentProfile
	 * 
	 * @return String
	 */
	public Profile getCurrentProfile()
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getCurrentProfile();
	}

	/**
	 * getProfiles
	 * 
	 * @return String[]
	 */
	public String[] getProfilePaths()
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getProfilePaths();
	}

	/**
	 * getProfile return a specific named profile
	 * 
	 * @param name
	 * @return Profile
	 */
	public Profile getProfile(String name)
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getProfile(name);
	}

	/**
	 * getProfiles Returns all profiles
	 * 
	 * @return Profile[]
	 */
	public Profile[] getProfiles()
	{
		return UnifiedEditorsPlugin.getDefault().getProfileManager().getProfiles();
	}

}
