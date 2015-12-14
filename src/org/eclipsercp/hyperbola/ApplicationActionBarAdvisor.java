package org.eclipsercp.hyperbola;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	
	private IWorkbenchAction exitAction;
	
	private IWorkbenchAction aboutAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	exitAction = ActionFactory.QUIT.create(window);
    	register(exitAction);
    	aboutAction = ActionFactory.ABOUT.create(window);
    	register(aboutAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager hyperbolaMenu = new MenuManager(
    			"&Hyperbola", "hyperbola");
    	hyperbolaMenu.add(exitAction);
    	hyperbolaMenu.add(new GroupMarker("other-actions"));
    	MenuManager helpMenu = new MenuManager(
    			"&Help", "help");
    	helpMenu.add(aboutAction);
    	menuBar.add(hyperbolaMenu);
    	menuBar.add(helpMenu);
    }
    
}
