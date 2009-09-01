/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.configuration.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 *
 */
public final class Messages extends NLS{
	private static final String BUNDLE_NAME = "com.aptana.ide.server.configuration.ui.messages"; //$NON-NLS-1$
	/**
	 * ApachePathHint
	 */
	public static String ApachePathHint;
	/**
	 * ApacheDocRootHint
	 */
	public static String ApacheDocRootHint;
	/**
	 * ApacheServerComposite_HOST
	 */
	public static String ApacheServerComposite_HOST;
	/**
	 * ApacheServerComposite_HOST_SHOULD_NOT_BE_EMPTY
	 */
	public static String ApacheServerComposite_HOST_SHOULD_NOT_BE_EMPTY;
	/**
	 * ApacheServerComposite_PORT
	 */
	public static String ApacheServerComposite_PORT;
	/**
	 * ApacheServerComposite_PORT_SHOULD_BE_BETWEEN
	 */
	public static String ApacheServerComposite_PORT_SHOULD_BE_BETWEEN;
	/**
	 * ApacheServerComposite_PORT_SHOULD_NOT_BE_EMPTY
	 */
	public static String ApacheServerComposite_PORT_SHOULD_NOT_BE_EMPTY;
	/**
	 * ApacheServerComposite_START_APACHE
	 */
	public static String ApacheServerComposite_START_APACHE;
	/**
	 * ApacheServerComposite_Apache
	 */
	public static String ApacheServerComposite_Apache;
	/**
	 * ApacheServerComposite_RESTART_APACHE
	 */
	public static String ApacheServerComposite_RESTART_APACHE;
	/**
	 * ApacheServerComposite_STOP_APACHE
	 */
	public static String ApacheServerComposite_STOP_APACHE;
	/**
	 * BasicServerComposite_Description
	 */
	public static String BasicServerComposite_Description;
	/**
	 * BasicServerComposite_LOG_PATH_CHOOSE
	 */
	public static String BasicServerComposite_LOG_PATH_CHOOSE;
	/**
	 * BasicServerComposite_LOG_PATH_TITLE
	 */
	public static String BasicServerComposite_LOG_PATH_TITLE;
	/**
	 * BasicServerComposite_LOG_SHOULD_BE_EMPTY_OR_POINT_TO_FILE
	 */
	public static String BasicServerComposite_LOG_SHOULD_BE_EMPTY_OR_POINT_TO_FILE;
	/**
	 * BasicServerComposite_PATH
	 */
	public static String BasicServerComposite_PATH;
	/**
	 * BasicServerComposite_NAME
	 */
	public static String BasicServerComposite_NAME;
	/**
	 * BasicServerComposite_PATH_NOT_EXISTS
	 */
	public static String BasicServerComposite_PATH_NOT_EXISTS;
	/**
	 * BasicServerComposite_FILE_SHOULD_BE_DIR
	 */
	public static String BasicServerComposite_FILE_SHOULD_BE_DIR;
	/**
	 * BasicServerComposite_DUBLICATE_NAME
	 */
	public static String BasicServerComposite_DUBLICATE_NAME;
	/**
	 * BasicServerComposite_EMPTY_NAME
	 */
	public static String BasicServerComposite_EMPTY_NAME;
	/**
	 * BasicServerComposite_BROWSE
	 */
	public static String BasicServerComposite_BROWSE;
	/**
	 * BasicServerComposite_DOC_ROOT
	 */
	public static String BasicServerComposite_DOC_ROOT;
	/**
	 * BasicServerComposite_DOCUMENT_ROOT_ERROR
	 */
	public static String BasicServerComposite_DOCUMENT_ROOT_ERROR;
	/**
	 * MySqlServerComposite_START_MYSQL
	 */
	public static String MySqlServerComposite_START_MYSQL;
	/**
	 * XAMPPServerComposite_BROWSE
	 */
	public static String XAMPPServerComposite_BROWSE;
    public static String XAMPPServerComposite_INF_Validate;
	/**
	 * XAMPPServerComposite_STOP
	 */
	public static String XAMPPServerComposite_STOP;
	/**
	 * XAMPPServerComposite_NOT_DIR
	 */
	public static String XAMPPServerComposite_NOT_DIR;
	/**
	 * XAMPPServerComposite_PATH_NOT_EXIST
	 */
	public static String XAMPPServerComposite_PATH_NOT_EXIST;
	/**
	 * XAMPPServerComposite_START
	 */
	public static String XAMPPServerComposite_START;

	

	private Messages() {
	}

	static{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
