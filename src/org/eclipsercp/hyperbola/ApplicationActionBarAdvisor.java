package org.eclipsercp.hyperbola;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;

	private IWorkbenchAction aboutAction;

	private IWorkbenchAction addContactAction;

	private IWorkbenchAction chatAction;

	private StatusLineContributionItem statusItem;

	private IWorkbenchAction preferencesAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(IWorkbenchWindow window) {
		exitAction = ActionFactory.QUIT.create(window);
		exitAction.setActionDefinitionId("org.eclipse.ui.file.exit");
		register(exitAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
		addContactAction = new AddContactAction(window);
		register(addContactAction);
		chatAction = new ChatAction(window);
		register(chatAction);
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager hyperbolaMenu = new MenuManager(
				"&Hyperbola", "hyperbola"); //$NON-NLS-1$ //$NON-NLS-2$
		hyperbolaMenu.add(addContactAction);
		hyperbolaMenu.add(chatAction);
		hyperbolaMenu.add(new Separator());
		hyperbolaMenu.add(preferencesAction);
		hyperbolaMenu.add(new Separator());
		hyperbolaMenu.add(exitAction);
		//hyperbolaMenu.add(new GroupMarker("other-actions"));
		//hyperbolaMenu.appendToGroup("other-actions", aboutAction);
		MenuManager helpMenu = new MenuManager(
				"&Help", "help"); //$NON-NLS-1$ //$NON-NLS-2$
		helpMenu.add(aboutAction);
		//hyperbolaMenu.add(helpMenu);
		menuBar.add(hyperbolaMenu);
		menuBar.add(helpMenu);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		coolBar.setLockLayout(true);
		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
		coolBar.add(toolbar);
		toolbar.add(addContactAction);
		toolbar.add(new Separator());
		toolbar.add(chatAction);
	}

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusItem = new StatusLineContributionItem("LoggedInStatus"); //$NON-NLS-1$
		statusItem.setText("Logged in"); //$NON-NLS-1$
		statusLine.add(statusItem);
	}
	
}
