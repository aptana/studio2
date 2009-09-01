/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor;
import org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock;

/**
 * Spelling preference block
 * 
 * @since 3.1
 */
public class SpellingPreferenceBlock implements ISpellingPreferenceBlock {

	private class NullStatusChangeListener implements IStatusChangeListener {

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener#statusChanged
		 * (org.eclipse.core.runtime.IStatus)
		 */
		public void statusChanged(IStatus status) {
		}
	}

	private class StatusChangeListenerAdapter implements IStatusChangeListener {

		private final IPreferenceStatusMonitor fMonitor;

		private IStatus fStatus;

		public StatusChangeListenerAdapter(IPreferenceStatusMonitor monitor) {
			super();
			this.fMonitor = monitor;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener#statusChanged
		 * (org.eclipse.core.runtime.IStatus)
		 */
		public void statusChanged(IStatus status) {
			this.fStatus = status;
			this.fMonitor.statusChanged(status);
		}

		public IStatus getStatus() {
			return this.fStatus;
		}
	}

	private final SpellingConfigurationBlock fBlock = new SpellingConfigurationBlock(
			new NullStatusChangeListener(), null);

	private SpellingPreferenceBlock.StatusChangeListenerAdapter fStatusMonitor;

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#createControl
	 * (org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(Composite parent) {
		return this.fBlock.createContents(parent);
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#initialize
	 * (org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor)
	 */
	public void initialize(IPreferenceStatusMonitor statusMonitor) {
		this.fStatusMonitor = new StatusChangeListenerAdapter(statusMonitor);
		this.fBlock.fContext = this.fStatusMonitor;
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#canPerformOk
	 * ()
	 */
	public boolean canPerformOk() {
		return (this.fStatusMonitor == null)
				|| (this.fStatusMonitor.getStatus() == null)
				|| !this.fStatusMonitor.getStatus().matches(IStatus.ERROR);
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performOk()
	 */
	public void performOk() {
		this.fBlock.performOk();
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performDefaults
	 * ()
	 */
	public void performDefaults() {
		this.fBlock.performDefaults();
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performRevert
	 * ()
	 */
	public void performRevert() {
		this.fBlock.performRevert();
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#dispose()
	 */
	public void dispose() {
		this.fBlock.dispose();
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#setEnabled
	 * (boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.fBlock.setEnabled(enabled);
	}
}
