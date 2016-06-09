package org.eclipsercp.hyperbola;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import org.eclipse.ui.part.ViewPart;
import org.eclipsercp.hyperbola.model.Session;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;

public class ContactsView extends ViewPart {

	public static final String ID =
			"org.eclipsercp.hyperbola.views.contacts"; //$NON-NLS-1$

	TreeViewer treeViewer;

	Action chatAction;

	private AdapterFactory adapterFactory = new AdapterFactory();

	ArrayList<String> openEditors = new ArrayList<>();

	private IPartListener partListener = new IPartListener() {

		@Override
		public void partOpened(IWorkbenchPart part) {
			trackOpenChatEditors(part);
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			trackOpenChatEditors(part);
		}

		private void trackOpenChatEditors(IWorkbenchPart part) {
			if (!(part instanceof ChatEditor)) {
				return;
			}
			ChatEditor editor = (ChatEditor) part;
			IEditorInput input = editor.getEditorInput();
			String participant = input.getName();
			if (openEditors.contains(participant)) {
				openEditors.remove(participant);
			} else {
				openEditors.add(participant);
			}
			treeViewer.refresh(true);
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
		}
	};

	private class ContactsDecorator implements ILabelDecorator, IFontDecorator {

		public ContactsDecorator() {
		}

		@Override
		public Image decorateImage(Image image, Object element) {
			return null;
		}

		@Override
		public String decorateText(String text, Object element) {
			return null;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Font decorateFont(Object element) {
			if(element instanceof RosterEntry) {
				RosterEntry entry = (RosterEntry)element;
				if(ContactsView.this.openEditors.contains(entry.getUser())) {
					return JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
				}
			}
			return null;
		}
	}

	public ContactsView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		HyperbolaLabelProvider hyperbolaLabelProvider = new HyperbolaLabelProvider();
		DecoratingLabelProvider decorator =
				new DecoratingLabelProvider(hyperbolaLabelProvider,
						new ContactsDecorator());
		treeViewer.setLabelProvider(decorator);

		treeViewer.setContentProvider(new HyperbolaContentProvider());
		Roster roster = Session.getInstance().getConnection().getRoster();
		treeViewer.setInput(roster);
		if(roster != null) {
			roster.addRosterListener(new RosterListener() {
				@Override
				public void entriesAdded(Collection<String> arg0) {
					refresh();
				}
				@Override
				public void entriesDeleted(Collection<String> arg0) {
					refresh();
				}
				@Override
				public void entriesUpdated(Collection<String> arg0) {
					refresh();
				}
				@Override
				public void presenceChanged(org.jivesoftware.smack.packet.Presence arg0) {
					refresh();
				};
			});
		}

		initDragAndDrop(treeViewer);
		getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
	}

	private void makeActions() {

		chatAction = new Action() {
			@Override
			public void run() {
				ITreeSelection selection = treeViewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof RosterEntry) {
					ChatEditorInput input = new ChatEditorInput(((RosterEntry) obj).getUser());
					try {
						getSite().getWorkbenchWindow().getActivePage().openEditor(input, ChatEditor.ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				chatAction.run();
			}
		});
	}

	private void hookContextMenu() {
	}

	void refresh() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				treeViewer.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	protected void initDragAndDrop(final StructuredViewer viewer) {
		int operations = DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[] { EditorInputTransfer
				.getInstance() };
		DragSourceListener listener = new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				if (EditorInputTransfer.getInstance().isSupportedType(
						event.dataType)) {
					String[] names = getUsers();
					EditorInputData[] inputs = new EditorInputData[names.length];
					if (names.length > 0) {
						for (int i = 0; i < names.length; i++) {
							String name = names[i];
							inputs[i] = EditorInputTransfer
									.createEditorInputData(ChatEditor.ID,
											new ChatEditorInput(name));
						}
						event.data = inputs;
						return;
					}
				}
				event.doit = false;
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
			}

			@Override
			public void dragStart(DragSourceEvent event) {
				super.dragStart(event);
			}
		};
		viewer.addDragSupport(operations, transferTypes, listener);
	}

	String[] getUsers() {
		ITreeSelection selection = treeViewer.getStructuredSelection();
		ArrayList<String> users = new ArrayList<>();
		if (!selection.isEmpty()) {
			for (Object obj : selection.toArray()) {
				if (obj instanceof RosterEntry) {
					String name = ((RosterEntry) obj).getUser();
					users.add(name);
				} else {
					users.clear();
					break;
				}
			}
		}

		return users.toArray(new String[]{});
	}

	@Override
	public void dispose() {
		Platform.getAdapterManager().unregisterAdapters(adapterFactory);
		getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
		super.dispose();
	}

}
