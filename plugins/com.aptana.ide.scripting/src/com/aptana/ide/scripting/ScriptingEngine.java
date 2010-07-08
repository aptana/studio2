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
package com.aptana.ide.scripting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.Trace;
import com.aptana.ide.core.ui.BaseTimingStartup;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.WebPerspectiveFactory;
import com.aptana.ide.core.ui.editors.ISaveAsEvent;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileContextContentEvent;
import com.aptana.ide.editors.unified.IFileContextListener;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IUnifiedBracketMatcherListener;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.IFileErrorListener;
import com.aptana.ide.editors.views.actions.ActionsView;
import com.aptana.ide.scripting.editors.Editor;
import com.aptana.ide.scripting.editors.EditorType;
import com.aptana.ide.scripting.events.ContentChangedEvent;
import com.aptana.ide.scripting.events.ErrorsChangedEvent;
import com.aptana.ide.scripting.events.PartActivatedEvent;
import com.aptana.ide.scripting.events.PartClosedEvent;
import com.aptana.ide.scripting.events.PartDeactivatedEvent;
import com.aptana.ide.scripting.events.PartOpenedEvent;
import com.aptana.ide.scripting.preferences.IPreferenceConstants;
import com.aptana.ide.scripting.views.GenericScriptableView;
import com.aptana.ide.scripting.views.ScriptableView;
import com.aptana.ide.scripting.views.View;
import com.aptana.ide.scripting.views.Views;

/**
 * @author Paul Colton
 * @author Kevin Lindsey
 */
public class ScriptingEngine extends BaseTimingStartup implements IFileErrorListener, IFileContextListener,
		IUnifiedBracketMatcherListener, ISaveAsEvent
{
	/**
	 * Default start port for scripting server
	 */
	public static final int SCRIPTING_SERVER_START_PORT = 9000;
	/*
	 * Fields
	 */
	private static Map<String, Object> supportedPerspectives;
	private static ScriptingEngine instance = null;

	private Global _global = null;
	private ScriptingHttpServer _server = null;
	private IPerspectiveListener _perspectiveListener;
	private IPartListener _partListener;
	private boolean _loaded;
	private ScriptingConsole _console;
	private MessageConsoleStream _consoleStream;

	/*
	 * Properties
	 */

	/**
	 * The Scripting Engine singleton
	 * 
	 * @return ScriptingEngine
	 */
	public static ScriptingEngine getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptingEngine();
		}

		return instance;
	}

	/**
	 * Get the global used by all scripts
	 * 
	 * @return Global
	 */
	public Global getGlobal()
	{
		return this._global;
	}

	/*
	 * Constructors
	 */

	static
	{
		supportedPerspectives = new HashMap<String, Object>();
		supportedPerspectives.put("com.aptana.ide.js.ui.WebPerspective", new Object()); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of ScriptingEngine
	 */
	public ScriptingEngine()
	{
		if (instance != null)
		{
			throw new IllegalStateException(Messages.ScriptingEngine_ScriptingEngine_Is_Singleton);
		}

		ScriptingEngine.instance = this;
	}

	/*
	 * Methods
	 */

	/**
	 * Adds the perspective listener to the current window
	 * 
	 * @param window
	 */
	private void addListenerToWindow(IWorkbenchWindow window)
	{
		IWorkbenchPage page = window.getActivePage();
		
		if (page != null)
		{
			IPerspectiveDescriptor perspective = page.getPerspective();
	
			if (perspective != null && this.perspectiveActivated(page, perspective) == false)
			{
				window.addPerspectiveListener(this._perspectiveListener);
			}
		}
	}

	/**
	 * bindToWorkbench
	 */
	private void bindToWorkbench()
	{
		final ScriptingEngine me = this;

		me._partListener = new IPartListener()
		{
			public void partActivated(IWorkbenchPart part)
			{
				String secondaryId = null;

				if (part instanceof IUnifiedEditor)
				{
					IUnifiedEditor editor = (IUnifiedEditor) part;

					editor.getFileContext().addLongDelayedFileListener(me);
					editor.getFileContext().addErrorListener(me);
					editor.addSaveAsListener(me);
				}

				if (part instanceof ActionsView)
				{
					View view = (View) me._global.getViews().getActionsView();
					
					if (view != null)
					{
						view.setView(part);
					}
				}
				else if (part instanceof GenericScriptableView)
				{
					secondaryId = ((GenericScriptableView) part).getViewSite().getSecondaryId();
					ScriptableView view = me._global.getViews().getView(secondaryId);
					
					if (view != null)
					{
						view.setView(part);
					}
				}

				firePartActivated(part, secondaryId);
			}

			public void partDeactivated(IWorkbenchPart part)
			{
				String secondaryId = null;

				if (part instanceof IUnifiedEditor)
				{
					IUnifiedEditor editor = (IUnifiedEditor) part;
					IFileService context = editor.getFileContext();
					
					if (context != null)
					{
						context.removeLongDelayedFileListener(me);
						context.removeErrorListener(me);
					}
				}

				if (part instanceof ActionsView)
				{
					View view = (View) me._global.getViews().getActionsView();
					
					if (view != null)
					{
						view.setView(null);
					}
				}
				else if (part instanceof GenericScriptableView)
				{
					secondaryId = ((GenericScriptableView) part).getViewSite().getSecondaryId();
					ScriptableView view = me._global.getViews().getView(secondaryId);
					
					if (view != null)
					{
						view.setView(null);
					}
				}

				firePartDeactivated(part, secondaryId);
			}

			public void partBroughtToTop(IWorkbenchPart part)
			{
			}

			public void partOpened(IWorkbenchPart part)
			{
				String secondaryId = null;

//				if (part.getSite().getId().equals("com.aptana.ide.js.ui.views.profilesView")) //$NON-NLS-1$
//				{
//					me._global.getViews().setInternalProfilesView(part);
//				}
				
//				if (part instanceof ContentOutline)
//				{
//					View view = (View) me._global.getViews().getOutlineView();
//					
//					if (view != null)
//					{
//						view.setView(part);
//					}
//				}
//				else
				if (part instanceof ActionsView)
				{
					View view = (View) me._global.getViews().getActionsView();
					
					if (view != null)
					{
						view.setView(part);
					}
				}
				// Keep this test last in case we're working with sub-classes of GenericScriptableView
				else if (part instanceof GenericScriptableView)
				{
					secondaryId = ((GenericScriptableView) part).getViewSite().getSecondaryId();
					ScriptableView view = me._global.getViews().getView(secondaryId);
					
					if (view != null)
					{
						view.setView(part);
					}
				}

				firePartOpened(part, secondaryId);
			}

			public void partClosed(IWorkbenchPart part)
			{
				String secondaryId = null;

				if (part instanceof IUnifiedEditor)
				{
					IUnifiedEditor editor = (IUnifiedEditor) part;
					
					editor.getFileContext().removeLongDelayedFileListener(me);
					editor.getFileContext().removeErrorListener(me);
					editor.removeSaveAsListener(me);
				}

				if (part instanceof ActionsView)
				{
					View view = (View) me._global.getViews().getActionsView();
					
					if (view != null)
					{
						view.setView(null);
					}
				}
				else if (part instanceof GenericScriptableView)
				{
					secondaryId = ((GenericScriptableView) part).getViewSite().getSecondaryId();
					
					ScriptableView view = me._global.getViews().getView(secondaryId);
					if(view != null)
					{
						view.setView(null);
					}
				}

				firePartClosed(part, secondaryId);
			}
		};

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

				if (window != null)
				{
					window.getPartService().addPartListener(me._partListener);
				}
			}
		});
	}

	/**
	 * @see com.aptana.ide.core.ui.editors.ISaveAsEvent#onSaveAs(org.eclipse.ui.IEditorPart, java.io.File,
	 *      java.io.File)
	 */
	public void onSaveAs(IEditorPart part, File oldFile, File newFIle)
	{
		firePartSaveAs(part, oldFile, newFIle);
	}

	/**
	 * runScript
	 * 
	 * @param scriptID
	 * @param callback
	 * @param args
	 */
	public void fireCallback(String scriptID, Object callback, Object[] args)
	{
		try
		{
			// get scope
			ScriptInfo info = this._global.getScriptInfo(scriptID);

			if (info != null)
			{
				Scriptable scope = info.getScope();

				// get display
				final IWorkbench workbench = PlatformUI.getWorkbench();
				Display display = workbench.getDisplay();

				// execute callback in the correct thread
				display.syncExec(new ScriptThread(scope, callback, args, info.getClassLoader()));
			}
			else
			{
				String message = StringUtils.format(Messages.ScriptingEngine_Script_Id_Not_Found, scriptID);
				
				IdeLog.logError(ScriptingPlugin.getDefault(), message);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingEngine_Error, e);
		}
	}

	/**
	 * firePartActivated
	 * 
	 * @param part
	 */
	private void firePartActivated(IWorkbenchPart part, String secondaryId)
	{
		Views views = this._global.getViews();
		String id = part.getSite().getId();
		String title = part.getTitle();
		String path = StringUtils.EMPTY;

		if (part instanceof IEditorPart)
		{
			path = getPathFromPart((IEditorPart) part);
		}

		PartActivatedEvent event = new PartActivatedEvent(views, title, id, secondaryId, path);

		views.fireEventListeners(event);
	}

	/**
	 * firePartDeactivated
	 * 
	 * @param part
	 */
	private void firePartDeactivated(IWorkbenchPart part, String secondaryId)
	{
		Views views = this._global.getViews();
		String id = part.getSite().getId();
		String title = part.getTitle();
		String path = null;

		if (part instanceof IEditorPart)
		{
			path = getPathFromPart((IEditorPart) part);
		}

		PartDeactivatedEvent event = new PartDeactivatedEvent(views, title, id, secondaryId, path);

		views.fireEventListeners(event);
	}

	/**
	 * firePartOpened
	 * 
	 * @param part
	 */
	private void firePartOpened(IWorkbenchPart part, String secondaryId)
	{
		Views views = this._global.getViews();
		String id = part.getSite().getId();
		String title = part.getTitle();
		String path = StringUtils.EMPTY;

		if (part instanceof IEditorPart)
		{
			path = getPathFromPart((IEditorPart) part);
		}

		PartOpenedEvent event = new PartOpenedEvent(views, title, id, secondaryId, path);

		views.fireEventListeners(event);
	}

	/**
	 * @param part
	 * @param newFile
	 */
	private void firePartSaveAs(IWorkbenchPart part, File oldFile, File newFile)
	{
		if(oldFile == null || newFile == null)
		{
			return;
		}
		
		Views views = this._global.getViews();
		String id = part.getSite().getId();
		String title = part.getTitle();

		PartClosedEvent event1 = new PartClosedEvent(views, title, id, null, oldFile.getAbsolutePath());
		views.fireEventListeners(event1);

		PartActivatedEvent event2 = new PartActivatedEvent(views, title, id, null, newFile.getAbsolutePath());
		views.fireEventListeners(event2);
	}

	/**
	 * firePartClosed
	 * 
	 * @param part
	 */
	private void firePartClosed(IWorkbenchPart part, String secondaryId)
	{
		Views views = this._global.getViews();
		String id = part.getSite().getId();
		String title = part.getTitle();
		String path = null;

		if (part instanceof IEditorPart)
		{
			path = getPathFromPart((IEditorPart) part);
		}

		PartClosedEvent event = new PartClosedEvent(views, title, id, secondaryId, path);

		views.fireEventListeners(event);
	}

	private String getPathFromPart(IEditorPart part)
	{
		IEditorInput input = part.getEditorInput();
		return CoreUIUtils.getPathFromEditorInput(input);
	}

	
	@Override
	public String getStartupName()
	{
		return "ScriptingEngine"; //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void startup()
	{
		Trace.info("ScriptingEngine: earlyStartup"); //$NON-NLS-1$
		
		final ScriptingEngine me = this;

		// create perspective listener so we'll start the scripting engine in our perspectives only
		this._perspectiveListener = new IPerspectiveListener()
		{
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
			{
				String message = StringUtils.format(Messages.ScriptingEngine_Activated, perspective.getId());
				
				Trace.info(message);
				me.perspectiveActivated(page, perspective);
			}

			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
			{
				String message = StringUtils.format(Messages.ScriptingEngine_Changed, perspective.getId());
				
				Trace.info(message);
			}
		};

		// create reset handler and register with our perspective factories
		Runnable resetEngine = new Runnable()
		{
			public void run()
			{
				Trace.info(Messages.ScriptingEngine_Reset);
				me.shutdown();
			}
		};
		WebPerspectiveFactory.addResettingHandler(resetEngine);

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

				if (window != null)
				{
					Trace.info(Messages.ScriptingEngine_Registering_Listener);
					addListenerToWindow(window);
				}
			}
		});
		startupDone();
	}

	/**
	 * initConsole
	 */
	public void initConsole()
	{
		if (this._console == null)
		{
			this._console = new ScriptingConsole("Aptana Scripting Console", null); //$NON-NLS-1$
			this._consoleStream = this._console.newMessageStream();
			
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						_consoleStream.setColor(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE));
					}
				}
			);
			
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { this._console });
		}
		
		//_consoleStream.println("Started");
	}
	
	/**
	 * getConsoleStream
	 *
	 * @return MessageConsoleStream
	 */
	public MessageConsoleStream getConsoleStream()
	{
		if(this._consoleStream == null)
		{
			initConsole();
		}
		return this._consoleStream;
	}
	
	/**
	 * getActiveEditor
	 * 
	 * @return IEditorPart
	 */
	public static IEditorPart getActiveEditor()
	{
		/**
		 * ActiveEditorRef
		 */
		class ActiveEditorRef
		{
			public IEditorPart activeEditor;
		}

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final ActiveEditorRef activeEditor = new ActiveEditorRef();
		Display display = workbench.getDisplay();
		IEditorPart result;

		display.syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

				// this can be null if you close all perspectives
				if (window != null && window.getActivePage() != null)
				{
					activeEditor.activeEditor = window.getActivePage().getActiveEditor();
				}
			}
		});

		if (activeEditor.activeEditor instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) activeEditor.activeEditor;

			result = editor.getEditor();
		}
		else
		{
			result = activeEditor.activeEditor;
		}

		return result;
	}

	/**
	 * Execute the script associated with this ScriptingEngine
	 */
	public void init()
	{
		if (this._loaded == false)
		{
			Context cx = Context.enter();

			try
			{
				// create our global
				this._global = new Global(cx);

				// start the HTTP server
				startServer();
				if(this._server != null)
				{
					this._global.put("serverPort", this._global, new Integer(this._server.getPort())); //$NON-NLS-1$
	
					// run the bootstrap script
					String scriptName = "/com/aptana/ide/scripting/resources/bootstrap.js"; //$NON-NLS-1$
					String source = FileUtilities.getResourceText(scriptName);
					cx.evaluateString(this._global, source, scriptName, 1, null);
				}
				
				// bind to the workbench events we're interested in
				bindToWorkbench();

				// make sure to register the error listener and delayed file listener on the active editor since we will
				// have missed its open and activate events
				IEditorPart activeEditor = getActiveEditor();

				if (activeEditor != null && activeEditor instanceof IUnifiedEditor)
				{
					IUnifiedEditor editor = (IUnifiedEditor) activeEditor;

					// editor.getFileContext().addDelayedFileListener(this);
					editor.getFileContext().addLongDelayedFileListener(this);
					editor.getFileContext().addErrorListener(this);
				}

				this._loaded = true;
			}
			catch (EcmaError e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingEngine_Error, e);
			}
			catch (EvaluatorException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingEngine_Error, e);
			}
			finally
			{
				Context.exit();
			}
		}
	}

	/**
	 * perspectiveActivated
	 * 
	 * @param page
	 * @param perspective
	 */
	private boolean perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
	{
		boolean result = false;

		if (supportedPerspectives.containsKey(perspective.getId()))
		{
			IPreferenceStore store = ScriptingPlugin.getDefault().getPreferenceStore();
			if(store.getBoolean(IPreferenceConstants.SCRIPTING_SERVER_START_AUTOMATICALLY))
			{
				init();
				result = true;
			}
		}

		return result;
	}

	/**
	 * Reload the scripting environment
	 */
	public void reload()
	{
		// shutdown scripting engine
		this.shutdown();

		// re-initialize
		this.init();
	}

	/**
	 * Shutdown the scripting engine
	 */
	private void shutdown()
	{
		if (this._loaded)
		{
			// remove part listener
			this.unbindToWorkbench();
			
			String[] keys = this._global.getScriptIds();

			for (int i = 0; i < keys.length; i++)
			{
				String key = keys[i];
				ScriptInfo info = this._global.getScriptInfo(key);
				Scriptable scope = info.getScope();
				Object onunload = scope.get("onunload", scope); //$NON-NLS-1$

				if (onunload instanceof Function)
				{
					Function unloadFunction = (Function) onunload;
					Context cx = Context.enter();

					unloadFunction.call(cx, scope, scope, new Object[0]);

					Context.exit();
				}
			}

			// clear scripts and scopes tables
			this._global.shutdown();
			
			// // shutdown HTTP server
			// if (this._server != null)
			// {
			// try
			// {
			// this._server.stop();
			// }
			// catch (IOException e)
			// {
			// e.printStackTrace();
			// }
			//				
			// this._server = null;
			// }

			this._loaded = false;
		}
	}

	/**
	 * Start up an HTTP server
	 */
	private void startServer()
	{
		if (this._server == null)
		{
			// initialize console
			initConsole();
			
			String pluginDir = CoreUIUtils.getPluginLocation(ScriptingPlugin.getDefault());
			String scriptsDir = pluginDir + File.separator + "scripts"; //$NON-NLS-1$
			System.setProperty("scripts.dir", scriptsDir); //$NON-NLS-1$
			
			String workspaceDir = CoreUIUtils.getWorkspaceDirectory();
			String userScriptsDir = workspaceDir + File.separator + "user.scripts"; //$NON-NLS-1$
			System.setProperty("user.scripts.dir", userScriptsDir); //$NON-NLS-1$

			if (new File(scriptsDir).exists())
			{
				IPreferenceStore store = ScriptingPlugin.getDefault().getPreferenceStore();
				int startPort = SCRIPTING_SERVER_START_PORT;
				
				if(store != null)
				{
					startPort = store.getInt(IPreferenceConstants.SCRIPTING_SERVER_START_PORT);
				}

				this._server = new ScriptingHttpServer(this, scriptsDir, startPort, startPort + 100);

				try
				{
					this._server.start();
					
					String message = StringUtils.format(Messages.ScriptingEngine_Started, this._server.getPort());
					
					IdeLog.logInfo(ScriptingPlugin.getDefault(), message);
				}
				catch (IOException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ScriptingEngine_Error_On_Start, e);
				}
			}
			else
			{
				String message = StringUtils.format(Messages.ScriptingEngine_Base_Directory_Does_Not_Exist, scriptsDir);
				
				IdeLog.logError(ScriptingPlugin.getDefault(), message);
			}
		}
	}

	/**
	 * unbindToWorkbench
	 */
	private void unbindToWorkbench()
	{
		if (this._partListener != null)
		{
			final ScriptingEngine me = this;
			final IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

					if (window != null)
					{
						window.getPartService().removePartListener(me._partListener);
						me._partListener = null;
					}
				}
			});
		}
	}


	/*
	 * Event Handlers
	 */

	/**
	 * @see com.aptana.ide.editors.unified.errors.IFileErrorListener#onErrorsChanged(com.aptana.ide.editors.unified.errors.IFileError[])
	 */
	public void onErrorsChanged(IFileError[] errors)
	{
		if (this._global != null)
		{
			IEditorPart part = getActiveEditor();

			if (part instanceof IUnifiedEditor)
			{
				IUnifiedEditor editor = (IUnifiedEditor) part;
				String mimeType = editor.getFileContext().getDefaultLanguage();

				EditorType editorType = this._global.getEditors().getEventTarget(mimeType);

				if (editorType != null)
				{
					ErrorsChangedEvent event = new ErrorsChangedEvent(editorType, errors);

					editorType.fireEventListeners(event);
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileContextListener#onContentChanged(com.aptana.ide.editors.unified.FileContextContentEvent)
	 */
	public void onContentChanged(FileContextContentEvent evt)
	{
		final IFileService fcx = evt.getSource();
		String mimeType = fcx.getDefaultLanguage();
		EditorType editorType = this._global.getEditors().getEventTarget(mimeType);

		if (editorType != null)
		{
			/**
			 * ActiveEditorRef
			 */
			class ActiveEditorRef
			{
				public IEditorPart activeEditor;
			}

			final IWorkbench workbench = PlatformUI.getWorkbench();
			final ActiveEditorRef activeEditor = new ActiveEditorRef();
			Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

					if (window == null)
					{
						return;
					}

					IWorkbenchPage[] pages = window.getPages();

					outer: for (int i = 0; i < pages.length; i++)
					{
						IWorkbenchPage page = pages[i];
						IEditorReference[] editorRefs = page.getEditorReferences();

						for (int j = 0; j < editorRefs.length; j++)
						{
							IEditorPart editor = editorRefs[j].getEditor(false);

							if (editor instanceof IUnifiedEditor)
							{
								IUnifiedEditor uniEditor = (IUnifiedEditor) editor;
								IFileService service = uniEditor.getFileContext();

								if (service instanceof EditorFileContext)
								{
									service = ((EditorFileContext) service).getFileContext();
								}

								if (service == fcx)
								{
									activeEditor.activeEditor = editor;
									break outer;
								}
							}
						}
					}
				}
			});

			// Object lexemes = wrapLexemes(fcx.getLexemeList());
			Editor editor = new Editor(this._global, activeEditor.activeEditor);
			ContentChangedEvent event = new ContentChangedEvent(editor);

			editorType.fireEventListeners("ContentChanged", new Object[] { event }); //$NON-NLS-1$
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedBracketMatcherListener#onBracketMatch(int)
	 */
	public IRegion onBracketMatch(int offset)
	{
		Callable callback = this._global.getEditors().getBracketMatcher();
		if (callback != null)
		{
			Context.enter();
			try
			{
				Object o = callback.call(Context.getCurrentContext(), this._global, this._global,
						new Object[] { new Integer(offset) });

				if (o instanceof Undefined)
				{
					return null;
				}

				return (IRegion) o;
			}
			finally
			{
				Context.exit();
			}
		}

		return null;
	}
}
