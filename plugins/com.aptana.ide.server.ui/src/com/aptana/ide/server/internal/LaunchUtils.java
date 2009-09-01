/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.internal;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 *
 */
public final class LaunchUtils {


	private LaunchUtils(){
		
	}

	/**
	 * @param program
	 * @param arguments
	 * @param workingDirectory
	 * @return created process
	 * @throws CoreException
	 */
	public static IProcess exec(String program,String[] arguments,String workingDirectory) throws CoreException{
		int cmdLineLength=arguments.length+1;
		String[] cmdLine = new String[cmdLineLength];
		cmdLine[0] = program;
		if (arguments != null) {
			System.arraycopy(arguments, 0, cmdLine, 1, arguments.length);
		}

		File workingDir = null;
		if (workingDirectory != null) {
			workingDir = new File(workingDirectory);
		}

		Process p = DebugPlugin.exec(cmdLine, workingDir);
		IProcess process = null;
		if (p != null) {
			IdeLog.logInfo(ServerUIPlugin.getDefault(), StringUtils.format(Messages.LaunchUtils_ERROR_MESSAGE, new String[] {program, StringUtils.join(" ", arguments)})); //$NON-NLS-1$
			Launch launch = new Launch(null,"run",null); //$NON-NLS-1$
			process = DebugPlugin.newProcess(launch, p, program);
			//DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
			process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
		}
		
		return process;		
	}
	
	/**
	 * @param commandLine
	 * @return rendered command line
	 */
	protected static String renderCommandLine(String[] commandLine) {
		if (commandLine.length < 1)
		{
			return ""; //$NON-NLS-1$
		}
		StringBuffer buf = new StringBuffer(commandLine[0]);
		for (int i = 1; i < commandLine.length; i++) {
			buf.append(' ');
			buf.append(commandLine[i]);
		}
		return buf.toString();
	}

}
