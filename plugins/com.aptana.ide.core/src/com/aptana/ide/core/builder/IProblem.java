package com.aptana.ide.core.builder;

import org.eclipse.core.resources.IMarker;

public interface IProblem
{

	public int INFO = IMarker.SEVERITY_INFO;
	public int WARNING = IMarker.SEVERITY_WARNING;
	public int ERROR = IMarker.SEVERITY_ERROR;

	/**
	 * Return the unique id for the problem type. Used to categorize a problem.
	 * 
	 * @return
	 */
	public int getId();

	public int getSeverity();

	public int lineNumber();

	public int startOffset();

	public int endOffset();

	public String getFilename();

	public String getMessage();
}
