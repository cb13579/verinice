/*******************************************************************************
 * Copyright (c) 2009 Daniel Murygin <dm[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.gs.ui.rcp.main.bsi.views;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import sernet.gs.ui.rcp.main.Activator;
import sernet.gs.ui.rcp.main.ExceptionUtil;
import sernet.gs.ui.rcp.main.ImageCache;
import sernet.gs.ui.rcp.main.actions.RightsEnabledAction;
import sernet.gs.ui.rcp.main.bsi.editors.BSIElementEditorInput;
import sernet.gs.ui.rcp.main.bsi.editors.EditorFactory;
import sernet.gs.ui.rcp.main.common.model.CnAElementFactory;
import sernet.gs.ui.rcp.main.preferences.PreferenceConstants;
import sernet.gs.ui.rcp.main.service.ServiceFactory;
import sernet.verinice.interfaces.ActionRightIDs;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.interfaces.ICommandService;
import sernet.verinice.iso27k.rcp.ILinkedWithEditorView;
import sernet.verinice.iso27k.rcp.LinkWithEditorPartListener;
import sernet.verinice.model.bsi.BSIModel;
import sernet.verinice.model.bsi.IBSIModelListener;
import sernet.verinice.model.bsi.Note;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.rcp.RightsEnabledView;
import sernet.verinice.service.commands.crud.DeleteNote;
import sernet.verinice.service.commands.crud.LoadNotes;

public class M365StatusView extends RightsEnabledView implements ILinkedWithEditorView {

    private static final Logger LOG = Logger.getLogger(M365StatusView.class);

    public static final String ID = "sernet.gs.ui.rcp.main.bsi.views.M365StatusView"; //$NON-NLS-1$

    private ExpandBar expandBar;

    private ISelectionListener selectionListener;

    private ICommandService commandService;

    private CnATreeElement currentCnaElement;

    private RightsEnabledAction addM365StatusAction;

    private IBSIModelListener modelListener;

    private IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);

    private Action linkWithEditorAction;

    private boolean linkingActive = true;

    public M365StatusView() {
    }

    @Override
    public String getRightID() {
        return ActionRightIDs.M365STATUS;
    }

    /*
     * @see sernet.verinice.rcp.RightsEnabledView#getViewId()
     */
    @Override
    public String getViewId() {
        return ID;
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        final int expandBarSpacing = 4;

        parent.setLayout(new FillLayout());
        toggleLinking(Activator.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.LINK_TO_EDITOR));
        try {
            expandBar = new ExpandBar(parent, SWT.V_SCROLL);
            expandBar.setSpacing(expandBarSpacing);
            hookPageSelection();
        } catch (Exception e) {
            ExceptionUtil.log(e, Messages.BrowserView_3);
            LOG.error("Error while creating control", e); //$NON-NLS-1$
        }
        makeActions();
        fillLocalToolBar();
    }

    /**
     * Passing the focus request to the viewer's control.
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        expandBar.setFocus();
    }

    private void hookPageSelection() {
        selectionListener = new ISelectionListener() {
            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                pageSelectionChanged(part, selection);
            }
        };
        getSite().getPage().addPostSelectionListener(selectionListener);
        getSite().getPage().addPartListener(linkWithEditorPartListener);
    }

    protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part == this) {
            return;
        }
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        if (((IStructuredSelection) selection).size() != 1) {
            return;
        }
        try {
            Object element = ((IStructuredSelection) selection).getFirstElement();
            elementSelected(element);
            if (element instanceof CnATreeElement) {
                setNewInput((CnATreeElement) element);
            }
        } catch (Exception e) {
            LOG.error("Error while loading m365 status", e); //$NON-NLS-1$
        }
    }

    private void elementSelected(Object element) {
    	if(element==null) {
    		LOG.error("element is null");
    		return;
    	}
    	LOG.trace(element);
    	LOG.trace(getCurrentCnaElement());
    	if (element instanceof CnATreeElement && !element.equals(getCurrentCnaElement())) {
    		if (addM365StatusAction.checkRights()) {
                addM365StatusAction.setEnabled(true);
            }
            setCurrentCnaElement((CnATreeElement) element);
            clear();
            loadNotes();
        } else {
            addM365StatusAction.setEnabled(false);
        }
    }

    private void fillLocalToolBar() {
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        manager.add(this.addM365StatusAction);
        manager.add(this.linkWithEditorAction);
    }

    private void makeActions() {
        addM365StatusAction = new RightsEnabledAction(ActionRightIDs.ADDM365STATUS) {
            @Override
            public void doRun() {
                Note note = new Note();
                note.setCnATreeElement(getCurrentCnaElement());
                note.setCnAElementTitel(getCurrentCnaElement().getTitle());
                note.setTitel(Messages.M365StatusView_2);
                note.addListener(new Note.INoteChangedListener() {
                    @Override
                    public void noteChanged() {
                        clear();
                        loadNotes();
                    }
                });
                EditorFactory.getInstance().openEditor(note);
            }
        };
        addM365StatusAction.setText(Messages.M365StatusView_3);
        addM365StatusAction.setImageDescriptor(
                ImageCache.getInstance().getImageDescriptor(ImageCache.M365STATUS_NEW));
        addM365StatusAction.setEnabled(false);

        linkWithEditorAction = new Action(Messages.M365StatusView_0, IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                toggleLinking(isChecked());
            }
        };
        linkWithEditorAction
                .setImageDescriptor(ImageCache.getInstance().getImageDescriptor(ImageCache.LINKED));
        linkWithEditorAction.setChecked(isLinkingActive());
    }

    public void loadNotes() {
        final int defaultMargin = 4;
        final int layoutVerticalSpacing = 10;
        final int gdHeightHint = 100;
        try {
            LoadNotes command = new LoadNotes(getCurrentCnaElement().getDbId());
            command = getCommandService().executeCommand(command);
            List<Note> noteList = command.getNoteList();
            if (noteList != null && noteList.size() > 0) {
                for (final Note note : noteList) {
                    note.addListener(new Note.INoteChangedListener() {
                        @Override
                        public void noteChanged() {
                            clear();
                            loadNotes();
                        }
                    });

                    // set transient cna-element-titel
                    note.setCnAElementTitel(getCurrentCnaElement().getTitle());
                    Composite composite = new Composite(expandBar, SWT.NONE);
                    GridLayout layout = new GridLayout(2, false);
                    layout.marginLeft = defaultMargin;
                    layout.marginTop = defaultMargin;
                    layout.marginRight = defaultMargin;
                    layout.marginBottom = defaultMargin;
                    layout.verticalSpacing = layoutVerticalSpacing;
                    composite.setLayout(layout);

                    GridData gdText = new GridData();
                    gdText.grabExcessHorizontalSpace = true;
                    gdText.grabExcessVerticalSpace = false;
                    gdText.horizontalAlignment = GridData.FILL;
                    gdText.verticalAlignment = GridData.CENTER;
                    gdText.heightHint = gdHeightHint;
                    gdText.verticalSpan = 2;
                    int style = SWT.BORDER | SWT.MULTI | SWT.READ_ONLY;
                    Text text = new Text(composite, style | SWT.WRAP | SWT.V_SCROLL);
                    text.setLayoutData(gdText);
                    if (note.getText() != null) {
                        text.setText(note.getText());
                    }

                    Button editButton = new Button(composite, SWT.NONE);
                    editButton.setImage(ImageCache.getInstance().getImage(ImageCache.EDIT));
                    editButton.setToolTipText(Messages.M365StatusView_4);
                    editButton.addSelectionListener(new SelectionListener() {
                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                        }

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            editNote(note);
                        }
                    });

                    Button deleteButton = new Button(composite, SWT.NONE);
                    deleteButton.setImage(ImageCache.getInstance().getImage(ImageCache.DELETE));
                    deleteButton.setToolTipText(Messages.M365StatusView_5);
                    deleteButton.addSelectionListener(new SelectionListener() {
                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                        }

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            boolean b = MessageDialog.openQuestion(
                                    M365StatusView.this.getSite().getShell(), Messages.M365StatusView_6,
                                    NLS.bind(Messages.M365StatusView_7, note.getTitel()));
                            if (b) {
                                deleteNote(note);
                            }
                        }
                    });

                    ExpandItem item0 = new ExpandItem(expandBar, SWT.NONE, 0);
                    if (note.getTitel() != null) {
                        item0.setText(note.getTitel());
                    }
                    item0.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
                    item0.setControl(composite);
                    item0.setExpanded(true);
                } // end for
            }
        } catch (Exception e) {
            LOG.error("Error while loading m365 status", e); //$NON-NLS-1$
            ExceptionUtil.log(e, "Error while loading m365 status"); //$NON-NLS-1$
        }
    }

    protected void editNote(Note note) {
        EditorFactory.getInstance().openEditor(note);
    }

    protected void deleteNote(Note note) {
        DeleteNote command = new DeleteNote(note);
        try {
            getCommandService().executeCommand(command);
        } catch (CommandException e) {
            LOG.error("Error while deleting m365 status", e); //$NON-NLS-1$
            ExceptionUtil.log(e, Messages.M365StatusView_12);
        }
        clear();
        loadNotes();
    }

    public void clear() {
        for (Control ctl : expandBar.getChildren()) {
            ctl.dispose();
        }
        for (ExpandItem item : expandBar.getItems()) {
            item.dispose();
        }
    }

    public ICommandService getCommandService() {
        if (commandService == null) {
            commandService = createCommandServive();
        }
        return commandService;
    }

    private ICommandService createCommandServive() {
        return ServiceFactory.lookupCommandService();
    }

    public CnATreeElement getCurrentCnaElement() {
        return currentCnaElement;
    }

    public void setCurrentCnaElement(CnATreeElement currentCnaElement) {
        this.currentCnaElement = currentCnaElement;
    }

    @Override
    public void dispose() {
        getSite().getPage().removePostSelectionListener(selectionListener);
        BSIModel model = CnAElementFactory.getLoadedModel();
        model.removeBSIModelListener(modelListener);
        getSite().getPage().removePartListener(linkWithEditorPartListener);
        super.dispose();
    }

    protected void toggleLinking(boolean checked) {
        this.linkingActive = checked;
        if (checked) {
            Optional.ofNullable(getSite().getPage().getActiveEditor())
                    .ifPresent(this::editorActivated);
        }
    }

    protected boolean isLinkingActive() {
        return linkingActive;
    }

    /*
     * @see
     * sernet.verinice.iso27k.rcp.ILinkedWithEditorView#editorActivated(org.
     * eclipse.ui.IEditorPart)
     */
    @Override
    public void editorActivated(IEditorPart activeEditor) {
        if (!isLinkingActive() || !getViewSite().getPage().isPartVisible(this)) {
            return;
        }
        CnATreeElement element = BSIElementEditorInput.extractElement(activeEditor);
        if (element == null) {
            return;
        }
        elementSelected(element);
    }

    private void setNewInput(CnATreeElement elmt) {
        setViewTitle(Messages.M365StatusView_8 + " " + elmt.getTitle());
    }

    private void setViewTitle(String title) {
        this.setContentDescription(title);
    }
}
