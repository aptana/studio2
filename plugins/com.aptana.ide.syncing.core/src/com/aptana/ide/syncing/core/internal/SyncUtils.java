package com.aptana.ide.syncing.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.aptana.ide.core.io.vfs.IExtendedFileStore;
import com.aptana.ide.syncing.core.SyncingPlugin;

/**
 * @author Max Stepanov
 *
 */
public final class SyncUtils {

	/**
	 * 
	 */
	private SyncUtils() {
	}

	public static void copy(IFileStore source, IFileInfo sourceInfo, IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = (monitor == null) ? new NullProgressMonitor() : monitor;
			checkCanceled(monitor);
			monitor.beginTask("", sourceInfo == null ? 3 : 2);
			if (sourceInfo == null) {
				sourceInfo = source.fetchInfo(IExtendedFileStore.DETAILED, subMonitorFor(monitor, 1));
			}
			checkCanceled(monitor);
			if (sourceInfo.isDirectory()) {
				destination.mkdir(EFS.NONE, subMonitorFor(monitor, 2));
			} else {
				final byte[] buffer = new byte[8192];
				long length = sourceInfo.getLength();
				int totalWork = (length == -1) ? IProgressMonitor.UNKNOWN : 1 + (int) (length / buffer.length);
				InputStream in = null;
				OutputStream out = null;
				try {
					in = source.openInputStream(EFS.NONE, subMonitorFor(monitor, 0));
					out = destination.openOutputStream(EFS.NONE, subMonitorFor(monitor, 0));
					IProgressMonitor subMonitor = subMonitorFor(monitor, 2);
					subMonitor.beginTask(MessageFormat.format("Copying {0}", source.toString()), totalWork);
					while (true) {
						int bytesRead = -1;
						try {
							bytesRead = in.read(buffer);
						} catch (IOException e) {
							error(MessageFormat.format("Failed reading {0}", source.toString()), e);
						}
						if (bytesRead == -1)
							break;
						try {
							out.write(buffer, 0, bytesRead);
						} catch (IOException e) {
							error(MessageFormat.format("Failed writing to {0}", destination.toString()), e);
						}
						subMonitor.worked(1);
					}
					subMonitor.done();
				} finally {
					safeClose(in);
					safeClose(out);	
				}
			}
			destination.putInfo(sourceInfo, EFS.SET_ATTRIBUTES | EFS.SET_LAST_MODIFIED | options, subMonitorFor(monitor, 1));
		} finally {
			monitor.done();
		}
	}
	
	private static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		if (monitor instanceof NullProgressMonitor) {
			return monitor;
		}
		return new SubProgressMonitor(monitor, ticks);
	}

	private static void checkCanceled(IProgressMonitor monitor) {
		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}
	
	private static void error(String message, Exception e) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, SyncingPlugin.PLUGIN_ID, message, e));
	}

	private static void safeClose(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException ignore) {
		}
	}

	private static void safeClose(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException ignore) {
		}
	}

}
