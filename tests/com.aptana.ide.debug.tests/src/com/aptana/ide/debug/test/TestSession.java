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
package com.aptana.ide.debug.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.debug.core.IDebugConstants;
import com.aptana.ide.debug.core.JSDebugPlugin;
import com.aptana.ide.debug.core.model.IJSDebugTarget;
import com.aptana.ide.debug.core.model.JSDebugModel;

/**
 * @author Max Stepanov
 */
public class TestSession implements IDebugEventSetListener
{
	private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("//\\[\\[([^\\x5D]+)\\]\\]\\s*$"); //$NON-NLS-1$
	private static final String INSTRUCTION_SPLIT = ";"; //$NON-NLS-1$
	private static final String ARGUMENT_SPLIT = ","; //$NON-NLS-1$

	private class LabelReference implements IReference
	{
		private String label;
		private IReference reference;

		/**
		 * LabelReference
		 * 
		 * @param label
		 */
		protected LabelReference(String label)
		{
			this.label = label;
		}

		/**
		 * @see com.aptana.ide.debug.test.IReference#getSourceFile()
		 */
		public String getSourceFile() throws CoreException
		{
			resolveLabel();
			return reference.getSourceFile();
		}

		/**
		 * @see com.aptana.ide.debug.test.IReference#getLineNumber()
		 */
		public int getLineNumber() throws CoreException
		{
			resolveLabel();
			return reference.getLineNumber();
		}

		/**
		 * resolveLabel
		 * 
		 * @throws CoreException
		 */
		private void resolveLabel() throws CoreException
		{
			if (reference == null)
			{
				reference = (IReference) labels.get(label);
			}
			if (reference == null)
			{
				throw new CoreException(null);
			}
		}
	}

	private MessageConsoleStream stream;

	private IJSDebugTarget target;
	private IThread thread;

	private Map instructions = new HashMap();
	private Map labels = new HashMap();

	private IInstruction lastInstruction;
	private String currentFile;
	private int currentLineNumber;

	/**
	 * TestSession
	 * 
	 * @param target
	 * @param stream
	 */
	public TestSession(IJSDebugTarget target, MessageConsoleStream stream)
	{
		this.target = target;
		clearBreakpoints();
		DebugPlugin.getDefault().addDebugEventListener(this);
		logConsole("----=== Debug session started ===----");
	}

	/**
	 * getDebugTarget
	 * 
	 * @return IDebugTarget
	 */
	public IDebugTarget getDebugTarget()
	{
		return target;
	}

	/**
	 * shutdown
	 */
	private void shutdown()
	{
		DebugPlugin.getDefault().removeDebugEventListener(this);
		clearBreakpoints();
	}

	/**
	 * clearBreakpoints
	 */
	private void clearBreakpoints()
	{
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(
				JSDebugModel.getModelIdentifier());
		for (int i = 0; i < breakpoints.length; ++i)
		{
			try
			{
				breakpoints[i].delete();
			}
			catch (CoreException e)
			{
				IdeLog.logError(JSDebugPlugin.getDefault(), StringUtils.EMPTY, e);
			}
		}
	}

	/**
	 * logConsole
	 * 
	 * @param message
	 */
	private void logConsole(String message)
	{
		stream.println(message);
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events)
	{
		for (int i = 0; i < events.length; ++i)
		{
			DebugEvent event = events[i];

			try
			{
				Object source = event.getSource();
				switch (event.getKind())
				{
					case DebugEvent.CREATE:
						if (source instanceof IThread && ((IThread) source).getDebugTarget() == target)
						{
							thread = (IThread) source;
						}
						break;
					case DebugEvent.TERMINATE:
						if (source == target)
						{
							shutdown();
							target = null;
							logConsole("----=== Debug session ended ===----");
						}
						break;
					case DebugEvent.SUSPEND:
						if (source == thread)
						{
							int line = thread.getTopStackFrame().getLineNumber();
							IInstruction[] instructionList = getLineInstructions(line);
							if (instructionList != null)
							{
								executeInstruction(instructionList);
							}
						}
						break;
					case DebugEvent.RESUME:
						if (source == thread)
						{

						}
						break;
					case DebugEvent.MODEL_SPECIFIC:
						if (source == target)
						{
							switch (event.getDetail())
							{
								case IDebugConstants.DEBUG_EVENT_URL_OPEN:
									loadAndParseFile((String) event.getData());
									break;
								case IDebugConstants.DEBUG_EVENT_URL_OPENED:
									System.out.println("URL opened<" + event.getData() + ">");
									break;
							}
						}
						break;
					default:
						break;
				}
			}
			catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * loadAndParseFile
	 * 
	 * @param filepath
	 */
	private void loadAndParseFile(String filepath)
	{
		LineNumberReader r = null;
		try
		{
			File file = new File(filepath);
			r = new LineNumberReader(new FileReader(file));
			currentFile = filepath;
			String line;
			while ((line = r.readLine()) != null)
			{
				currentLineNumber = r.getLineNumber();
				parseLine(line);
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(JSDebugPlugin.getDefault(), StringUtils.EMPTY, e);
		}
		finally
		{
			if (r != null)
			{
				try
				{
					r.close();
				}
				catch (IOException ignore)
				{
				}
			}
		}
	}

	/**
	 * parseLine
	 * 
	 * @param line
	 */
	private void parseLine(String line)
	{
		Matcher matcher = INSTRUCTION_PATTERN.matcher(line);
		if (matcher.find())
		{
			int count = matcher.groupCount();
			for (int i = 1; i <= count; ++i)
			{
				processInstruction(matcher.group(i));
			}
		}
	}

	/**
	 * processInstruction
	 * 
	 * @param string
	 */
	private void processInstruction(String string)
	{
		System.out.println(">" + string);
		String[] list = string.trim().split(INSTRUCTION_SPLIT);
		GroupInstruction group = null;
		for (int i = 0; i < list.length; ++i)
		{
			string = list[i];
			IInstruction instruction = null;
			if ("step".equals(string) || "stepinto".equals(string) || "into".equals(string))
			{
				instruction = new StepInstruction(thread, StepInstruction.STEP_INTO);
			}
			else if ("stepover".equals(string) || "over".equals(string))
			{
				instruction = new StepInstruction(thread, StepInstruction.STEP_OVER);
			}
			else if ("stepreturn".equals(string) || "return".equals(string))
			{
				instruction = new StepInstruction(thread, StepInstruction.STEP_RETURN);
			}
			else if ("run".equals(string) || "resume".equals(string))
			{
				instruction = new SuspendResumeInstruction(thread, SuspendResumeInstruction.RESUME);
			}
			else if ("suspend".equals(string))
			{
				instruction = new SuspendResumeInstruction(thread, SuspendResumeInstruction.SUSPEND);
			}
			else if ("term".equals(string) || "terminate".equals(string))
			{
				instruction = new TerminateInstruction(thread);
			}
			else if (string.startsWith("delay "))
			{
				string = string.substring(6).trim();
				int m = 1;
				if (string.indexOf('s') != -1)
				{
					m = 1000;
					string = string.substring(0, string.indexOf('s'));
				}
				int delay = 0;
				try
				{
					delay = Integer.parseInt(string);
				}
				catch (NumberFormatException e)
				{
					System.out.println(e.getMessage());
					return;
				}
				instruction = new DelayInstruction(delay * m);
			}
			else if (string.startsWith("bp"))
			{
				string = string.substring(2).trim();
				if (string.length() == 0)
				{
					instruction = new BreakpointInstruction(currentFile, currentLineNumber);
				}
				else if (string.charAt(0) == '+' || string.charAt(0) == '-')
				{
					int line = 0;
					try
					{
						line = Integer.parseInt(string);
					}
					catch (NumberFormatException e)
					{
						System.out.println(e.getMessage());
						return;
					}
					instruction = new BreakpointInstruction(currentFile, currentLineNumber + line);
				}
				else if (string.charAt(0) == '#')
				{
					String label = string.substring(1);
					IReference sourceRef = (IReference) labels.get(label);
					if (sourceRef == null)
					{
						sourceRef = new LabelReference(label);
					}
					instruction = new BreakpointInstruction(sourceRef);
				}
			}
			else if (string.startsWith("runto "))
			{
			}
			else if (string.startsWith("print "))
			{
				string = string.substring(6).trim();
				String[] args = string.split(ARGUMENT_SPLIT);
				int frameId = -1;
				String variableName = "";
				if (args.length == 2)
				{
					try
					{
						frameId = Integer.parseInt(args[0]);
					}
					catch (NumberFormatException e)
					{
					}
					variableName = args[1];
				}
				else if (args.length == 1)
				{
					variableName = args[0];
				}
				instruction = new PrintValueInstruction(thread, frameId, variableName);
			}
			else if (string.startsWith("test "))
			{
				string = string.substring(5).trim();
				String[] args = string.split(ARGUMENT_SPLIT);
				if (args.length < 3)
				{
					continue;
				}
				int frameId = -1;
				String variableName = "";
				String value = null;
				String valueType = null;
				int pos = 0;
				if (args.length == 4)
				{
					try
					{
						frameId = Integer.parseInt(args[pos++]);
					}
					catch (NumberFormatException e)
					{
					}
				}
				variableName = args[pos++];
				value = args[pos++];
				if ("null".equals(value))
				{
					value = null;
				}
				valueType = args[pos++];
				if ("null".equals(valueType))
				{
					valueType = null;
				}
				instruction = new VerifyValueInstruction(thread, frameId, variableName, value, valueType);
			}
			else if (string.startsWith("#"))
			{
				String label = string.substring(1);
				labels.put(label, new SourceLineReferrence(currentFile, currentLineNumber));
			}
			if (instruction != null)
			{
				if (list.length > 1 && group == null)
				{
					group = new GroupInstruction();
					addInstruction(currentFile, currentLineNumber, group);
				}
				if (group != null)
				{
					group.add(instruction);
				}
				else
				{
					addInstruction(currentFile, currentLineNumber, instruction);
				}
			}
		}
	}

	/**
	 * addInstruction
	 * 
	 * @param fileName
	 * @param lineNumber
	 * @param instruction
	 */
	private void addInstruction(String fileName, int lineNumber, IInstruction instruction)
	{
		Integer key = new Integer(lineNumber);
		Object prev = instructions.get(key);
		if (prev != null)
		{
			List list;
			if (prev instanceof List)
			{
				list = (List) prev;
			}
			else
			{
				list = new ArrayList(2);
				list.add(prev);
				instructions.put(key, list);
			}
			list.add(instruction);
		}
		else
		{
			instructions.put(key, instruction);
		}
	}

	/**
	 * getLineInstructions
	 * 
	 * @param lineNumber
	 * @return IInstruction[]
	 */
	private IInstruction[] getLineInstructions(int lineNumber)
	{
		Object object = instructions.get(new Integer(lineNumber));
		IInstruction[] list;
		if (object instanceof List)
		{
			list = (IInstruction[]) ((List) object).toArray(new IInstruction[((List) object).size()]);
		}
		else if (object instanceof IInstruction)
		{
			list = new IInstruction[] { (IInstruction) object };
		}
		else if (lastInstruction != null)
		{
			list = new IInstruction[] { lastInstruction };
		}
		else
		{
			return null;
		}
		lastInstruction = list[list.length - 1];
		return list;
	}

	/**
	 * executeInstruction
	 * 
	 * @param instructionList
	 */
	private void executeInstruction(final IInstruction[] instructionList)
	{
		new Thread("Debug Instruction")
		{
			{
				setPriority(Thread.MIN_PRIORITY);
			}

			public void run()
			{
				try
				{
					for (int i = 0; i < instructionList.length; ++i)
					{
						instructionList[i].execute();
					}
				}
				catch (CoreException e)
				{
					IdeLog.logError(JSDebugPlugin.getDefault(), StringUtils.EMPTY, e);
				}
			}

		}.start();
	}
}
