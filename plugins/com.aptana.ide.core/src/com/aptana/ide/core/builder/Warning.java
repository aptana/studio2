package com.aptana.ide.core.builder;


public class Warning implements IProblem
{

	private int id;
	private String filename;
	private int lineNumber;
	private int start;
	private int end;
	private String message;

	public Warning(int id, String filename, int lineNumber, int start, int end, String message)
	{
		this.id = id;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.start = start;
		this.end = end;
		this.message = message;
	}

	public int getSeverity()
	{
		return WARNING;
	}

	public int endOffset()
	{
		return end;
	}

	public String getFilename()
	{
		return filename;
	}

	public int getId()
	{
		return id;
	}

	public String getMessage()
	{
		return message;
	}

	public int lineNumber()
	{
		return lineNumber;
	}

	public int startOffset()
	{
		return start;
	}

}
