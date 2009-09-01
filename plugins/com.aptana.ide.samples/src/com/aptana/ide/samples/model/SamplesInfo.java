/**
 * 
 */
package com.aptana.ide.samples.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.aptana.ide.samples.handlers.IPreviewHandler;
import com.aptana.ide.samples.handlers.IProjectCreationHandler;

/**
 * @author paul
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SamplesInfo
{

	private String name;
	private String directory;
	private URL infoFile;
	private String iconFile;
	private IProjectCreationHandler creationHandler;
	private IPreviewHandler previewHandler;

	private List<String> natures = new ArrayList<String>();
	private List<SamplesEntry> rootSamples = new ArrayList<SamplesEntry>();
	private List<String> includes = new ArrayList<String>();
	private String extensionId;
	private String extensionPluginId;

	/**
	 * Gets the directory
	 * 
	 * @return - directory
	 */
	public String getDirectory()
	{
		return directory;
	}

	/**
	 * Sets the directory
	 * 
	 * @param directory
	 */
	public void setDirectory(String directory)
	{
		this.directory = directory;
		loadRootSamples();
	}

	private void loadRootSamples()
	{
		rootSamples.clear();
		File file = new File(directory);
		File[] samps = file.listFiles();
		if (samps != null)
		{
			for (int i = 0; i < samps.length; i++)
			{
				if (samps[i].isDirectory())
				{
					SamplesEntry entry = new SamplesEntry(this, samps[i], true);
					rootSamples.add(entry);
				}
			}
		}
	}

	/**
	 * Adds a nature for this sample
	 * 
	 * @param natureId
	 */
	public void addNature(String natureId)
	{
		this.natures.add(natureId);
	}

	/**
	 * Gets the natures for this sample
	 * 
	 * @return - nature ids
	 */
	public String[] getNatures()
	{
		return (String[]) this.natures.toArray(new String[0]);
	}

	/**
	 * Gets the root samples
	 * 
	 * @return - root samples
	 */
	public List<SamplesEntry> getRootSamples()
	{
		return this.rootSamples;
	}

	/**
	 * Gets the icon file
	 * 
	 * @return - icon file
	 */
	public String getIconFile()
	{
		return iconFile;
	}

	/**
	 * Sets the icon file
	 * 
	 * @param iconFile
	 */
	public void setIconFile(String iconFile)
	{
		this.iconFile = iconFile;
	}

	/**
	 * Gets the info file
	 * 
	 * @return - url for browser
	 */
	public URL getInfoFile()
	{
		return infoFile;
	}

	/**
	 * Sets the info file
	 * 
	 * @param infoFile
	 */
	public void setInfoFile(URL infoFile)
	{
		this.infoFile = infoFile;
	}

	/**
	 * Gets the name
	 * 
	 * @return - name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the creation handler
	 * 
	 * @param handler
	 */
	public void setCreationHandler(IProjectCreationHandler handler)
	{
		this.creationHandler = handler;
	}

	/**
	 * Gets the creation handler
	 * 
	 * @return - handler
	 */
	public IProjectCreationHandler getCreationHandler()
	{
		return this.creationHandler;
	}

	/**
	 * Sets the preview handler
	 * 
	 * @param handler
	 */
	public void setPreviewHandler(IPreviewHandler handler)
	{
		this.previewHandler = handler;
	}

	/**
	 * Gets the preview handler
	 * 
	 * @return - handler
	 */
	public IPreviewHandler getPreviewHandler()
	{
		return this.previewHandler;
	}

	/**
	 * Add include path
	 * 
	 * @param file
	 */
	public void addIncludePath(String file)
	{
		this.includes.add(file);
	}

	/**
	 * Get include paths
	 * 
	 * @return - list of includes
	 */
	public List<String> getIncludePaths()
	{
		return this.includes;
	}

	public void setExtensionId(String extensionId) {
		this.extensionId = extensionId;
	}
	public String getExtensionId(){
		return extensionId;
	}

	public void setExtensionPluginId(String extensionPluginId) {
		this.extensionPluginId = extensionPluginId;
	}
	public String getExtensionPluginId(){
		return extensionPluginId;
	}

}
