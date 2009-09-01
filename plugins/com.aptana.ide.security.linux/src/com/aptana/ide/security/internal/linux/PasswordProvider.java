package com.aptana.ide.security.internal.linux;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.Base64;
import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.preferences.IPreferenceConstants;
import com.aptana.ide.core.ui.Messages;
import com.aptana.ide.security.linux.Activator;

public class PasswordProvider extends org.eclipse.equinox.security.storage.provider.PasswordProvider
{

	private static final String ALGORITHM = "AES/ECB/PKCS5Padding"; //$NON-NLS-1$
	private String accountName = System.getProperty("user.home"); //$NON-NLS-1$

	@Override
	public PBEKeySpec getPassword(IPreferencesContainer container, int passwordType)
	{
		if (accountName == null)
			return null;

		final boolean newPassword = ((passwordType & CREATE_NEW_PASSWORD) != 0);
		final boolean passwordChange = ((passwordType & PASSWORD_CHANGE) != 0);

		try
		{
			if (!newPassword && !passwordChange)
			{
				char[] existing = getPassword();
				if (existing != null && existing.length != 0)
					return new PBEKeySpec(existing);
			}

			// Prompt user via dialog!
			if (!useUI())
				return null;
			final String[] result = new String[1];
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					StorageLoginDialog loginDialog = new StorageLoginDialog(Display.getDefault().getActiveShell(),
							newPassword, passwordChange);
					if (loginDialog.open() == Window.OK)
						result[0] = loginDialog.getPassword();
					else
						result[0] = null;
				}
			});
			String password = result[0];
			if (password == null || password.trim().length() == 0)
				return null;
			writePassword(password);
			return new PBEKeySpec(password.toCharArray());
		}
		catch (IOException e)
		{
			IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Determines if it is a good idea to show UI prompts
	 */
	private static boolean useUI()
	{
		return PlatformUI.isWorkbenchRunning();
	}

	private void writePassword(String password) throws IOException
	{
		SecretKeySpec key = getKeySpec();
		byte[] encrypted = encrypt(key, password);
		if (encrypted != null && encrypted.length > 0)
		{
			String b64 = Base64.encodeBytes(encrypted);
			FileUtils.writeStringToFile(b64, getPasswordFile());
		}
	}

	private char[] getPassword() throws IOException
	{
		String encrypted = getEncryptedPassword();
		if (encrypted == null)
			return new char[0];
		byte[] bytes = Base64.decode(encrypted);

		if (bytes != null)
		{
			SecretKeySpec key = getKeySpec();
			byte[] decrypted = decrypt(key, bytes);
			if (decrypted == null || decrypted.length == 0)
				return new char[0];
			return new String(decrypted).toCharArray();
		}
		else
		{
			return new char[0];
		}
	}

	private String getEncryptedPassword() throws IOException
	{
		File file = getPasswordFile();
		if (!file.exists())
			return null;
		return FileUtils.readContent(file);
	}

	private File getPasswordFile() throws IOException
	{
		File file = new File(accountName + File.separator + ".aptanasecure", ".store");
		file.getParentFile().mkdirs();
		return file;
	}

	private SecretKeySpec getKeySpec()
	{
		String ksPref = Platform.getPreferencesService().getString(AptanaCorePlugin.ID, IPreferenceConstants.CACHED_KEY, "", null);
		byte[] key = null;

		if (!"".equals(ksPref)) //$NON-NLS-1$
		{
			try
			{
				byte[] bytes = Base64.decode(ksPref);
				if (bytes != null)
				{
					key = bytes;
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(),
						Messages.AptanaAuthenticator_ERR_UnableToDecodeExistingKey, e);
			}
		}

		KeyGenerator kgen;
		if (key == null || key.length == 0) //$NON-NLS-1$
		{
			try
			{
				kgen = KeyGenerator.getInstance("AES"); //$NON-NLS-1$
				kgen.init(128);

				SecretKey skey = kgen.generateKey();
				key = skey.getEncoded();
				String b64 = Base64.encodeBytes(skey.getEncoded());
				IEclipsePreferences node = new InstanceScope().getNode(AptanaCorePlugin.ID);
				node.put(IPreferenceConstants.CACHED_KEY, b64);
				node.flush();
			}
			catch (NoSuchAlgorithmException e)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchAlgorithm, e);
				return null;
			}
			catch (BackingStoreException e)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), com.aptana.ide.security.internal.linux.Messages.PasswordProvider_ERR_UnableToStoreKey, e);
				return null;
			}
		}
		return new SecretKeySpec(key, "AES"); //$NON-NLS-1$
	}

	private byte[] encrypt(SecretKeySpec skeySpec, String password)
	{
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return cipher.doFinal(password.getBytes());
		}
		catch (NoSuchAlgorithmException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchAlgorithm, e);
		}
		catch (NoSuchPaddingException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchPadding, e);
		}
		catch (InvalidKeyException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_InvalidKey, e);
		}
		catch (IllegalBlockSizeException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_IllegalBlockSize, e);
		}
		catch (BadPaddingException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_BadPadding, e);
		}
		return null;
	}

	private byte[] decrypt(SecretKeySpec skeySpec, byte[] encryptedPassword)
	{
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return cipher.doFinal(encryptedPassword);
		}
		catch (NoSuchAlgorithmException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchAlgorithm, e);
		}
		catch (NoSuchPaddingException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_NoSuchPadding, e);
		}
		catch (InvalidKeyException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_InvalidKey, e);
		}
		catch (IllegalBlockSizeException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_IllegalBlockSize, e);
		}
		catch (BadPaddingException e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.AptanaAuthenticator_ERR_BadPadding, e);
		}
		return null;
	}
}
