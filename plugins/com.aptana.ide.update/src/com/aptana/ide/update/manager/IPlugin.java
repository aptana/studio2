package com.aptana.ide.update.manager;

import java.net.URL;

public interface IPlugin
{

	public String getId();

	public String getVersion();

	public URL getURL();

	public String getName();
}
