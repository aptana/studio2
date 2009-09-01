package com.aptana.ide.intro.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WWinPluginPulldown;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.PerspectiveManager;
import com.aptana.ide.core.ui.WebPerspectiveFactory;
import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.intro.preferences.IPreferenceConstants;
import com.aptana.ide.server.jetty.portal.PortalServer;

/**
 * Show the "My Aptana" actions drop-down in the toolbar.
 * 
 * @author Shalom Gibly
 */
public class ShowMyAptanaAction implements IWorkbenchWindowPulldownDelegate2 {

    private static final String CACHED_IMAGE = "cached_intro_image"; //$NON-NLS-1$
    private static final String LOCAL_IMAGE_LOCATION = "/icons/aptana_home.png"; //$NON-NLS-1$
    private static final int DAY = 1000 * 60 * 60 * 24;

    private IWorkbenchWindow window;
    private Menu toolbarMenu = null;

    private ToolItem toolItem;
    private ImageLoader loader;
    private ImageData[] imageData;
    private Thread animateThread;
    private Image[] image;

    private long lastChecked;
    private boolean animationRunning;

    public ShowMyAptanaAction() {
    }

    /**
     * org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.
     * widgets.Control)
     */
    public Menu getMenu(Control parent) {
        if (toolbarMenu != null) {
            toolbarMenu.dispose();
        }
        toolbarMenu = new Menu(parent);
        buildMenu(toolbarMenu);

        return toolbarMenu;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate2#getMenu(org.eclipse.swt.widgets.Menu)
     */
    public Menu getMenu(Menu parent) {
        Menu menu = new Menu(parent);
        buildMenu(menu);

        return menu;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        if (toolbarMenu != null) {
            toolbarMenu.dispose();
            toolbarMenu = null;
        }
        toolItem = null;
        animateThread = null;
        if (image != null) {
            for (int i = 0; i < image.length; i++) {
                image[i].dispose();
            }
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if (animationRunning) {
            // stops the animation
            animationRunning = false;
        }
        openEditor();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // locates the ToolItem associated with the action
        if (action == null || !(action instanceof WWinPluginPulldown)) {
            return;
        }
        if (toolItem == null) {
            WWinPluginPulldown pulldown = (WWinPluginPulldown) action;
            CoolBarManager manager = ((WorkbenchWindow) this.window)
                    .getCoolBarManager();
            // this returns the list of actionSets groups
            IContributionItem[] items = manager.getItems();
            for (IContributionItem item : items) {
                if (item instanceof IToolBarContributionItem) {
                    IToolBarContributionItem toolbarItem = (IToolBarContributionItem) item;
                    // this returns the list of actual items for the actions
                    IContributionItem[] children = toolbarItem
                            .getToolBarManager().getItems();
                    for (IContributionItem child : children) {
                        if (child.getId().equals(action.getId())) {
                            // found the toolbar item that corresponds to the
                            // action
                            ActionContributionItem actionItem = (ActionContributionItem) child;
                            toolItem = (ToolItem) actionItem.getWidget();
                            File file = getLocalImageFile();
                            if (file.exists()) {
                                setDefaultImage(file.getPath());
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (toolItem == null || !haventCheckedInADay()) {
            return;
        }

        lastChecked = System.currentTimeMillis();
        if (!isRemoteImageUpdated()) {
            return;
        }
        loadImage();
        toolItem.setImage(image[0]);
        updateCoolbar();
        if (isAnimated()) {
            startAnimationThreads(toolItem);
        }
    }

    public static void openEditor() {
        switchPerspective();
        IPreferenceStore prefs = IntroPlugin.getDefault().getPreferenceStore();
        String editorId = prefs.getString(IPreferenceConstants.INTRO_EDITOR_ID);
        IEditorPart editorPart = CoreUIUtils.openEditor(editorId, true);
        if (editorPart == null) {
            // falls back to the default
            editorId = prefs
                    .getDefaultString(IPreferenceConstants.INTRO_EDITOR_ID);
            prefs.setValue(IPreferenceConstants.INTRO_EDITOR_ID, editorId);
            CoreUIUtils.openEditor(editorId, true);
        }
    }

    private void buildMenu(Menu menu) {
        ActionUtils.buildMenu(menu, window);
    }

    /*
     * In case needed (and approved by the user), we will switch to the Aptana
     * perspective.
     */
    private static void switchPerspective() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (PerspectiveManager.shouldSwitchPerspective(window,
                WebPerspectiveFactory.PERSPECTIVE_ID)) {
            PerspectiveManager.switchToPerspective(window,
                    WebPerspectiveFactory.PERSPECTIVE_ID);
        }
    }

    private void setDefaultImage(String filepath) {
        if (filepath == null || filepath.length() == 0) {
            return;
        }
        loader = new ImageLoader();
        try {
            imageData = loader.load(filepath);
            if (imageData.length > 0) {
                image = new Image[1];
                image[0] = new Image(Display.getCurrent(), imageData[0]);
                toolItem.setImage(image[0]);
                updateCoolbar();
            }
        } catch (Exception e) {
            IdeLog.logError(IntroPlugin.getDefault(), e.getLocalizedMessage(), e);
        }
    }

    private void loadImage() {
        loader = new ImageLoader();
        boolean loaded = false;
        InputStream stream = getRemoteImageStream();
        if (stream != null) {
            try {
                imageData = loader.load(stream);
                loaded = true;
                cacheImage();
            } catch (SWTException e) {
            }
        }
        if (!loaded) {
            // failed to get the remote image; falls back to the cached one
            imageData = loader.load(getLocalImageStream());
        }

        int numFramesOfAnimation = imageData.length;
        image = new Image[numFramesOfAnimation];
        int fullWidth = loader.logicalScreenWidth;
        int fullHeight = loader.logicalScreenHeight;
        Display display = Display.getCurrent();
        for (int i = 0; i < numFramesOfAnimation; ++i) {
            if (i == 0) {
                // for the first frame of animation, just draw the first frame
                image[i] = new Image(display, imageData[i]);
                fullWidth = imageData[i].width;
                fullHeight = imageData[i].height;
            } else {
                // after the first frame of animation, draw the background
                // or previous frame first, then the new image data
                image[i] = new Image(display, fullWidth, fullHeight);
                GC gc = new GC(image[i]);
                gc.fillRectangle(0, 0, fullWidth, fullHeight);
                switch (imageData[i].disposalMethod) {
                case SWT.DM_FILL_BACKGROUND:
                    gc.fillRectangle(imageData[i].x, imageData[i].y,
                            imageData[i].width, imageData[i].height);
                    break;
                default:
                    /* Restore the previous image before drawing. */
                    gc.drawImage(image[i - 1], 0, 0, fullWidth, fullHeight, 0,
                            0, fullWidth, fullHeight);
                    break;
                }
                Image newFrame = new Image(display, imageData[i]);
                gc.drawImage(newFrame, 0, 0, imageData[i].width,
                        imageData[i].height, imageData[i].x, imageData[i].y,
                        imageData[i].width, imageData[i].height);
                newFrame.dispose();
                gc.dispose();
            }
        }
    }

    private void startAnimationThreads(final ToolItem item) {
        animationRunning = true;
        animateThread = new Thread("Intro icon animation") { //$NON-NLS-1$

            private int imageDataIndex = 0;

            public void run() {
                Display display = Display.getDefault();
                try {
                    int repeatCount = loader.repeatCount;
                    while (animationRunning
                            && (loader.repeatCount == 0 || repeatCount > 0)) {
                        imageDataIndex = (imageDataIndex + 1)
                                % imageData.length;
                        if (!display.isDisposed()) {
                            display.asyncExec(new Runnable() {
                                public void run() {
                                    if (!item.isDisposed()) {
                                        item.setImage(image[imageDataIndex]);
                                    }
                                }
                            });
                        }

                        // Sleep for the specified delay time (adding
                        // commonly-used slow-down fudge factors).
                        try {
                            int ms = imageData[imageDataIndex].delayTime * 10;
                            if (ms < 20) {
                                ms += 30;
                            }
                            if (ms < 30) {
                                ms += 10;
                            }
                            Thread.sleep(ms);
                        } catch (InterruptedException e) {
                        }

                        // If we have just drawn the last image, decrement the
                        // repeat count and start again.
                        if (imageDataIndex == imageData.length - 1) {
                            repeatCount--;
                        }
                    }
                } catch (SWTException e) {
                    IdeLog.logError(IntroPlugin.getDefault(), e
                            .getLocalizedMessage(), e);
                }
            }
        };
        animateThread.setDaemon(true);
        animateThread.start();
    }

    private boolean isAnimated() {
        return imageData.length > 1;
    }

    /**
     * @return true if there is a new remote image for the action
     */
    private boolean isRemoteImageUpdated() {
        HttpURLConnection httpURLConnection = null;
        try {
            URL location = new URL(getRemoteImageLocation());
            URLConnection urlConnection = location.openConnection();

            if (urlConnection instanceof HttpURLConnection) {
                httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.addRequestProperty(
                        "Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
                httpURLConnection.setRequestMethod("HEAD"); //$NON-NLS-1$
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Check for lastModified?
                    boolean updated = true;
                    IPreferenceStore pref = IntroPlugin.getDefault()
                            .getPreferenceStore();
                    long lastModified = httpURLConnection.getLastModified();
                    if (lastModified == 0) {
                        // unknown
                    } else {
                        long lastLastModified = pref.getLong(location
                                .toString());
                        if (lastLastModified >= lastModified) {
                            updated = false;
                        }
                    }
                    pref.setValue(location.toString(), lastModified);
                    return updated;
                }
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            // cleanup
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return false;
    }

    private InputStream getLocalImageStream() {
        File file = getLocalImageFile();
        if (file.exists()) {
            try {
                return file.toURI().toURL().openStream();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
        }
        // no cached version; uses the local one in the plugin
        return getClass().getResourceAsStream(LOCAL_IMAGE_LOCATION);
    }

    private InputStream getRemoteImageStream() {
        String remoteLocation = getRemoteImageLocation();
        try {
            URL url = new URL(remoteLocation);
            if (OnlineDetectionService.isAvailable(url)) {
                return url.openStream();
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private File getLocalImageFile() {
        IPath directory = IntroPlugin.getDefault().getStateLocation();
        String remotePath = getRemoteImageLocation();
        String ext = getExtension(remotePath);
        return directory.append(CACHED_IMAGE + ext).toFile();
    }

    private void cacheImage() {
        String filepath = getLocalImageFile().getAbsolutePath();
        loader.save(filepath, getImageFormat(getExtension(filepath)));
    }

    private boolean haventCheckedInADay() {
        return lastChecked == 0
                || lastChecked < System.currentTimeMillis() - DAY;
    }

    private void updateCoolbar() {
        // resizes the coolbar appropriately
        ((WorkbenchWindow) this.window).getCoolBarManager().update(true);
    }

    private static String getRemoteImageLocation() {
        return IntroPlugin.getDefault().getPreferenceStore().getString(
                IPreferenceConstants.INTRO_TOOLBAR_IMAGE_LOCATION);
    }

    private static String getExtension(String filepath) {
        int index = filepath.lastIndexOf("."); //$NON-NLS-1$
        return index < 0 ? "" : filepath.substring(index); //$NON-NLS-1$
    }

    private static int getImageFormat(String extension) {
        if (extension.equals(".png")) { //$NON-NLS-1$ 
            return SWT.IMAGE_PNG;
        }
        if (extension.equals(".gif")) { //$NON-NLS-1$
            return SWT.IMAGE_GIF;
        }
        if (extension.equals(".bmp")) { //$NON-NLS-1$
            return SWT.IMAGE_BMP;
        }
        if (extension.equals(".jpg")) { //$NON-NLS-1$
            return SWT.IMAGE_JPEG;
        }
        return SWT.IMAGE_ICO;
    }
}
