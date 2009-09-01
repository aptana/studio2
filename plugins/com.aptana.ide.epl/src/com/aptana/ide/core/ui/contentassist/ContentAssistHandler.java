/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.core.ui.contentassist;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.contentassist.AbstractControlContentAssistSubjectAdapter;
import org.eclipse.jface.contentassist.ComboContentAssistSubjectAdapter;
import org.eclipse.jface.contentassist.SubjectControlContentAssistant;
import org.eclipse.jface.contentassist.TextContentAssistSubjectAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * @author Pavel Petrochenko
 *
 */
public final class ContentAssistHandler
{
	/**
	 * The target control.
	 */
	private Control fControl;
	/**
	 * The content assist subject adapter.
	 */
	private AbstractControlContentAssistSubjectAdapter fContentAssistSubjectAdapter;
	/**
	 * The content assistant.
	 */
	private SubjectControlContentAssistant fContentAssistant;
	/**
	 * The currently installed FocusListener, or <code>null</code> iff none installed. This is also used as flag to
	 * tell whether content assist is enabled
	 */
	private FocusListener fFocusListener;
	/**
	 * The currently installed IHandlerActivation, or <code>null</code> iff none installed.
	 */
	private IHandlerActivation fHandlerActivation;

	/**
	 * Creates a new {@link ContentAssistHandler} for the given {@link Combo}. Only a single
	 * {@link ContentAssistHandler} may be installed on a {@link Combo} instance. Content Assist is enabled by default.
	 * 
	 * @param combo
	 *            target combo
	 * @param contentAssistant
	 *            a configured content assistant
	 * @return a new {@link ContentAssistHandler}
	 */
	public static ContentAssistHandler createHandlerForCombo(Combo combo,
			SubjectControlContentAssistant contentAssistant)
	{
		return new ContentAssistHandler(combo, new ComboContentAssistSubjectAdapter(combo), contentAssistant);
	}

	/**
	 * Creates a new {@link ContentAssistHandler} for the given {@link Text}. Only a single
	 * {@link ContentAssistHandler} may be installed on a {@link Text} instance. Content Assist is enabled by default.
	 * 
	 * @param text
	 *            target text
	 * @param contentAssistant
	 *            a configured content assistant
	 * @return a new {@link ContentAssistHandler}
	 */
	public static ContentAssistHandler createHandlerForText(Text text, SubjectControlContentAssistant contentAssistant)
	{
		return new ContentAssistHandler(text, new TextContentAssistSubjectAdapter(text), contentAssistant);
	}

	/**
	 * Internal constructor.
	 * 
	 * @param control
	 *            target control
	 * @param subjectAdapter
	 *            content assist subject adapter
	 * @param contentAssistant
	 *            content assistant
	 */
	private ContentAssistHandler(Control control, AbstractControlContentAssistSubjectAdapter subjectAdapter,
			SubjectControlContentAssistant contentAssistant)
	{
		this.fControl = control;
		this.fContentAssistant = contentAssistant;
		this.fContentAssistSubjectAdapter = subjectAdapter;
		this.setEnabled(true);
		this.fControl.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				ContentAssistHandler.this.setEnabled(false);
			}
		});
	}

	/**
	 * @return <code>true</code> iff content assist is enabled
	 */
	public boolean isEnabled()
	{
		return this.fFocusListener != null;
	}

	/**
	 * Controls enablement of content assist. When enabled, a cue is shown next to the focused field and the affordance
	 * hover shows the shortcut.
	 * 
	 * @param enable
	 *            enable content assist iff true
	 */
	public void setEnabled(boolean enable)
	{
		if (enable == this.isEnabled())
		{
			return;
		}

		if (enable)
		{
			this.enable();
		}
		else
		{
			this.disable();
		}
	}

	/**
	 * Enable content assist.
	 */
	private void enable()
	{
		if (!this.fControl.isDisposed())
		{
			this.fContentAssistant.install(this.fContentAssistSubjectAdapter);
			this.installCueLabelProvider();
			this.installFocusListener();
			if (this.fControl.isFocusControl())
			{
				this.activateHandler();
			}
		}
	}

	/**
	 * Disable content assist.
	 */
	private void disable()
	{
		if (!this.fControl.isDisposed())
		{
			this.fContentAssistant.uninstall();
			this.fContentAssistSubjectAdapter.setContentAssistCueProvider(null);
			this.fControl.removeFocusListener(this.fFocusListener);
			this.fFocusListener = null;
			if (this.fHandlerActivation != null)
			{
				this.deactivateHandler();
			}
		}
	}

	/**
	 * Create and install the {@link LabelProvider} for fContentAssistSubjectAdapter.
	 */
	private void installCueLabelProvider()
	{
		ILabelProvider labelProvider = new LabelProvider()
		{
			/*
			 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
			 */
			public String getText(Object element)
			{
				IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(
						IBindingService.class);
				TriggerSequence[] activeBindings = bindingService
						.getActiveBindingsFor(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
				if (activeBindings.length == 0)
				{
					return Messages.ContentAssistHandler_TXT_CA_Available; 
				}
				return activeBindings[0].format();
			}
		};
		this.fContentAssistSubjectAdapter.setContentAssistCueProvider(labelProvider);
	}

	/**
	 * Create fFocusListener and install it on fControl.
	 */
	private void installFocusListener()
	{
		this.fFocusListener = new FocusListener()
		{
			public void focusGained(final FocusEvent e)
			{
				if (ContentAssistHandler.this.fHandlerActivation == null)
				{
					ContentAssistHandler.this.activateHandler();
				}
			}

			public void focusLost(FocusEvent e)
			{
				if (ContentAssistHandler.this.fHandlerActivation != null)
				{
					ContentAssistHandler.this.deactivateHandler();
				}
			}
		};
		this.fControl.addFocusListener(this.fFocusListener);
	}

	/**
	 * Create and register fHandlerSubmission.
	 */
	private void activateHandler()
	{
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);
		if (handlerService == null)
		{
			return;
		}

		IHandler handler = new AbstractHandler()
		{
			public Object execute(ExecutionEvent event) throws ExecutionException
			{
				if (ContentAssistHandler.this.isEnabled())
				{
					ContentAssistHandler.this.fContentAssistant.showPossibleCompletions();
				}
				return null;
			}
		};
		this.fHandlerActivation = handlerService.activateHandler(
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, handler);
	}

	/**
	 * Unregister the {@link IHandlerActivation} from the shell.
	 */
	private void deactivateHandler()
	{
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);
		if (handlerService != null)
		{
			handlerService.deactivateHandler(this.fHandlerActivation);
		}
		this.fHandlerActivation = null;
	}
}