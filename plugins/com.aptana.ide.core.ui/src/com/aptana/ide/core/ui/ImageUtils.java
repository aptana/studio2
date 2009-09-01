/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.db.AptanaDB;
import com.aptana.ide.core.db.IResultSetHandler;

/**
 * ImageUtils
 * 
 * @author Paul Colton
 */
public final class ImageUtils
{
	private static java.awt.Color whiteBackground = null;
	private static ImageRegistry imageRegistry = null;
	private static Image fIconDrive = null;
	private static ISharedImages sharedImages = null;
	private static Image iconFolder = null;
	private static Image iconFile = null;
	private static boolean imageCacheLoaded = false;

	/**
	 * Holds a hash of icons
	 */
	public static Map<String, Image> fileIconsHash = new HashMap<String, Image>();

	/**
	 * Private constructor for utility class
	 */
	private ImageUtils()
	{
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image descriptor registry. If the image
	 * descriptor cannot be retrieved, attempt to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor associated with resource or the default "missing" image descriptor if one could not
	 *         be found
	 */
	public static ImageDescriptor getImageDescriptor(String imageFilePath)
	{
		ImageDescriptor imageDescriptor = CoreUIPlugin.getImageDescriptor(imageFilePath);

		if (imageDescriptor == null)
		{
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * Returns an icon representing the specified file.
	 * 
	 * @param file
	 * @param backgroundColor
	 * @return Image
	 */
	public static Image getIcon(File file, Color backgroundColor)
	{
		if (!CoreUIUtils.onMacOSX)
		{
			String fileType = null;
			String fileDisplayName = null;
			try {
				fileType = FileSystemView.getFileSystemView().getSystemTypeDescription(file);
				fileDisplayName = FileSystemView.getFileSystemView().getSystemDisplayName(file);
			} catch (Exception ignore) {
				/* catch and ignore all swing issues here */
			}

			if (fileDisplayName != null && fileType != null)
			{
				// fileType is "" for Desktop
				if (fileDisplayName.equals("Desktop")) //$NON-NLS-1$
				{
					fileType = "Desktop"; //$NON-NLS-1$
				}

				if (fileIconsHash.containsKey(fileType))
				{
					return fileIconsHash.get(fileType);
				}
				else if (fileIconsHash.containsKey(fileType + "#" + fileDisplayName)) //$NON-NLS-1$
				{
					return fileIconsHash.get(fileType + "#" + fileDisplayName); //$NON-NLS-1$
				}
				else
				{
					Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
					if (icon instanceof ImageIcon)
					{
						Image image = awtImageIconToSWTImage(icon, backgroundColor);

						if (fileType.equals("Application") || fileType.equals("System Folder") //$NON-NLS-1$ //$NON-NLS-2$
								|| fileDisplayName.equals("Desktop") || fileType.indexOf("Shortcut") > -1) //$NON-NLS-1$ //$NON-NLS-2$
						{
							fileIconsHash.put(fileType + "#" + fileDisplayName, image); //$NON-NLS-1$
						}
						else
						{
							fileIconsHash.put(fileType, image);
						}

						return image;
					}
				}
			}
		}

		if (file.getName().equals("")) //$NON-NLS-1$
		{
			return getDriveIcon();
		}

		if (FileUtils.isDirectory(file))
		{
			return getFolderIcon();
		}

		int lastDotPos = file.getName().indexOf('.');

		if (lastDotPos == -1)
		{
			return getFileIcon();
		}

		Image image = getIcon(file.getName().substring(lastDotPos + 1));

		return image == null ? getFileIcon() : image;
	}

	/**
	 * getDriveIcon
	 * 
	 * @return Image
	 */
	public static Image getDriveIcon()
	{
		if (fIconDrive == null)
		{
			fIconDrive = getImageDescriptor("icons/drive.gif").createImage(); //$NON-NLS-1$
		}

		return fIconDrive;
	}

	/**
	 * getFolderIcon
	 * 
	 * @return Image
	 */
	public static Image getFolderIcon()
	{
		checkSharedImages();

		if (iconFolder == null)
		{
			iconFolder = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}

		return iconFolder;
	}

	/**
	 * getFileIcon
	 * 
	 * @return Image
	 */
	public static Image getFileIcon()
	{
		checkSharedImages();

		if (iconFile == null)
		{
			iconFile = sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
		}

		return iconFile;
	}

	/**
	 * checkSharedImages
	 */
	private static void checkSharedImages()
	{
		if (sharedImages == null)
		{
			sharedImages = PlatformUI.getWorkbench().getSharedImages();
		}
	}

	/**
	 * getIcon Returns the icon for the file type with the specified extension.
	 * 
	 * @param extension
	 * @return Image
	 */
	public static Image getIcon(String extension)
	{
		if (imageRegistry == null)
		{
			imageRegistry = new ImageRegistry();
		}

		Image image = imageRegistry.get(extension);

		if (image != null)
		{
			return image;
		}

		if (extension == null)
		{
			return iconFile;
		}

		Program program = Program.findProgram(extension);
		ImageData imageData = (program == null ? null : program.getImageData());

		if (imageData != null)
		{
			image = new Image(Display.getCurrent(), imageData);
			imageRegistry.put(extension, image);
		}
		else
		{
			image = iconFile;
		}

		return image;
	}

	/**
	 * awtImageIconToSWTImage
	 * 
	 * @param icon
	 * @param backgroundColor
	 * @return Image
	 */
	public static Image awtImageIconToSWTImage(Icon icon, Color backgroundColor)
	{
		ImageIcon i = (ImageIcon) icon;
		java.awt.Color backColor = null;

		if (backgroundColor != null)
		{
			backColor = swtColorToAWTColor(backgroundColor);
		}
		else
		{
			if (whiteBackground == null)
			{
				whiteBackground = swtColorToAWTColor(new Color(Display.getCurrent(), 255, 255, 255));
			}

			backColor = whiteBackground;
		}

		BufferedImage bi = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D imageGraphics = bi.createGraphics();
		imageGraphics.drawImage(i.getImage(), 0, 0, backColor, (ImageObserver) null);
		ImageData data = awtBufferedImageToSWTImageData(bi);

		try
		{
			return new Image(Display.getCurrent(), data);
		}
		finally
		{
			imageGraphics.dispose();
		}
	}

	/**
	 * SWTColorToAWTColor
	 * 
	 * @param background
	 * @return java.awt.Color
	 */
	public static java.awt.Color swtColorToAWTColor(org.eclipse.swt.graphics.Color background)
	{
		return new java.awt.Color(background.getRed(), background.getGreen(), background.getBlue());
	}

	/**
	 * AWTBufferedImageToSWTImageData
	 * 
	 * @param bufferedImage
	 * @return ImageData
	 */
	public static ImageData awtBufferedImageToSWTImageData(BufferedImage bufferedImage)
	{
		if (bufferedImage.getColorModel() instanceof DirectColorModel)
		{
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel
					.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
					.getPixelSize(), palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];

			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}

			return data;
		}
		else if (bufferedImage.getColorModel() instanceof IndexColorModel)
		{
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];

			for (int i = 0; i < rgbs.length; i++)
			{
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}

			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
					.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];

			for (int y = 0; y < data.height; y++)
			{
				for (int x = 0; x < data.width; x++)
				{
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}

			return data;
		}

		return null;
	}

	/**
	 * getImageCacheNames
	 * 
	 * @param conn
	 * @return
	 */
	private static List<String> getImageCacheNames(Connection conn)
	{
		AptanaDB db = AptanaDB.getInstance();
		final List<String> list = new ArrayList<String>();

		db.execute("select name FROM CACHE", new IResultSetHandler() //$NON-NLS-1$
		{
			public void processResultSet(ResultSet resultSet) throws SQLException
			{
				String name = resultSet.getString(1);

				list.add(name);
			}
		});

		return list;
	}

	/**
	 * loadImageCache
	 * 
	 * @param display
	 * @return int
	 */
	public static int loadImageCache(final Display display)
	{
		int result = -1;

		if (imageCacheLoaded == false)
		{
			imageCacheLoaded = true;

			try
			{
				if (checkTable() == false)
				{
					createTable();
				}
			}
			catch (SQLException e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_ErrorInitializingDbConnection, e);

				if (e.getNextException() != null)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_ErrorInitializingDbConnection, e
							.getNextException());
				}
			}

			final class Handler implements IResultSetHandler
			{
				public int count = 0;

				public void processResultSet(ResultSet resultSet) throws SQLException
				{
					String name = resultSet.getString(1);
					byte[] data = resultSet.getBytes(2);

					ImageLoader loader = new ImageLoader();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					loader.load(in);

					ImageUtils.fileIconsHash.put(name, new Image(display, loader.data[0]));

					count++;
				}
			}

			AptanaDB db = AptanaDB.getInstance();
			Handler handler = new Handler();

			db.execute("SELECT * FROM CACHE", handler); //$NON-NLS-1$

			result = handler.count;
		}

		return result;
	}

	/**
	 * saveImageCache
	 * 
	 * @return int
	 */
	public static int saveImageCache()
	{
		Connection conn = AptanaDB.getInstance().getConnection();
		int numRows = 0;
		
		if (conn != null)
		{
			List<String> currentList = getImageCacheNames(conn);
			Iterator<String> keys = fileIconsHash.keySet().iterator();
			String sql = "insert into CACHE (name, image, filesize) values (?,?,?)"; //$NON-NLS-1$
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	
			while (keys.hasNext())
			{
				String name = keys.next();
	
				if (currentList.contains(name) == false)
				{
					Image image = ImageUtils.fileIconsHash.get(name);
	
					try
					{
						// Write out image data to memory byte array
						ImageLoader saver = new ImageLoader();
	
						saver.data = new ImageData[] { image.getImageData() };
						saver.save(out, SWT.IMAGE_BMP);
	
						byte[] imageBytes = out.toByteArray();
	
						PreparedStatement prepStmt = conn.prepareStatement(sql);
	
						prepStmt.setString(1, name);
						prepStmt.setBytes(2, imageBytes);
						prepStmt.setInt(3, imageBytes.length);
	
						numRows += prepStmt.executeUpdate();
	
						prepStmt.close();
					}
					catch (SQLException e)
					{
						IdeLog.logInfo(CoreUIPlugin.getDefault(), Messages.ImageUtils_ErrorSavingIconImageCache, e);
					}
				}
	
				out.reset();
			}
		}

		return numRows;
	}

	/**
	 * createTable
	 */
	private static void createTable()
	{
		AptanaDB db = AptanaDB.getInstance();

		db.execute("CREATE TABLE CACHE (name varchar(255) PRIMARY KEY,image BLOB(25000),filesize int)"); //$NON-NLS-1$
	}

	/**
	 * checkTable
	 * 
	 * @return
	 * @throws SQLException
	 */
	private static boolean checkTable() throws SQLException
	{
		Connection conn = null;
		Statement s = null;
		boolean result = true;

		try
		{
			conn = AptanaDB.getInstance().getConnection();
			
			if (conn != null)
			{
				s = conn.createStatement();
				s.execute("select name FROM CACHE where name='test'"); //$NON-NLS-1$
			}
			else
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_NoConnection);
				
				result = false;
			}
		}
		catch (SQLException sqle)
		{
			String theError = (sqle).getSQLState();

			/** If table exists will get - WARNING 02000: No row was found * */
			if (theError.equals("42X05")) // Table does not exist //$NON-NLS-1$
			{
				result = false;
			}
			else if (theError.equals("42X14") || theError.equals("42821")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_IncorrectTableDefinition);

				throw sqle;
			}
			else
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ImageUtils_SQLException);

				throw sqle;
			}
		}
		finally
		{
			if (s != null)
			{
				s.close();
			}

			if (conn != null)
			{
				conn.close();
			}
		}

		return result;
	}
}
