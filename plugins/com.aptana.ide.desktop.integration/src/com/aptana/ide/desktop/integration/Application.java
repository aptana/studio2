package com.aptana.ide.desktop.integration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.desktop.integration.server.DesktopIntegrationServerActivator;
import com.aptana.ide.desktop.integration.server.LaunchHelper;

public class Application implements IApplication {

    private IApplication productApplication;

    public Object start(IApplicationContext context) throws Exception {
        int port = -1;
        try {
            final Object args = context.getArguments().get(
                    IApplicationContext.APPLICATION_ARGS);
            if (args instanceof String[]) {
            	port = checkForRunningInstance();
            	if (port < 0) {
            		// Another instance is not running; launches the application
            		String productApplicationId = Platform.getProduct()
            		.getApplication();
            		Object application = getApplication(productApplicationId);
            		if (application instanceof IApplication) {
            			productApplication = (IApplication) application;
            			// Start the thread here looping
            			Thread thread = new Thread(new Runnable() {
            				public void run() {
            					while (true) {
            						int port = checkForRunningInstance();
            						if (port < 0) {
            							try {
            								Thread.sleep(1000);
            							} catch (InterruptedException e) {
            								// TODO log
            								break;
            							}
            						} else {
            							getLaunchHelper()
            							.sendInitialFilesAndInstallFeatures(
            									port, (String[]) args);
            							break;
            						}
            					}
            				}
            			});
            			thread.start();

            			return productApplication.start(context);
            		} else {
            			// TODO Log error
            		}
            	} else {
            		getLaunchHelper().sendInitialFilesAndInstallFeatures(port, (String[]) args);
            	}
            }
        } catch (Exception ex) {
            IdeLog.logError(DesktopIntegrationActivator.getDefault(),
                    Messages.Application_ERR_UnableToGetRunningInstance, ex);
        } catch (Error ex) {
            IdeLog.logError(DesktopIntegrationActivator.getDefault(),
                    Messages.Application_ERR_UnableToGetRunningInstance, ex);
        }
        return EXIT_OK;
    }

    public void stop() {
        if (productApplication != null) {
            productApplication.stop();
        }
    }

    /*
     * return the application to run, or null if not even the default
     * application is found.
     */
    private Object getApplication(String applicationId) throws CoreException {
        // Find the name of the application as specified by the PDE JUnit
        // launcher.
        // If no application is specified, the 3.0 default workbench application
        // is returned.
        IExtension extension = Platform.getExtensionRegistry().getExtension(
                Platform.PI_RUNTIME, Platform.PT_APPLICATIONS, applicationId);

        // If the extension does not have the correct grammar, return null.
        // Otherwise, return the application object.
        IConfigurationElement[] elements = extension.getConfigurationElements();
        if (elements.length > 0) {
            IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
            if (runs.length > 0) {
                Object runnable = runs[0].createExecutableExtension("class"); //$NON-NLS-1$
                if (runnable instanceof IPlatformRunnable
                        || runnable instanceof IApplication)
                    return runnable;
            }
        }
        return null;
    }

    private static int checkForRunningInstance() {
        return getLaunchHelper().checkForRunningInstance();
    }

    private static LaunchHelper getLaunchHelper() {
        return DesktopIntegrationServerActivator.getDefault().getLaunchHelper();
    }
}
