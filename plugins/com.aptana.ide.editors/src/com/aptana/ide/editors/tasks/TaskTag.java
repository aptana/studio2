package com.aptana.ide.editors.tasks;

class TaskTag
{

	private int priority;
	private String messages;
	private int lineNumber;
	private int startOffset;
	private int endOffset;

	public TaskTag(int priority, String message, int lineNumber, int startOffset, int endOffset)
	{
		this.priority = priority;
		this.messages = message;
		this.lineNumber = lineNumber;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	public int getEndOffset()
	{
		return endOffset;
	}

	public String getMessage()
	{
		return messages;
	}

	public int getStartOffset()
	{
		return startOffset;
	}

	public int getPriority()
	{
		return priority;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

}
