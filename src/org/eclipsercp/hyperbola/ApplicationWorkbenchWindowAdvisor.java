package org.eclipsercp.hyperbola;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	
	private Image statusImage;

	private TrayItem trayItem;

	private Image trayImage;

	private ApplicationActionBarAdvisor actionBarAdvisor;

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
    	actionBarAdvisor = new ApplicationActionBarAdvisor(configurer);
    	return actionBarAdvisor;
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setShowMenuBar(true);
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(false);
        configurer.setShowPerspectiveBar(false);
        configurer.setTitle("Hyperbola"); //$NON-NLS-1$
    }
    
    public void postWindowOpen() {
    	statusImage = AbstractUIPlugin.imageDescriptorFromPlugin(
    			Application.PLUGIN_ID,
    			IImageKeys.ONLINE).createImage();
    	IStatusLineManager statusline = getWindowConfigurer().
    			getActionBarConfigurer().getStatusLineManager();
    	statusline.setMessage(statusImage, "Online");
    }
    
    public void dispose() {
    	if(statusImage != null)
			statusImage.dispose();
		if(trayImage != null)
			trayImage.dispose();
		if(trayItem != null)
			trayItem.dispose();
    }
}
